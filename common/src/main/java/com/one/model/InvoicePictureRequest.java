package com.one.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvoicePictureRequest {
    private String timeStamp;
    private String uncode;
    private String idno;
    private String sign;
    private String code;
    private String type;
}
