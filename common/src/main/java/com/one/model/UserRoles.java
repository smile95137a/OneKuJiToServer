package com.one.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Data
@Schema(description = "用戶角色關聯模型")
@Table(name = "user_roles")
@Entity
public class UserRoles {

    @Schema(description = "用戶 ID", example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Schema(description = "角色 ID", example = "1")
    @Column(name = "role_id")
    private Long roleId;
}
