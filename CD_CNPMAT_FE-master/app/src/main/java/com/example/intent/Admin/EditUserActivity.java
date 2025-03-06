package com.example.intent.Admin;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.intent.Api.ApiResponse;
import com.example.intent.Api.ApiService;
import com.example.intent.Api.RetrofitClient;
import com.example.intent.Model.User;
import com.example.intent.R;
import com.example.intent.Request.UpdateUserRequest;
import com.example.intent.Token.TokenManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditUserActivity extends AppCompatActivity {

    private EditText edtPhoneNumber, edtName, edtEmail, edtAddress;
    private Button btnConfirm;
    private TokenManager tokenManager;
    private ApiService apiService;
    private ImageView imgBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);

        edtPhoneNumber = findViewById(R.id.edtPhoneNumber);
        edtName = findViewById(R.id.edtName);
        edtEmail = findViewById(R.id.edtEmail);
        edtAddress = findViewById(R.id.edtAddress);
        btnConfirm = findViewById(R.id.btnConfirm);
        imgBack = findViewById(R.id.imgBack);

        String name = getIntent().getStringExtra("name");
        String email = getIntent().getStringExtra("email");
        String phone = getIntent().getStringExtra("phone");
        String address = getIntent().getStringExtra("address");
        String role = getIntent().getStringExtra("role");
        Long userId = getIntent().getLongExtra("userId", -1);

        edtPhoneNumber.setText(phone);
        edtName.setText(name);
        edtEmail.setText(email);
        edtAddress.setText(address);

        tokenManager = new TokenManager(this);
        apiService = RetrofitClient.getInstance().createService(ApiService.class);

        edtAddress.setOnClickListener(v -> showAddressSelectionDialog());

        btnConfirm.setOnClickListener(v -> {
            String updatedName = edtName.getText().toString().trim();
            String updatedEmail = edtEmail.getText().toString().trim();
            String updatedPhone = edtPhoneNumber.getText().toString().trim();
            String updatedAddress = edtAddress.getText().toString().trim();

            if (TextUtils.isEmpty(updatedName)) {
                Toast.makeText(this, "Vui lòng nhập họ và tên!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!updatedEmail.contains("@") || !updatedEmail.contains(".")) {
                Toast.makeText(this, "Email không hợp lệ!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(updatedPhone) || !updatedPhone.matches("\\d+")) {
                Toast.makeText(this, "Số điện thoại chỉ được chứa số!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(updatedAddress)) {
                Toast.makeText(this, "Vui lòng nhập địa chỉ!", Toast.LENGTH_SHORT).show();
                return;
            }

            updateUserInfo(userId, updatedPhone, updatedEmail, updatedName, updatedAddress);
        });

        imgBack.setOnClickListener(v -> finish());
    }

    private void showAddressSelectionDialog() {
        String[] classes = {"Phú La, Hà Đông, Hà Nội", "Kiến Hưng, Hà Đông, Hà Nội", "La Khê, Hà Đông, Hà Nội", "Mộ Lao, Hà Đông, Hà Nội", "Nguyễn Trãi, Hà Đông, Hà Nội", "Quang Trung, Hà Đông, Hà Nội", "Vạn Phúc, Hà Đông, Hà Nội", "Văn Quán, Hà Đông, Hà Nội"};
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Chọn địa chỉ")
                .setItems(classes, (dialog, which) -> edtAddress.setText(classes[which]))
                .show();
    }

    private void updateUserInfo(long userId, String name, String email, String phone, String address) {
        UpdateUserRequest request = new UpdateUserRequest(name, email, phone, address);
        String token = "Bearer " + tokenManager.getToken();

        apiService.updateUser(token, userId, request).enqueue(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(EditUserActivity.this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(EditUserActivity.this, UserManagementActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(EditUserActivity.this, "Cập nhật thất bại!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                Toast.makeText(EditUserActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
