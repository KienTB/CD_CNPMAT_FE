package com.example.intent.Admin;

import android.os.Bundle;
import android.text.TextUtils;
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
import com.example.intent.Model.Notification;
import com.example.intent.Model.User;
import com.example.intent.R;
import com.example.intent.Request.UpdateNotificationRequest;
import com.example.intent.Request.UpdateUserRequest;
import com.example.intent.Token.TokenManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditNotificationActivity extends AppCompatActivity {
    private EditText etTitle, etContent;
    private Button btnSubmitNotification;
    private TokenManager tokenManager;
    private ApiService apiService;
    private ImageView imgBack;
    private Long notificationId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_notification);

        etTitle = findViewById(R.id.etTitle);
        etContent = findViewById(R.id.etContent);
        btnSubmitNotification = findViewById(R.id.btnSubmitNotification);
        imgBack = findViewById(R.id.imgBack);

        String title = getIntent().getStringExtra("title");
        String content = getIntent().getStringExtra("content");
        notificationId = getIntent().getLongExtra("notificationId", -1);

        etTitle.setText(title);
        etContent.setText(content);

        tokenManager = new TokenManager(this);
        apiService = RetrofitClient.getInstance().createService(ApiService.class);

        btnSubmitNotification.setOnClickListener(v -> {
            String updatedTitle = etTitle.getText().toString().trim();
            String updatedContent = etContent.getText().toString().trim();

            if (TextUtils.isEmpty(updatedTitle)) {
                Toast.makeText(this, "Vui lòng nhập tiu đề!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(updatedContent)) {
                Toast.makeText(this, "Vui lòng nhập nội dung!", Toast.LENGTH_SHORT).show();
                return;
            }

            updateNotificationInfo(updatedTitle, updatedContent);
        });

        imgBack.setOnClickListener(v -> finish());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void updateNotificationInfo(String title, String content) {
        UpdateNotificationRequest request = new UpdateNotificationRequest(title, content);
        String token = "Bearer " + tokenManager.getToken();

        apiService.updateNotification(token, notificationId, request).enqueue(new Callback<ApiResponse<Notification>>() {
            @Override
            public void onResponse(Call<ApiResponse<Notification>> call, Response<ApiResponse<Notification>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(EditNotificationActivity.this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(EditNotificationActivity.this, "Cập nhật thất bại!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Notification>> call, Throwable t) {
                Toast.makeText(EditNotificationActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}