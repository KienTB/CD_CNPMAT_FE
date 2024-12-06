package com.example.intent.Teacher;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import com.example.intent.Model.Student;
import com.example.intent.R;
import com.example.intent.StudentAdapter;
import com.example.intent.Token.TokenManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AttendanceActivity extends AppCompatActivity {
    private RecyclerView recyclerViewStudents;
    private StudentAdapter studentAdapter;
    private List<Student> studentList = new ArrayList<>();
    private TokenManager tokenManager;
    private ApiService apiService;
    private ImageView imgBackToExtension;
    private SearchView searchView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_attendance);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        recyclerViewStudents = findViewById(R.id.recyclerViewStudents);
        imgBackToExtension = findViewById(R.id.imgBackToExtension);
        searchView = findViewById(R.id.searchView);

        studentAdapter = new StudentAdapter(studentList, student -> {
            Intent intent = new Intent(AttendanceActivity.this, AttendanceInputActivity.class);
            intent.putExtra("studentId", student.getStudentId());
            startActivity(intent);
        });

        recyclerViewStudents.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewStudents.setAdapter(studentAdapter);

        tokenManager = new TokenManager(this);
        apiService = RetrofitClient.getInstance().createService(ApiService.class);

        long teacherId = tokenManager.getTeacherId();
        if (teacherId != -1) {
            fetchStudentsByTeacherId(teacherId);
        } else {
            Toast.makeText(this, "Không tìm thấy ID giáo viên. Vui lòng đăng nhập lại.", Toast.LENGTH_SHORT).show();
        }

        imgBackToExtension.setOnClickListener(v -> finish());

        setupSearchView();
    }

    private void fetchStudentsByTeacherId(long teacherId) {
        String token = tokenManager.getToken();

        apiService.getStudentByTeacherId("Bearer " + token, teacherId)
                .enqueue(new Callback<ApiResponse<List<Student>>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<List<Student>>> call, Response<ApiResponse<List<Student>>> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                            studentList.clear();
                            studentList.addAll(response.body().getData());
                            studentAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(AttendanceActivity.this,
                                    "Tải danh sách học sinh thất bại: " + (response.body() != null ? response.body().getMessage() : "Lỗi không xác định"),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<List<Student>>> call, Throwable t) {
                        Toast.makeText(AttendanceActivity.this, "Gọi API thất bại: " + t.getMessage(), Toast.LENGTH_SHORT).show();
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
                filterStudents(newText);
                return true;
            }
        });
    }

    private void filterStudents(String query) {
        List<Student> filteredList = new ArrayList<>();
        for (Student student : studentList) {
            if (student.getName().toLowerCase().contains(query.toLowerCase()) ||
                    String.valueOf(student.getStudentId()).contains(query)) {
                filteredList.add(student);
            }
        }
        studentAdapter.updateList(filteredList);
    }
}