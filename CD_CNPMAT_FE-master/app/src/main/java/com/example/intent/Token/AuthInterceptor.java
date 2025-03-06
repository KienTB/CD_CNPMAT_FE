package com.example.intent.Token;

import android.content.Context;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor{
    private TokenManager tokenManager;
    private Context context;

    public AuthInterceptor(TokenManager tokenManager) {
        this.tokenManager = tokenManager;
        this.context = context;
    }

    @Override
    public Response intercept(Interceptor.Chain chain) throws IOException {
        Request originalRequest = chain.request();
        Request.Builder requestBuilder = chain.request().newBuilder();

        String token = tokenManager.getToken();
        if (token != null) {
            requestBuilder.addHeader("Authorization", "Bearer " + token);
        }

        Response response = chain.proceed(requestBuilder.build());

        return chain.proceed(requestBuilder.build());
    }
}
