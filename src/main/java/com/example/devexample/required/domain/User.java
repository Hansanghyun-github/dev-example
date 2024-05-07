package com.example.devexample.required.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor
@ToString
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    private String email;
    private String nickname;
    private int age;

    @Column(columnDefinition = "CHAR(4)")
    private String mbti;

    @Enumerated(value = EnumType.STRING)
    private Gender gender;
    private int budget;

    @Column(name = "created_date")
    private LocalDateTime createdDate;
    @Column(name = "edited_date")
    private LocalDateTime editedDate;

    @Builder
    public User(String email, String nickname, int age, String mbti, String gender, int budget) {
        this.email = email;
        this.nickname = nickname;
        this.age = age;
        this.mbti = mbti;
        this.gender = Gender.valueOf(gender);
        this.budget = budget;
        this.createdDate = LocalDateTime.now();
        this.editedDate = LocalDateTime.now();
    }
}
