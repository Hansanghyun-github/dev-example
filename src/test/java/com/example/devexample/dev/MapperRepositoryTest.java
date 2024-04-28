package com.example.devexample.dev;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
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
@Transactional
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class MapperRepositoryTest {
    @Autowired
    MemberMapper memberMapper;
    @Autowired
    MemberRepository memberRepository;

    @Test
    void 마이바티스_테스트() throws Exception {
        // given
        Member han1 = new Member("Han10");
        memberRepository.save(han1);
        memberRepository.save(new Member("Han20"));
        memberRepository.save(new Member("Han30"));
        
        // when
        List<Member> list = memberMapper.findByName(han1.getName());

        // then
        assertThat(list)
                .hasSize(1)
                .contains(han1);
    }
}