package com.demo.techassignment.Model.Enum;

import lombok.Getter;

@Getter
public enum TrnType {
    DEPOSIT(1),
    WITHDRAWAL(2),
    TRANSFER(3),
    DUITNOW(4);

    private final int value;

    TrnType(int value) {
        this.value = value;
    }

    public static TrnType fromValue(int value) {
        for (TrnType type : TrnType.values()) {
            if (type.getValue() == value) {
                return type;
            }
        }
        return null;
    }
}
