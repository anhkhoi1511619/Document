package com.example.connect.final_class;

public enum VehicleType {
    TRAIN(1),
    BUS(2),
    LIMOUSINE_BUS(3),
    SHIP(4),
    CABLE_WAY(5);

    public final int value;

    VehicleType(int v) {
        this.value = v;
    }

    public static VehicleType fromId(int v) {
        for (VehicleType x : values()) if (x.value == v) return x;
        return values()[0];
    }

    public String toString() {
        switch (value) {
            case 1:
                return "電車";
            case 2:
                return "バス";
            case 3:
                return "リムジンバス";
            case 4:
                return "船舶";
            case 5:
                return "索道";
        }
        return "";
    }
}
