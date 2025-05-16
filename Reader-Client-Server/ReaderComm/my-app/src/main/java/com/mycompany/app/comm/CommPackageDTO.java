package com.mycompany.app.comm;

public class CommPackageDTO {
	private final int SUM_STRING_LENGTH = 2;
	private final int SUM_BYTE_LENGTH = 1;

	protected String stx;
	protected String etx;
	protected String txData;
	protected String txCmd;
	protected String txSqNo;

	protected String rxCommData;
	protected byte[] rxData;
	protected byte rxCmd;
	protected byte rxSqNo;

	protected String rxDataString;
	protected String rxCmdString;
	protected String rxSqNoString;


	protected int txSize;
	protected int sumType;
	int SUM_TYPE_INTEX_HEX = 0;
		/**
	 * 概要：コンストラクタ
	 *
	 * @param stx     STXのキャラクタ
	 * @param etx     ETXのキャラクタ
	 * @param sumType SUM_TYPE_INTEL_HEX インテルHEX方式（SUMまで足すと0になる)
	 *                SUM_TYPE_SUM 単純加算のみ
	 */
	public CommPackageDTO(String stx, String etx, int sumType) {
		this.stx = stx;
		this.etx = etx;
		this.sumType = sumType;

		txData = "";
		txCmd = "";
		txSqNo = "";

		rxData = null;
		rxCmd = 0x00;
		rxSqNo = 0x00;
	}

		/**
	 * 概要：送信コマンドを設定
	 *
	 * @param txCmd コマンド(String形式 0~9,A~F の2桁)
	 */
	public void setTxCmd(String txCmd) {
		this.txCmd = txCmd;
	}

	/**
	 * 概要：送信コマンドを取得
	 *
	 * @return コマンド(String形式 0 ~ 9, A ~ F の2桁)
	 */
	public String getTxCmd() {
		return txCmd;
	}

	/**
	 * 概要：送信シーケンス番号を設定
	 *
	 * @param sqNo シーケンス番号(String形式 0~9,A~F の偶数桁)
	 */
	public void setTxSqNo(String sqNo) {
		this.txSqNo = sqNo;
	}

	/**
	 * 概要：送信シーケンス番号を取得
	 *
	 * @return sqNo シーケンス番号(String形式 0~9,A~F の偶数桁)
	 */
	public String getTxSqNo() {
		return txSqNo;
	}

	/**
	 * 概要：送信データを設定
	 *
	 * @param txData 送信データ(String形式 0~9,A~F の偶数桁)
	 */
	public void setTxData(String txData) {
		this.txData = txData;
	}

	public byte[] makeTxData() {
		String sizeSum;
		String dataSum;
		String strTxData = "";
		byte byteSizeSum;
		byte byteDataSum;
		try {

			txData = txCmd + txSqNo + txData;
			txSize = calcByteLength(txData);

			String strSize = DataTypeConverter.format4HexDigit(txSize);
			byte[] byteSize = DataTypeConverter.deserialize(strSize);
			byte[] byteData = DataTypeConverter.deserialize(txData);
			if (sumType == SUM_TYPE_INTEX_HEX) {
				byteSizeSum = DataTypeConverter.calcIntelHexSumFromByteToByte(byteSize);
				byteDataSum = DataTypeConverter.calcIntelHexSumFromByteToByte(byteData);
			} else {
				byteSizeSum = DataTypeConverter.calcSumFromByteToByte(byteSize);
				byteDataSum = DataTypeConverter.calcSumFromByteToByte(byteData);
			}
			sizeSum = DataTypeConverter.format(byteSizeSum);
			dataSum = DataTypeConverter.format(byteDataSum);

			strTxData = stx + strSize + sizeSum + txData + dataSum + etx;

			System.err.println("sending data : " + strTxData);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return DataTypeConverter.deserialize(strTxData);
	}
		/**
	 * 概要：送信データ長を計算
	 *
	 * @param str 送信データ(String形式 0~9,A~F の偶数桁)
	 */
	private int calcByteLength(String str) {
		return (str.length() / 2);
	}
	
	public int getRxCmd()
	{
		return 1;
	}
	
}
