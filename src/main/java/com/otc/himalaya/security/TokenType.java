package com.otc.himalaya.security;

public enum TokenType {
    LOGIN("L"),
    REFRESH("R");

    private String type;

    TokenType(String type) {this.type = type;}

    public String getType() {
        return type;
    }

    public static boolean isLoginToken(String type) {
        return LOGIN.type.equals(type);
    }

    public static boolean isFreshToken(String type) {
        return REFRESH.type.equals(type);
    }
}
