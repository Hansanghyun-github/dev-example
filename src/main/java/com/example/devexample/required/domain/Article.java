package com.example.devexample.required.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "articles")
@Getter
@NoArgsConstructor
public class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "article_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String things;
    private String emotion;
    private int satisfaction;
    private int amount;

    @Enumerated(value = EnumType.STRING)
    private RegisterType register_type;

    @Column(name = "created_date")
    private LocalDateTime createdDate;
    @Column(name = "edited_date")
    private LocalDateTime editedDate;

    @Builder
    public Article(User user, String things, String emotion, int satisfaction, int amount, String register_type) {
        this.user = user;
        this.things = things;
        this.emotion = emotion;
        this.satisfaction = satisfaction;
        this.amount = amount;
        this.register_type = RegisterType.valueOf(register_type);
        this.createdDate = LocalDateTime.now();
        this.editedDate = LocalDateTime.now();
    }
}
