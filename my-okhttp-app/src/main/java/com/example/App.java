package com.example;
import com.example.connect.*;


public class App {
    public static void main(String[] args) throws Exception {
	ConnectHelper helper = ConnectHelper.getInstance();
	//LoginResponse res = helper.login("http:192.168.254.249:5174/login", "123453456", "abcdxyz");
	// System.out.println("accessToken: "+ res.accessToken);
	// System.out.println("expiresIn: "+ res.expiresIn);
	// System.out.println("tokenType: "+ res.tokenType);
	//boolean result = helper.fetchProfile("http:192.168.254.249:5173/profile/detail", 1511621, "Rasberry Pi5");
	boolean result = helper.uploadLogOkHttp("http:192.168.254.249:5173/upload-log?machineID=960", "log_20240908_091424_06717_001.tar.gz");
	System.out.println("Result: "+ result);
	//helper.rideGetOff();
    }
}
