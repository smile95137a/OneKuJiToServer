package com.one.frontend.service;

import com.one.model.PrizeDetail;
import com.one.repository.PrizeDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PrizeDetailService {

    @Autowired
    private PrizeDetailRepository prizeDetailRepository;
    public String createPrizeDetail(PrizeDetail prizeDetail) {
        try {
            prizeDetailRepository.createPrizeDetail(prizeDetail);
            return "1";
        }catch (Exception e){
            return "0";
        }
    }

    public String updatePrizeDetail(PrizeDetail prizeDetail) {
        try {
            prizeDetailRepository.updatePrizeDetail(prizeDetail);
            return "1";
        }catch (Exception e){
            return "0";
        }
    }

    public PrizeDetail getPrizeDetailById(Integer prizeDetailId) {
        return prizeDetailRepository.getPrizeDetailById(prizeDetailId);
    }

    public String deletePrizeDetail(Integer prizeDetailId) {
        try {
            prizeDetailRepository.deletePrizeDetail(prizeDetailId);
            return "1";
        }catch (Exception e){
            return "0";
        }
    }

    public List<PrizeDetail> getAllPrizeDetails() {
        return prizeDetailRepository.getAllPrizeDetails();
    }
}
