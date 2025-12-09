package com.one.onekuji.util;

import com.one.onekuji.util.storage.ImageStorageService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class ImageUtilDelegationTest {

    @MockBean
    private ImageStorageService storageService;

    @Test
    public void testUploadDelegatesToStorageService() throws Exception {
        when(storageService.upload(any())).thenReturn("https://example.com/image.jpg");

        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "fake-image-bytes".getBytes());
        String url = ImageUtil.upload(file);
        assertThat(url).isEqualTo("https://example.com/image.jpg");
    }
}
