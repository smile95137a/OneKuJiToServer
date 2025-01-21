package com.one.frontend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "cvs_store_info")
@NoArgsConstructor
@AllArgsConstructor
public class CvsStoreInfo {
    @Id
    private String uuid;

    // 存储 CSV 开头的字段
    @Column(name = "cvs_store_id", length = 9, nullable = false)
    private String cvsStoreID;

    @Column(name = "cvs_store_name", length = 10, nullable = false)
    private String cvsStoreName;

    @Column(name = "cvs_address", length = 60, nullable = false)
    private String cvsAddress;

    @Column(name = "cvs_telephone", length = 20, nullable = false)
    private String cvsTelephone;

    @Column(name = "cvs_outside", length = 1, nullable = false)
    private String cvsOutside;
}
