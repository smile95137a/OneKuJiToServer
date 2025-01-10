package com.one.onekuji.service;

import com.one.onekuji.model.User;
import com.one.onekuji.repository.RoleRepository;
import com.one.onekuji.repository.UserRepository;
import com.one.onekuji.request.SliverUpdate;
import com.one.onekuji.request.UserReq;
import com.one.onekuji.response.UserRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    public List<UserRes> getAllUsers() {
        List<UserRes> all = userRepository.findAll();

        // 使用 `forEach` 来直接修改每个对象的地址
        all.forEach(x -> x.setAddress(x.getCity() + x.getArea() + x.getAddress()));

        // 修改用户名：如果 provider 不是 "LOCAL"，则设置为 email
        all.forEach(x -> {
            if (!"local".equals(x.getProvider())) {
                x.setUsername(x.getEmail());
            }
        });

        return all;
    }



    public UserRes getUserById(Long userId) {
        return userRepository.findById(userId);
    }

    public User createUser(UserReq userReq) {
        User user = new User();
        user.setUsername(userReq.getUsername());
        user.setPassword(userReq.getPassword());
        user.setNickname(userReq.getNickName());
        user.setEmail(userReq.getEmail());
        user.setPhoneNumber(userReq.getPhoneNumber());
        user.setAddress(userReq.getAddress());
        user.setRoleId(userReq.getRoleId());
        user.setBalance(userReq.getBalance());
        user.setCreatedAt(LocalDateTime.now());
        user.setBonus(BigDecimal.valueOf(0.0));
        user.setSliverCoin(BigDecimal.valueOf(0.0));
        userRepository.insert(user);

        return user;
    }

    public User updateUser(Long userId, UserReq userReq) {
        User user = userRepository.findById2(userId);
        if (user != null) {
            user.setUsername(userReq.getUsername());
            user.setPassword(userReq.getPassword());
            user.setNickname(userReq.getNickName());
            user.setEmail(userReq.getEmail());
            user.setPhoneNumber(userReq.getPhoneNumber());
            user.setAddress(userReq.getAddress());
            user.setRoleId(userReq.getRoleId());
            user.setUpdatedAt(LocalDateTime.now());

            userRepository.update(user);
        }

        return user;
    }

    public void deleteUser(Long userId) {
        userRepository.delete(userId);
    }

    public void updateSliver(SliverUpdate sliverUpdate) {
        if (sliverUpdate != null && sliverUpdate.getUserId() != null && !sliverUpdate.getUserId().isEmpty()) {
            // 调用批量更新银币的方法
            // 1. 执行批量更新
            userRepository.updateSliverCoinBatch( sliverUpdate.getUserId(),
                    sliverUpdate.getSliverCoin(),
                    sliverUpdate.getBonus());

            // 2. 记录更新日志
            userRepository.logUpdate(sliverUpdate.getUserId(),
                    sliverUpdate.getSliverCoin(),
                    sliverUpdate.getBonus());
        } else {
            // 如果用户ID列表为空，抛出异常或处理其他逻辑
            throw new IllegalArgumentException("User ID list cannot be null or empty");
        }
    }

}
