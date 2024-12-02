package com.one.frontend.repository;

import com.one.frontend.model.DrawResult;
import com.one.frontend.model.Role;
import com.one.frontend.model.User;
import com.one.frontend.response.UserRes;
import org.apache.ibatis.annotations.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Mapper
public interface UserRepository {

	@Select("select * from user where id = #{userId}")
	UserRes getUserById(@Param("userId") Long userId);

	@Select("select * from user where user_uid = #{userUid}")
	UserRes getUserByUserUid(@Param("userUid") String userUid);

	@Select("select * from user where id = #{userId}")
	User getUserBId(@Param("userId") Long userId);

	@Select("select * from user where id = #{userId}")
	User getById(@Param("userId") Long userId);

	@Select("SELECT * FROM user")
	List<User> getAllUser();

	@Insert("INSERT INTO `user` (username, password, email, nickname, phone_number, address, city, area, address_name, line_id, created_at, balance, bonus, sliver_coin, provider, role_id, user_uid, status, draw_count, invoice_info, invoice_info_email,zip_code,vehicle) "
			+ "VALUES (#{username}, #{password}, #{email}, #{nickname}, #{phoneNumber}, #{address}, #{city}, #{area}, #{addressName}, #{lineId}, #{createdAt}, #{balance}, #{bonus}, #{sliverCoin}, #{provider}, #{roleId}, #{userUid}, #{status}, #{drawCount}, #{invoiceInfo}, #{invoiceInfoEmail},#{zipCode} , #{vehicle})")
	void createUser(User user);

	@Update("UPDATE `user` SET password = #{password}, nickname = #{nickname}, "
			+ "email = #{email}, phone_number = #{phoneNumber}, address = #{address}, city = #{city}, area = #{area}, address_name = #{addressName}, line_id = #{lineId}, updated_at = #{updatedAt}, status = #{status}, balance = #{balance}, bonus = #{bonus}, sliver_coin = #{sliverCoin}, draw_count = #{drawCount}, "
			+ "invoice_info = #{invoiceInfo}, invoice_info_email = #{invoiceInfoEmail} , vehicle = #{vehicle} " + "WHERE id = #{id}")
	void update(User user);

	@Delete("DELETE FROM user WHERE id = #{userId}")
	void deleteUser(@Param("userId") Long userId);

	@Select("SELECT id,username, password , role_id FROM user WHERE username = #{username}")
	Optional<User> findByUsername(@Param("username") String username);

	@Select("SELECT r.* FROM roles r " + "JOIN users_roles ur ON r.id = ur.role_id " + "WHERE ur.user_id = #{userId}")
	@Results({ @Result(property = "id", column = "id"), @Result(property = "name", column = "name") })
	Set<Role> selectRolesByUserId(Long userId);

	@Select("select * from user where role_id = #{roleId}")
	int countByRoleId(int roleId);

	@Update("UPDATE user SET balance = #{amount} WHERE id = #{userId}")
	void deductUserBalance(@Param("userId") Long userId, @Param("amount") BigDecimal amount);

	@Update("UPDATE user SET sliver_coin = #{amount} WHERE id = #{userId}")
	void deductUserSliver(@Param("userId") Long userId, @Param("amount") BigDecimal amount);

	void insertBatch(List<DrawResult> drawResults);

	@Select("select balance from user where id = #{userId}")
	Integer getBalance(Long userId);

	@Select("select * from user where username = #{username}")
	User getUserByUserName(String username);

	@Select("select bonus from `user` where id = #{userId}")
	Integer getBonusPoints(Long userId);

	@Select("select sliver_coin from `user` where id = #{userId}")
	BigDecimal getSliver(Long userId);

	@Update("UPDATE user SET bonus = #{amount} WHERE id = #{userId}")
	void deductUserBonusPoints(@Param("userId") Long userId, @Param("amount") BigDecimal amount);

	@Update("update `user` set balance = balance + tradeAmt where id = #{userId}")
	void updateAccoutnt(String tradeAmt, String userId);

	@Select("select * from `user` where google_id = #{googleId}")
	User findByGoogleId(String googleId);

	@Insert("INSERT INTO `user` (username, password, email, phone_number, address, createdAt, balance, bonus, google_id) "
			+ "VALUES (#{username}, #{password}, #{email}, #{phoneNumber}, #{address}, #{createdAt}, #{balance}, #{bonus}, #{googleId})")
	void createGoogleUser(User user);

	@Update("update `user` set bonus = bonus + #{bonus} where id = #{userId}")
	void updateBonus(@Param("userId") Long userId, @Param("bonus") BigDecimal bonus);

	@Update("update `user` set  draw_count = draw_count + 1 where id = #{userId} ")
	void addDrawCount(Long userId);

	@Select("select * from user where email  = #{email}")
	Optional<User> getUserByEmail(String email);

	@Update("update `user` set sliver_coin = sliver_coin + #{sliverCoin} where id = #{userId}")
	void updateSliverCoin(@Param("userId") Long userId, @Param("sliverCoin") BigDecimal sliverCoin);
	@Update("update `user` set role_id = #{roleId} where id = #{id}")
	void updateUserRoleId(User user);
	@Update("update `user` set balance = balance + #{balance} where id = #{userId}")
	void updateBalance(@Param("userId") Long userId, @Param("balance") int balance);

	@Update("update `user` set password = #{user.password} , updated_at = #{user.updatedAt} where id = #{user.id}")
	void resetUser(@Param("user") User user);
}