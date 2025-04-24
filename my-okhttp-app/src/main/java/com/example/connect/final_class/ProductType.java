package com.example.connect.final_class;

public enum ProductType {
    SECTION(1),
    SECTION_AND_AREA(2),
    AREA_FREE(3);

    public final int value;

    ProductType(int v) {
        this.value = v;
    }

    public static ProductType fromId(int v) {
        for (ProductType x : values()) if (x.value == v) return x;
        return values()[0];
    }

    public String toString() {
        switch (value) {
            case 0:
                return "区間定期";
            case 1:
                return "区間＋エリア";
            case 2:
                return "エリアフリー";
        }
        return "";
    }
}

