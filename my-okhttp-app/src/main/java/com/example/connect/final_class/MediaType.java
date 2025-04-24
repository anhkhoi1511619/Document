package com.example.connect.final_class;

public enum MediaType {
    INVALID(0),
    QR(1),
    IC(2);

    public final int value;

    MediaType(int v) {
        this.value = v;
    }

    public static MediaType fromId(int v) {
        for (MediaType x : values()) if (x.value == v) return x;
        return values()[0];
    }

    public String toString() {
        switch (value) {
            case 1:
                return "QR";
            case 2:
                return "IC";
        }
        return "";
    }
}
