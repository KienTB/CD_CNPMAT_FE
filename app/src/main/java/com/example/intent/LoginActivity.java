package com.example.intent;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private EditText edtPhoneNumber, edtPassword;
    private Button btnLogIn;
    private TextView txtForgotPassword;
    private ApiService apiService;

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
            loginUser(phoneNumber, password);
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

    private void loginUser(String phoneNumber, String password) {
        LoginRequest loginRequest = new LoginRequest(phoneNumber, password);

        apiService.login(loginRequest).enqueue(new Callback<ApiResponse<AuthResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<AuthResponse>> call, Response<ApiResponse<AuthResponse>> response) {
                Log.d("LoginActivity", "Response Code: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<AuthResponse> apiResponse = response.body();

                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        AuthResponse authResponse = apiResponse.getData();

                        Log.d("LoginActivity", "Auth Token: " + authResponse.getToken());
                        Log.d("LoginActivity", "User Role: " + (authResponse.getRole() != null ? authResponse.getRole() : "No User Object"));
                        Log.d("LoginActivity", "Teacher ID: " + authResponse.getTeacherId());

                        TokenManager tokenManager = new TokenManager(LoginActivity.this);
                        tokenManager.saveToken(authResponse.getToken());

                        if (authResponse.getRole() != null) {
                            tokenManager = new TokenManager(LoginActivity.this);
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
                    Log.e("LoginActivity", "Error: " + response.code() + ", Message: " + response.message());
                    Toast.makeText(LoginActivity.this, "Error: " + response.code(), Toast.LENGTH_SHORT).show();
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

        switch(role.toLowerCase()) {
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
        Log.d("LoginActivity", "Navigation completed successfully");
        finish();
    }
}
