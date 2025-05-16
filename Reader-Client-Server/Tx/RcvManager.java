import java.net.ServerSocket;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RcvManager {
	final ExecutorService executor = Executors.newSingleThreadExecutor();
	final String address;
	final int port;
	public static void main(String[] args) {
		System.out.println("Hello World");
		var rcvManager = new RcvManager ("192.168.254.45", 51002);
	}

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
					final int BUFFER_SIZE = 1024;
					byte[] buffer = new byte[BUFFER_SIZE];					
					while (!Thread.currentThread().isInterrupted()) {
						try {
							while (socket.getInputStream().available() > 0) {
								size = socket.getInputStream().read(buffer);
							} 
							if (size <= 0) continue;
							byte[] rawData = Arrays.copyOfRange(buffer, 0, size);
							System.out.println( "Server is read.... with size " + rawData.length + " data: ");
							for(int i=0; i< rawData.length ; i++) {
								System.out.print(rawData[i] +" ");
							}
							System.out.print("\n");
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
