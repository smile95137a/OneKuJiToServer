package com.one.config.security.oauth2;

import com.one.frontend.config.security.CustomUserDetails;
import com.one.frontend.config.security.SecurityUtils;
import com.one.frontend.model.Cart;
import com.one.frontend.model.PrizeCart;
import com.one.frontend.model.User;
import com.one.frontend.repository.CartRepository;
import com.one.frontend.repository.PrizeCartRepository;
import com.one.frontend.repository.UserRepository;
import com.one.frontend.util.RandomUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
	private final UserRepository userRepository;
	@Autowired
	private PrizeCartRepository prizeCartRepository;

	@Autowired
	private CartRepository cartRepository;
	@Override
	public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) {
		log.info("CustomOAuth2UserService loadUser");
		OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);
		CustomUserDetails customUserDetails = processOAuth2User(oAuth2UserRequest, oAuth2User);
		return customUserDetails;
	}

	private CustomUserDetails processOAuth2User(OAuth2UserRequest oAuth2UserRequest, OAuth2User oAuth2User) {
		String clientRegistrationId = oAuth2UserRequest.getClientRegistration().getRegistrationId();
		CustomAbstractOAuth2UserInfo customAbstractOAuth2UserInfo = OAuth2Utils.getOAuth2UserInfo(clientRegistrationId,
				oAuth2User.getAttributes());

		String userEmail = customAbstractOAuth2UserInfo.getEmail();
		var oUser = userRepository.getUserByEmail(userEmail);
		if (oUser.isEmpty()) {
			oUser = Optional.of(registerNewOAuthUser(oAuth2UserRequest, customAbstractOAuth2UserInfo));
		}

		User user = oUser.get();

		OAuth2Provider registeredProviderId = OAuth2Provider.fromString(clientRegistrationId);
		if (!user.getProvider().equals(registeredProviderId.name())) {
			String incorrectProviderChoice = String.format("抱歉，此電子郵件已與 %s 帳戶綁定。請使用 %s 帳戶登錄，而不是 %s。", user.getProvider(),
					user.getProvider(), registeredProviderId);
			throw new InternalAuthenticationServiceException(incorrectProviderChoice);
		}

		List<GrantedAuthority> authorities = oAuth2User.getAuthorities().stream().collect(Collectors.toList());
		authorities.add(new SimpleGrantedAuthority(SecurityUtils.ROLE_DEFAULT));

		return CustomUserDetails.builder().id(Long.valueOf(user.getId())).username(user.getUsername())
				.password(user.getPassword()).name(user.getNickname()).email(user.getEmail()).authorities(authorities)
				.attributes(oAuth2User.getAttributes()).build();
	}

	private User registerNewOAuthUser(OAuth2UserRequest oAuth2UserRequest,
			CustomAbstractOAuth2UserInfo customAbstractOAuth2UserInfo) {
		String clientRegistrationId = oAuth2UserRequest.getClientRegistration().getRegistrationId();
		OAuth2Provider provider = OAuth2Provider.fromString(clientRegistrationId);
		User userEntity = new User();
		userEntity.setUsername(RandomUtils.genRandom(32));
		userEntity.setNickname(customAbstractOAuth2UserInfo.getName());
		userEntity.setEmail(customAbstractOAuth2UserInfo.getEmail());
		userEntity.setProvider(provider.name());
		userEntity.setBalance(BigDecimal.ZERO);
		userEntity.setBonus(BigDecimal.ZERO);
		userEntity.setCreatedAt(LocalDateTime.now());
		userEntity.setRoleId(3L);
		userEntity.setSliverCoin(BigDecimal.ZERO);
		userEntity.setStatus("ACTIVE");
		userEntity.setUserUid(UUID.randomUUID().toString());
		userRepository.createUser(userEntity);
		// 4. 创建用户相关资源：购物车和奖品购物车
		User userCart = userRepository.getUserByUserName(userEntity.getUsername());
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

		userEntity = userRepository.getUserByEmail(customAbstractOAuth2UserInfo.getEmail()).get();
		return userEntity;
	}

}
