package com.example.intent.Admin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.example.intent.Model.User;
import com.example.intent.R;
import com.example.intent.Token.TokenManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserManagementActivity extends AppCompatActivity {
    private RecyclerView recyclerViewUsers;
    private UserAdapter userAdapter;
    private List<User> userList = new ArrayList<>();
    private TokenManager tokenManager;
    private ApiService apiService;
    private ImageView imgBackToExtension;
    private Button btnAddUser;
    private SearchView searchView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_management);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        recyclerViewUsers = findViewById(R.id.recyclerViewUsers);
        imgBackToExtension = findViewById(R.id.imgBackToExtension);
        btnAddUser = findViewById(R.id.btnAddUser);
        searchView = findViewById(R.id.searchView);

        userAdapter = new UserAdapter(userList, user -> {
            Intent intent = new Intent(UserManagementActivity.this, UserDetailActivity.class);
            intent.putExtra("userId", user.getUserId());
            intent.putExtra("phoneNumber", user.getPhoneNumber());
            intent.putExtra("email", user.getEmail());
            intent.putExtra("role", user.getRole());
            intent.putExtra("name", user.getName());
            intent.putExtra("address", user.getAddress());
            intent.putExtra("teacherId", user.getTeacherId());
            startActivity(intent);
        });

        recyclerViewUsers.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewUsers.setAdapter(userAdapter);

        tokenManager = new TokenManager(this);
        apiService = RetrofitClient.getInstance().createService(ApiService.class);

        long userId = tokenManager.getUserId();
        if (userId != -1) {
            fetchUsersByUserId(userId);
        } else {
            Toast.makeText(this, "Không tìm thấy ID giáo viên. Vui lòng đăng nhập lại.", Toast.LENGTH_SHORT).show();
        }

        imgBackToExtension.setOnClickListener(v -> finish());

        btnAddUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserManagementActivity.this, UserRegisterActivity.class);
                startActivity(intent);
            }
        });

        setupSearchView();
    }

    private void fetchUsersByUserId(long userId) {
        String token = tokenManager.getToken();

        apiService.getAllUsers("Bearer " + token)
                .enqueue(new Callback<ApiResponse<List<User>>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<List<User>>> call, Response<ApiResponse<List<User>>> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                            userList.clear();
                            userList.addAll(response.body().getData());
                            userAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(UserManagementActivity.this,
                                    "Tải danh sách người dùng thất bại: " + (response.body() != null ? response.body().getMessage() : "Lỗi không xác định"),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<List<User>>> call, Throwable t) {
                        Toast.makeText(UserManagementActivity.this, "Gọi API thất bại: " + t.getMessage(), Toast.LENGTH_SHORT).show();
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
                filterUser(newText);
                return true;
            }
        });
    }

    private void filterUser(String query) {
        List<User> filteredList = new ArrayList<>();
        for (User user : userList) {
            if (user.getName().toLowerCase().contains(query.toLowerCase()) ||
                    String.valueOf(user.getUserId()).contains(query)) {
                filteredList.add(user);
            }
        }
        userAdapter.updateList(filteredList);
    }

}