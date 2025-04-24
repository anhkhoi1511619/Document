package com.example.connect.final_class;

public enum Qualification {
    VALID_MORE_THAN_15DAY(0),
    VALID_LESS_THAN_15DAY(1),
    EXPIRED(2);

    public final int value;

    Qualification(int v) {
        this.value = v;
    }

    public static Qualification fromId(int v) {
        for (Qualification x : values()) if (x.value == v) return x;
        return values()[0];
    }

    public String toString() {
        switch (value) {
            case 0:
                return "有効期限内（15日以上）";
            case 1:
                return "有効期限内（15日未満）";
            case 2:
                return "有効期限切れ";
        }
        return "";
    }
}
