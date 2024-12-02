package com.one.frontend.repository;

import com.one.frontend.model.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository("passRepo")
public interface PasswordResetRepo extends JpaRepository<PasswordResetToken, Integer> {
	Optional<PasswordResetToken> findByToken(String token);

	List<PasswordResetToken> findBSyUserIdOrderByCreateTimeDesc(Long userId);
}