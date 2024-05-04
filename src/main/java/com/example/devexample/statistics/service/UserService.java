package com.example.devexample.statistics.service;

import org.springframework.stereotype.Service;

@Service
public class UserService {
    public String getUserMBTI() {
        // TODO 현재 세션 이용해 인증 한 유저의 mbti 반환해야 함
        return "ISTJ";
    }
}
