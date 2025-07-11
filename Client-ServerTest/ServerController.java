
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;



public class ServerController {
    static final String TAG_SERVER = "CONNECT_SERVER_SUB";
      public static void main(String[] args) {
            System.out.println("Hello World");
            var sc = new ServerController(52101);
            Executors.newScheduledThreadPool(10).scheduleAtFixedRate(() -> {
                sc.executeSyncReceive();
            },100, 100, TimeUnit.MILLISECONDS);
      }
      
    public enum DataStatus {
        UNKNOWN,
        SENDING,
        SEND_DONE,
        RECEIVING,
        RECEIVE_DONE;
    }
    
    public enum ConnectionStatus {
        CONNECTING,
        RESET_CONNECT,
        CONNECTED,
        CONNECT_FAIL,
        CLOSED,
        UNKNOWN
    }
  

    public ServerController(int port) {
        this.port = port;
        var now = new Date();
        var timeElapsed = now.getTime();
        System.out.println( "From the time the app starts to the time the Host(port: "+this.port+") is opened"+" in "+timeElapsed+"ms");
        executorServiceForSocketServer.submit(this::open);
    }

    public void doClose() {
        close();
    }
    public void reopen() {
        asyncReopen();
    }
    //------------------ This is library, please contact to Email:khoi.nguyen.ts.lecip.jp
    // if having bug or want to maintenance---------------------//
    final ExecutorService executorServiceForSocketServer = Executors.newSingleThreadExecutor();
    //------------------ ↓↓↓Here will create socket and server socket↓↓↓---------------------//
    ServerSocket serverSocket;
    Socket socket;
    int port;
    public int getPort() {
        return port;
    }

    ConnectionStatus connectionStatus = ConnectionStatus.UNKNOWN;

    public ConnectionStatus getConnectionStatus() {
        return connectionStatus;
    }

    public void setConnectionStatus(ConnectionStatus connectionStatus) {
        this.connectionStatus = connectionStatus;
    }

    public DataStatus getDataStatus() {
        return dataStatus;
    }

    public void setDataStatus(DataStatus dataStatus) {
        this.dataStatus = dataStatus;
    }
    void open() {
        try {
            System.out.println( "Opening socket at ( Port: "+port+ ")");
            serverSocket = new ServerSocket();
            serverSocket.setReuseAddress(true);
            serverSocket.setSoTimeout(10000);
            serverSocket.setReceiveBufferSize(13000);
            serverSocket.bind(new InetSocketAddress(port));
            socket = null;
            System.out.println( "Server is preparing to communication with port " + port);
            System.out.println("Opening server socket at ( Port: "+port+
                    ") with bound: "+serverSocket.isBound()+
                    " closed: "+serverSocket.isClosed());
            connectionStatus = ConnectionStatus.CONNECTED;
        // }
        // catch (BindException e) {
        //     System.out.println( "Server is having error.... with port " + port + " detail error "+ e);
        //     e.printStackTrace();
        //     connectionStatus = ConnectionStatus.CONNECT_FAIL;
        //     if (!isPortInUse(port)) {
        //         System.out.println("Server started on port " + port);
        //     } else {
        //         System.out.println("Port " + port + " is already in use. Reusing existing connection.");
        //         close();
        //         open();
        //     }
        } catch (IOException e) {
            System.out.println( "Server is having error.... with port " + port + " detail error "+ e);
            e.printStackTrace();
            connectionStatus = ConnectionStatus.CONNECT_FAIL;
        } 
    }

    public boolean isPortInUse(int port) {
        try (Socket tmpsocket = new Socket("localhost", port)) {
            socket = tmpsocket;
            return true; // Nếu kết nối thành công, nghĩa là cổng đang có server chạy
        } catch (IOException e) {
            return false; // Nếu không kết nối được, nghĩa là cổng chưa dùng
        }
    }
    boolean isHandShake = false;

    public boolean isHandShake() {
        return isHandShake;
    }

    public void handleHandShake() {
        executorServiceForSocketServer.submit(this::handShake);
    }
    void handShake() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                if (socket == null) {
                    isHandShake = false;
                    System.out.println( "Server is waiting for handshaking with client at port "+port);
                    socket = serverSocket.accept();
                    System.out.println( "Server successfully handshakes with client at port "+port);
                }
                isHandShake = true;
            }
        } catch (IOException e) {
            System.out.println( "Error while  waiting for handshaking with client at port "+port+": "+e.getMessage());
        }
    }
    void asyncReopen() {
        connectionStatus = ConnectionStatus.CONNECTING;
        executorServiceForSocketServer.submit(() -> {
            close();
            open();
        });
    }
    DataStatus dataStatus;

    /**
     * タイムアウト設定を導入し、長時間待機を回避する仕組みを実装する
     * @return data
     */
    byte[] executeSyncReceive() {
        byte[] data = new byte[]{0};
        Callable<byte[]> callable = this::receive;
        Future<?> future = executorServiceForSocketServer.submit(callable);
        try {
            data = (byte[]) future.get(100, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            System.out.println("The polling interval of SERVER( Port: "+port+")  is interrupted due to the total polling time exceeds 100ms, Exception: "+e);
            future.cancel(true);
        }
        return data;
    }
    byte[] receive() {
        boolean isHandShake = false;
        dataStatus = DataStatus.UNKNOWN;
        byte[] emptyData = new byte[]{0};
        if(serverSocket == null ) return emptyData;
        try {
            if(serverSocket.isClosed()) {
                System.out.println( "Server Socket is closed");
//                throw new RuntimeException("Server Socket is closed");
            }
            final int BUFFER_SIZE = 1024;
            byte[] buffer = new byte[BUFFER_SIZE];
            int size = 0;
            //System.out.println( "Server is waiting for connecting with port"+port);
            if(socket != null && socket.getInputStream().available() > 0) {
                System.out.println( "Server Input stream of socket is available ");
                isHandShake = true;
            }
            serverSocket.setSoTimeout(10);

            if(!isHandShake){
                System.out.println( "Server is waiting for handshaking with client at port "+port);
                socket = serverSocket.accept();
            //    return emptyData;
           }
            if(socket != null) {
                if(socket.isClosed()) {
                    System.out.println( "Socket is closed");
//                    throw new RuntimeException("Socket is closed");
                }
            }
            dataStatus = DataStatus.RECEIVING;
            System.out.println( "Server is connecting.... with port " + port);
            while (socket.getInputStream().available() > 0) {
                size = socket.getInputStream().read(buffer);
            }
            byte[] rawData = Arrays.copyOfRange(buffer, 0, size);
            System.out.println( "Server is read.... with length " + rawData.length + " data: " + Arrays.toString(rawData));
            dataStatus = DataStatus.RECEIVE_DONE;
            return rawData;
        } catch (SocketTimeoutException exception) {
            System.out.println( "Server is having error due to timeout "+exception+" port "+port);
        } catch (IOException e) {
            System.out.println( "Server is having error due to IO Stream "+e+" port "+port);
        } catch (Exception e) {
            System.out.println( "Server is having unknown error.... port: " + port +" with detail error " + e);
        }
        dataStatus = DataStatus.UNKNOWN;
        return emptyData;
    }
    void close() {
        System.out.println( "Socket of Host is closing with port: "+this.port);
        try {
            if(serverSocket != null) {
                serverSocket.close();
                serverSocket = null;
            }
            if(socket != null) {
                socket.getOutputStream().flush();
                socket.getOutputStream().close();
                socket.getInputStream().close();
                socket.close();
                socket = null;
            }
            connectionStatus = ConnectionStatus.CLOSED;
        }catch (IOException e) {
            System.out.println( "Socket or Socket Server can not close due to  "+ e);
            connectionStatus = ConnectionStatus.CONNECT_FAIL;
        }
    }

}
