package com.demo.techassignment.Model.Enum;

import lombok.Getter;

@Getter
public enum AccStatus {
    ACTIVE(1),
    INACTIVE(2),
    CLOSE(3);

    private final int value;
    AccStatus(int value) {
        this.value = value;
    }

    public static AccStatus fromValue(int value) {
        for (AccStatus status : AccStatus.values()) {
            if (status.getValue() == value) {
                return status;
            }
        }
        return null;
    }
}
