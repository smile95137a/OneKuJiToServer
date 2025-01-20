package com.one.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "marquee")
public class Marquee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id")
    private long userId; 

    @Column(name = "create_date")
    private LocalDateTime createDate;
    
    
}