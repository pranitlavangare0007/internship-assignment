package com.pranit.assignment.models;

import com.pranit.assignment.entity.AuthorType;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    private Long authorId;

    @Enumerated(EnumType.STRING)
    private AuthorType authorType;

    private String content;

    private int depthLevel;

    private LocalDateTime createdAt;
}