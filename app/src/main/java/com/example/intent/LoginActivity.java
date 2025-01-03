package com.example.intent;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.intent.Admin.AdminMainActivity;
import com.example.intent.Api.ApiResponse;
import com.example.intent.Api.ApiService;
import com.example.intent.Api.RetrofitClient;
import com.example.intent.Parent.ParentMainActivity;
import com.example.intent.Request.LoginRequest;
import com.example.intent.Teacher.TeacherMainActivity;
import com.example.intent.Token.AuthResponse;
import com.example.intent.Token.TokenManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private EditText edtPhoneNumber, edtPassword;
    private Button btnLogIn;
    private TextView txtForgotPassword;
    private ApiService apiService;

    private static final int MAX_ATTEMPTS = 10;
    private static final long MONITORING_PERIOD = 30000;
    private static final long LOCKOUT_DURATION = 10000;
    private List<Long> loginAttempts = new ArrayList<>();
    private Handler handler = new Handler();
    private CountDownTimer lockoutTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        edtPhoneNumber = findViewById(R.id.edtPhoneNumber);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogIn = findViewById(R.id.btnLogIn);
        txtForgotPassword = findViewById(R.id.txtForgotPassword);

        apiService = RetrofitClient.getInstance().createService(ApiService.class);

        btnLogIn.setOnClickListener(view -> {
            String phoneNumber = edtPhoneNumber.getText().toString();
            String password = edtPassword.getText().toString();
            if (phoneNumber.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Vui lòng điền vào tất cả các ô trống", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!isLoginThrottled()) {
                loginUser(phoneNumber, password);
            }
        });

        txtForgotPassword.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
            finish();
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void clearPreviousUserData() {
        TokenManager tokenManager = new TokenManager(LoginActivity.this);
        tokenManager.clearStudentData();

        getSharedPreferences("UserPrefs", MODE_PRIVATE).edit().clear().apply();

        Log.d("LoginActivity", "Dữ liệu người dùng trước đã được xóa.");
    }

    private boolean isLoginThrottled() {
        long currentTime = System.currentTimeMillis();
        loginAttempts.removeIf(timestamp ->
                currentTime - timestamp > MONITORING_PERIOD);

        loginAttempts.add(currentTime);

        if (loginAttempts.size() > MAX_ATTEMPTS) {
            btnLogIn.setEnabled(false);

            Toast.makeText(this,
                    "Bạn đã gửi quá nhiều yêu cầu trong 30s, vui lòng đợi 10s rồi thử lại!",
                    Toast.LENGTH_LONG).show();

            if (lockoutTimer != null) {
                lockoutTimer.cancel();
            }

            lockoutTimer = new CountDownTimer(LOCKOUT_DURATION, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    btnLogIn.setText("Đăng nhập (" + (millisUntilFinished / 1000) + "s)");
                }

                @Override
                public void onFinish() {
                    btnLogIn.setEnabled(true);
                    btnLogIn.setText("Đăng nhập");
                    loginAttempts.clear();
                }
            }.start();

            return true;
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (lockoutTimer != null) {
            lockoutTimer.cancel();
        }
        handler.removeCallbacksAndMessages(null);
    }

    private void loginUser(String phoneNumber, String password) {
        LoginRequest loginRequest = new LoginRequest(phoneNumber, password);

        apiService.login(loginRequest).enqueue(new Callback<ApiResponse<AuthResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<AuthResponse>> call, Response<ApiResponse<AuthResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<AuthResponse> apiResponse = response.body();

                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        AuthResponse authResponse = apiResponse.getData();

                        Log.d("LoginActivity", "Auth Token: " + authResponse.getToken());
                        Log.d("LoginActivity", "User Role: " + (authResponse.getRole() != null ? authResponse.getRole() : "No User Object"));
                        Log.d("LoginActivity", "Teacher ID: " + authResponse.getTeacherId());

                        TokenManager tokenManager = new TokenManager(LoginActivity.this);
                        tokenManager.saveToken(authResponse.getToken());
                        clearPreviousUserData();

                        if (authResponse.getRole() != null) {
                            if (authResponse.getTeacherId() != null) {
                                tokenManager.saveTeacherId(authResponse.getTeacherId());
                                Log.d("LoginActivity", "Saved Teacher ID: " + authResponse.getTeacherId());
                            } else {
                                Log.e("LoginActivity", "Teacher ID is null");
                            }
                            navigateBasedOnRole(authResponse.getRole());
                        }
                    } else {
                        String message = apiResponse.getMessage() != null ? apiResponse.getMessage() : "Login failed";
                        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (response.code() == 400) {
                        Toast.makeText(LoginActivity.this, "Số điện thoại hoặc mật khẩu không hợp lệ", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e("LoginActivity", "Error: " + response.code() + ", Message: " + response.message());
                        Toast.makeText(LoginActivity.this, "Lỗi: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<AuthResponse>> call, Throwable t) {
                Log.e("LoginActivity", "Login error: " + t.getMessage(), t);
                Toast.makeText(LoginActivity.this, "Network error. Please try again later.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateBasedOnRole(String role) {
        Intent intent;
        String logMessage = "Chào mừng, bạn đã đăng nhập thành công!";
        Log.d("LoginActivity", logMessage);

        Toast.makeText(LoginActivity.this, logMessage, Toast.LENGTH_SHORT).show();

        switch (role.toLowerCase()) {
            case "admin":
                Log.d("LoginActivity", "Admin login successful");
                intent = new Intent(LoginActivity.this, AdminMainActivity.class);
                break;
            case "teacher":
                Log.d("LoginActivity", "Teacher login successful");
                intent = new Intent(LoginActivity.this, TeacherMainActivity.class);
                break;
            case "parent":
                Log.d("LoginActivity", "Parent login successful");
                intent = new Intent(LoginActivity.this, ParentMainActivity.class);
                break;
            default:
                Log.e("LoginActivity", "Invalid role detected: " + role);
                Toast.makeText(this, "Vai trò không hợp lệ: " + role, Toast.LENGTH_SHORT).show();
                return;
        }
        startActivity(intent);
        finish();
    }
}
