package com.spring.sikyozo.domain.ai.entity;

import com.spring.sikyozo.domain.menu.entity.Menu;
import com.spring.sikyozo.domain.user.entity.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name="p_ai")
public class Ai {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id", nullable = false)
    private User user;

    @OneToOne
    @JoinColumn(name = "menu_id",nullable = false)
    private Menu menu;

    @Column(columnDefinition = "text",nullable = false)
    private String question;

    @Column(columnDefinition = "text")
    private String answer;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length=10)
    private AiStatus status = AiStatus.SUCCESS;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public void saveAiResponse(User user, String responseText, String requestText,Menu menu) {
        this.user = user;
        this.answer = responseText;
        this.question = requestText;
        this.menu = menu;
    }

}
