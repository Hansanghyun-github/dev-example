package com.example.devexample.dev;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
public interface MemberMapper {
    List<Member> findByName(String name);
}
