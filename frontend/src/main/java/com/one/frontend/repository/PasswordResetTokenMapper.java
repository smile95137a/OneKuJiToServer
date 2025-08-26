package com.one.frontend.repository;

import com.one.frontend.model.PasswordResetToken;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
@Component
@RequiredArgsConstructor
public class PasswordResetTokenMapper {

	public PasswordResetToken toPasswordResetToken(Long userId, String token, int expiryMinutes) {
		var now = LocalDateTime.now();
		var expiresAt = now.plus(expiryMinutes, ChronoUnit.MINUTES);

		return PasswordResetToken.builder()
				.userId(userId)
				.token(token)
				.isActive(false)
				.passwordChanged(false)
				.createTime(now)
				.expireTime(expiresAt)
				.build();
	}
}
