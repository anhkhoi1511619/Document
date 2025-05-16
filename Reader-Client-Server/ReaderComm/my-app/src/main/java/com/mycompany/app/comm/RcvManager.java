package com.mycompany.app.comm;

import java.net.ServerSocket;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RcvManager {
	final ExecutorService executor = Executors.newSingleThreadExecutor();
	final String address;
	final int port;

	public RcvManager (String address, int port)
	{
		this.address = address;
		this.port = port;
		executor.execute(() -> {
			System.out.println("Establishing reader communication thread for " + address + ":" + port);
			try {
				var serverSocket = new ServerSocket(port);
				serverSocket.setReceiveBufferSize(13000);
				byte[] data = new byte[30];
				// todo データ処理を呼ぶ
				while (!Thread.currentThread().isInterrupted()) {
					System.out.println("Waiting for connection for " + address + ":" + port);
					var socket = serverSocket.accept();
					System.out.println("Server Socket opened for " + address + ":" + port);
					var os = socket.getOutputStream();
					int size = 0;
					int offset = 0;
					while (!Thread.currentThread().isInterrupted()) {
						try {
							System.out.println("Server Socket read for " + address + ":" + port);
							byte[] trunk = new byte[50];
							Arrays.fill(trunk,(byte) 0);
							boolean firstTrunk = offset == 0;
							var trunkSize = socket.getInputStream().read(trunk);
							if(trunkSize <= 0) {
								break;
							}
							System.out.println( "Server is read.... with size " + trunkSize);
							while (socket.getInputStream().available() > 0) {
								size = socket.getInputStream().read(data);
							}
							byte[] rawData = Arrays.copyOfRange(data, 0, size);
							System.out.println( "Server is read.... with length " + rawData.length + " data: " + Arrays.toString(rawData));
							System.out.println( "Server is read.... with size " + trunkSize);

							//Thread.sleep(1000);
						} catch (Exception e) {
							System.out.println("Error while receiving data from reader-client side" + address + ":" + port + " - " + e.getMessage());
						}
					}	
				}
			} catch (Exception e) {
				System.out.println( "Error while receiving data from reader-client side" + address + ":" + port + " - " + e.getMessage());
			}
		});
	}
}
