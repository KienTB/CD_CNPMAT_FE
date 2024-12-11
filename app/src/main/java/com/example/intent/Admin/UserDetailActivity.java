package com.example.intent.Admin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.intent.Api.ApiResponse;
import com.example.intent.Api.ApiService;
import com.example.intent.Api.RetrofitClient;
import com.example.intent.R;
import com.example.intent.Token.TokenManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserDetailActivity extends AppCompatActivity {

    private TextView tvName, tvEmail, tvPhone, tvAddress, tvRole, tvUserId, tvTeacherId;
    private Button btnEdit, btnDelete;
    private ImageView imgBack;
    private TokenManager tokenManager;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);

        imgBack = findViewById(R.id.imgBack);
        tvName = findViewById(R.id.tvName);
        tvEmail = findViewById(R.id.tvEmail);
        tvPhone = findViewById(R.id.tvPhone);
        tvAddress = findViewById(R.id.tvAddress);
        tvRole = findViewById(R.id.tvRole);
        tvUserId = findViewById(R.id.tvUserId);
        tvTeacherId = findViewById(R.id.tvTeacherId);
        btnEdit = findViewById(R.id.btnEdit);
        btnDelete = findViewById(R.id.btnDelete);

        Intent intent = getIntent();
        Long userId = intent.getLongExtra("userId", -1);
        Long teacherId = intent.getLongExtra("teacherId", -1);
        String name = intent.getStringExtra("name");
        String email = intent.getStringExtra("email");
        String phone = intent.getStringExtra("phoneNumber");
        String address = intent.getStringExtra("address");
        String role = intent.getStringExtra("role");

        tvName.setText("Họ và tên: " + name);
        tvEmail.setText("Email: " + email);
        tvPhone.setText("Số điện thoại: " + phone);
        tvAddress.setText("Địa chỉ: " + address);
        tvRole.setText("Vai trò: " + role);
        tvUserId.setText("Mã người dùng: " + userId);
        tvTeacherId.setText("Mã giáo viên: " + teacherId);

        tokenManager = new TokenManager(this);
        apiService = RetrofitClient.getInstance().createService(ApiService.class);

        btnEdit.setOnClickListener(v -> {
            Intent editIntent = new Intent(UserDetailActivity.this, EditUserActivity.class);
            editIntent.putExtra("userId", userId);
            editIntent.putExtra("name", name);
            editIntent.putExtra("email", email);
            editIntent.putExtra("phone", phone);
            editIntent.putExtra("address", address);
            editIntent.putExtra("role", role);
            startActivity(editIntent);
        });

        btnDelete.setOnClickListener(v -> {
            if (userId != -1) {
                showDeleteConfirmationDialog(userId);
            } else {
                Toast.makeText(this, "Không tìm thấy người dùng!", Toast.LENGTH_SHORT).show();
            }
        });

        imgBack.setOnClickListener(v -> finish());
    }

    private void showDeleteConfirmationDialog(Long userId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Xác nhận xóa");
        builder.setMessage("Bạn có chắc chắn muốn xóa người dùng này không?");
        builder.setPositiveButton("Xóa", (dialog, which) -> deleteUser(userId));
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void deleteUser(Long userId) {
        String token = tokenManager.getToken();
        apiService = RetrofitClient.getInstance().createService(ApiService.class);

        Call<ApiResponse<String>> call = apiService.deleteUser("Bearer " + token, userId);

        call.enqueue(new Callback<ApiResponse<String>>() {
            @Override
            public void onResponse(Call<ApiResponse<String>> call, Response<ApiResponse<String>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(UserDetailActivity.this, "Xóa người dùng thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(UserDetailActivity.this, "Không thể xóa người dùng!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<String>> call, Throwable t) {
                Toast.makeText(UserDetailActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}