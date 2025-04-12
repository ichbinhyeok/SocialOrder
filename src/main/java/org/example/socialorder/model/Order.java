package org.example.socialorder.model;
import java.time.LocalDateTime;

public class Order {
    private Long id;
    private Long postId;
    private Long commentId;
    private String customerName;
    private String customerExternalId;
    private Long productId;
    private Double quantity;
    private String unitType;
    private Boolean pickupAfterSix;
    private String status; // Enum으로 변경 가능
    private String originalComment;
    private String parsedResult;
    private String parsingMethod;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Getters and Setters
}