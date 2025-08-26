package com.one.frontend.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Schema(description = "角色模型")
@Table(name = "role")
@Entity
public class Role {

    @Schema(description = "角色唯一識別碼", example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Schema(description = "角色名稱", example = "ADMIN")
    @Column(nullable = false, length = 255)
    private String name;

    @Column
    private String roleName;
}
