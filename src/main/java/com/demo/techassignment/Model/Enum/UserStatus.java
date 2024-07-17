package com.demo.techassignment.Model.Enum;

import lombok.Getter;

@Getter
public enum UserStatus {
    ACTIVE(1),
    INACTIVE(2);

    private final int value;

    UserStatus(int value) {
        this.value = value;
    }

    public static UserStatus fromValue(int value) {
        for (UserStatus status : UserStatus.values()) {
            if (status.getValue() == value) {
                return status;
            }
        }
        return null;
    }
}
