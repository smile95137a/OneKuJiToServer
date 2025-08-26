package com.one.onekuji.repository;

import com.one.onekuji.model.Role;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface RoleRepository {

    // 查詢所有角色
    @Select("SELECT * FROM role")
    List<Role> findAll();

    // 根據ID查詢角色
    @Select("SELECT * FROM role WHERE id = #{id}")
    Role findById(Long id);

    // 插入新角色
    @Insert("INSERT INTO role(name) VALUES(#{name})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Role role);

    // 更新角色
    @Update("UPDATE role SET name = #{role.name} WHERE id = #{id}")
    void update(@Param("id") Long id, @Param("role") Role role);

    // 刪除角色
    @Delete("DELETE FROM role WHERE id = #{id}")
    void delete(Long id);

    @Select("SELECT * FROM role WHERE name = #{name}")
    Role findByName(String name);
}
