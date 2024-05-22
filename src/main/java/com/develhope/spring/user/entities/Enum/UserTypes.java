package com.develhope.spring.User.entities.Enum;

public enum UserTypes {
    BUYER,
    SELLER,
    ADMIN,
    NOT_DEFINED;


    public static UserTypes convertFromString(String role) {
        return switch (role.toLowerCase()) {
            case "admin" -> UserTypes.ADMIN;
            case "seller" -> UserTypes.SELLER;
            case "buyer" -> UserTypes.BUYER;
            default -> UserTypes.NOT_DEFINED;
        };
    }
}
