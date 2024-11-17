package com.example.intent;

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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddStudentActivity extends AppCompatActivity {
    private EditText edtMHS;
    private Button btnConfirm;
    private ApiService apiService;
    private ImageView imgBack;
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

        apiService = RetrofitClient.getInstance().createService(ApiService.class);

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String studentId = edtMHS.getText().toString().trim();
                if (studentId.isEmpty()) {
                    Toast.makeText(AddStudentActivity.this, "Vui lòng nhập mã học sinh", Toast.LENGTH_SHORT).show();
                    return;
                }
                getStudentById(studentId);
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

    private void getStudentById(String studentId){
        apiService.getStudentById(Integer.parseInt(studentId)).enqueue(new Callback<ApiResponse<Student>>() {
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
        String studentInfo = "Mã học sinh: " + student.getStudentId() + "\n" +
                "Tên học sinh: " + student.getName() + "\n" +
                "Ngày sinh: " + student.getBirthDate() + "\n" +
                "Giới tính: " + student.getGender() + "\n" +
                "Lớp học: " + student.getClass_name() + "\n" +
                "Địa chỉ: " + student.getAddress();

        new AlertDialog.Builder(this)
                .setTitle("Thông tin học sinh")
                .setMessage(studentInfo)
                .setPositiveButton("Đóng", (dialog, which) -> dialog.dismiss())
                .show();
    }
}