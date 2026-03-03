package com.example.demo.model;

public enum CategoryType {
    DIEN_THOAI("Điện thoại"),
    LAPTOP("Laptop");

    private final String displayName;

    CategoryType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
