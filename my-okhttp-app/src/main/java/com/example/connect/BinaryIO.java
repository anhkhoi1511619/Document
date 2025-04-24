package com.example.connect;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.Arrays;


public class BinaryIO {
    public static String toHexString(byte[] input) {
        StringBuilder sb = new StringBuilder(input.length * 2);
        for (byte b : input) {
            sb.append(String.format("%02x", b).toUpperCase());
        }
        return sb.toString();
    }

    public static byte[] parseHex(String input) {
        int n = input.length();
        byte[] data = new byte[n / 2];
        for (int i = 0;i<n;i += 2) {
            byte x = (byte) Integer.parseInt(input.substring(i,Math.min(n,i+2)), 16);
            data[i / 2] = x;
        }
        return data;
    }

    public static String readString(byte[] input, int offset, int size) {
        return new String(Arrays.copyOfRange(input, offset, offset + size));
    }

    public static int readBCDNumber(byte[] input, int offset, int size) {
        return castIntFromBCD(Arrays.copyOfRange(input, offset, offset + size));
    }

    public static int readNumber(byte[] input, int offset, int size) {
        return castInt(input, offset, size);
    }
	public static int castIntFromBCD(byte[] input) {
		//input [0x20,0x23], output 2023
		int ret = 0;
		for(byte x: input) ret = ret*100 + (x>>4 & 0x0f)*10 + (x & 0x0f);
		return ret;
	}

	public static int castInt(byte[] input, int offset, int length) {
		//input [0xab,0xcd,0x11,0x22], output 0xabcd1122
		int ret = 0;
		int n = Math.min(offset+length, input.length);
		for(int i = offset;i<n;i++) ret = ret<<8 | (0xff & (int)input[i]);
		return ret;
	}

    public static void bcdToTime(int input, Calendar output) {
        int second = input % 100;
        input /= 100;
        int minute = input % 100;
        input /= 100;
        int hour = input;
        output.set(Calendar.HOUR_OF_DAY, hour);
        output.set(Calendar.MINUTE, minute);
        output.set(Calendar.SECOND, second);
    }

    public static void bcdToDate(int input, Calendar output) {
        int day = input % 100;
        input /= 100;
        int month = input % 100;
        input /= 100;
        int year = input;
        output.set(Calendar.YEAR, year);
        output.set(Calendar.MONTH, month - 1); // Java MONTH start at 0, January = 0, February = 1, etc...
        output.set(Calendar.DAY_OF_MONTH, day);
    }

    public static void writeHexString(String input, ByteArrayOutputStream output, int minSize) {
        byte[] data = parseHex(input);
        for (byte x: data) {
            output.write(x);
        }
        for(int i = data.length;i<minSize;i++) {
          output.write(0);
        }
    }

    public static void writeHexString(String input, ByteArrayOutputStream output) {
        for (byte x: parseHex(input)) {
            output.write(x);
        }
    }

    public static void writeString(String input, ByteArrayOutputStream output, int minSize) {
        char[] data = input.toCharArray();
        for (char c: data) {
            output.write(c);
        }
        for(int i = data.length;i<minSize;i++) {
          output.write(0);
        }
    }

    public static void writeString(String input, ByteArrayOutputStream output) {
        for (char c : input.toCharArray()) {
            output.write(c);
        }
    }

    public static void writeBCD(int input, ByteArrayOutputStream output, int size) {
        writeNumber(integerToBCD(input), output, size);
    }
	public static int integerToBCD(int x) {
		// input 11222023, output 0x11222023
		int ret = 0;
			for(int i=0;i<32;i+=4) {
					ret |= (x%10) << i;
					x /= 10;
			}
			return ret;
	}
    
    public static void writeDate(Calendar input, ByteArrayOutputStream output) {
        writeBCD(input.get(Calendar.YEAR), output, 2);
        // Java MONTH start at 0, January = 0, February = 1, etc...
        writeBCD(input.get(Calendar.MONTH) + 1, output, 1);
        writeBCD(input.get(Calendar.DAY_OF_MONTH), output, 1);
    }

    public static void writeTime(Calendar input, ByteArrayOutputStream output) {
        writeBCD(input.get(Calendar.HOUR_OF_DAY), output, 1);
        writeBCD(input.get(Calendar.MINUTE), output, 1);
        writeBCD(input.get(Calendar.SECOND), output, 1);
    }

    public static void writeNumber(int input, ByteArrayOutputStream output, int size) {
        while (size-- > 0) {
            byte x = (byte) (0xff & (input >> (size * 8)));
            output.write(x);
        }
    }
}
