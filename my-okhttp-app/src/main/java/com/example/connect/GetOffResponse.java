package com.example.connect;
import com.example.connect.final_class.*;


import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.Arrays;

public class GetOffResponse {
    public short resultCode;
    public short resultDetailCode;
    public RideType rideType = RideType.BOARD;
    public UserType userType = UserType.ADULT;
    public Qualification qualification = Qualification.VALID_MORE_THAN_15DAY;
    public AutoSetting autoChargeSetting = AutoSetting.DISABLED;
    public DiscountType discountType = DiscountType.DISCOUNT;
    public PaymentType payType = PaymentType.RECURRING;
    public byte inSectionNumber;

    // multi info
    public byte adult;
    public byte child;
    public byte adultSp;
    public byte childSp;
    public UserType ticketUserType = UserType.ADULT;
    public TicketType ticketType = TicketType.SILVER_PASS_65;
    public ProductType ticketProductType = ProductType.AREA_FREE;
    public Calendar ticketLimitDate = Calendar.getInstance(); // BCD, 4 bytes
    public int balance;
    public int rideFare;
    public int substractBalance;
    public int lowBalance;

    public String toString() {
        String ret = "";
        for (Field field : this.getClass().getFields()) {
            try {
                Object val = field.get(this);
                String valStr = val != null ? val.toString() : "null";
                if (field.getType() == Calendar.class) {
                    valStr = "...";
                }
                ret += field.getName() + ": " + valStr + ",";
            } catch (Exception e) {
                continue;
            }
        }
        return ret;
    }

    public byte[] serialize() {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        stream.write(rideType.value);
        stream.write(userType.value);
        stream.write(qualification.value);
        stream.write(autoChargeSetting.value);
        stream.write(discountType.value);
        stream.write(payType.value);
        stream.write(inSectionNumber);
        stream.write(adult);
        stream.write(child);
        stream.write(adultSp);
        stream.write(childSp);
        stream.write(ticketUserType.value);
        stream.write(ticketType.value);
        stream.write(ticketProductType.value);
        BinaryIO.writeDate(ticketLimitDate, stream);
        BinaryIO.writeNumber(balance, stream, 3);
        BinaryIO.writeNumber(rideFare, stream, 3);
        BinaryIO.writeNumber(substractBalance, stream, 3);
        BinaryIO.writeNumber(lowBalance, stream, 3);
        return stream.toByteArray();
    }

    public void deserialize(byte[] data) {
        int offset = 0;
        resultCode = (short) BinaryIO.readNumber(data, offset, 2);
        offset += 2;
        resultDetailCode = (short) BinaryIO.readNumber(data, offset, 2);
        offset += 2;
        rideType = RideType.fromId(data[offset]);
        offset += 1;
        userType = UserType.fromId(data[offset]);
        offset += 1;
        qualification = Qualification.fromId(data[offset]);
        offset += 1;
        autoChargeSetting = AutoSetting.fromId(data[offset]);
        offset += 1;
        discountType = DiscountType.fromId(data[offset]);
        offset += 1;
        payType = PaymentType.fromId(data[offset]);
        offset += 1;
        inSectionNumber = data[offset];
        offset += 1;
        adult = data[offset];
        offset += 1;
        child = data[offset];
        offset += 1;
        adultSp = data[offset];
        offset += 1;
        childSp = data[offset];
        offset += 1;
        ticketUserType = UserType.fromId(data[offset]);
        offset += 1;
        ticketType = TicketType.fromId(data[offset]);
        offset += 1;
        ticketProductType = ProductType.fromId(data[offset]);
        offset += 1;
        BinaryIO.bcdToDate(BinaryIO.readBCDNumber(data, offset, 4), ticketLimitDate);
        offset += 4;
        balance = BinaryIO.readNumber(data, offset, 3);
        offset += 3;
        rideFare = BinaryIO.readNumber(data, offset, 3);
        offset += 3;
        substractBalance = BinaryIO.readNumber(data, offset, 3);
        offset += 3;
        lowBalance = BinaryIO.readNumber(data, offset, 3);
        offset += 3;
    }
}

