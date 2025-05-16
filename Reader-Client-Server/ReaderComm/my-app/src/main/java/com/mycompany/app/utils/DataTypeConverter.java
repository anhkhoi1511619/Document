package com.mycompany.app.utils;

public class DataTypeConverter {

	/**
	 * 概要：SUM値計算（単純加算）
	 * @param data SUM計算したいバイト配列
	 * @return SUM値(1byte)
	 */
	public static byte calcSumFromByteToByte(byte[] data){
		byte sum = 0x00;
		for(byte bytes : data){
			sum += bytes;
		}
		return sum;
	}

	/**
	 * 概要：SUM値計算 IntelHex方式(有効範囲のssデータを1byte単位で単純加算し、
	 * 加算結果の下2桁が00hとなるようにチェックサムコードを求める。
	 * @param data SUM計算したいバイト配列
	 * @return SUM値(1byte)
	 */
	public static byte calcIntelHexSumFromByteToByte(byte[] data){
		byte simple_sum = calcSumFromByteToByte(data);
		return (byte)(-simple_sum);
	}

	/**
	 * 概要：SUM値計算（単純加算）
	 * @param data SUM計算したい文字列
	 * @return SUM値(1byte)
	 */
	public static String calcSumFromStringToString(String data){
		byte[] byteData = deserialize(data);
		byte byteSum = calcSumFromByteToByte(byteData);
		return format(byteSum);
	}

	/**
	 * 概要：SUM値計算 IntelHex方式(有効範囲のデータを1byte単位で単純加算し、
	 * 加算結果の下2桁が00hとなるようにチェックサムコードを求める。
	 * @param data SUM計算したいバイト配列
	 * @return SUM値(1byte)
	 */
	public static String calcIntelHexSumFromStringToString(String data){
		byte[] byteData = deserialize(data);
		byte byteSum = calcIntelHexSumFromByteToByte(byteData);
		return format(byteSum);
	}


	/**
	 * 概要：SUM値計算 精算処理部用マスタ( 4byteSum IntelHexType )
	 * @param data SUM計算したいバイト配列
	 * @return SUM値(1byte)
	 */
	public static byte[] clacSumForMaster(byte[] data)
	{
		long tSum = 0;
		for( byte b : data )
		{
			tSum += ( (long)b & 0x00FF );
		}
		tSum &= 0x00000000FFFFFFFF;
		tSum = (-tSum);

		byte[] sum = new byte[4];
		sum[0] = (byte)((tSum & 0x00FF000000) >> 24 );
		sum[1] = (byte)((tSum & 0x0000FF0000) >> 16 );
		sum[2] = (byte)((tSum & 0x000000FF00) >> 8);
		sum[3] = (byte)(tSum  & 0x00000000FF);

		return sum;
	}


	/**
	 * 概要：ByteのString文字列をByte列に変換( 1234ABCD → 0x12 0x34 0x56 0xAB 0xCD )
	 * @param input 変換したいString文字列
	 * @return 変換後byte配列
	 */
	public static byte[] deserialize(String input){
		int n = input.length();
		var data = new byte[n / 2];
		for (int i = 0;i<n;i += 2) {
			byte x = (byte) Integer.parseInt(input.substring(i,Math.min(n,i+2)), 16);
			data[i / 2] = x;
		}
		return data;
	}

	/**
	 * 概要：ByteのString文字列をByteに変換( 1A → 0x1A )
	 *      1byte分のみ,3文字以上は例外発生
	 * @param str 変換したいString
	 * @return 変換後byte data
	 */
	public static byte stringToByte(String str) throws NumberFormatException {
		if( str.length() <= 2){
			return (byte) Integer.parseInt(str, 16);
		}
		else
		{
			throw new NumberFormatException("変換値は2桁以下でお願いします");
		}
	}

	/**
	 * 概要：1byteをStringに変換
	 * @param input 変換したいbyte
	 * @return 変換後String data
	 */
	public static String format(byte input){
		return String.format("%02X", input);
	}

	/**
	 * 概要：intをStringに変換(4桁固定)
	 * @param input 変換したいint
	 * @return 変換後String data
	 */
	public static String format4HexDigit(int input)  {
		if(input > 0xFFFF || input < 0) {
			return "FFFF";
		}
		return String.format("%04X", input);
	}

	/**
	 * 概要：intをStringに変換(6桁固定)
	 * @param input 変換したいint
	 * @return 変換後String data
	 */
	public static String format6HexDigit(int input)  {
		if(input > 0xFFFFFF || input < 0) {
			return "FFFFFF";
		}
		return String.format("%06X", input);
	}

	/**
	 * 概要：byte配列をStringに変換
	 * @param input 変換したいbyte配列
	 * @return 変換後String data
	 */
	public static String format(byte[] input) {
		if(input == null) {
			return "";
		}
		var ret = new StringBuilder();
		for (byte x: input) {
			ret.append(format(x));
		}
		return ret.toString();
	}

	/**
	 * 概要：メンテナンスメニュー画面の表示数に基づいて表示の処理
	 * input: [1]　ー(digits:3)ー＞　output: [001]
	 * input: [1000]　ー(digits:2)ー＞　output: [00]
	 * input: [-10]　ー(digits:2)ー＞　output: [00]
	 * @param input 値元
	 * @param digits 表示数
	 * @return 変換数値
	 */
	public static String format(int input, int digits) {
		if(digits <= 0) {
			return "";
		}
		if ((input < 0) || (String.valueOf(input).length() > digits)) {
			return String.format("%0" + digits + "d", 0);
		}
		return String.format("%0" + digits + "d", input);
	}


	/**
	 * 概要：他の画面の表示数に基づいて表示の処理
	 * input: [1]　ー(digits:3)ー＞　output: [1]
	 * input: [1000]　ー(digits:2)ー＞　output: []
	 * input: [-10]　ー(digits:2)ー＞　output: []
	 * @param input 値元
	 * @param digits 表示数
	 * @return 変換数値
	 */
	public static String sanitizeNumber(int input, int digits) {
		if ((input < 0) || (String.valueOf(input).length() > digits)) {
			return "";
		} else {
			return String.valueOf(input);
		}
	}

	public static String castAscii(byte[] input) {
		//input [30,41,42], output '0AB'
		return castAscii(input, 0, input.length);
	}

	public static String castAscii(byte[] input, int offset, int length) {
		//input [30,41,42], output '0AB'
		StringBuilder ret = new StringBuilder();
		int n = Math.min(offset+length, input.length);
		for(int i = offset;i<n;i++) {
			ret.append((char) (input[i] & 0xFF));
		}
		return ret.toString();
	}

	/**
	 * 概要：byte配列をIntに変換
	 * @return 変換数値
	 */
	public static int castInt(byte[] input) {
		//input [0xab,0xcd,0x11,0x22], output 0xabcd1122
		return castInt(input, 0, input.length);
	}

	public static int castInt(byte[] input, int offset, int length) {
		//input [0xab,0xcd,0x11,0x22], output 0xabcd1122
		int ret = 0;
		int n = Math.min(offset+length, input.length);
		for(int i = offset;i<n;i++) ret = ret<<8 | (0xff & (int)input[i]);
		return ret;
	}

	public static long castLong(byte[] input, int offset, int length) {
		//input [0xab,0xcd,0x11,0x22,0xab,0xcd,0x11,0x22], output 0xabcd1122abcd1122
		long ret = 0;
		int n = Math.min(offset+length, input.length);
		for(int i = offset;i<n;i++) ret = ret<<8 | (0xff & (long)input[i]);
		return ret;
	}

	public static int castIntFromBCD(byte[] input) {
		//input [0x20,0x23], output 2023
		int ret = 0;
		for(byte x: input) ret = ret*100 + (x>>4 & 0x0f)*10 + (x & 0x0f);
		return ret;
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

	// public static String utf8ToShiftJ(String input) {
	// 	byte[] bytes = new byte[0];
	// 	try {
	// 		bytes = input.getBytes("Shift_JIS");
	// 	} catch (UnsupportedEncodingException e) {
	// 		e.printStackTrace();
	// 	}
	// 	return toShiftJisString(bytes);
	// }

	private static String toShiftJisString(byte[] shiftJisCode) {
		StringBuilder stringBuilder = new StringBuilder();
		String shiftJisString;

		try {
			stringBuilder.append(DataTypeConverter.format(shiftJisCode));
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}

		shiftJisString = stringBuilder.toString();

		return shiftJisString;
	}

	/**
	 * 概要：Intをbyte（Hex）配列にに変換
	 * @param data　Int
	 * @param size　表示数
	 * @return byte（Hex）配列
	 */
	public static byte[] bytesOf(int data, byte size) {
		// input: 0x22ff, 2 -> output: [0x22, 0xff]
		byte[] ret = new byte[size];
		for (int i = 0; i < size; i++) ret[size-i-1] = (byte) ((data>>(i*8)) & 0xff);
		return ret;
	}
	public static byte[] bytesOf(long data, byte size) {
		// input: 0x22ff, 2 -> output: [0x22, 0xff]
		byte[] ret = new byte[size];
		for (int i = 0; i < size; i++) ret[size-i-1] = (byte) ((data>>(i*8)) & 0xff);
		return ret;
	}
	/**
	 * 概要：Intをbyte（Hex）配列にに変換
	 * @param data　Int
	 * @param size　表示数
	 * @return byte（Hex）配列
	 */
	public static byte[] bcdBytesOf(int data, int size) {
		// input: 123456, 3 -> output: [0x12, 0x34, 0x56]
		byte[] ret = new byte[size];
		for (int i = size-1; i>= 0; i--) {
			var x = ((data%100/10)<<4) + (data%10);
			ret[i] = (byte)x;
			data /= 100;
		}
		return ret;
	}
}
