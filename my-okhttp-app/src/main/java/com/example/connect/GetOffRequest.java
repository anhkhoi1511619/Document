package com.example.connect;
import com.example.connect.final_class.*;


import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.util.Calendar;

public class GetOffRequest {
    public String apiID;
    public BusinessType businessType;
    public static short currentRequestId = 0;
    public short requestId = 0;

    public MediaType media;
    public String data; // 61 bytes

    public Calendar dateTime; // BCD, date 4 bytes + time 3 bytes, YYYYMMDD, 2006/12/28 => 0x20061228, HHmmSS 12:20:31 => 0x122031
    public InsufficientUsage useUpSf;
    public DiscountUsage useDiscount;
    public VehicleType vehicleType;
    // vehicle info
    public String machineId; // 8 bytes
    public String corporateCode; // 4 bytes
    public String officeCode; // 4 bytes
    public String machineCode; // 2 bytes
    public String vehicleNumber; // 5 bytes
    public int lineageNumber; // 3 bytes
    public int pieceNumber; // 3 bytes
    public Calendar firstDate; // 3 bytes, BCD, HHmmSS 12:45:00 => 0x124500
    public ReaderType readerType;
    // multi info
    public int adult;
    public int child;
    public int adultSp;
    public int childSp;
    // section info
    public int inSectionNumber;
    // SF info
    public int rideFare; // 3 bytes

    public GetOffRequest() {
        apiID = "BWBA100N";
        businessType = BusinessType.NORMAL;
        requestId = currentRequestId;
        currentRequestId += 1;
        media = MediaType.QR;
        data = "123456"; // 61 bytes
        dateTime = Calendar.getInstance();
        useUpSf = InsufficientUsage.NO;
        useDiscount = DiscountUsage.NO;
        vehicleType = VehicleType.BUS;
        machineId = "12345678";
        corporateCode = "1234";
        officeCode = "1234";
        machineCode = "12";
        vehicleNumber = "12345";
        lineageNumber = 0;
        pieceNumber = 0;
        firstDate = Calendar.getInstance();
        readerType = ReaderType.MULTI_BOARD;
        adult = 0;
        child = 0;
        adultSp = 0;
        childSp = 0;
        inSectionNumber = 0;
        rideFare = 0;
    }

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

    public GetOffRequest setBusinessType(BusinessType i) {
        businessType = i;
        return this;
    }

    public GetOffRequest setBusinessType(int i) {
        businessType = BusinessType.fromId(i);
        return this;
    }

    public GetOffRequest setMedia(MediaType i) {
        media = i;
        return this;
    }

    public GetOffRequest setMedia(int i) {
        media = MediaType.fromId(i);
        return this;
    }

    public GetOffRequest setData(String str) {
        data = str;
        return this;
    }

    public GetOffRequest setUseUpSF(InsufficientUsage i) {
        useUpSf = i;
        return this;
    }

    public GetOffRequest setUseUpSF(int i) {
        useUpSf = InsufficientUsage.fromId(i);
        return this;
    }

    public GetOffRequest setVehicleType(VehicleType i) {
        vehicleType = i;
        return this;
    }

    public GetOffRequest setVehicleType(int i) {
        vehicleType = VehicleType.fromId(i);
        return this;
    }
    public GetOffRequest setReaderType(int i) {
        readerType = ReaderType.fromId(i);
        return this;
    }

    public GetOffRequest setReaderType(ReaderType t) {
        readerType = t;
        return this;
    }


    public byte[] serialize() {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        BinaryIO.writeString(apiID, stream);
        stream.write(businessType.value);
        BinaryIO.writeNumber(requestId, stream, 2);
        stream.write(media.value);
        BinaryIO.writeHexString(data, stream, 61);
        BinaryIO.writeDate(dateTime, stream);
        BinaryIO.writeTime(dateTime, stream);
        stream.write(useUpSf.value);
        stream.write(useDiscount.value);
        stream.write(vehicleType.value);
        BinaryIO.writeString(machineId, stream);
        BinaryIO.writeString(corporateCode, stream);
        BinaryIO.writeString(officeCode, stream);
        BinaryIO.writeString(machineCode, stream);
        BinaryIO.writeString(vehicleNumber, stream);
        BinaryIO.writeNumber(lineageNumber, stream, 3);
        BinaryIO.writeNumber(pieceNumber, stream, 3);
        BinaryIO.writeTime(firstDate, stream);
        stream.write(readerType.value);
        stream.write(adult);
        stream.write(child);
        stream.write(adultSp);
        stream.write(childSp);
        stream.write(inSectionNumber);
        BinaryIO.writeNumber(rideFare, stream, 3);
        return stream.toByteArray();
    }

    public void deserialize(String input) {
        deserialize(BinaryIO.parseHex(input));
    }

    public void deserialize(byte[] input) {
        int offset = 0;
        apiID = BinaryIO.readString(input, offset, 8);
        offset += 8;
        businessType = BusinessType.fromId(input[offset]);
        offset += 1;
        requestId = (short) BinaryIO.readNumber(input, offset, 2);
        offset += 2;
        media = MediaType.fromId(input[offset]);
        offset += 1;
        data = BinaryIO.readString(input, offset, 61);
        offset += 61;
        BinaryIO.bcdToDate(BinaryIO.readBCDNumber(input, offset, 4), dateTime);
        offset += 4;
        BinaryIO.bcdToTime(BinaryIO.readBCDNumber(input, offset, 3), dateTime);
        offset += 3;
        useUpSf = InsufficientUsage.fromId(input[offset]);
        offset += 1;
        useDiscount = DiscountUsage.fromId(input[offset]);
        offset += 1;
        vehicleType = VehicleType.fromId(input[offset]);
        offset += 1;
        machineId = BinaryIO.readString(input, offset, 8);
        offset += 8;
        corporateCode = BinaryIO.readString(input, offset, 4);
        offset += 4;
        officeCode = BinaryIO.readString(input, offset, 4);
        offset += 4;
        machineCode = BinaryIO.readString(input, offset, 2);
        offset += 2;
        vehicleNumber = BinaryIO.readString(input, offset, 5);
        offset += 5;
        lineageNumber = BinaryIO.readNumber(input, offset, 3);
        offset += 3;
        pieceNumber = BinaryIO.readNumber(input, offset, 3);
        offset += 3;
        BinaryIO.bcdToTime(BinaryIO.readBCDNumber(input, offset, 3), firstDate);
        offset += 3;
        readerType = ReaderType.fromId(input[offset]);
        offset += 1;
        adult = input[offset];
        offset += 1;
        child = input[offset];
        offset += 1;
        adultSp = input[offset];
        offset += 1;
        childSp = input[offset];
        offset += 1;
        inSectionNumber = input[offset];
        offset += 1;
        rideFare = BinaryIO.readNumber(input, offset, 3);
        offset += 3;
    }
}
