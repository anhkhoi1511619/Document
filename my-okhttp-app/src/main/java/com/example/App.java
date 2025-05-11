package com.example;
import com.example.connect.*;


public class App {
    public static void main(String[] args) throws Exception {
	ConnectHelper helper = ConnectHelper.getInstance();
	//LoginResponse res = helper.login("http:192.168.0.147:5173/login", "123453456", "abcdxyz");
	 //System.out.println("accessToken: "+ res.accessToken);
	// System.out.println("expiresIn: "+ res.expiresIn);
	// System.out.println("tokenType: "+ res.tokenType);
	//boolean result = helper.fetchProfile("http:192.168.0.147:5173/profile/detail", 1511621, "Rasberry Pi5");
	//System.out.println("result: "+ result);

	boolean isSuccessful = helper.uploadLogOkHttp("http:192.168.10.104:5173/upload-log?machineID=960", "log_20240908_091424_06717_001.tar.gz");
	//GetURLResponse result = helper.getURL("http://192.168.0.147:5173/geturl", "960");
	//System.out.println("URL: "+ result.url);
	//boolean isSuccessful = helper.uploadLogOkHttp(result.url, "my-okhttp-app.zip");
	//boolean isSuccessful = helper.download("http://192.168.0.147:5173/packages/01_9999_20240912_001.zip", "./");
	System.out.println("isSuccessful: "+ isSuccessful);
	//helper.rideGetOff();

	//MasterListResponse result = helper.masterListRes("http://192.168.0.147:5173/masterver", "960");
	//System.out.println("URL: "+ result.masterVersion.get(0).url);
    //boolean isSuccessful = helper.download(result.masterVersion.get(0).url, "./");
	//System.out.println("isSuccessful: "+ isSuccessful);

    }
}
