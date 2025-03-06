package com.example.intent.Admin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.intent.Api.ApiResponse;
import com.example.intent.Api.ApiService;
import com.example.intent.Api.RetrofitClient;
import com.example.intent.R;
import com.example.intent.Token.TokenManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationDetailActivity extends AppCompatActivity {
    private TextView tvTitle, tvContent;
    private Button btnEdit, btnDelete;
    private ImageView imgBack;
    private TokenManager tokenManager;
    private ApiService apiService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_notification_detail);

        tvTitle = findViewById(R.id.tvTitle);
        tvContent = findViewById(R.id.tvContent);
        btnEdit = findViewById(R.id.btnEdit);
        btnDelete = findViewById(R.id.btnDelete);
        imgBack = findViewById(R.id.imgBack);

        Intent intent = getIntent();
        Long notificationId = intent.getLongExtra("notificationId", -1);
        String title = intent.getStringExtra("title");
        String content = intent.getStringExtra("content");

        tvTitle.setText("Tiêu đề: " + title);
        tvContent.setText("Nội dung: " + content);

        tokenManager = new TokenManager(this);
        apiService = RetrofitClient.getInstance().createService(ApiService.class);

        btnEdit.setOnClickListener(v -> {
            Intent editIntent = new Intent(NotificationDetailActivity.this, EditNotificationActivity.class);
            editIntent.putExtra("notificationId", notificationId);
            editIntent.putExtra("title", title);
            editIntent.putExtra("content", content);
            startActivity(editIntent);
        });

        btnDelete.setOnClickListener(v -> {
            if (notificationId != -1) {
                showDeleteConfirmationDialog(notificationId);
            } else {
                Toast.makeText(this, "Không tìm thấy thông báo!", Toast.LENGTH_SHORT).show();
            }
        });

        imgBack.setOnClickListener(v -> finish());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void showDeleteConfirmationDialog(Long notificationId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Xác nhận xóa");
        builder.setMessage("Bạn có chắc chắn muốn xóa thông báo này không?");
        builder.setPositiveButton("Xóa", (dialog, which) -> deleteNotification(notificationId));
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void deleteNotification(Long notificationId) {
        String token = tokenManager.getToken();
        apiService = RetrofitClient.getInstance().createService(ApiService.class);

        Call<ApiResponse<String>> call = apiService.deleteUser("Bearer " + token, notificationId);

        call.enqueue(new Callback<ApiResponse<String>>() {
            @Override
            public void onResponse(Call<ApiResponse<String>> call, Response<ApiResponse<String>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(NotificationDetailActivity.this, "Xóa thông báo thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(NotificationDetailActivity.this, "Không thể xóa thông báo!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<String>> call, Throwable t) {
                Toast.makeText(NotificationDetailActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}