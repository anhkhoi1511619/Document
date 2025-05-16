package com.mycompany.app.comm;
import com.mycompany.app.utils.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Hashtable;


public class ConnectionManager {
    private Hashtable<String, Config> tcpConfigurationDB = new Hashtable<>();
    private static ConnectionManager INSTANCE;

    public static ConnectionManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ConnectionManager();
        }
        return INSTANCE;
    }

    /***
     * 概要：TCP接続設定保存
     *
     * @param ip    IPアドレス先
     * @param tcp   接続設定値
     */
    private void create(String ip, Config tcp) {
        tcpConfigurationDB.put(ip, tcp);
    }

    private void remove(String ip) {
        tcpConfigurationDB.remove(ip);
    }

    /***
     * 概要：TCP接続設定取得
     * @param ip    IPアドレス先
     * @return      TCP接続設定
     */
    public Config get(String ip) {
        return tcpConfigurationDB.get(ip);
    }

    public class Config {

        private Socket socket;
        private OutputStream outBuf;
        private InputStream inBuf;
        boolean isBusy;

        private int failCount;

        public Config(Socket s, OutputStream o, InputStream i) {
            socket = s;
            outBuf = o;
            inBuf = i;
            failCount = 0;
            isBusy = false;
        }

        public Socket getSocket() {
            return socket;
        }

        public OutputStream getOutBuf() {
            return outBuf;
        }

        public InputStream getInBuf() {
            return inBuf;
        }

        public int getFailureCount() {
            return failCount;
        }

        public void resetFailureCount() {
            failCount = 0;
        }

        public void increaseFailureCount() {
            failCount++;
        }

        public void setBusy(boolean busy) {
            isBusy = busy;
        }

        public boolean isBusy() {
            return isBusy;
        }
    }

    /***
     * 概要：TCP接続確立行い
     *
     * @param address   接続望むIPアドレス先
     * @param port      接続望むポート先
     * @return          TCP接続確立状態
     */
    public boolean createIfNotExist(String address, int port) {
        if (get(address) == null || get(address).getSocket().isClosed()) {
            try {
                final int TIME_OUT = 100;
                if(!InetAddress.getByName(address).isReachable(TIME_OUT)) {
                    System.out.println( "Address not reachable "+address);
                    return false;
                }
                Socket socket = new Socket(address, port);
                socket.setSoTimeout(TIME_OUT);
                var config = new Config(socket, socket.getOutputStream(), socket.getInputStream());
                create(address, config);
                System.out.println( "Openned socket "+socket.getRemoteSocketAddress().toString());
            } catch (IOException e) {
                System.out.println( "Error while openning socket "+address+":"+port+" - "+e.getMessage());
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    /***
     * 概要：TCP接続クローズ
     * @param address   IPアドレス
     */
    public void close(String address) {
        try {
            Config tcpCon = get(address);
            if (tcpCon != null) {
                tcpCon.getSocket().close();
                remove(address);
            }
        } catch ( Exception e) {
            System.out.println( "Error while closing socket "+ address);
            e.printStackTrace();
        }
    }

}
