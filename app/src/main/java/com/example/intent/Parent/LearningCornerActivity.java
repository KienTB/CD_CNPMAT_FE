package com.example.intent.Parent;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.intent.Api.ApiService;
import com.example.intent.Api.RetrofitClient;
import com.example.intent.Model.Grade;
import com.example.intent.R;
import com.example.intent.Token.TokenManager;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LearningCornerActivity extends AppCompatActivity {
    ImageView imgBackToExtension;
    private RecyclerView rvAssignments;
    private GradeAdapter gradeAdapter;
    private List<Grade> gradeList = new ArrayList<>();
    private TokenManager tokenManager;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_learning_corner);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        imgBackToExtension = findViewById(R.id.imgBackToExtension);
        rvAssignments = findViewById(R.id.rvAssignments);

        tokenManager = new TokenManager(this);
        apiService = RetrofitClient.getInstance().createService(ApiService.class);

        rvAssignments.setLayoutManager(new LinearLayoutManager(this));
        gradeAdapter = new GradeAdapter(gradeList);
        rvAssignments.setAdapter(gradeAdapter);

        imgBackToExtension.setOnClickListener(v -> finish());

        String studentDataJson = tokenManager.getStudentData();
        if (studentDataJson != null) {
            Long studentId = parseStudentIdFromJson(studentDataJson);
            if (studentId != null) {
                loadAttendanceData(studentId);
            }
        }
    }

    private Long parseStudentIdFromJson(String studentDataJson) {
        try {
            JSONObject jsonObject = new JSONObject(studentDataJson);
            return jsonObject.optLong("studentId", -1);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void loadAttendanceData(Long studentId) {
        String token = tokenManager.getToken();
        Log.d("API_DEBUG", "Token: " + token);
        Log.d("API_DEBUG", "Student ID: " + studentId);

        apiService.getGradesByStudentId("Bearer " + token, studentId)
                .enqueue(new Callback<List<Grade>>() {
                    @Override
                    public void onResponse(Call<List<Grade>> call, Response<List<Grade>> response) {
                        Log.d("API_DEBUG", "Response code: " + response.code());
                        if (response.isSuccessful() && response.body() != null) {
                            gradeList.clear();
                            gradeList.addAll(response.body());
                            runOnUiThread(() -> {
                                gradeAdapter.notifyDataSetChanged();
                                if (gradeList.isEmpty()) {
                                    Toast.makeText(LearningCornerActivity.this,
                                            "Không có dữ liệu điểm danh",
                                            Toast.LENGTH_LONG).show();
                                }
                            });
                        } else {
                            handleErrorResponse(response);
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Grade>> call, Throwable t) {
                        Log.e("API_DEBUG", "API Call Failed", t);
                        runOnUiThread(() -> {
                            Toast.makeText(LearningCornerActivity.this,
                                    "Lỗi: " + t.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        });
                    }
                });
    }

    private void handleErrorResponse(Response<?> response) {
        try {
            String errorBody = response.errorBody() != null
                    ? response.errorBody().string()
                    : "Không có thông tin lỗi";
            Log.e("API_DEBUG", "Error Body: " + errorBody);

            runOnUiThread(() -> Toast.makeText(
                    LearningCornerActivity.this,
                    "Lỗi: " + errorBody,
                    Toast.LENGTH_LONG).show());
        } catch (Exception e) {
            Log.e("API_DEBUG", "Error parsing error body", e);
        }
    }
}