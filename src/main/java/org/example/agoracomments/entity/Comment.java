package org.example.agoracomments.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
@Data
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String text;

    @Column(name = "post_id", nullable = false)
    private Long postId;   // Просто ID, связи @ManyToOne нет!

    @Column(name = "comment_author")
    private String commentAuthor; // Чтобы не запрашивать каждый раз имя

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

}
