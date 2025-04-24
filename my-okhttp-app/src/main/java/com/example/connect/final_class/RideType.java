package com.example.connect.final_class;


public enum RideType {
    BOARD(1),
    ALIGHT(2),
    SINGLE_ALIGHT(3);

    public final int value;

    RideType(int v) {
        this.value = v;
    }

    public static RideType fromId(int v) {
        for (RideType x : values()) if (x.value == v) return x;
        return values()[0];
    }

    public String toString() {
        switch (value) {
            case 1:
                return "乗車";
            case 2:
                return "降車";
            case 3:
                return "単独降車";
        }
        return "";
    }
}
