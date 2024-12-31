package com.one.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_reward")
public class UserReward {

    // Getters and Setters
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "reward_amount", precision = 10, scale = 2)
    private BigDecimal rewardAmount;

    @Column(name = "reward_date", nullable = false)
    private LocalDate rewardDate;

    @Column(name = "created_at", updatable = false)
    private LocalDate createdAt;


    @Column(name = "threshold_amount", precision = 10, scale = 2)
    private BigDecimal thresholdAmount;  // 新增欄位，用於記錄領取的門檻金額
}
