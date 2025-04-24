package com.example.connect.final_class;

public enum UserType {
    ADULT(1),
    CHILD(2),
    ADULT_SPECIAL(3),
    CHILD_SPECIAL(4);

    public final int value;

    UserType(int v) {
        this.value = v;
    }

    public static UserType fromId(int v) {
        for (UserType x : values()) if (x.value == v) return x;
        return values()[0];
    }

    public String toString() {
        switch (value) {
            case 1:
                return "大人";
            case 2:
                return "小児";
            case 3:
                return "大特";
            case 4:
                return "小特";
        }
        return "";
    }
}
