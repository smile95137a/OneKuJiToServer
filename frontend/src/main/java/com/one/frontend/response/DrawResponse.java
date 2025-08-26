package com.one.frontend.response;

import com.one.frontend.model.PrizeNumber;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DrawResponse {

    private List<PrizeNumber> prizeNumberList;

    private LocalDateTime endTimes;

}
