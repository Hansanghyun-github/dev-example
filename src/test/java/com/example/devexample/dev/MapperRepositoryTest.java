package com.example.devexample.dev;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest
//@Transactional
class MapperRepositoryTest {
    @Autowired
    MemberMapper memberMapper;
    @Autowired
    MemberRepository memberRepository;
    @Test
    @DisplayName("mybatis test")
    void mybatis_test() throws Exception {
        // given
        Member han1 = new Member("Han10");
        memberRepository.save(han1);
        memberRepository.save(new Member("Han20"));
        memberRepository.save(new Member("Han30"));
        
        // when
        List<Member> list = memberMapper.findByName(han1.getName());

        // then
        System.out.println(list.get(0));
        assertThat(list.size()).isOne();
        assertThat(list.get(0)).isEqualTo(han1);
    }
}