package com.example.connect.final_class;


public enum DiscountType {
    NO_DISCOUNT(0),
    DISCOUNT(1),
    PRIVILEGES(2),
    DISCOUNT_AND_PRIVILEGES(3);

    public final int value;

    DiscountType(int v) {
        this.value = v;
    }

    public static DiscountType fromId(int v) {
        for (DiscountType x : values()) if (x.value == v) return x;
        return values()[0];
    }

    public String toString() {
        switch (value) {
            case 0:
                return "割引なし";
            case 1:
                return "割引あり";
            case 2:
                return "優待あり";
            case 3:
                return "割引＋優待";
        }
        return "";
    }
}
