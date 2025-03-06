package com.example.intent;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
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

import com.example.intent.Api.ApiResponse;
import com.example.intent.Api.ApiService;
import com.example.intent.Api.RetrofitClient;
import com.example.intent.Api.UserCheckResponse;
import com.example.intent.Request.OtpRequest;
import com.example.intent.Request.PhoneRequest;
import com.example.intent.Token.TokenManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordActivity extends AppCompatActivity {
    private ImageView imgBackToExtension;
    private Button btnNext;
    private EditText edtPhoneNumber;
    private ApiService apiService;
    private TokenManager tokenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_password);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        imgBackToExtension = findViewById(R.id.imgBackToExtension);
        btnNext = findViewById(R.id.btnNext);
        edtPhoneNumber = findViewById(R.id.edtPhoneNumber);

        apiService = RetrofitClient.getInstance().createService(ApiService.class);
        tokenManager = new TokenManager(this);

        imgBackToExtension.setOnClickListener(v -> finish());

        edtPhoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                btnNext.setEnabled(!s.toString().trim().isEmpty());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnNext.setOnClickListener(v -> {
            String phoneNumber = edtPhoneNumber.getText().toString().trim();
            if (!phoneNumber.isEmpty()) {
                checkPhoneAndSendOTP(phoneNumber);
            }
        });
    }

    private void checkPhoneAndSendOTP(String phoneNumber) {
        PhoneRequest phoneRequest = new PhoneRequest(phoneNumber);
        apiService.checkPhone(phoneRequest).enqueue(new Callback<ApiResponse<UserCheckResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<UserCheckResponse>> call, Response<ApiResponse<UserCheckResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserCheckResponse userData = response.body().getData();
                    sendOtp(userData.getUserId(), userData.getEmail(), phoneNumber);
                } else {
                    Toast.makeText(ForgotPasswordActivity.this, "Số điện thoại không tồn tại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<UserCheckResponse>> call, Throwable t) {
                Toast.makeText(ForgotPasswordActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendOtp(Long userId, String email, String phoneNumber) {
        OtpRequest otpRequest = new OtpRequest(userId, email);
        apiService.sendOtp(otpRequest).enqueue(new Callback<ApiResponse<String>>() {
            @Override
            public void onResponse(Call<ApiResponse<String>> call, Response<ApiResponse<String>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Intent intent = new Intent(ForgotPasswordActivity.this, ResetPasswordActivity.class);
                    intent.putExtra("phone_number", phoneNumber);
                    intent.putExtra("user_id", userId);
                    startActivity(intent);
                } else {
                    Toast.makeText(ForgotPasswordActivity.this, "Lỗi gửi OTP", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<String>> call, Throwable t) {
                Toast.makeText(ForgotPasswordActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
