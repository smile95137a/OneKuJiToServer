package com.one.onekuji.service;

import com.one.onekuji.model.Role;
import com.one.onekuji.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    // 獲取所有角色
    public List<Role> getAllRole() {
        return roleRepository.findAll();
    }

    // 根據ID獲取角色
    public Role getRoleById(Long id) {
        return roleRepository.findById(id);
    }

    // 創建新角色
    public void createRole(Role role) {
        roleRepository.insert(role);
    }

    // 更新角色
    public void updateRole(Long id, Role role) {
        roleRepository.update(id, role);
    }

    // 刪除角色
    public void deleteRole(Long id) {
        roleRepository.delete(id);
    }
}
