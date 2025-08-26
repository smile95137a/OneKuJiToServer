package com.one.service;

import com.one.model.*;
import com.one.repository.*;
import com.one.request.UserReq;
import com.one.response.UserRes;
import com.one.util.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private CartRepository cartRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private PrizeCartRepository prizeCartRepository;

	@Autowired
	private VerificationTokenRepository tokenRepository;

	@Autowired
	private MailService mailService;

	@Value("${verification.url}")
	private String verificationUrl;

	private final String MEMBER = "未驗證會員";

	public UserRes getUserById(Long userId) {
		return userRepository.getUserById(userId);
	}

	public UserRes registerUser(UserReq userDto) throws Exception {
		try {
			// 1. 检查用户是否存在
			Optional<User> check = userRepository.getUserByEmail(userDto.getUsername());
			if (check.isPresent()) {
				throw new Exception("帳號已存在");
			}

			// 2. 获取角色和加密密码
			Role memberRole = roleRepository.findByName(this.MEMBER);
			String encryptedPassword = passwordEncoder.encode(userDto.getPassword());

			// 3. 创建用户并保存
			User user = User.builder()
					.userUid(RandomUtils.genRandom(32))
					.username(userDto.getUsername())
					.password(encryptedPassword)
					.nickname(userDto.getNickname())
					.email(userDto.getUsername())
					.phoneNumber(userDto.getPhoneNumber())
					.city(userDto.getCity())
					.area(userDto.getArea())
					.address(userDto.getAddress())
					.addressName(userDto.getAddressName())
					.lineId(userDto.getLineId())
					.createdAt(LocalDateTime.now())
					.roleId(memberRole.getId())
					.balance(BigDecimal.ZERO)
					.bonus(BigDecimal.ZERO)
					.sliverCoin(BigDecimal.ZERO)
					.status("ACTIVE")
					.drawCount(0L)
					.provider("local")
					.invoiceInfoEmail(userDto.getEmail())
					.vehicle(userDto.getVehicle())
					.build();
			userRepository.createUser(user);

			// 4. 创建用户相关资源：购物车和奖品购物车
			User userCart = userRepository.getUserByUserName(userDto.getUsername());
			Cart cart = new Cart();
			cart.setUserId(userCart.getId());
			cart.setUserUid(userCart.getUserUid());
			cart.setCreatedAt(LocalDateTime.now());
			cart.setUpdatedAt(LocalDateTime.now());
			cartRepository.addCart(cart);

			PrizeCart prizeCart = new PrizeCart();
			prizeCart.setUserId(userCart.getId());
			prizeCart.setUserUid(userCart.getUserUid());
			prizeCart.setCreatedAt(LocalDateTime.now());
			prizeCart.setUpdatedAt(LocalDateTime.now());
			prizeCartRepository.addPrizeCart(prizeCart);

			// 5. 生成验证链接并发送邮件
			generateVerificationTokenAndSendEmail(userCart);

			// 6. 返回用户信息
			UserRes userRes = new UserRes();
			userRes.setUsername(user.getUsername());
			userRes.setEmail(user.getEmail());
			userRes.setPhoneNumber(user.getPhoneNumber());
			userRes.setAddress(user.getAddress());

			return userRes;

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}


	@Transactional
	public boolean updateUser(UserReq req, Long userId) throws Exception {
		try {
			User user = userRepository.getById(userId);
			user.setVehicle(req.getVehicle());
			user.setNickname(req.getNickname());
			user.setAddressName(req.getAddressName());
			user.setCity(req.getCity());
			user.setArea(req.getArea());
			user.setAddress(req.getAddress());
			user.setLineId(req.getLineId());
			user.setPhoneNumber(req.getPhoneNumber());
			user.setVehicle(req.getVehicle());
			user.setInvoiceInfo(req.getInvoiceInfo());
			user.setInvoiceInfoEmail(req.getEmail());
			user.setUpdatedAt(LocalDateTime.now());
			System.out.println(user);
			userRepository.update(user);
			return true;
		} catch (Exception e) {
			throw new Exception("Failed to update user with ID: " + userId, e);
		}
	}

	@Transactional
	public boolean updateUserInvoice(UserReq req, Long userId) throws Exception {
		try {
			User user = userRepository.getById(userId);
			user.setVehicle(req.getVehicle());
			user.setInvoiceInfo(req.getInvoiceInfo());
			user.setInvoiceInfoEmail(req.getInvoiceInfoEmail());
			user.setUpdatedAt(LocalDateTime.now());

			userRepository.update(user);
			return true;
		} catch (Exception e) {
			throw new Exception("Failed to update user with ID: " + userId, e);
		}
	}

	public void generateVerificationTokenAndSendEmail(User user) {
		// 1. 生成唯一 token
		String token = UUID.randomUUID().toString();
		VerificationToken verificationToken = new VerificationToken();
		verificationToken.setToken(token);
		verificationToken.setUserId(user.getId());
		verificationToken.setExpiryDate(LocalDateTime.now().plusHours(24)); // 设置 24 小时过期
		tokenRepository.save(verificationToken);

		// 2. 生成验证链接
		String verificationUrls = verificationUrl + token;

		// 3. 发送邮件
		mailService.sendVerificationMail(user.getEmail(), verificationUrls);
	}
}