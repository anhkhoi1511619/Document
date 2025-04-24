package com.example.connect.final_class;


public enum AutoSetting {
    DISABLED(0),
    ENABLED(1);

    public final int value;

    AutoSetting(int v) {
        this.value = v;
    }

    public static AutoSetting fromId(int v) {
        for (AutoSetting x : values()) if (x.value == v) return x;
        return values()[0];
    }

    public String toString() {
        switch (value) {
            case 0:
                return "オートチャージ実施なし";
            case 1:
                return "オートチャージ実施あり";
        }
        return "";
    }
}
