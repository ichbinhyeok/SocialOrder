package org.example.socialorder.model;

import java.util.List;


public class Customer {
    private Long id;
    private String name;
    private String externalId;
    private String platformType;
    private List<Order> orderHistory;
    private String preferredPickupTime;
    private String notes;

    // Getters and Setters
}