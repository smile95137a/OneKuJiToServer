package com.one.model;

import com.one.frontend.response.ProductDetailRes;
import com.one.frontend.util.StringListConverter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "gacha_message")
public class GachaMessage  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;

    @Column(name = "nick_name")
    private String nickName;

    @Column(name = "product_detail", nullable = false, unique = true)
    @Convert(converter = StringListConverter.class)
    private ProductDetailRes productDetail;
}
