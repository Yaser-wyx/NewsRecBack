package com.yaser.news.constant;

import lombok.Getter;

public enum Roles {

    USER("user"),
    ADMIN("admin");

    @Getter
    private final String roleName;

    Roles(String roleName) {
        this.roleName = roleName;

    }
}
