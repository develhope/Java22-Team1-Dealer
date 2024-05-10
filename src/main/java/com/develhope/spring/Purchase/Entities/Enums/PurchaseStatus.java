package com.develhope.spring.Purchase.Entities.Enums;

import com.develhope.spring.User.Entities.Enum.UserTypes;

public enum PurchaseStatus {
    ACQUIRED,
    DEPOSIT,
    COMPLETED,
    NOT_DEFINED;

    public static PurchaseStatus convertFromString(String status) {
        return switch (status.toLowerCase()) {
            case "acquired" -> PurchaseStatus.ACQUIRED;
            case "deposit" -> PurchaseStatus.DEPOSIT;
            case "completed" -> PurchaseStatus.COMPLETED;
            default -> PurchaseStatus.NOT_DEFINED;
        };
    }
}
