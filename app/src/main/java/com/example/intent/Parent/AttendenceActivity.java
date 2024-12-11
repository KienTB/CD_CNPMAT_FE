package com.example.intent.Parent;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.intent.Api.ApiService;
import com.example.intent.Api.RetrofitClient;
import com.example.intent.Teacher.AttendanceAdapter;
import com.example.intent.Model.Schedule;
import com.example.intent.R;
import com.example.intent.Token.TokenManager;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AttendenceActivity extends AppCompatActivity {
    private ImageView imgBackToExtension;
    private RecyclerView rvAttendance;
    private AttendanceAdapter attendanceAdapter;
    private List<Schedule> scheduleList = new ArrayList<>();
    private TokenManager tokenManager;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendence);

        imgBackToExtension = findViewById(R.id.imgBackToExtension);
        rvAttendance = findViewById(R.id.rvAttendance);

        tokenManager = new TokenManager(this);
        apiService = RetrofitClient.getInstance().createService(ApiService.class);

        imgBackToExtension.setOnClickListener(v -> finish());

        rvAttendance.setLayoutManager(new LinearLayoutManager(this));
        attendanceAdapter = new AttendanceAdapter(scheduleList);
        rvAttendance.setAdapter(attendanceAdapter);

        // Lấy student_id từ TokenManager
        String studentDataJson = tokenManager.getStudentData();
        if (studentDataJson != null) {
            Long studentId = parseStudentIdFromJson(studentDataJson);
            if (studentId != null) {
                loadAttendanceData(studentId);
            } else {
                // Xử lý trường hợp student_id không hợp lệ
            }
        } else {
            // Xử lý trường hợp không có dữ liệu học sinh
        }
    }

    private Long parseStudentIdFromJson(String studentDataJson) {
        // Giải mã JSON để lấy student_id (Sử dụng thư viện JSON như Gson)
        try {
            // Giả sử studentDataJson là chuỗi JSON chứa trường studentId
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

        apiService.getSchedulesByStudentId("Bearer " + token, studentId)
                .enqueue(new Callback<List<Schedule>>() {
                    @Override
                    public void onResponse(Call<List<Schedule>> call, Response<List<Schedule>> response) {
                        Log.d("API_DEBUG", "Response code: " + response.code());
                        if (response.isSuccessful() && response.body() != null) {
                            scheduleList.clear();
                            scheduleList.addAll(response.body());
                            runOnUiThread(() -> {
                                attendanceAdapter.notifyDataSetChanged();
                                if (scheduleList.isEmpty()) {
                                    Toast.makeText(AttendenceActivity.this,
                                            "Không có dữ liệu điểm danh",
                                            Toast.LENGTH_LONG).show();
                                }
                            });
                        } else {
                            handleErrorResponse(response);
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Schedule>> call, Throwable t) {
                        Log.e("API_DEBUG", "API Call Failed", t);
                        runOnUiThread(() -> {
                            Toast.makeText(AttendenceActivity.this,
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
                    AttendenceActivity.this,
                    "Lỗi: " + errorBody,
                    Toast.LENGTH_LONG).show());
        } catch (Exception e) {
            Log.e("API_DEBUG", "Error parsing error body", e);
        }
    }
}
