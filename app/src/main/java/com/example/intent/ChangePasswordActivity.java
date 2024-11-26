package com.example.intent;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

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
    private ImageView imgBack;
    private EditText edtCurrentPassword, edtNewPassword, edtConfirmPassword;
    private Button btnSave;
    private ApiService apiService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_change_password);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        imgBack = findViewById(R.id.imgBack);
        edtCurrentPassword = findViewById(R.id.edtCurrentPassword);
        edtNewPassword = findViewById(R.id.edtNewPassword);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);
        btnSave = findViewById(R.id.btnSave);

        apiService = RetrofitClient.getInstance().createService(ApiService.class);

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInput()) {
                    changePassword();
                }
            }
        });
    }

    private boolean validateInput() {
        String currentPassword = edtCurrentPassword.getText().toString().trim();
        String newPassword = edtNewPassword.getText().toString().trim();
        String confirmPassword = edtConfirmPassword.getText().toString().trim();

        if (TextUtils.isEmpty(currentPassword)) {
            edtCurrentPassword.setError("Vui lòng nhập mật khẩu hiện tại");
            return false;
        }

        if (TextUtils.isEmpty(newPassword)) {
            edtNewPassword.setError("Vui lòng nhập mật khẩu mới");
            return false;
        }

        if (newPassword.length() < 8) {
            edtNewPassword.setError("Mật khẩu phải có ít nhất 8 ký tự");
            return false;
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            edtConfirmPassword.setError("Vui lòng xác nhận mật khẩu mới");
            return false;
        }

        if (!newPassword.equals(confirmPassword)) {
            edtConfirmPassword.setError("Mật khẩu xác nhận không khớp");
            return false;
        }
        return true;
    }

    private void changePassword() {
    String currentPassword = edtCurrentPassword.getText().toString().trim();
    String newPassword = edtNewPassword.getText().toString().trim();
    String confirmPassword = edtConfirmPassword.getText().toString().trim();

    ChangePasswordRequest request = new ChangePasswordRequest(
            currentPassword,
            newPassword,
            confirmPassword
    );

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

            if (response.isSuccessful() && response.body() != null) {
                if (response.body().isSuccess()) {
                    Toast.makeText(ChangePasswordActivity.this,
                            "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(ChangePasswordActivity.this,
                            response.body().getMessage(), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(ChangePasswordActivity.this,
                        "Có lỗi xảy ra, vui lòng thử lại", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onFailure(Call<ApiResponse<AuthResponse>> call, Throwable t) {
            Toast.makeText(ChangePasswordActivity.this,
                    "Lỗi kết nối, vui lòng thử lại", Toast.LENGTH_SHORT).show();
        }
    });
}

}