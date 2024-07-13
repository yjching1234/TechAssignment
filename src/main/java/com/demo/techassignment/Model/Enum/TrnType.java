package com.demo.techassignment.Model.Enum;

public enum TrnType {
    DEPOSIT(1),
    WITHDRAWAL(2),
    TRANSFER(3);

    private final int value;

    TrnType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static TrnType fromValue(int value) {
        for (TrnType type : TrnType.values()) {
            if (type.getValue() == value) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid value for TrnType: " + value);
    }
}
