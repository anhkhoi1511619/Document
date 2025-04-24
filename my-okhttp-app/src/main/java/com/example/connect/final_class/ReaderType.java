package com.example.connect.final_class;


public enum ReaderType {
    MULTI_BOARD(1),
    MULTI_ALIGHT(2),
    SINGLE_ALIGHT(3),
    BOTH(4);

    public final int value;

    ReaderType(int v) {
        this.value = v;
    }

    public static ReaderType fromId(int v) {
        for (ReaderType x : values()) if (x.value == v) return x;
        return values()[0];
    }

    public String toString() {
        switch (value) {
            case 1:
                return "多区乗車";
            case 2:
                return "多区降車";
            case 3:
                return "均一降車";
            case 4:
                return "兼用";
        }
        return "";
    }
}
