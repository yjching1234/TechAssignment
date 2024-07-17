package com.demo.techassignment.Model.Enum;

import lombok.Getter;

@Getter
public enum Role {
    USER(1),
    STAFF(2),
    ADMIN(3);

    private final int value;

    Role(int value) {
        this.value = value;
    }

    public static Role fromValue(int value) {
        for (Role role : Role.values()) {
            if (role.getValue() == value) {
                return role;
            }
        }
        return null;
    }
}
