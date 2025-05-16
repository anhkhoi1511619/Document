package jp.co.lecip.livumanagerapp.comm.exdev.connections.tcp.manager;

import android.util.Log;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import jp.co.lecip.livumanagerapp.ConstantNumber;
import jp.co.lecip.livumanagerapp.comm.exdev.PingChecker;
import jp.co.lecip.livumanagerapp.comm.exdev.connections.tcp.data.SocketDataProcessor;
import jp.co.lecip.livumanagerapp.manager.error.ErrorManager;
import jp.co.lecip.livumanagerapp.utility.DataTypeConverter;
import jp.co.lecip.livumanagerapp.utility.TLog;

public class RcvManager implements ConstantNumber {
	final ExecutorService executor = Executors.newSingleThreadExecutor();
	final String address;
	final int port;
	public RcvManager(String address, int port) {
		this.address = address;
		this.port = port;
		PingChecker.add(address);
		executor.execute(() -> {
			TLog.i(TAG_TCP, "Establishing reader communication thread for " + address + ":" + port);
			var processor = new SocketDataProcessor(address, port);
			try {
				var serverSocket = new ServerSocket(port);
				serverSocket.setReceiveBufferSize(EX_DEV_READER_BUF_SIZE);
				byte[] data = new byte[EX_DEV_READER_BUF_SIZE];
				// todo データ処理を呼ぶ
				processor.run();
				while (!Thread.currentThread().isInterrupted()) {
					//ErrorManager.addErrCom(address);
					TLog.i(TAG_TCP, "Waiting for connection for " + address + ":" + port);
					var socket = serverSocket.accept();
					TLog.i(TAG_TCP, "Server Socket opened for " + address + ":" + port);
					processor.configureIOStream(socket.getOutputStream());
					int size = 0;
					int offset = 0;
					while (!Thread.currentThread().isInterrupted()) {
						try {
							byte[] trunk = new byte[EX_DEV_READER_BUF_SIZE];
							Arrays.fill(trunk,(byte) 0);
							boolean firstTrunk = offset == 0;
							var trunkSize = socket.getInputStream().read(trunk);
							if(trunkSize <= 0) {
								break;
							}
							System.arraycopy(trunk, 0, data, offset, trunkSize);
							offset += trunkSize;
							if (firstTrunk) {
								// first 3 bytes of the first trunk is the package size
								size = DataTypeConverter.castInt(Arrays.copyOfRange(data, 1, 3)) + EX_DEV_FIX_PART_LENGTH;
							}
							offset += trunkSize;
							if (offset >= size) {
								byte[] cloned = new byte[EX_DEV_READER_BUF_SIZE];
								Arrays.fill(cloned, (byte) 0);
								cloned = Arrays.copyOfRange(data, 0, trunkSize);
								TLog.d(TAG_TCP, "Received " + trunkSize +
										" bytes: "+ DataTypeConverter.format(cloned)+
										" from reader-client side"+ socket.getRemoteSocketAddress().toString());
								processor.push(cloned);
								offset = 0;
							}
							ErrorManager.removeErrCom(address);
						}  catch (IOException e) {
							//ErrorManager.addErrCom(address);
							TLog.e(TAG_TCP, "Error while receiving data from reader-client side" + address + ":" + port + " - " + e.getMessage());
							break;
						}
					}
					TLog.v(TAG_TCP, "Listening Server Reader Thread for "+address+" is about to CLOSED");
					socket.close();
				}
			} catch (IOException e) {
				//ErrorManager.addErrCom(address);
				TLog.e(TAG_TCP, "Error while reading incoming data from reader-client side"+
						address+": "+e.getMessage());
				e.printStackTrace();
			} finally {
				TLog.e(TAG_TCP, "Process　Server Reader Thread for "+address+" is about to SHUTDOWN ");
				processor.shutdown();
			}
		});
	}

	public void shutdown() {
		executor.shutdownNow();
		PingChecker.remove(address);
	}

}