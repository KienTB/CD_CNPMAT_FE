package com.example.intent.Api;

import com.example.intent.Request.ChangePasswordRequest;
import com.example.intent.Request.LoginRequest;
import com.example.intent.Model.Student;
import com.example.intent.Model.User;
import com.example.intent.RefreshTokenRequest;
import com.example.intent.Request.RegisterRequest;
import com.example.intent.Token.AuthResponse;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {
    @Headers({
            "Accept: application/json",
            "Content-Type: application/json"
    })
    @POST("api/user/login")
    Call<ApiResponse<AuthResponse>> login(@Body LoginRequest loginRequest);
    @POST("api/user/register")
    Call<ApiResponse<User>> register(@Body RegisterRequest registerRequest);
    @POST("api/user/change-password")
    Call<ApiResponse<AuthResponse>> changePassword(
            @Header("Authorization") String token,
            @Body ChangePasswordRequest changePasswordRequest);
    @GET("api/students/{studentId}")
    Call<ApiResponse<Student>> getStudentById(
            @Header("Authorization") String token,
            @Path("studentId") int studentId);
    @GET("api/user/profile")
    Call<ApiResponse<Map<String, Object>>> getUserProfile(@Header("Authorization") String token);
    @POST("api/auth/refresh")
    Call<ApiResponse<AuthResponse>> refreshToken(@Body RefreshTokenRequest refreshTokenRequest);
}
