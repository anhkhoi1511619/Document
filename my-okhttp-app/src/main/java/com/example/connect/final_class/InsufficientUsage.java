package com.example.connect.final_class;

public enum InsufficientUsage {
    NO(0),
    YES(1);
    public final int value;

    InsufficientUsage(int v) {
        this.value = v;
    }

    public static InsufficientUsage fromId(int v) {
        for (InsufficientUsage x : values()) if (x.value == v) return x;
        return values()[0];
    }

    public String toString() {
        switch (value) {
            case 0:
                return "残高不足時の引去なし";
            case 1:
                return "残高不足時の引去あり";
        }
        return "";
    }
}
