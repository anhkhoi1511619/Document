package com.example.connect.final_class;

public enum TicketType {
    COMMUTER_PASS(1),
    STUDENT_PASS(2),
    SILVER_PASS_65(3);

    public final int value;

    TicketType(int v) {
        this.value = v;
    }

    public static TicketType fromId(int v) {
        for (TicketType x : values()) if (x.value == v) return x;
        return values()[0];
    }

    public String toString() {
        switch (value) {
            case 1:
                return "通勤定期";
            case 2:
                return "通学定期";
            case 3:
                return "シルバーパス";
        }
        return "";
    }
}
