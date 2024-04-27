package com.example.devexample.dev;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
public class MemberController {
    private final MemberRepository memberRepository;
    private final TestObject to;

    private final MapperRepository memberMapper;

    @PutMapping("/members")
    public void saveMember(@RequestBody String name){
        memberRepository.save(new Member(name));
    }

    @DeleteMapping("/members/{id}")
    public int deleteMember(@PathVariable Long id){
        if(!memberRepository.existsById(id))
            return -1;
        memberRepository.deleteById(id);
        return 0;
    }

    @GetMapping("/members/{id}")
    public Member getMember(@PathVariable Long id){
        return memberRepository.findById(id)
                .orElse(new Member("null member"));
    }

    @GetMapping("/members")
    public List<Member> getMembers(){
        log.info("GET /members");
        return memberRepository.findAll();
    }

    @GetMapping("/members/name/{name}")
    public List<Member> findByName(@PathVariable String name){
        return memberMapper.findByName(name);
    }

    @GetMapping("/hello")
    public String getHello(){
        return to.getData();
    }

    @GetMapping("/health")
    public void healthCheck(){
        log.debug("health check");
    }
}
