
import java.io.IOException;
import java.net.NoRouteToHostException;
import java.net.Socket;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;


public class ClientController {

    static final String TAG_CLIENT = "CONNECT_CLIENT";
    
    public static void main(String[] args) {
            System.out.println("Hello World");
            var sc = new ClientController("127.0.0.1", 52102);
            while (true)
            {
                sc.setData (new byte[] {0x01, 0x02, 0x03});
                //sc.setData (new byte[] {});
                sc.send();
                try {
                    Thread.sleep (100);
                } catch (Exception e)
                {
                    System.out.println("Exception: "+e);
                }
            }
    }
      

    public ClientController(String IP, int port) {
        //Constructor
        this.IP = IP;
        this.port = port;
        var now = new Date();
        //var timeElapsed = now.getTime() - MainActivity.appStartTime.getTime();
        System.out.println( "From the time the app starts to the time the Client socket connection opens (socketの接続開始) ( IP: "+this.IP+ " port: " +port+")");
        executorServiceForSocket.submit(this::syncOpen);
    }

    public void doClose() {
        close();
    }
    public void setData(byte[] data) {
        this.data = data;
    }
    public void reopen() {
        asyncReopen();
    }
    //------------------ This is library, please contact to Email:khoi.nguyen.ts.lecip.jp
    // if having bug or want to maintenance---------------------//
    final ScheduledExecutorService executorServiceForSocket = Executors.newScheduledThreadPool(10);
    byte[] data = new byte[] {1,2,3,4,5};

    //------------------ ↓↓↓Here will create socket↓↓↓---------------------//
    Socket socket;
    String IP;
    int port;

    public String getIP() {//TODO: Here for JUnit Test. Remove after test
        return IP;
    }

    public int getPort() {
        return port;
    }
    ConnectionStatus connectionStatus = ConnectionStatus.UNKNOWN;
    DataStatus dataStatus = DataStatus.UNKNOWN;


    public ConnectionStatus getConnectionStatus() {
        return connectionStatus;
    }

    public void setConnectionStatus(ConnectionStatus connectionStatus) {
        this.connectionStatus = connectionStatus;
    }

    public void setDataStatus(DataStatus dataStatus) {
        this.dataStatus = dataStatus;
    }
    public DataStatus getDataStatus() {
        return dataStatus;
    }

    int failCount = 0;
    ScheduledFuture<?> retryConnectionSchedule;
    void stopRetryConnection() {
        if(retryConnectionSchedule != null) retryConnectionSchedule.cancel(true);
    }
    void syncOpen() {
        try {
            System.out.println( "Opening socket at (IP: " + IP + " Port: " + port + ")");
            connectionStatus = ConnectionStatus.CONNECTING;
            socket = new Socket(IP, port);
            socket.setSoTimeout(100000);
            System.out.println( "Socket opened " + socket.getRemoteSocketAddress());
            connectionStatus = ConnectionStatus.CONNECTED;
            System.out.println( "Opened socket at (IP: " + IP + " Port: " + port +
                    ") with bound: " + socket.isBound() +
                    " connected: " + socket.isConnected() +
                    " closed: " + socket.isClosed()
            );
        } catch (NoRouteToHostException e) {
            System.out.println( "No route to host: "+IP+" (attempted "+failCount+" times)");
            connectionStatus = ConnectionStatus.CONNECT_FAIL;
            if (failCount++ > 3){
                System.out.println( "Reset fail count over 3 times by ping unreachable. Skip to reopen socket");
                return;
            }
            close();
            stopRetryConnection();
            retryConnectionSchedule = executorServiceForSocket.schedule(this::syncOpen, 2, TimeUnit.SECONDS);
        } catch (IOException e) {
            System.out.println("Error while opening client socket: "+e+"  at "+IP);
            connectionStatus = ConnectionStatus.CONNECT_FAIL;
        }
    }
    void asyncReopen() {
        System.out.println( IP + " is preparing to reopen....");
        failCount = 0;
        stopRetryConnection();
        executorServiceForSocket.submit(()->{
            close();
            syncOpen();
        });
    }
    void send() {
        dataStatus = DataStatus.SENDING;
        try {
            if(socket == null) throw new RuntimeException();
            if (socket.isClosed()) {
                dataStatus = DataStatus.UNKNOWN;
                System.out.println( "Client Socket is closing at" + socket.getRemoteSocketAddress() + ". Please wait for next step to reset client socket..");
                //close();//current socket is having error.Remove strictly now
                throw new RuntimeException();
            }
            failCount = 0;
            socket.setSoTimeout(0);
            socket.getOutputStream().write(data);
            socket.getOutputStream().flush();
            System.out.println( "Socket write " + Arrays.toString(data) + "  to address " + socket.getRemoteSocketAddress());
            dataStatus = DataStatus.SEND_DONE;
            return;
        } catch (RuntimeException e) {
            System.out.println( "Client Socket is null at " + IP);
        } catch (IOException e) {
            System.out.println( "Error "+e+" while write data of client socket "+socket.getRemoteSocketAddress()+" at "+IP);
        } finally {
            if (failCount++ >= 20)
            {
                failCount = 0;
                System.out.println( "Socket is forcely restart at "+IP+" with " +failCount +" times");
                asyncReopen();
                dataStatus = DataStatus.UNKNOWN;
            }
        }
    }
    //------------------ ↓↓↓Here will close socket↓↓↓---------------------//
    void close() {
        System.out.println( "Socket of Client closed with IP: "+this.IP+ " port: " +port);
        if(socket == null) {
            return;
        }
        try {
            socket.getInputStream().close();
            socket.getOutputStream().flush();
            socket.getOutputStream().close();
            socket.close();
            socket = null;
            connectionStatus = ConnectionStatus.CLOSED;
        } catch (IOException e) {
            System.out.println( "Error while closing socket "+socket.getRemoteSocketAddress()+" : "+e);
        }
    }
}
