package com.example.intent.Api;

import com.example.intent.Model.Grade;
import com.example.intent.Model.Notification;
import com.example.intent.Model.Schedule;
import com.example.intent.Model.DataStudent;
import com.example.intent.Request.ChangePasswordRequest;
import com.example.intent.Request.GradeRequest;
import com.example.intent.Request.LoginRequest;
import com.example.intent.Model.Student;
import com.example.intent.Model.User;
import com.example.intent.RefreshTokenRequest;
import com.example.intent.Request.NotificationRegisterRequest;
import com.example.intent.Request.OtpRequest;
import com.example.intent.Request.OtpVerificationRequest;
import com.example.intent.Request.PhoneRequest;
import com.example.intent.Request.RegisterRequest;
import com.example.intent.Request.ResetPasswordRequest;
import com.example.intent.Request.ScheduleRequest;
import com.example.intent.Request.StudentRegisterRequest;
import com.example.intent.Request.UpdateGradeRequest;
import com.example.intent.Request.UpdateNotificationRequest;
import com.example.intent.Request.UpdateScheduleRequest;
import com.example.intent.Request.UpdateStudentRequest;
import com.example.intent.Request.UpdateUserRequest;
import com.example.intent.Token.AuthResponse;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiService {
    @Headers({
            "Accept: application/json",
            "Content-Type: application/json"
    })

    @POST("api/user/login")
    Call<ApiResponse<AuthResponse>> login(@Body LoginRequest loginRequest);

    @GET("api/user/profile")
    Call<ApiResponse<Map<String, Object>>> getUserProfile(
            @Header("Authorization") String token);

    @POST("api/admin/user/register")
    Call<ApiResponse<User>> register(
            @Header("Authorization") String token,
            @Body RegisterRequest registerRequest);

    @POST("api/user/change-password")
    Call<ApiResponse<AuthResponse>> changePassword(
            @Header("Authorization") String token,
            @Body ChangePasswordRequest changePasswordRequest);

    @GET("api/user/parent/id")
    Call<ApiResponse<List<Long>>> getAllUserIdParent(@Header("Authorization") String token);

    @GET("api/user/teacher/id")
    Call<ApiResponse<List<Long>>> getAllUserIdTeacher(@Header("Authorization") String token);

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
    Call<ApiResponse<List<DataStudent>>> getStudentByTeacherId(
            @Header("Authorization") String token,
            @Path("teacherId") Long teacherId);

    @GET("api/teacher/grade/{studentId}")
    Call<List<Grade>> getGradesForTeacher(
            @Header("Authorization") String token,
            @Path("studentId") Long studentId
    );

    @POST("api/teacher/add/grade")
    Call<ApiResponse<Grade>> addGrade(
            @Header("Authorization") String token,
            @Body GradeRequest gradeRequest);

    @PUT("api/teacher/edit/grade/{studentId}/{gradeId}")
    Call<ApiResponse<Grade>> updateGrade(
            @Header("Authorization") String token,
            @Path("studentId") Long studentId,
            @Path("gradeId") Long gradeId,
            @Body UpdateGradeRequest updateGradeRequest
    );

    @DELETE("api/teacher/delete/grade/{studentId}/{gradeId}")
    Call<ApiResponse<Void>> deleteGrade(
            @Header("Authorization") String token,
            @Path("studentId") Long studentId,
            @Path("gradeId") Long gradeId
    );

    @GET("api/teacher/schedule/{studentId}")
    Call<List<Schedule>> getSchedulesForTeacher(
            @Header("Authorization") String token,
            @Path("studentId") Long studentId
    );

    @POST("api/teacher/add/schedule")
    Call<ApiResponse<Schedule>> addSchedule(
            @Header("Authorization") String token,
            @Body ScheduleRequest scheduleRequest);

    @PUT("api/teacher/edit/schedule/{studentId}/{scheduleId}")
    Call<ApiResponse<Schedule>> updateSchedule(
            @Header("Authorization") String token,
            @Path("studentId") Long studentId,
            @Path("scheduleId") Long scheduleId,
            @Body UpdateScheduleRequest updateScheduleRequest
    );

    @DELETE("api/teacher/delete/schedule/{studentId}/{scheduleId}")
    Call<ApiResponse<Void>> deleteSchedule(
            @Header("Authorization") String token,
            @Path("studentId") Long studentId,
            @Path("scheduleId") Long scheduleId
    );

    @GET("api/get/notifications")
    Call<ApiResponse<List<Notification>>> getNotifications(
            @Header("Authorization") String token);

    @GET("api/admin/get/all/users")
    Call<ApiResponse<List<User>>> getAllUsers(
            @Header("Authorization") String token);

    @GET("api/admin/get/all/students")
    Call<StudentApiResponse<List<DataStudent>>> getAllStudents(
            @Header("Authorization") String token);

    @POST("api/admin/register/student")
    Call<ApiResponse<Student>> registerStudent(
            @Header("Authorization") String token,
            @Body StudentRegisterRequest studentRegisterRequest);

    @PUT("api/admin/update/user/{userId}")
    Call<ApiResponse<User>> updateUser(
            @Header("Authorization") String token,
            @Path("userId") Long userId,
            @Body UpdateUserRequest updateUserRequest);

    @DELETE("api/admin/delete/user/{userId}")
    Call<ApiResponse<String>> deleteUser(
            @Header("Authorization") String token,
            @Path("userId") Long userId);

    @PUT("api/admin/update/student/{studentId}")
    Call<ApiResponse<Student>> updateStudent(
            @Header("Authorization") String token,
            @Path("studentId") Long studentId,
            @Body UpdateStudentRequest updateStudentRequest);

    @DELETE("api/admin/delete/student/{studentId}")
    Call<ApiResponse<String>> deleteStudent(
            @Header("Authorization") String token,
            @Path("studentId") Long studentId);

    @POST("api/admin/register/notification")
    Call<ApiResponse<Notification>> registerNotification(
            @Header("Authorization") String token,
            @Body NotificationRegisterRequest notificationRegisterRequest);

    @PUT("api/admin/update/notification/{notificationId}")
    Call<ApiResponse<Notification>> updateNotification(
            @Header("Authorization") String token,
            @Path("notificationId") Long notificationId,
            @Body UpdateNotificationRequest updateNotificationRequest);

    @DELETE("api/admin/delete/notification/{notificationId}")
    Call<ApiResponse<String>> deleteNotification(
            @Header("Authorization") String token,
            @Path("notificationId") Long notificationId);

    @POST("/api/otp/check-phone")
    Call<ApiResponse<UserCheckResponse>> checkPhone(@Body PhoneRequest request);

    @POST("api/otp/send")
    Call<ApiResponse<String>> sendOtp(@Body OtpRequest otpRequest);

    @POST("api/otp/verify")
    Call<ApiResponse<String>> verifyOtp(@Body OtpVerificationRequest otpVerificationRequest);

    @POST("api/otp/reset-password")
    Call<ApiResponse<String>> resetPassword(@Body ResetPasswordRequest resetPasswordRequest);


    @POST("api/auth/refresh")
    Call<ApiResponse<AuthResponse>> refreshToken(@Body RefreshTokenRequest refreshTokenRequest);
}
