package org.example.socialorder.model;

import java.time.LocalDateTime;

public class Comment {
    private Long id;
    private Long postId;
    private String externalCommentId;
    private String authorName;
    private String authorId;
    private String content;
    private LocalDateTime commentDate;
    private Boolean isProcessed;
    private String responseCommentId;

    // Getters and Setters
}