package com.example.connect;

import okio.Okio;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.RequestBody;
import okhttp3.MediaType;
import okhttp3.Call;
import okhttp3.EventListener;
import okhttp3.Protocol;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.SocketTimeoutException;
import java.net.URL;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.*;
import java.util.function.Consumer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.File;


public class ConnectHelper
{
	static ConnectHelper instance = null;
	final MediaType MEDIA_TYPE_JSON = MediaType.get("application/json");
	final MediaType MT_BINARY = MediaType.parse("application/octet-stream");
	final MediaType MT_GZIP = MediaType.parse("application/gzip");

	OkHttpClient client;
	OkHttpClient clientWithoutAuth;
	final AccessTokenInterceptor tokenFetcher;
	EventListener eventListener;
	final ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);

    public ConnectHelper() {
		eventListener = new EventListener() {
            @Override
            public void connectStart(Call call, InetSocketAddress inetSocketAddress, Proxy proxy) {
                System.err.println("接続開始");
            }

            @Override
            public void connectFailed(Call call, InetSocketAddress inetSocketAddress, Proxy proxy, Protocol protocol, IOException ioe) {
                System.err.println("接続失敗: " + ioe.getMessage());
            }

            @Override
            public void requestBodyStart(Call call) {
               System.err.println("リクエスト書き込み開始");
            }

            @Override
            public void requestFailed(Call call, IOException ioe) {
                System.err.println("リクエスト書き込み失敗: " + ioe.getMessage());
            }

            @Override
            public void responseBodyStart(Call call) {
                System.err.println("レスポンス読み込み開始");
            }

            @Override
            public void responseFailed(Call call, IOException ioe) {
                System.err.println("レスポンス読み込み失敗: " + ioe.getMessage());
            }
        };
		tokenFetcher = new AccessTokenInterceptor();
		client = new OkHttpClient().newBuilder()
									.addInterceptor(tokenFetcher)
									.eventListener(eventListener)
									.build();
		clientWithoutAuth = new OkHttpClient().newBuilder()
									.eventListener(eventListener)
									.build();
    }


	
	public static synchronized ConnectHelper getInstance () {
		if (instance == null)
		{
			instance = new ConnectHelper();
		}
		return instance;
	}
	public void loginAsync(String url, String id, String password, Consumer<LoginResponse> callback)
	{
		executor.execute(()->callback.accept(login(url,id,password)));
	}
	public LoginResponse login(String url, String id, String password)
	{
		LoginRequest requestBody = new LoginRequest();
		requestBody.id = id;
		requestBody.password = password;
		RequestBody body = RequestBody.create(MEDIA_TYPE_JSON, requestBody.serialize().toString());
		System.err.println("Request: " + requestBody);	
		Request request = new Request.Builder()
				.url(url)
				.method("POST", body)
				.addHeader("Content-Type", "application/json")
				.build();
		try (Response response = client.newCall(request).execute()) {
            JSONObject object = new JSONObject(response.body().string());
			LoginResponse ret = new LoginResponse();
			ret.deserialize(object);
			System.err.println("Received: " + ret);
			response.close();
			return ret;
        }
        catch (Exception e)
        {
			
		}
		return new LoginResponse();
	}
	
	public Boolean fetchProfile(String url, int id, String machine)
	{
		ProfileRequest requestBody = new ProfileRequest();
		requestBody.id = id;
		requestBody.machine = machine;
		RequestBody body = RequestBody.create(MEDIA_TYPE_JSON, requestBody.serialize().toString());
		System.out.println("Request: " + requestBody);	
		Request request = new Request.Builder()
				.url(url)
				.method("POST", body)
				.addHeader("Content-Type", "application/json")
				.build();
		try (Response response = client.newCall(request).execute()) {
            //JSONObject object = new JSONObject(response.body().string());
			//LoginResponse ret = new LoginResponse();
			//ret.deserialize(object);
			System.err.println("Received: " + response.body().string());
			response.close();
			return true;
        }
        catch (Exception e)
        {
			
		}
		return false;
	}

	public GetURLResponse getURL(String url, String machineId)
	{
		GetURLRequest requestBody = new GetURLRequest();
		requestBody.machineId = machineId;
		RequestBody body = RequestBody.create(MEDIA_TYPE_JSON, requestBody.serialize().toString());
		System.out.println("Request: " + requestBody);	
		Request request = new Request.Builder()
				.url(url)
				.method("POST", body)
				.addHeader("Content-Type", "application/json")
				.build();
		try (Response response = client.newCall(request).execute()) {
            JSONObject object = new JSONObject(response.body().string());
			GetURLResponse ret = new GetURLResponse();
			ret.deserialize(object);
			System.err.println("Received: " + ret);
			response.close();
			return ret;
        }
        catch (Exception e)
        {
			System.err.println("Exception: " + e);
			
		}
		return new GetURLResponse();
	}
	public MasterListResponse masterListRes(String url, String machineId)
	{
		MasterListRequest requestBody = new MasterListRequest();
		requestBody.machineId = machineId;
		RequestBody body = RequestBody.create(MEDIA_TYPE_JSON, requestBody.serialize().toString());
		System.out.println("Request: " + requestBody);	
		Request request = new Request.Builder()
				.url(url)
				.method("POST", body)
				.addHeader("Content-Type", "application/json")
				.build();
		try (Response response = client.newCall(request).execute()) {
            JSONObject object = new JSONObject(response.body().string());
			MasterListResponse ret = new MasterListResponse();
			ret.deserialize(object);
			System.err.println("Received: " + ret);
			response.close();
			return ret;
        }
        catch (Exception e)
        {
			System.err.println("Exception: " + e);
			
		}
		return new MasterListResponse();
	}
	
	public void rideGetOff()
	{
		GetOffResponse ret = new GetOffResponse();
        GetOffRequest requestData = new GetOffRequest();
        byte[] buffer = requestData.serialize();
		System.err.println("Request: " + requestData);
        
        Request request = new Request.Builder()
            .url("http://192.168.10.104:5173/ride-getoff")
            .post(RequestBody.create(buffer, MT_BINARY))
            .build();
        try (Response response = client.newCall(request).execute()) {
			if (response.isSuccessful())
			{
				byte[] data = response.body().bytes();
				ret.deserialize(data);
				System.out.println("Received: " + ret + ", raw binary data = " + format(data));
			}
			else {
				System.err.println("Response is not successful");
			}
            response.close();
        }
        catch (Exception e)
        {
			
		}
	}

    public boolean uploadLogOkHttp(String url, String file) {
        try {
            if (url.isEmpty()) {
                return false;
            }
            var client = new OkHttpClient.Builder()
                    .connectTimeout(50, TimeUnit.SECONDS)
                    .writeTimeout(100, TimeUnit.SECONDS)
                    .readTimeout(50, TimeUnit.SECONDS)
                    .retryOnConnectionFailure(true)
                    .build();
            var data = Files.readAllBytes(Paths.get(file));
            var body = RequestBody.create(
                    data,
                    MT_GZIP);
            var request = new Request.Builder()
                    .url(url)
                    .put(body)
                    .header("Content-Length", String.valueOf(data.length))
                    .header("Content-Type", "application/gzip")
                    .build();
            var response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                System.out.println("Log uploaded successfully!");
            } else {
                System.out.println("Failed to upload file: " + response.code() + " " + response.message());
            }
            return response.isSuccessful();
        } catch (IOException e) {
            System.out.println( "error while making upload request, " + e.getMessage());
			e.printStackTrace();
        }
        return false;
    }

    public static String guessFileName(String urlString) {
        try {
            URL url = new URL(urlString);
            String path = url.getPath();
            if (path == null || path.isEmpty()) return "unknown";

            String fileName = path.substring(path.lastIndexOf('/') + 1);

            // Nếu không có tên file sau dấu "/", đặt tên mặc định
            if (fileName.isEmpty()) {
                return "download";
            }

            return fileName;
        } catch (Exception e) {
            return "download";
        }
    }

	public boolean download(String url, String saveTo) {
        if (url == null || !url.startsWith("http")) {
            System.out.println( "DOWNLOAD, Link not start with http (" + url + "), skip");
            return false;
        }
        try {
            var filename = guessFileName(url);
            System.out.println( "DOWNLOAD, file name = " + filename);
            var dir = new File(saveTo);
           	dir.mkdirs();
            dir.setReadable(true, false);
            dir.setWritable(true, false);
            dir.setExecutable(true, false);
            var file = new File(saveTo + "/" + filename);
            file.setReadable(true, false);
            file.setWritable(true, false);
            var request = new Request.Builder().url(url).build();
            var response = clientWithoutAuth.newCall(request).execute();
            if (response.body() == null) {
                return false;
            }
            var source = response.body().source();
            var sink = Okio.buffer(Okio.sink(file));
            var buffer = sink.getBuffer();
            final int DOWNLOAD_CHUNK_SIZE = 8 * 1024;
            while (source.read(buffer, DOWNLOAD_CHUNK_SIZE) != -1) {
                sink.emit();
            }
            sink.flush();
            sink.close();
            source.close();
            return true;
        } catch (IOException e) {
            System.out.println( "download failed " + e.getMessage());
        }
        return false;
    }

    public String format(byte[] input) {
		if(input == null) {
			return "";
		}
		StringBuilder ret = new StringBuilder();
		for (byte x: input) {
			ret.append(format(x));
		}
		return ret.toString();
	}
	public String format(byte input){
		return String.format("%02X", input);
	}
	
	
}
