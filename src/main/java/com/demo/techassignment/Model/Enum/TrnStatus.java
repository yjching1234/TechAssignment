package com.demo.techassignment.Model.Enum;

import lombok.Getter;

@Getter
public enum TrnStatus {
    PENDING(1),
    SUCCESS(2),
    COMPLETED(3),
    CANCELED(4);

    private final int value;

    TrnStatus(int value) {
        this.value = value;
    }

    public static TrnStatus fromValue(int value) {
        for (TrnStatus trnStatus : TrnStatus.values()) {
            if (trnStatus.getValue() == value) {
                return trnStatus;
            }
        }
        return null;
    }
}
