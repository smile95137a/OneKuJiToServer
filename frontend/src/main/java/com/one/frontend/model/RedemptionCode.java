package com.one.frontend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "redemption_codes")
public class RedemptionCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 10, unique = true, nullable = false)
    private String code;

    @Column(name = "is_redeemed", nullable = false)
    private Boolean isRedeemed;

    @Column(name = "redeemed_at")
    private LocalDateTime redeemedAt;

    @Column(name = "user_id")
    private Long userId;

    private Long productId;


}
