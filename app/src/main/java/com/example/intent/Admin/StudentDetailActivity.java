package com.example.intent.Admin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

public class StudentDetailActivity extends AppCompatActivity {
    private ImageView imgBack;
    private TextView tvStudentName, tvStudentId, tvStudentClass,
            tvStudentBirthDate, tvStudentGender, tvStudentAddress, tvUserId, tvTeacherId;
    private Button btnEdit, btnDelete;
    private TokenManager tokenManager;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_detail2);

        applyEdgeToEdge();
        initializeViews();
        setupData();
        setupListeners();
    }

    private void applyEdgeToEdge() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void initializeViews() {
        imgBack = findViewById(R.id.imgBack);
        tvStudentName = findViewById(R.id.tvStudentName);
        tvStudentId = findViewById(R.id.tvStudentId);
        tvStudentClass = findViewById(R.id.tvStudentClass);
        tvStudentBirthDate = findViewById(R.id.tvStudentBirthDate);
        tvStudentGender = findViewById(R.id.tvStudentGender);
        tvStudentAddress = findViewById(R.id.tvStudentAddress);
        tvUserId = findViewById(R.id.tvUserId);
        tvTeacherId = findViewById(R.id.tvTeacherId);
        btnEdit = findViewById(R.id.btnEdit);
        btnDelete = findViewById(R.id.btnDelete);
    }

    private void setupData() {
        Intent intent = getIntent();

        Long studentId = intent.getLongExtra("studentId", -1);
        Long userId = intent.getLongExtra("userId", -1);
        Long teacherId = intent.getLongExtra("teacherId", -1);
        String name = intent.getStringExtra("name");
        String birthDate = intent.getStringExtra("birthDate");
        String gender = intent.getStringExtra("gender");
        String className = intent.getStringExtra("class_name");
        String address = intent.getStringExtra("address");

        tvStudentName.setText(String.format("Họ và tên: %s", name));
        tvStudentId.setText(String.format("Mã học sinh: %d", studentId));
        tvStudentClass.setText(String.format("Lớp: %s", className));
        tvStudentBirthDate.setText(String.format("Ngày sinh: %s", birthDate));
        tvStudentGender.setText(String.format("Giới tính: %s", gender));
        tvStudentAddress.setText(String.format("Địa chỉ: %s", address));
        tvUserId.setText(String.format("Mã phụ huynh: %d", userId));
        tvTeacherId.setText(String.format("Mã giáo viên: %d", teacherId));

        tokenManager = new TokenManager(this);
        apiService = RetrofitClient.getInstance().createService(ApiService.class);
    }

    private void setupListeners() {
        btnEdit.setOnClickListener(v -> {
            Intent editIntent = new Intent(StudentDetailActivity.this, EditStudentActivity.class);
            editIntent.putExtras(getIntent()); // Pass all extras to the edit activity
            startActivity(editIntent);
        });

        btnDelete.setOnClickListener(v -> {
            Long studentId = getIntent().getLongExtra("studentId", -1);
            if (studentId != -1) {
                showDeleteConfirmationDialog(studentId);
            } else {
                Toast.makeText(this, "Không tìm thấy mã học sinh!", Toast.LENGTH_SHORT).show();
            }
        });

        imgBack.setOnClickListener(v -> finish());
    }

    private void showDeleteConfirmationDialog(Long studentId) {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa học sinh này không?")
                .setPositiveButton("Xóa", (dialog, which) -> deleteStudent(studentId))
                .setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void deleteStudent(Long studentId) {
        String token = tokenManager.getToken();

        apiService.deleteStudent("Bearer " + token, studentId).enqueue(new Callback<ApiResponse<String>>() {
            @Override
            public void onResponse(Call<ApiResponse<String>> call, Response<ApiResponse<String>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(StudentDetailActivity.this, "Xóa học sinh thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(StudentDetailActivity.this, "Không thể xóa học sinh!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<String>> call, Throwable t) {
                Toast.makeText(StudentDetailActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
