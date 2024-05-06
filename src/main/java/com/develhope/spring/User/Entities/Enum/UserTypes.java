package com.develhope.spring.User.Entities.Enum;

public enum UserTypes {
    BUYER,
    SELLER,
    ADMIN,
    NOT_DEFINED


    public static UserTypes convertFromString(String role) {
        return switch (role) {
            case "admin" -> UserTypes.ADMIN;
            case "seller" -> UserTypes.SELLER;
            case "buyer" -> UserTypes.BUYER;
            default -> UserTypes.NOT_DEFINED;
        };
    }
}
