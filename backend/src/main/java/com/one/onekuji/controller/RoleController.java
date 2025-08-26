package com.one.onekuji.controller;

import com.one.onekuji.model.ApiResponse;
import com.one.onekuji.model.Role;
import com.one.onekuji.service.RoleService;
import com.one.onekuji.util.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/role")
public class RoleController {

    @Autowired
    private RoleService roleService;

    // 查詢所有角色
    @GetMapping("/query")
    public ResponseEntity<ApiResponse<List<Role>>> getAllRole() {
        List<Role> roleList = roleService.getAllRole();
        if (roleList == null || roleList.isEmpty()) {
            ApiResponse<List<Role>> response = ResponseUtils.failure(404, "無角色", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        ApiResponse<List<Role>> response = ResponseUtils.success(200, null, roleList);
        return ResponseEntity.ok(response);
    }

    // 根據ID查詢角色
    @GetMapping("/{roleId}")
    public ResponseEntity<ApiResponse<Role>> getRoleById(@PathVariable("roleId") Long roleId) {
        Role role = roleService.getRoleById(roleId);
        if (role == null) {
            ApiResponse<Role> response = ResponseUtils.failure(404, "角色未找到", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        ApiResponse<Role> response = ResponseUtils.success(200, null, role);
        return ResponseEntity.ok(response);
    }

    // 創建新角色
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<Void>> createRole(@RequestBody Role role) {
        roleService.createRole(role);
        ApiResponse<Void> response = ResponseUtils.success(201, "角色創建成功", null);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 更新角色
    @PutMapping("/update/{roleId}")
    public ResponseEntity<ApiResponse<Void>> updateRole(@PathVariable("roleId") Long roleId, @RequestBody Role role) {
        roleService.updateRole(roleId, role);
        ApiResponse<Void> response = ResponseUtils.success(200, "角色更新成功", null);
        return ResponseEntity.ok(response);
    }

    // 刪除角色
    @DeleteMapping("/delete/{roleId}")
    public ResponseEntity<ApiResponse<Void>> deleteRole(@PathVariable("roleId") Long roleId) {
        roleService.deleteRole(roleId);
        ApiResponse<Void> response = ResponseUtils.success(200, "角色刪除成功", null);
        return ResponseEntity.ok(response);
    }
}
