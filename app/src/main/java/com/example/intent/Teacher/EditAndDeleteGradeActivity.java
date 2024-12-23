package com.example.intent.Teacher;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.intent.Api.ApiResponse;
import com.example.intent.Api.ApiService;
import com.example.intent.Api.RetrofitClient;
import com.example.intent.Model.Grade;
import com.example.intent.Model.Schedule;
import com.example.intent.R;
import com.example.intent.Request.UpdateGradeRequest;
import com.example.intent.Request.UpdateScheduleRequest;
import com.example.intent.Token.TokenManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditAndDeleteGradeActivity extends AppCompatActivity {
    private EditText etSubject, etScore;
    private RadioGroup radioGroupStatus;
    private RadioButton radioPresent, radioAbsent, radioLate;
    private Button btnEdit, btnDelete;
    private ImageView imgBackToExtension;

    private TokenManager tokenManager;
    private ApiService apiService;
    private Long studentId;
    private Long gradeId;
    private String attendanceStatus;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_and_delete_score);

        imgBackToExtension = findViewById(R.id.imgBackToExtension);
        etSubject = findViewById(R.id.etSubject);
        etScore = findViewById(R.id.etScore);
        radioGroupStatus = findViewById(R.id.radioGroupStatus);
        radioPresent = findViewById(R.id.radioPresent);
        radioAbsent = findViewById(R.id.radioAbsent);
        radioLate = findViewById(R.id.radioLate);
        btnEdit = findViewById(R.id.btnEdit);
        btnDelete = findViewById(R.id.btnDelete);

        tokenManager = new TokenManager(this);
        apiService = RetrofitClient.getInstance().createService(ApiService.class);

        imgBackToExtension.setOnClickListener(v -> finish());

        Intent intent = getIntent();
        gradeId = intent.getLongExtra("gradeId", -1);
        studentId = intent.getLongExtra("studentId", -1);

        String subject = intent.getStringExtra("subject");
        String receivedScore = intent.getStringExtra("score");
        String term = intent.getStringExtra("term");

        etSubject.setText(subject != null ? subject : "");
        etScore.setText(receivedScore != null ? receivedScore : "");

        switch (term) {
            case "Giữa kỳ":
                radioPresent.setChecked(true);
                attendanceStatus = "Giữa kỳ";
                break;
            case "Cuối kỳ":
                radioAbsent.setChecked(true);
                attendanceStatus = "Cuối kỳ";
                break;
            case "Tổng kết":
                radioLate.setChecked(true);
                attendanceStatus = "Tổng kết";
                break;
            default:
                attendanceStatus = null;
                break;
        }

        radioGroupStatus.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton selectedRadioButton = findViewById(checkedId);
            if (selectedRadioButton != null) {
                attendanceStatus = selectedRadioButton.getTag().toString();
            }
        });

        etScore.setOnClickListener(v -> showScoreSelectionDialog());

        etSubject.setOnClickListener(v -> showSubjectSelectionDialog());

        btnEdit.setOnClickListener(v -> updateGrade());

        btnDelete.setOnClickListener(v -> deleteGrade());

        btnEdit.setOnClickListener(v -> showConfirmDialog("Cập nhật", "Bạn có chắc muốn cập nhật dữ liệu này?", () -> updateGrade()));
        btnDelete.setOnClickListener(v -> showConfirmDialog("Xóa", "Bạn có chắc muốn xóa dữ liệu này?", () -> deleteGrade()));

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void showConfirmDialog(String title, String message, Runnable onConfirmAction) {
        new android.app.AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Đồng ý", (dialog, which) -> onConfirmAction.run())
                .setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void showSubjectSelectionDialog() {
        String[] subjects = {"Toán", "Văn", "Anh", "Lý", "Hóa", "Sinh", "Sử", "Địa"};
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Chọn môn học")
                .setItems(subjects, (dialog, which) -> etSubject.setText(subjects[which]))
                .show();
    }

    private void showScoreSelectionDialog() {
        String[] subjects = {"1.0", "2.0", "3.0", "4.0", "5.0", "6.0", "7.0", "8.0", "9.0", "10.0"};
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Chọn môn học")
                .setItems(subjects, (dialog, which) -> etScore.setText(subjects[which]))
                .show();
    }

    private void updateGrade() {
        if (studentId != -1 && gradeId != -1) {
            String token = tokenManager.getToken();
            String subject = etSubject.getText().toString().trim();
            String score = etScore.getText().toString().trim();

            if (subject.isEmpty() || score.isEmpty() || attendanceStatus == null) {
                Toast.makeText(this, "Vui lòng nhập đủ thông tin!", Toast.LENGTH_SHORT).show();
                return;
            }

            UpdateGradeRequest updateRequest = new UpdateGradeRequest();
            updateRequest.setSubject(subject);
            updateRequest.setScore(Float.parseFloat(score));
            updateRequest.setTerm(attendanceStatus);

            apiService.updateGrade("Bearer " + token, studentId, gradeId, updateRequest)
                    .enqueue(new Callback<ApiResponse<Grade>>() {
                        @Override
                        public void onResponse(Call<ApiResponse<Grade>> call, Response<ApiResponse<Grade>> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                Toast.makeText(EditAndDeleteGradeActivity.this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(EditAndDeleteGradeActivity.this, "Cập nhật thất bại!", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ApiResponse<Grade>> call, Throwable t) {
                            Toast.makeText(EditAndDeleteGradeActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(this, "Không tìm thấy dữ liệu để cập nhật.", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteGrade() {
        if (studentId != -1 && gradeId != -1) {
            String token = tokenManager.getToken();
            apiService.deleteGrade("Bearer " + token, studentId, gradeId)
                    .enqueue(new Callback<ApiResponse<Void>>() {
                        @Override
                        public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(EditAndDeleteGradeActivity.this, "Xóa thành công", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(EditAndDeleteGradeActivity.this, "Xóa thất bại", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                            Toast.makeText(EditAndDeleteGradeActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(this, "Không tìm thấy dữ liệu để xóa.", Toast.LENGTH_SHORT).show();
        }
    }

}