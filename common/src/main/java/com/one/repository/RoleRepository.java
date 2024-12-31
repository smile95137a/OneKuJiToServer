package com.one.repository;

import com.one.frontend.model.Role;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Optional;

@Mapper
public interface RoleRepository {

    @Select("select * from role")
    List<Role> getAllRole();

    @Select("select * from role where id = #{roleId}")
    Role getRoleById(Integer roleId);

    @Select("select * from role where id = #{i}")
    Optional<Role> findById(int i);

    @Select("SELECT * FROM role WHERE name = #{name}")
    Role findByName(String name);
}
