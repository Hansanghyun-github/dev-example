package com.example.devexample.article.entity;

import com.example.devexample.user.entity.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Entity
@Table(name = "articles")
@Getter
@DynamicUpdate
@NoArgsConstructor
@ToString(exclude = {"user"})
public class Article extends BaseEntity {
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

    @Builder
    public Article(User user, String things, String emotion, int satisfaction, int amount, String register_type) {
        this.user = user;
        this.things = things;
        this.emotion = emotion;
        this.satisfaction = satisfaction;
        this.amount = amount;
        this.register_type = RegisterType.valueOf(register_type);
    }
}
