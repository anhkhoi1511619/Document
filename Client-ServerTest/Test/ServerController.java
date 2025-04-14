
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.InetSocketAddress;
import java.net.BindException;
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
            var sc = new ServerController(52102);
            //sc.handleHandShake();
            while (true)
            {
                try {
                    //if (sc.isHandShake())sc.receive();
                    sc.executeSyncReceive();
                    Thread.sleep (100);
                } catch (InterruptedException e)
                {
                   System.out.println("Exception: "+e);
                }
            }
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
            serverSocket = new ServerSocket(port);
            serverSocket.setSoTimeout(100000);
            serverSocket.setReceiveBufferSize(13000);
            socket = null;
            System.out.println( "Server is preparing to communication with port " + port);
            System.out.println("Opening server socket at ( Port: "+port+
                    ") with bound: "+serverSocket.isBound()+
                    " closed: "+serverSocket.isClosed());
            connectionStatus = ConnectionStatus.CONNECTED;
        }    
         catch (IOException e) {
            System.out.println( "Server is having error.... with port " + port + " detail error "+ e);
            e.printStackTrace();
            connectionStatus = ConnectionStatus.CONNECT_FAIL;
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
            handShake();
        }
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
    
    
    int failCount = 0;
    byte[] receive() {
        boolean isHandShake = false;
        dataStatus = DataStatus.UNKNOWN;
        byte[] emptyData = new byte[]{0};
        if(serverSocket == null ) {
            System.out.println( "Server Socket is null");
            return emptyData;
        }
         //if (!isHandShake) {
         //   System.out.println( "Waiting for handshaking, Skip receive data");
         //   return emptyData;
         //}
        try {
            if(serverSocket.isClosed()) {
                System.out.println( "Server Socket is closed");
                throw new RuntimeException("Server Socket is closed");
            }
            final int BUFFER_SIZE = 1024;
            byte[] buffer = new byte[BUFFER_SIZE];
            int size = 0;
            if(socket != null && socket.getInputStream().available() > 0) {// Network 
                //throw new RuntimeException("Server Input stream of socket is unavailable");
                isHandShake = true;
            } 
            failCount = 0;
            serverSocket.setSoTimeout(10);
             if (!isHandShake)
             {
                System.out.println( "Server Input stream of socket is unavailable");
                socket = serverSocket.accept(); 
             }
            if(socket != null) {
                if(socket.isClosed()) {
                    System.out.println( "Socket is closed");
                    throw new RuntimeException("Socket is closed");
                }
            }
            dataStatus = DataStatus.RECEIVING;
            while (socket.getInputStream().available() > 0) {
                size = socket.getInputStream().read(buffer);
            }
            byte[] rawData = Arrays.copyOfRange(buffer, 0, size);
            System.out.println( "Server is read.... with data " + rawData.length);
            dataStatus = DataStatus.RECEIVE_DONE;
            return rawData;
        } catch (Exception e) {
            System.out.println( "Server is having error "+ failCount + "times at port: " + port +" with detail error " + e);
        } finally {
                if(failCount++ == 20){
                    System.out.println( "Server is forcely handshake again with client at port "+port);
                    try {
                        socket = serverSocket.accept();
                    } catch (Exception e) {
                        System.out.println( "Server is having unknown error.... port: " + port +" with detail error " + e +" at try-catch-finally()");
                    }
                    failCount = 0;
                }
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
