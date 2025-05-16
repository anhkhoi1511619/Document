package com.mycompany.app.comm;

import com.mycompany.app.utils.*;
import java.io.OutputStream;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class SocketDataProcessor {
	final ExecutorService dedicatedExecutor = Executors.newSingleThreadExecutor();
	final ScheduledExecutorService scheduledExecutor =
			Executors.newSingleThreadScheduledExecutor();
	String rcvDataAll;                          // 受信データALL
	String command;                             // コマンド部格納用の変数
	String seqNo;                               // シーケンス番号格納用の変数
	String dataSize;                            // 受信データのサイズ
	String dataSizeSum;                         // 受信データのサイズサム
	String rcvData;                             // 受信データ(byte型データを連結)
	String rcvDataSum;                          // 受信データ部サム(command～データ部のサム)
	final String tx_ip;                               // 送信側（相手側）のIPアドレス
	final int rcvPort;                                // RX用のポート
	int nonComCnt;                              // 通信断回数カウント(10回以上で通信異常)
	int readerNo = -100;
	String lastSeqNo = "";

	final Queue<byte[]> queue = new ArrayDeque<>();    // 受信データ一時保管用のキュー
	
	OutputStream outputStream;

	public static final int RCV_DATA_SIZE_START = 2;
	public static final int RCV_DATA_SIZE_END = 6;
	public static final int RCV_DATA_SIZE_SUM_START = 6;
	public static final int RCV_DATA_SIZE_SUM_END = 8;
	public static final int RCV_DATA_COMMAND_START = 8;
	public static final int RCV_DATA_COMMAND_END = 10;
	public static final int RCV_DATA_SEQ_NO_START = 10;
	public static final int RCV_DATA_SEQ_NO_END = 12;
	public static final int RCV_DATA_DATA_START = 12;
	public static final int ERR_COM_MAX = 100; //設定値*20ms(25なら0.5秒)

	public SocketDataProcessor(String ip, int port) {
		//Log.d(TAG, "new SocketDataProcessor "+ip+":"+port);
		tx_ip = ip;
		rcvPort = port;
	}

	/**
	 * 概要：データ処理部メイン
	 *
	 */
	// public void run() {
	// 	boolean isController = isController(tx_ip);
	// 	final int PROCESS_FREQ = 100;
	// 	scheduledExecutor.scheduleAtFixedRate(() -> {
	// 		// キューの先頭の値を取り出してキューから削除する
	// 		// 値がない場合は null を返す
	// 		byte[] data = queue.poll();
	// 		// 値がある場合→ null ではない
	// 		if(data != null) {
	// 			// エラークリア
	// 			ReaderComErrClear(rcvPort);
	// 			// データ処理行い
	// 			dataAnalysis(data);
	// 			if(lastSeqNo.equals(seqNo)) {
	// 				return;
	// 			}
	// 			//Log.v("SocketDataProcessor", "Processing data from "+tx_ip);
	// 			lastSeqNo = seqNo;
	// 			RcvDataManager rcvDataManager = new RcvDataManager();
	// 			rcvDataManager.configureOutputStream(outputStream);
	// 			rcvDataManager.run(tx_ip, command, rcvData, isController);
	// 		} else {
	// 			// エラーチェック
	// 			ReaderComErrChk(rcvPort);
	// 		}
	// 	}, 5, PROCESS_FREQ, MILLISECONDS);
	// }

	// public void shutdown() {
	// 	Log.d(TAG, "Shutting down socket data processor...");
	// 	scheduledExecutor.shutdownNow();
	// 	dedicatedExecutor.shutdownNow();
	// }

	public void configureIOStream(OutputStream oStream) {
		this.outputStream = oStream;
	}

	void dataAnalysis(byte[] rx_data) {
		// 受信したデータ部結合し取得
		rcvDataAll = DataTypeConverter.format(rx_data);

		// 受信データからデータサイズを取得
		dataSize = rcvDataAll.substring(RCV_DATA_SIZE_START, RCV_DATA_SIZE_END);

		// 受信データからデータサイズサムを取得
		dataSizeSum = rcvDataAll.substring(RCV_DATA_SIZE_SUM_START, RCV_DATA_SIZE_SUM_END);

		// 受信データからコマンドを取得
		command = rcvDataAll.substring(RCV_DATA_COMMAND_START, RCV_DATA_COMMAND_END);

		// 受信データからシーケンスNo.を取得
		seqNo = rcvDataAll.substring(RCV_DATA_SEQ_NO_START, RCV_DATA_SEQ_NO_END);

		// 受信データからデータ部を取得
		rcvData = rcvDataAll.substring(RCV_DATA_DATA_START, (Integer.parseInt(dataSize, 16) * 2) + 8);

		// 受信データからデータ部サムを取得
		rcvDataSum = getRcvDataAllDataSum(rcvDataAll);

		// サイズサムチェック結果取得
		boolean sizeSumChk = checkSizeSum(dataSize, dataSizeSum);

		// データサムチェック結果取得
		boolean dataSumChk = checkDataSum(rcvDataAll, dataSize, rcvDataSum);

		//Log.d("RcvDataManager", "received data, CMD = "+command+", data = "+rcvData);
	}

	// public void processNow(byte[] data) {
	// 	// 受信データ解析実施
	// 	dataAnalysis(data);
	// 	if(rcvData.isEmpty()) {
	// 		return;
	// 	}
	// 	// レスポンス実施
	// 	dedicatedExecutor.execute(() -> {
	// 		RcvDataManager rcvDataManager = new RcvDataManager();
	// 		rcvDataManager.run(tx_ip, command, rcvData, isController(tx_ip));
	// 	});
	// }

	/**
	 * 概要：受信キュー内容セット
	 *
	 * @param rx_data 受信データ
	 */
	public void push(byte[] rx_data) {
		queue.add(rx_data);
	}

	/**
	 * 概要：データ部サム取得
	 *
	 * @return 受信キュー
	 */
	String getRcvDataAllDataSum(String rcvDataAll) {
		int i = Integer.parseInt(dataSize, 16) * 2 + 8;
        return rcvDataAll.substring(i, i+2);
	}


	/**
	 * 概要：データサイズサムのチェック機能
	 *
	 * @param dataSize    データサイズ
	 * @param dataSizeSum 電文状のデータサイズサム
	 * @return true：サム位置、false：サム不一致
	 */
	boolean checkSizeSum(String dataSize, String dataSizeSum) {

		boolean chkResult = false;

		String sizeSum = DataTypeConverter.calcIntelHexSumFromStringToString(dataSize);
		if(sizeSum.equals(dataSizeSum)) {
			chkResult = true;
		}
		else {
			//Log.d(TAG_TCP_RCV, "データサイズサムエラー");
		}

		return chkResult;
	}

	/**
	 * 概要：データ部サムのチェック機能
	 *
	 * @param rcvDataAll 受信データ全て
	 * @param dataSize   データサイス
	 * @param rcvDataSum 受信データのデータ部サム
	 * @return true：サム位置、false：サム不一致
	 */
	boolean checkDataSum(String rcvDataAll, String dataSize, String rcvDataSum) {

		boolean chkResult = false;
		int endOffset = (Integer.parseInt(dataSize, 16) * 2);

		String dataSum = DataTypeConverter.calcIntelHexSumFromStringToString(rcvDataAll.substring(RCV_DATA_COMMAND_START, endOffset + 8));


		if(dataSum.equals(rcvDataSum)) {
			chkResult = true;
		}
		else {
			//Log.d(TAG_TCP_RCV, "データ部サムエラー");
		}

		return chkResult;
	}

	public String getRcvData()
	{
		return rcvData;
	}

	// /**
	//  * 概要：通信断回数チェック(規定回数を超えるとエラーセット)
	//  *
	//  * @param port
	//  */
	// void ReaderComErrChk(int port) {
	// 	if(port == TCP_PORT_CTRL_TICKET_GET_OFF) {
	// 		if(nonComCnt >= ERR_COM_MAX) {
	// 			if(!ErrorManager.isExistedMessage(MessageErrorEnum.ERROR_GET_OFF_READER_COM)) {
	// 				ErrorManager.setMessage(MessageErrorEnum.ERROR_GET_OFF_READER_COM);
	// 			}
	// 		}
	// 		else {
	// 			nonComCnt++;
	// 		}
	// 	}
	// 	else if(port == TCP_PORT_CTRL_TICKET_RW_1) {
	// 		if(nonComCnt >= ERR_COM_MAX) {
	// 			if(!ErrorManager.isExistedMessage(MessageErrorEnum.ERROR_RIDE_1_READER_COM)) {
	// 				ErrorManager.setMessage(MessageErrorEnum.ERROR_RIDE_1_READER_COM);
	// 			}
	// 		}
	// 		else {
	// 			nonComCnt++;
	// 		}
	// 	}
	// }

	// /**
	//  * 概要：通信断エラークリア
	//  *
	//  * @param port
	//  */
	// void ReaderComErrClear(int port) {
	// 	nonComCnt = 0;

	// 	if(port == TCP_PORT_CTRL_TICKET_GET_OFF) {
	// 		if(ErrorManager.isExistedMessage(MessageErrorEnum.ERROR_GET_OFF_READER_COM)) {
	// 			ErrorManager.clearMessage(MessageErrorEnum.ERROR_GET_OFF_READER_COM);
	// 		}
	// 	}
	// 	else if(port == TCP_PORT_CTRL_TICKET_RW_1) {
	// 		if(ErrorManager.isExistedMessage(MessageErrorEnum.ERROR_RIDE_1_READER_COM)) {
	// 			ErrorManager.clearMessage(MessageErrorEnum.ERROR_RIDE_1_READER_COM);
	// 		}

	// 	}
	// }


	// /**
	//  * 概要：リーダ番号を取得
	//  * @return readerNo:リーダー番号
	//  */
	// public int getReaderNo() {
	// 	return readerNo;
	// }

	// boolean isController(String ip) {
	// 	return 	ip.equals(CTRL_TCP_IP_ADDRESS[0]) || ip.equals(CTRL_TCP_IP_ADDRESS[1]) ||
	// 			ip.equals(CTRL_TCP_IP_ADDRESS[2]) || ip.equals(CTRL_TCP_IP_ADDRESS[3]);
	// }
}
