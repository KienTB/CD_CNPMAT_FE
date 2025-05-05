package com.example.intent.Admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.intent.Api.ApiResponse;
import com.example.intent.Api.ApiService;
import com.example.intent.Api.RetrofitClient;
import com.example.intent.Model.Notification;
import com.example.intent.Adapter.NotificationAdapter;
import com.example.intent.R;
import com.example.intent.Token.TokenManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationManagementActivity extends AppCompatActivity {
    private ImageView imgBackToExtension;
    private RecyclerView recyclerViewNotifications;
    private NotificationAdapter notificationAdapter;
    private List<Notification> notificationList = new ArrayList<>();
    private TokenManager tokenManager;
    private ApiService apiService;
    private Button btnAddNotification;
    private SearchView searchView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_notification_management);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recyclerViewNotifications = findViewById(R.id.recyclerViewNotifications);
        imgBackToExtension = findViewById(R.id.imgBackToExtension);
        btnAddNotification = findViewById(R.id.btnAddNotification);
        searchView = findViewById(R.id.searchView);

        notificationAdapter = new NotificationAdapter(notificationList, notification -> {
            Intent intent = new Intent(NotificationManagementActivity.this, NotificationDetailActivity.class);
            intent.putExtra("notificationId", notification.getNotificationId());
            intent.putExtra("title", notification.getTitle());
            intent.putExtra("content", notification.getContent());
            startActivity(intent);
        });

        recyclerViewNotifications.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewNotifications.setAdapter(notificationAdapter);

        tokenManager = new TokenManager(this);
        apiService = RetrofitClient.getInstance().createService(ApiService.class);

        long userId = tokenManager.getUserId();
        if (userId != -1) {
            fetchNotificationsByUserId(userId);
        } else {
            Toast.makeText(this, "Không tìm thấy ID giáo viên. Vui lòng đăng nhập lại.", Toast.LENGTH_SHORT).show();
        }

        imgBackToExtension.setOnClickListener(v -> finish());

        btnAddNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NotificationManagementActivity.this, NotificationRegisterActivity.class);
                startActivity(intent);
            }
        });

        setupSearchView();
    }

    private void fetchNotificationsByUserId(long userId) {
        String token = tokenManager.getToken();

        apiService.getNotifications("Bearer " + token)
                .enqueue(new Callback<ApiResponse<List<Notification>>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<List<Notification>>> call, Response<ApiResponse<List<Notification>>> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                            notificationList.clear();
                            notificationList.addAll(response.body().getData());
                            notificationAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(NotificationManagementActivity.this,
                                    "Tải danh sách thông báo thất bại: " + (response.body() != null ? response.body().getMessage() : "Lỗi không xác định"),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<List<Notification>>> call, Throwable t) {
                        Toast.makeText(NotificationManagementActivity.this, "Gọi API thất bại: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterNotification(newText);
                return true;
            }
        });
    }

    private void filterNotification(String query) {
        List<Notification> filteredList = new ArrayList<>();
        for (Notification notification : notificationList) {
            if (notification.getTitle().toLowerCase().contains(query.toLowerCase()) ||
                    String.valueOf(notification.getNotificationId()).contains(query)) {
                filteredList.add(notification);
            }
        }
        notificationAdapter.updateData(filteredList);
    }

}