package com.one.frontend.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.one.frontend.model.Marquee;
import com.one.frontend.model.MarqueeDetail;
import com.one.frontend.repository.MarqueeMapper;
import com.one.frontend.response.MarqueeWithDetailsRes;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MarqueeService {

    private final MarqueeMapper marqueeMapper;

    /**
     * 插入跑馬燈並返回生成的主鍵 ID
     * 
     * @param userId 用戶 ID
     * @return 插入的跑馬燈 ID
     */
    public Long createMarquee(Long userId) {
        Marquee marquee = new Marquee();
        marquee.setUserId(userId);
        marquee.setCreateDate(LocalDateTime.now());
        return marqueeMapper.addMarqueeAndReturnId(marquee);
    }

    /**
     * 插入跑馬燈明細
     * 
     * @param marqueeId 跑馬燈 ID
     * @param grade     明細等級
     * @param name      明細名稱
     */
    public void addMarqueeDetail(Long marqueeId, String grade, String name) {
        MarqueeDetail detail = new MarqueeDetail();
        detail.setMarqueeId(marqueeId);
        detail.setGrade(grade);
        detail.setName(name);
        marqueeMapper.addMarqueeDetail(detail);
    }

    /**
     * 查詢所有跑馬燈及其明細和用戶信息
     * 
     * @return 跑馬燈及其明細的列表
     */
    public List<MarqueeWithDetailsRes> getAllMarqueeWithDetailsAndUser() {
        return marqueeMapper.findAllWithDetailsAndUser();
    }
}
