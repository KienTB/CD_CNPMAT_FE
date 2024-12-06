package com.example.intent.Teacher;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.intent.Api.ApiResponse;
import com.example.intent.Api.ApiService;
import com.example.intent.Api.RetrofitClient;
import com.example.intent.Model.Schedule;
import com.example.intent.R;
import com.example.intent.Request.ScheduleRequest;
import com.example.intent.Token.TokenManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AttendanceInputActivity extends AppCompatActivity {

    private ImageView imgBackToExtension, imgCalendarPreview;
    private Button btnSubmit;
    private RadioGroup radioGroupStatus;
    private TokenManager tokenManager;
    private ApiService apiService;
    private Long studentId;
    private Long userId;
    private EditText etActivity;
    private String attendanceStatus;
    private String scheduleDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_attendance_input);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        imgCalendarPreview = findViewById(R.id.imgCalendarPreview);
        imgBackToExtension = findViewById(R.id.imgBackToExtension);
        btnSubmit = findViewById(R.id.btnSubmit);
        radioGroupStatus = findViewById(R.id.radioGroupStatus);
        etActivity = findViewById(R.id.etActivity);

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

        radioGroupStatus.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton selectedRadioButton = findViewById(checkedId);
            attendanceStatus = selectedRadioButton.getTag().toString();
        });

        btnSubmit.setOnClickListener(v -> submitAttendance());

        imgCalendarPreview.setOnClickListener(v -> showFullCalendarDialog());
    }

    private void showFullCalendarDialog() {
        Dialog calendarDialog = new Dialog(this);
        calendarDialog.setContentView(R.layout.dialog_full_calendar);

        CalendarView fullCalendarView = calendarDialog.findViewById(R.id.fullCalendarView);
        Button btnCloseCalendar = calendarDialog.findViewById(R.id.btnCloseCalendar);

        fullCalendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            scheduleDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth);
            Toast.makeText(this, "Đã chọn ngày: " + scheduleDate, Toast.LENGTH_SHORT).show();
        });

        btnCloseCalendar.setOnClickListener(v -> calendarDialog.dismiss());

        calendarDialog.show();
    }

    private void submitAttendance() {
        String activity = etActivity.getText().toString().trim();
        if (activity.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập môn học!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (attendanceStatus == null) {
            Toast.makeText(this, "Vui lòng chọn trạng thái điểm danh!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (scheduleDate == null) {
            Toast.makeText(this, "Vui lòng chọn ngày!", Toast.LENGTH_SHORT).show();
            return;
        }

        ScheduleRequest scheduleRequest = new ScheduleRequest(
                null,
                studentId,
                userId,
                activity,
                scheduleDate,
                attendanceStatus
        );

        String token = "Bearer " + tokenManager.getToken();
        apiService.addSchedule(token, scheduleRequest)
                .enqueue(new Callback<ApiResponse<Schedule>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<Schedule>> call, Response<ApiResponse<Schedule>> response) {
                        if (response.isSuccessful() &&
                                response.body() != null &&
                                (response.body().isSuccess() ||
                                        response.body().getMessage().equals("Attendance added successfully"))) {
                            Toast.makeText(AttendanceInputActivity.this, "Lưu lịch điểm danh thành công!", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            String message = (response.body() != null) ? response.body().getMessage() : "Lỗi không xác định";
                            Toast.makeText(AttendanceInputActivity.this, "Lưu lịch điểm danh thất bại: " + message, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<Schedule>> call, Throwable t) {
                        Toast.makeText(AttendanceInputActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("AttendanceInputActivity", "API call failed", t);
                    }
                });

        Log.d("Attendance", "Student ID: " + studentId +
                ", User ID: " + userId +
                ", Status: " + attendanceStatus);

        Toast.makeText(this, "Đã ghi nhận điểm danh!", Toast.LENGTH_SHORT).show();
        finish();
    }
}
