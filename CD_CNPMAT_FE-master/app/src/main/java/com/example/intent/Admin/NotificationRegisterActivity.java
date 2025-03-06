package com.example.intent.Admin;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.intent.Api.ApiResponse;
import com.example.intent.Api.ApiService;
import com.example.intent.Api.RetrofitClient;
import com.example.intent.Model.Notification;
import com.example.intent.R;
import com.example.intent.Request.NotificationRegisterRequest;
import com.example.intent.Token.TokenManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationRegisterActivity extends AppCompatActivity {

    private EditText etNotificationTitle, etNotificationContent;
    private Button btnSubmitNotification;
    private ImageView imgBackToExtension;
    private TokenManager tokenManager;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_register);

        etNotificationTitle = findViewById(R.id.etNotificationTitle);
        etNotificationContent = findViewById(R.id.etNotificationContent);
        btnSubmitNotification = findViewById(R.id.btnSubmitNotification);
        imgBackToExtension = findViewById(R.id.imgBackToExtension);

        imgBackToExtension.setOnClickListener(v -> finish());

        tokenManager = new TokenManager(this);
        apiService = RetrofitClient.getInstance().createService(ApiService.class);

        btnSubmitNotification.setOnClickListener(view -> processFormFields());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void processFormFields() {
        if (!validateTitle() || !validateContent()) {
            return;
        }

        String title = etNotificationTitle.getText().toString();
        String content = etNotificationContent.getText().toString();
        Long userId = tokenManager.getUserId();

        NotificationRegisterRequest notificationRegisterRequest = new NotificationRegisterRequest(title, content, userId);
        registerNotification(notificationRegisterRequest);
    }

    private void registerNotification(NotificationRegisterRequest notificationRegisterRequest) {
        String token = tokenManager.getToken();
        Call<ApiResponse<Notification>> call = apiService.registerNotification("Bearer " + token, notificationRegisterRequest);

        call.enqueue(new Callback<ApiResponse<Notification>>() {
            @Override
            public void onResponse(Call<ApiResponse<Notification>> call, Response<ApiResponse<Notification>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        showSuccessDialog();
                    } else {
                        Toast.makeText(NotificationRegisterActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(NotificationRegisterActivity.this, "Registration failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Notification>> call, Throwable t) {
                Toast.makeText(NotificationRegisterActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showSuccessDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Đăng ký thông báo thành công")
                .setMessage("Thông báo của bạn đã được đăng ký thành công!")
                .setPositiveButton("OK", (dialog, which) -> {
                    Intent intent = new Intent(NotificationRegisterActivity.this, NotificationManagementActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    finish();
                })
                .show();
    }

    private boolean validateTitle() {
        String title = etNotificationTitle.getText().toString();
        if (title.isEmpty()) {
            etNotificationTitle.setError("Tiêu đề không được để trống");
            return false;
        } else {
            etNotificationTitle.setError(null);
            return true;
        }
    }

    private boolean validateContent() {
        String content = etNotificationContent.getText().toString();
        if (content.isEmpty()) {
            etNotificationContent.setError("Nội dung không được để trống");
            return false;
        } else {
            etNotificationContent.setError(null);
            return true;
        }
    }
}
