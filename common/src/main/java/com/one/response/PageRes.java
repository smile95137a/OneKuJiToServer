package com.one.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageRes<T> {

    private List<T> list;
    private long total;
    private int page;
    private int size;
    private int totalPages;

    public static <T> PageRes<T> of(List<T> list, long total, int page, int size) {
        int totalPages = (int) Math.ceil((double) total / size);
        return new PageRes<>(list, total, page, size, totalPages);
    }
}
