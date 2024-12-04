package com.example.intent.Parent;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.example.intent.Model.Student;
import com.example.intent.R;
import com.example.intent.Token.TokenManager;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddStudentActivity extends AppCompatActivity {
    private EditText edtMHS;
    private Button btnConfirm;
    private ApiService apiService;
    private ImageView imgBack;
    private TokenManager tokenManager;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_student);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        edtMHS = findViewById(R.id.edtMHS);
        btnConfirm = findViewById(R.id.btnConfirm);

        tokenManager = new TokenManager(this);
        apiService = RetrofitClient.getInstance().createService(ApiService.class);

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String studentId = edtMHS.getText().toString().trim();
                if (studentId.isEmpty()) {
                    Toast.makeText(AddStudentActivity.this, "Vui lòng nhập mã học sinh", Toast.LENGTH_SHORT).show();
                    return;
                }
                getStudentById(Integer.parseInt(studentId));
            }
        });

        imgBack = findViewById(R.id.imgBack);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void getStudentById(int studentId) {
        String token = "Bearer " + tokenManager.getToken();
        apiService.getStudentById(token, studentId).enqueue(new Callback<ApiResponse<Student>>() {
            @Override
            public void onResponse(Call<ApiResponse<Student>> call, Response<ApiResponse<Student>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Student> apiResponse = response.body();
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        Student student = apiResponse.getData();
                        displayStudentInfo(student);
                    } else {
                        Toast.makeText(AddStudentActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(AddStudentActivity.this, "Không tìm thấy học sinh", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Student>> call, Throwable t) {
                Log.e("AddStudentActivity", "Lỗi mạng: " + t.getMessage(), t);
                Toast.makeText(AddStudentActivity.this, "Lỗi mạng, vui lòng thử lại sau", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayStudentInfo(Student student) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Thông tin học sinh");

        String message = "Mã học sinh: " + student.getStudentId() + "\n" +
                "Tên: " + student.getName() + "\n" +
                "Ngày sinh: " + student.getBirthDate() + "\n" +
                "Giới tính: " + student.getGender() + "\n" +
                "Lớp: " + student.getClass_name()+ "\n" +
                "Địa chỉ: " + student.getAddress();
        builder.setMessage(message);

        builder.setPositiveButton("Xác nhận", (dialog, which) -> {
            // Chuyển đối tượng Student sang JSON string
            Gson gson = new Gson();
            String studentJson = gson.toJson(student);

            tokenManager.saveStudentData(studentJson);

            Toast.makeText(AddStudentActivity.this, "Thêm học sinh thành công!", Toast.LENGTH_SHORT).show();

            // Chuyển đến màn hình chính
            Intent intent = new Intent(AddStudentActivity.this, ParentMainActivity.class);
            intent.putExtra("studentName", student.getName());
            intent.putExtra("studentClass", student.getClass_name());
            intent.putExtra("tabIndex", 2); // Tab thứ 3 (index = 2)
            startActivity(intent);
            finish();
        });

        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}