package com.example.connect.final_class;


public enum DiscountUsage {
    NO(0),
    YES(1);
    public final int value;

    DiscountUsage(int v) {
        this.value = v;
    }

    public static DiscountUsage fromId(int v) {
        for (DiscountUsage x : values()) if (x.value == v) return x;
        return values()[0];
    }

    public String toString() {
        switch (value) {
            case 0:
                return "利用回数制限有りの割引適用なし";
            case 1:
                return "利用回数制限有りの割引適用あり";
        }
        return "";
    }
}
