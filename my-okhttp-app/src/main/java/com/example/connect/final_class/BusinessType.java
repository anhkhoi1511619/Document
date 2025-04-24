package com.example.connect.final_class;


public enum BusinessType {
    NORMAL(0),
    NINZU(1),
    SECTION(2),
    NINZU_SECTION(3),
    KINGAKU(4),
    NINZU_KINGAKU(5),
    TEIGAKU(6),
    NINZU_TEIGAKU(7);

    public final int value;

    BusinessType(int v) {
        this.value = v;
    }

    public static BusinessType fromId(int v) {
        for (BusinessType x : values()) if (x.value == v) return x;
        return values()[0];
    }

    public String toString() {
        switch (value) {
            case 0:
                return "通常";
            case 1:
                return "人数";
            case 2:
                return "区間";
            case 3:
                return "人＋区";
            case 4:
                return "金額";
            case 5:
                return "人＋金";
            case 6:
                return "定額";
            case 7:
                return "人＋定";
        }
        return "";
    }
}
