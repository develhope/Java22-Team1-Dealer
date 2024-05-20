package com.develhope.spring.purchase.entities.enums;

public enum PurchaseStatus {
    ACQUIRED,
    DEPOSIT,
    COMPLETED;

    public static PurchaseStatus convertFromString(String status) {
        return switch (status.toLowerCase()) {
            case "deposit" -> PurchaseStatus.DEPOSIT;
            case "completed" -> PurchaseStatus.COMPLETED;
            default -> PurchaseStatus.ACQUIRED;
        };
    }
}
