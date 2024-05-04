package com.example.devexample.dev;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
//@Transactional
@RequiredArgsConstructor
public class MapperRepository {
    private final MemberMapper memberMapper;
    public List<Member> findByName(String name){
        return memberMapper.findByName(name);
    }
}
