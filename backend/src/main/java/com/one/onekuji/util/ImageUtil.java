package com.one.onekuji.util;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.List;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;

@Component
public class ImageUtil {

	@Value("${pictureFile.path}")
	private String picturePath;

	@Value("${pictureFile.path-mapping}")
	private String picturePathMapping;

	private static String staticPicturePath;
	private static String staticPicturePathMapping;
	private static final int TARGET_SIZE = 1600;
	private static final int STORE_TARGET_SIZE = 2000;
	private static final int RECT_WIDTH = 800;
	private static final int STORE_TARGET_WIDTH = 380;
	private static final int RECT_HEIGHT = 600;
	private static final int STORE_TARGET_HEIGHT = 216;
	private static final float OUTPUT_QUALITY = 0.85f;
	private static final float STORE_OUTPUT_QUALITY = 0.85f;

	@PostConstruct
	public void init() {
		staticPicturePath = picturePath.endsWith("/") ? picturePath : picturePath + "/";
		staticPicturePathMapping = picturePathMapping.endsWith("/") ? picturePathMapping : picturePathMapping + "/";
	}

	public static String[] upload(MultipartFile file, int[][] rwdSizes) {
		return uploadFile(file, rwdSizes, false);
	}

	public static String storeUpload(MultipartFile file) {
		return storeUploadFile(file, false);
	}

	public static String uploadForCKEditor(MultipartFile file) {
		return uploadFile(file, null, true)[0];
	}

	public static String uploadRectangle(MultipartFile file) {
		return uploadRectangleFile(file);
	}

	private static String[] uploadFile(MultipartFile file, int[][] rwdSizes, boolean isForCKEditor) {
		if (file.isEmpty()) {
			throw new IllegalArgumentException("File is empty");
		}

		String originalFileName = file.getOriginalFilename();
		String uniqueFileName = UUID.randomUUID().toString() + "_"
				+ (originalFileName != null ? originalFileName : "image");
		String fileExtension = getFileExtension(originalFileName);

		if (fileExtension == null) {
			throw new IllegalArgumentException("Invalid file extension");
		}

		var filePaths = new ArrayList<String>();
		String filePath = staticPicturePath + uniqueFileName;
		File dest = new File(filePath);

		try {
			Files.createDirectories(Paths.get(staticPicturePath));

			BufferedImage originalImage = ImageIO.read(file.getInputStream());
			if (originalImage == null) {
				throw new IOException("Failed to read image file");
			}

			if (isForCKEditor) {
				Thumbnails.of(originalImage).scale(1).toFile(dest);
				filePaths.add(staticPicturePathMapping + uniqueFileName);
			} else {

				for (int[] size : rwdSizes) {
					String rwdFilePath = staticPicturePath + size[0] + "x" + size[1] + "_" + uniqueFileName;
					File rwdDest = new File(rwdFilePath);
					BufferedImage processedImage = cropImageFromCenter(originalImage, size[0], size[1]);
					ImageIO.write(processedImage, fileExtension, rwdDest);
					filePaths.add(staticPicturePathMapping + size[0] + "x" + size[1] + "_" + uniqueFileName);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("File upload failed", e);
		}

		return filePaths.toArray(new String[0]);
	}

//	private static BufferedImage cropImageFromCenter(BufferedImage image, double targetWidth, double targetHeight)
//			throws IOException {
//		return Thumbnails.of(image).size((int) targetWidth, (int) targetHeight).crop(Positions.CENTER)
//				.asBufferedImage();
//	}


	public static BufferedImage cropImageFromCenter(BufferedImage image, double targetWidth) throws IOException {
	    int originalWidth = image.getWidth();
	    int originalHeight = image.getHeight();

	    // 計算左右裁切的起始 x 位置，確保垂直方向不裁切
	    int x = (originalWidth - (int) targetWidth) / 2;
	    int y = 0; // 保持上下不變

	    // 確保裁切範圍在合法區間內
	    if (x < 0 || targetWidth > originalWidth) {
	        throw new IllegalArgumentException("Target width exceeds the original image width.");
	    }

	    // 執行裁剪
	    return image.getSubimage(x, y, (int) targetWidth, originalHeight);
	}

	private static String storeUploadFile(MultipartFile file, boolean isForCKEditor) {
		if (file.isEmpty()) {
			throw new IllegalArgumentException("File is empty");
		}

		String originalFileName = file.getOriginalFilename();
		String uniqueFileName = UUID.randomUUID().toString() + "_"
				+ (originalFileName != null ? originalFileName : "image");
		String fileExtension = getFileExtension(originalFileName);

		if (fileExtension == null) {
			throw new IllegalArgumentException("Invalid file extension");
		}

		String filePath = staticPicturePath + uniqueFileName;
		File dest = new File(filePath);

		try {
			Files.createDirectories(Paths.get(staticPicturePath));

			BufferedImage originalImage = ImageIO.read(file.getInputStream());
			if (originalImage == null) {
				throw new IOException("Failed to read image file");
			}

			if (isForCKEditor) {
				Thumbnails.of(originalImage).scale(1).toFile(dest);
			} else {
				BufferedImage processedImage = storeProcessImageWithAspectRatio(originalImage);
				ImageIO.write(processedImage, fileExtension, dest);
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("File upload failed", e);
		}

		return staticPicturePathMapping + uniqueFileName;
	}

	private static BufferedImage storeProcessImageWithAspectRatio(BufferedImage originalImage) {
		int originalWidth = originalImage.getWidth();
		int originalHeight = originalImage.getHeight();

		// 定义目标尺寸
		final int TARGET_WIDTH = 2048;
		final int TARGET_HEIGHT = 1810;

		// 计算缩放比例
		double scale = Math.min((double) TARGET_WIDTH / originalWidth, (double) TARGET_HEIGHT / originalHeight);

		int scaledWidth = (int) (originalWidth * scale);
		int scaledHeight = (int) (originalHeight * scale);

		BufferedImage finalImage = new BufferedImage(TARGET_WIDTH, TARGET_HEIGHT, BufferedImage.TYPE_INT_RGB);

		Graphics2D g2d = finalImage.createGraphics();
		g2d.setColor(Color.WHITE);
		g2d.fillRect(0, 0, TARGET_WIDTH, TARGET_HEIGHT);

		try {
			BufferedImage scaledImage = Thumbnails.of(originalImage).size(scaledWidth, scaledHeight)
					.outputQuality(OUTPUT_QUALITY).asBufferedImage();

			// 居中放置图片
			int x = (TARGET_WIDTH - scaledWidth) / 2;
			int y = (TARGET_HEIGHT - scaledHeight) / 2;

			g2d.drawImage(scaledImage, x, y, null);
		} catch (IOException e) {
			throw new RuntimeException("Failed to process image", e);
		} finally {
			g2d.dispose();
		}

		return finalImage;
	}

	private static String uploadRectangleFile(MultipartFile file) {
		if (file.isEmpty()) {
			throw new IllegalArgumentException("File is empty");
		}

		String originalFileName = file.getOriginalFilename();
		String uniqueFileName = UUID.randomUUID().toString() + "_"
				+ (originalFileName != null ? originalFileName : "rectangle_image");
		String fileExtension = getFileExtension(originalFileName);

		if (fileExtension == null) {
			throw new IllegalArgumentException("Invalid file extension");
		}

		String filePath = staticPicturePath + uniqueFileName;
		File dest = new File(filePath);

		try {
			Files.createDirectories(Paths.get(staticPicturePath));

			BufferedImage originalImage = ImageIO.read(file.getInputStream());
			if (originalImage == null) {
				throw new IOException("Failed to read image file");
			}

			BufferedImage processedImage = processRectangleImageWithAspectRatio(originalImage);
			ImageIO.write(processedImage, fileExtension, dest);

		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Rectangle file upload failed", e);
		}

		return staticPicturePathMapping + uniqueFileName;
	}

	private static BufferedImage processImageWithAspectRatio(BufferedImage originalImage) {
		int originalWidth = originalImage.getWidth();
		int originalHeight = originalImage.getHeight();

		double scale = Math.min((double) TARGET_SIZE / originalWidth, (double) TARGET_SIZE / originalHeight);

		int scaledWidth = (int) (originalWidth * scale);
		int scaledHeight = (int) (originalHeight * scale);

		BufferedImage finalImage = new BufferedImage(TARGET_SIZE, TARGET_SIZE, BufferedImage.TYPE_INT_RGB);

		Graphics2D g2d = finalImage.createGraphics();
		g2d.setColor(Color.WHITE);
		g2d.fillRect(0, 0, TARGET_SIZE, TARGET_SIZE);

		try {
			BufferedImage scaledImage = Thumbnails.of(originalImage).size(scaledWidth, scaledHeight)
					.outputQuality(OUTPUT_QUALITY).asBufferedImage();

			int x = (TARGET_SIZE - scaledWidth) / 2;
			int y = (TARGET_SIZE - scaledHeight) / 2;

			g2d.drawImage(scaledImage, x, y, null);
		} catch (IOException e) {
			throw new RuntimeException("Failed to process image", e);
		} finally {
			g2d.dispose();
		}

		return finalImage;
	}

	private static BufferedImage processRectangleImageWithAspectRatio(BufferedImage originalImage) {
		int originalWidth = originalImage.getWidth();
		int originalHeight = originalImage.getHeight();

		double scale = Math.min((double) RECT_WIDTH / originalWidth, (double) RECT_HEIGHT / originalHeight);

		int scaledWidth = (int) (originalWidth * scale);
		int scaledHeight = (int) (originalHeight * scale);

		BufferedImage finalImage = new BufferedImage(RECT_WIDTH, RECT_HEIGHT, BufferedImage.TYPE_INT_RGB);

		Graphics2D g2d = finalImage.createGraphics();
		g2d.setColor(Color.BLACK);
		g2d.fillRect(0, 0, RECT_WIDTH, RECT_HEIGHT);

		try {
			BufferedImage scaledImage = Thumbnails.of(originalImage).size(scaledWidth, scaledHeight)
					.outputQuality(OUTPUT_QUALITY).asBufferedImage();

			int x = (RECT_WIDTH - scaledWidth) / 2;
			int y = (RECT_HEIGHT - scaledHeight) / 2;

			g2d.drawImage(scaledImage, x, y, null);
		} catch (IOException e) {
			throw new RuntimeException("Failed to process rectangle image", e);
		} finally {
			g2d.dispose();
		}

		return finalImage;
	}

	private static String getFileExtension(String fileName) {
		if (fileName != null && fileName.contains(".")) {
			return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
		}
		return null;
	}
}
