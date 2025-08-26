package com.one.onekuji.service;

import com.one.onekuji.model.SignIn;
import com.one.onekuji.repository.SignInMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class SignInService {

    private static final int MAX_SIGN_IN_ENTRIES = 8;
    private static final Double MAX_PROBABILITY_SUM = 1.0;
    @Autowired
    private SignInMapper signInMapper;

    public List<SignIn> getAllSignIns() {
        return signInMapper.findAll();
    }

    public SignIn getSignInById(Long id) {
        return signInMapper.findById(id);
    }

    public void createSignIn(SignIn signIn) throws Exception {
        int count = signInMapper.countAll();
        if (count >= MAX_SIGN_IN_ENTRIES) {
            throw new Exception("轉盤最多設定八個請從更新做修改");
        }

        if(signIn.getProbability() >= MAX_PROBABILITY_SUM){
            throw new Exception("不能設定超過1");
        }
        signIn.setNumber(UUID.randomUUID().toString());
        signIn.setCreatedDate(LocalDateTime.now());
        signIn.setUpdateDate(LocalDateTime.now());
        signInMapper.insert(signIn);
    }

    public void updateSignIn(Long id, SignIn signIn) throws Exception {
        if(signIn.getProbability() >= MAX_PROBABILITY_SUM){
            throw new Exception("不能設定超過1");
        }
        signIn.setId(id);
        signIn.setUpdateDate(LocalDateTime.now());
        signInMapper.update(signIn);
    }

    public void deleteSignIn(Long id) {
        signInMapper.delete(id);
    }

    public int countSignIns() {
        return signInMapper.countAll();
    }


}
