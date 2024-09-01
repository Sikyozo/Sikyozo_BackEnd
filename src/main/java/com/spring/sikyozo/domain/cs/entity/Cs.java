package com.spring.sikyozo.domain.cs.entity;

import com.spring.sikyozo.domain.review.entity.Review;
import com.spring.sikyozo.domain.user.entity.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "p_cs")

public class Cs {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id", nullable = false)
    private Review review;

    @Column(columnDefinition = "text")
    private String answer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "answer_user_id")
    private User user;

    private LocalDateTime answerCreatedAt;
    private LocalDateTime answerUpdatedAt;
    private LocalDateTime answerDeletedAt;
}
