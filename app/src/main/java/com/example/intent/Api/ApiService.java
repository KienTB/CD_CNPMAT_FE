package com.example.intent.Api;

import com.example.intent.LoginRequest;
import com.example.intent.Model.Student;
import com.example.intent.Model.User;
import com.example.intent.RegisterRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {
    @Headers({
            "Accept: application/json",
            "Content-Type: application/json"
    })
    @POST("api/user/login")
    Call<ApiResponse<User>> login(@Body LoginRequest loginRequest);
    @POST("api/user/register")
    Call<ApiResponse<User>> register(@Body RegisterRequest registerRequest);
    @GET("api/students/{studentId}")
    Call<ApiResponse<Student>> getStudentById(@Path("studentId") int studentId);
}
