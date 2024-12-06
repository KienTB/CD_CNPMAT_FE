package com.example.intent.Api;

import com.example.intent.Model.Grade;
import com.example.intent.Model.Schedule;
import com.example.intent.Request.ChangePasswordRequest;
import com.example.intent.Request.GradeRequest;
import com.example.intent.Request.LoginRequest;
import com.example.intent.Model.Student;
import com.example.intent.Model.User;
import com.example.intent.RefreshTokenRequest;
import com.example.intent.Request.RegisterRequest;
import com.example.intent.Request.ScheduleRequest;
import com.example.intent.Token.AuthResponse;

import java.util.List;
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

    @GET("api/user/profile")
    Call<ApiResponse<Map<String, Object>>> getUserProfile(@Header("Authorization") String token);

    @GET("api/parent/student/{studentId}")
    Call<ApiResponse<Student>> getStudentById(
            @Header("Authorization") String token,
            @Path("studentId") int studentId);

    @GET("api/parent/schedule/{studentId}")
    Call<List<Schedule>> getSchedulesByStudentId(
            @Header("Authorization") String token,
            @Path("studentId") Long studentId);

    @GET("api/parent/grade/{studentId}")
    Call<List<Grade>> getGradesByStudentId(
            @Header("Authorization") String token,
            @Path("studentId") Long studentId);

    @GET("api/teacher/student/{teacherId}")
    Call<ApiResponse<List<Student>>> getStudentByTeacherId(
            @Header("Authorization") String token,
            @Path("teacherId") Long teacherId);

    @POST("api/teacher/add/grade")
    Call<ApiResponse<Grade>> addGrade(
            @Header("Authorization") String token,
            @Body GradeRequest gradeRequest);

    @POST("api/teacher/add/schedule")
    Call<ApiResponse<Schedule>> addSchedule(
            @Header("Authorization") String token,
            @Body ScheduleRequest scheduleRequest);

    @POST("api/auth/refresh")
    Call<ApiResponse<AuthResponse>> refreshToken(@Body RefreshTokenRequest refreshTokenRequest);
}
