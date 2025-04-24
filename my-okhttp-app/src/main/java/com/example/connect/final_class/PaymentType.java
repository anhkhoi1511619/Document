package com.example.connect.final_class;

public enum PaymentType {
    SF(0),
    RECURRING(1),
    SF_AND_RECURRING(2),
    SF_INSUFFICIENT(3);

    public final int value;

    PaymentType(int v) {
        this.value = v;
    }

    public static PaymentType fromId(int v) {
        for (PaymentType x : values()) if (x.value == v) return x;
        return values()[0];
    }

    public String toString() {
        switch (value) {
            case 0:
                return "SF";
            case 1:
                return "定期";
            case 2:
                return "SF＋定期（飛付・乗越）";
            case 3:
                return "SF不足";
        }
        return "";
    }
}
