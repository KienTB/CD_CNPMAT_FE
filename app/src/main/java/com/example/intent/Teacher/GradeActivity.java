package com.example.intent.Teacher;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import com.example.intent.Model.DataStudent;
import com.example.intent.Model.Grade;
import com.example.intent.Model.Student;
import com.example.intent.R;
import com.example.intent.Request.GradeRequest;
import com.example.intent.StudentAdapter;
import com.example.intent.Token.TokenManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GradeActivity extends AppCompatActivity {
    private RecyclerView recyclerViewStudents;
    private StudentAdapter studentAdapter;
    private List<Student> studentList = new ArrayList<>();
    private TokenManager tokenManager;
    private ApiService apiService;
    private ImageView imgBackToExtension;
    private EditText etSubject, etScore;
    private RadioGroup radioGroupStatus;
    private Button btnSubmit;
    private String attendanceStatus;
    private Long userId;
    private SearchView searchView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_score);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        recyclerViewStudents = findViewById(R.id.recyclerViewStudents);
        imgBackToExtension = findViewById(R.id.imgBackToExtension);
        searchView = findViewById(R.id.searchView);

        recyclerViewStudents.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewStudents.setAdapter(studentAdapter);
        recyclerViewStudents = findViewById(R.id.recyclerViewStudents);
        imgBackToExtension = findViewById(R.id.imgBackToExtension);
        radioGroupStatus = findViewById(R.id.radioGroupStatus);
        btnSubmit = findViewById(R.id.btnSubmit);
        etScore = findViewById(R.id.etScore);
        etSubject = findViewById(R.id.etSubject);

        studentAdapter = new StudentAdapter(studentList, student -> {
            Toast.makeText(this, "Clicked on: " + student.getName(), Toast.LENGTH_SHORT).show();
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
            return;
        }

        userId = tokenManager.getUserId();
        if (userId == -1) {
            Toast.makeText(this, "Không tìm thấy thông tin người dùng!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        imgBackToExtension.setOnClickListener(v -> finish());

        radioGroupStatus.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton selectedRadioButton = findViewById(checkedId);
            if (selectedRadioButton != null) {
                attendanceStatus = selectedRadioButton.getTag().toString();
            }
        });

        etSubject.setOnClickListener(v -> showSubjectSelectionDialog());

        etScore.setOnClickListener(v -> showScoreSelectionDialog());

        btnSubmit.setOnClickListener(v -> submitScore());

        setupSearchView();
    }

    private void showSubjectSelectionDialog() {
        String[] subjects = {"Toán", "Văn", "Anh", "Lý", "Hóa", "Sinh", "Sử", "Địa"};
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Chọn môn học")
                .setItems(subjects, (dialog, which) -> etSubject.setText(subjects[which]))
                .show();
    }

    private void showScoreSelectionDialog() {
        String[] subjects = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Chọn môn học")
                .setItems(subjects, (dialog, which) -> etScore.setText(subjects[which]))
                .show();
    }

    private void fetchStudentsByTeacherId(long teacherId) {
        String token = tokenManager.getToken();

        apiService.getStudentByTeacherId("Bearer " + token, teacherId)
                .enqueue(new Callback<ApiResponse<List<DataStudent>>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<List<DataStudent>>> call, Response<ApiResponse<List<DataStudent>>> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                            studentList.clear();
                            for (DataStudent dataStudent : response.body().getData()) {
                                Student student = new Student(
                                        dataStudent.getStudentId(),
                                        dataStudent.getName(),
                                        dataStudent.getBirthDate(),
                                        dataStudent.getGender(),
                                        dataStudent.getClass_name(),
                                        dataStudent.getAddress(),
                                        dataStudent.getUser(),
                                        dataStudent.getTeacher()
                                );
                                studentList.add(student);
                            }
                            studentAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(GradeActivity.this,
                                    "Failed to fetch students: " + (response.body() != null ? response.body().getMessage() : "Unknown error"),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<List<DataStudent>>> call, Throwable t) {
                        Toast.makeText(GradeActivity.this, "API call failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void submitScore() {
        String subject = etSubject.getText().toString().trim();
        String scoreText = etScore.getText().toString().trim();
        if (subject.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập môn học!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (attendanceStatus == null) {
            Toast.makeText(this, "Vui lòng chọn kỳ học!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (scoreText.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập điểm!", Toast.LENGTH_SHORT).show();
            return;
        }

        float score;
        try {
            score = Float.parseFloat(scoreText);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Điểm không hợp lệ!", Toast.LENGTH_SHORT).show();
            return;
        }

        List<Long> selectedStudentIds = studentAdapter.getSelectedStudentIds();
        if (selectedStudentIds.isEmpty()) {
            Toast.makeText(this, "Vui lòng chọn ít nhất một học sinh!", Toast.LENGTH_SHORT).show();
            return;
        }

        for (Long studentId : selectedStudentIds) {
            GradeRequest gradeRequest = new GradeRequest(studentId, userId, subject, score, attendanceStatus);

            String token = "Bearer " + tokenManager.getToken();
            apiService.addGrade(token, gradeRequest)
                    .enqueue(new Callback<ApiResponse<Grade>>() {
                        @Override
                        public void onResponse(Call<ApiResponse<Grade>> call, Response<ApiResponse<Grade>> response) {
                            if (response.isSuccessful() &&
                                    response.body() != null &&
                                    (response.body().isSuccess() ||
                                            response.body().getMessage().equals("Grade added successfully"))) {
                                Toast.makeText(GradeActivity.this, "Lưu điểm thành công!", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                String message = (response.body() != null) ? response.body().getMessage() : "Lỗi không xác định";
                                Toast.makeText(GradeActivity.this, "Lưu điểm thất bại: " + message, Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ApiResponse<Grade>> call, Throwable t) {
                            Toast.makeText(GradeActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.e("ScoreInputActivity", "API call failed", t);
                        }
                    });

            Log.d("Attendance", "Student ID: " + studentId +
                    ", User ID: " + userId +
                    ", Status: " + attendanceStatus);

            Toast.makeText(this, "Đã ghi nhận điểm!", Toast.LENGTH_SHORT).show();
            finish();
        }
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
            if (String.valueOf(student.getStudentId()).contains(query)) {
                filteredList.add(student);
            }
        }
        studentAdapter.updateList(filteredList);
    }
}