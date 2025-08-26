package com.one.frontend.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Schema(description = "訂單模型")
@Table(name = "`sign_in`")
public class SignIn {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name ="id")
    private Long id;
    @Column(name ="sliver_price")
    private BigDecimal sliverPrice;
    @Column(name ="probability")
    private Double probability;
    @Column(name ="number")
    private String number;
    @Column(name ="created_date")
    private LocalDateTime createdDate;
    @Column(name ="update_date")
    private LocalDateTime updateDate;

}
