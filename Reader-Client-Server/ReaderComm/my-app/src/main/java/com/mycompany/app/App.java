package com.mycompany.app;

import com.mycompany.app.comm.*;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {
            System.out.println("Hello World");
            int port = 51002;
            while (true)
            {
                var dto = new CommPackageDTO("02","03",0);
				dto.setTxSqNo("02");
				dto.setTxCmd("00");
				dto.setTxData("00"+"00"+"01");
                SendManager.sendAsync (dto, "192.168.254.45", port, true);
                try {
                    Thread.sleep (100);
                } catch (Exception e)
                {
                    System.out.println("Exception: "+e);
                }
            }
    }
}
