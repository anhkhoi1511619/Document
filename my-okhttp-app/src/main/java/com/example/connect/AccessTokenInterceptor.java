package com.example.connect;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import java.net.HttpURLConnection;
public class AccessTokenInterceptor implements Interceptor {
    static String lastToken = "";
    static OkHttpClient client = new OkHttpClient().newBuilder()
            .build();
    static MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
    @Override
    public Response intercept(Chain chain) {
        Response response = null;
        try {
            Request request = newRequestWithAccessToken(chain.request(), lastToken);    
            response = chain.proceed(request);
    
            if (response.code() == HttpURLConnection.HTTP_UNAUTHORIZED) {
                synchronized (this) {
                    lastToken = loginAccessToken();
                    System.err.println( "new token: "+lastToken);
                    response.close();
                    return chain.proceed(newRequestWithAccessToken(request, lastToken));
                }
            }
        } catch (Exception e) {
        }
        return response;
    }

    private Request newRequestWithAccessToken(Request request, String accessToken) {
        return request.newBuilder()
                .header("Authorization", "Bearer " + accessToken)
                .build();
    }

    /**
     * ログイン後、アクセストークンを取得
     */
    public String loginAccessToken() {
        ConnectHelper helper = ConnectHelper.getInstance();
        LoginResponse res = helper.login("http:192.168.10.104:5173/login", "123453456", "abcdxyz");
        return res.accessToken;
    }
}
