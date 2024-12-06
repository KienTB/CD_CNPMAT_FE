package com.example.intent.Teacher;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.intent.Api.ApiResponse;
import com.example.intent.Api.ApiService;
import com.example.intent.Api.RetrofitClient;
import com.example.intent.Model.Grade;
import com.example.intent.R;
import com.example.intent.Request.GradeRequest;
import com.example.intent.Token.TokenManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScoreInputActivity extends AppCompatActivity {

    private ImageView imgBackToExtension;
    private EditText etSubject, etScore;
    private RadioGroup rgTerm;
    private RadioButton rbMidTerm, rbFinalTerm, rbSummary;
    private Button btnSubmit;
    private TokenManager tokenManager;
    private ApiService apiService;

    private Long studentId;
    private Long userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score_input);

        imgBackToExtension = findViewById(R.id.imgBackToExtension);
        etSubject = findViewById(R.id.etSubject);
        etScore = findViewById(R.id.etScore);
        rgTerm = findViewById(R.id.rgTerm);
        rbMidTerm = findViewById(R.id.rbMidTerm);
        rbFinalTerm = findViewById(R.id.rbFinalTerm);
        rbSummary = findViewById(R.id.rbSummary);
        btnSubmit = findViewById(R.id.btnSubmit);

        tokenManager = new TokenManager(this);
        apiService = RetrofitClient.getInstance().createService(ApiService.class);

        studentId = getIntent().getLongExtra("studentId", -1);
        if (studentId == -1) {
            Toast.makeText(this, "Không tìm thấy thông tin học sinh!", Toast.LENGTH_SHORT).show();
            finish();
        }

        userId = tokenManager.getUserId();
        if (userId == -1) {
            Toast.makeText(this, "Không tìm thấy thông tin người dùng!", Toast.LENGTH_SHORT).show();
            finish();
        }

        imgBackToExtension.setOnClickListener(v -> finish());

        btnSubmit.setOnClickListener(v -> saveScore());
    }

    private void saveScore() {
        String subject = etSubject.getText().toString().trim();
        String scoreStr = etScore.getText().toString().trim();

        if (subject.isEmpty() || scoreStr.isEmpty() || rgTerm.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }

        float score;
        try {
            score = Float.parseFloat(scoreStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Điểm phải là một số hợp lệ!", Toast.LENGTH_SHORT).show();
            return;
        }

        String term;
        int selectedTermId = rgTerm.getCheckedRadioButtonId();
        if (selectedTermId == rbMidTerm.getId()) {
            term = "Giữa kỳ";
        } else if (selectedTermId == rbFinalTerm.getId()) {
            term = "Cuối kỳ";
        } else {
            term = "Tổng kết";
        }

        GradeRequest gradeRequest = new GradeRequest(studentId, userId, subject, score, term);

        String token = "Bearer " + tokenManager.getToken();
        apiService.addGrade(token, gradeRequest)
                .enqueue(new Callback<ApiResponse<Grade>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<Grade>> call, Response<ApiResponse<Grade>> response) {
                        if (response.isSuccessful() &&
                                response.body() != null &&
                                (response.body().isSuccess() ||
                                        response.body().getMessage().equals("Grade added successfully"))) {
                            Toast.makeText(ScoreInputActivity.this, "Lưu điểm thành công!", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            String message = (response.body() != null) ? response.body().getMessage() : "Lỗi không xác định";
                            Toast.makeText(ScoreInputActivity.this, "Lưu điểm thất bại: " + message, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<Grade>> call, Throwable t) {
                        Toast.makeText(ScoreInputActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("ScoreInputActivity", "API call failed", t);
                    }
                });
    }
}
