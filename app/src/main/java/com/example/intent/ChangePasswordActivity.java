package com.example.intent;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.intent.Api.ApiResponse;
import com.example.intent.Api.ApiService;
import com.example.intent.Api.RetrofitClient;
import com.example.intent.Request.ChangePasswordRequest;
import com.example.intent.Token.AuthResponse;
import com.example.intent.Token.TokenManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePasswordActivity extends AppCompatActivity {
    private EditText edtCurrentPassword, edtNewPassword, edtConfirmPassword;
    private Button btnSave;
    private ImageView btnToggleCurrentPassword, btnToggleNewPassword, btnToggleConfirmPassword, imgBack;
    private boolean isCurrentPasswordVisible = false, isNewPasswordVisible = false, isConfirmPasswordVisible = false;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        edtCurrentPassword = findViewById(R.id.edtCurrentPassword);
        edtNewPassword = findViewById(R.id.edtNewPassword);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);
        btnSave = findViewById(R.id.btnSave);
        btnToggleCurrentPassword = findViewById(R.id.btnToggleCurrentPassword);
        btnToggleNewPassword = findViewById(R.id.btnToggleNewPassword);
        btnToggleConfirmPassword = findViewById(R.id.btnToggleConfirmPassword);
        imgBack = findViewById(R.id.imgBack);

        imgBack.setOnClickListener(v -> finish());

        apiService = RetrofitClient.getInstance().createService(ApiService.class);

        setupPasswordToggle(edtCurrentPassword, btnToggleCurrentPassword, () -> {
            isCurrentPasswordVisible = !isCurrentPasswordVisible;
            return isCurrentPasswordVisible;
        });

        setupPasswordToggle(edtNewPassword, btnToggleNewPassword, () -> {
            isNewPasswordVisible = !isNewPasswordVisible;
            return isNewPasswordVisible;
        });

        setupPasswordToggle(edtConfirmPassword, btnToggleConfirmPassword, () -> {
            isConfirmPasswordVisible = !isConfirmPasswordVisible;
            return isConfirmPasswordVisible;
        });

        btnSave.setOnClickListener(v -> {
            if (validateInput()) {
                changePassword();
            }
        });
    }

    private void setupPasswordToggle(EditText editText, ImageView toggleButton, VisibilityToggle visibilityToggle) {
        toggleButton.setOnClickListener(v -> {
            if (visibilityToggle.toggle()) {
                editText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                toggleButton.setImageResource(R.drawable.ic_eye_off);
            } else {
                editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                toggleButton.setImageResource(R.drawable.ic_eye_off);
            }
            editText.setSelection(editText.getText().length());
        });
    }

    private boolean validateInput() {
        String currentPassword = edtCurrentPassword.getText().toString().trim();
        String newPassword = edtNewPassword.getText().toString().trim();
        String confirmPassword = edtConfirmPassword.getText().toString().trim();

        if (TextUtils.isEmpty(currentPassword)) {
            Toast.makeText(this, "Vui lòng nhập mật khẩu hiện tại", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (TextUtils.isEmpty(newPassword)) {
            Toast.makeText(this, "Vui lòng nhập mật khẩu mới", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (newPassword.length() < 8) {
            Toast.makeText(this, "Mật khẩu phải có ít nhất 8 ký tự", Toast.LENGTH_SHORT).show();
            return false;
        }

        String passwordPattern = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@#$%^&+=!]).{8,}$";
        if (!newPassword.matches(passwordPattern)) {
            Toast.makeText(this, "Mật khẩu phải có ít nhất 8 ký tự, bao gồm chữ in hoa, chữ thường, số và ký tự đặc biệt", Toast.LENGTH_LONG).show();
            return false;
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(this, "Vui lòng xác nhận mật khẩu mới", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!newPassword.equals(confirmPassword)) {
            Toast.makeText(this, "Mật khẩu xác nhận không khớp", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void changePassword() {
        String currentPassword = edtCurrentPassword.getText().toString().trim();
        String newPassword = edtNewPassword.getText().toString().trim();
        String confirmPassword = edtConfirmPassword.getText().toString().trim();

        ChangePasswordRequest request = new ChangePasswordRequest(currentPassword, newPassword, confirmPassword);

        TokenManager tokenManager = new TokenManager(this);
        String token = tokenManager.getToken();

        if (token == null) {
            Toast.makeText(this, "Phiên đăng nhập đã hết hạn, vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
            return;
        }

        String authHeader = "Bearer " + token;

        apiService.changePassword(authHeader, request).enqueue(new Callback<ApiResponse<AuthResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<AuthResponse>> call, Response<ApiResponse<AuthResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(ChangePasswordActivity.this, "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(ChangePasswordActivity.this, "Có lỗi xảy ra, vui lòng thử lại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<AuthResponse>> call, Throwable t) {
                Toast.makeText(ChangePasswordActivity.this, "Lỗi kết nối, vui lòng thử lại", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @FunctionalInterface
    private interface VisibilityToggle {
        boolean toggle();
    }
}
