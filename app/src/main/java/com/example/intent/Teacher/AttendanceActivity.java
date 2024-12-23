package com.example.intent.Teacher;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.intent.Api.ApiResponse;
import com.example.intent.Api.ApiService;
import com.example.intent.Api.RetrofitClient;
import com.example.intent.Model.DataStudent;
import com.example.intent.Model.Schedule;
import com.example.intent.Model.Student;
import com.example.intent.R;
import com.example.intent.Request.ScheduleRequest;
import com.example.intent.StudentAdapter;
import com.example.intent.Token.TokenManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
    private EditText etActivity, etDate;
    private RadioGroup radioGroupStatus;
    private Button btnSubmit;
    private String attendanceStatus;
    private String scheduleDate;
    private Long userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);

        recyclerViewStudents = findViewById(R.id.recyclerViewStudents);
        imgBackToExtension = findViewById(R.id.imgBackToExtension);
        radioGroupStatus = findViewById(R.id.radioGroupStatus);
        btnSubmit = findViewById(R.id.btnSubmit);
        etActivity = findViewById(R.id.etActivity);
        etDate = findViewById(R.id.etDate);

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

        etActivity.setOnClickListener(v -> showSubjectSelectionDialog());

        etDate.setOnClickListener(v -> showDatePickerDialog());

        btnSubmit.setOnClickListener(v -> submitAttendance());
    }

    private void showSubjectSelectionDialog() {
        String[] subjects = {"Toán", "Văn", "Anh", "Lý", "Hóa", "Sinh", "Sử", "Địa"};
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Chọn môn học")
                .setItems(subjects, (dialog, which) -> etActivity.setText(subjects[which]))
                .show();
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, selectedYear, selectedMonth, selectedDay) -> {
            String rawDate = String.format(Locale.getDefault(), "%d/%d/%d", selectedDay, selectedMonth + 1, selectedYear);
            try {
                scheduleDate = convertDateFormat(rawDate);
                etDate.setText(scheduleDate);
            } catch (Exception e) {
                Toast.makeText(this, "Lỗi chuyển đổi ngày: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("AttendanceActivity", "Date conversion failed", e);
            }
        }, year, month, day);

        datePickerDialog.show();
    }

    private String convertDateFormat(String inputDate) throws Exception {
        SimpleDateFormat inputFormat = new SimpleDateFormat("d/M/yyyy", Locale.getDefault());
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date date = inputFormat.parse(inputDate);
        return outputFormat.format(date);
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
                            String message = response.body() != null ? response.body().getMessage() : "Lỗi không xác định";
                            Toast.makeText(AttendanceActivity.this, "Tải danh sách học sinh thất bại: " + message, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<List<DataStudent>>> call, Throwable t) {
                        Toast.makeText(AttendanceActivity.this, "Gọi API thất bại: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("AttendanceActivity", "API call failed", t);
                    }
                });
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

        List<Long> selectedStudentIds = studentAdapter.getSelectedStudentIds();
        if (selectedStudentIds.isEmpty()) {
            Toast.makeText(this, "Vui lòng chọn ít nhất một học sinh!", Toast.LENGTH_SHORT).show();
            return;
        }

        for (Long studentId : selectedStudentIds) {
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
                                Toast.makeText(AttendanceActivity.this, "Lưu lịch điểm danh thành công!", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                String message = (response.body() != null) ? response.body().getMessage() : "Lỗi không xác định";
                                Toast.makeText(AttendanceActivity.this, "Lưu lịch điểm danh thất bại: " + message, Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ApiResponse<Schedule>> call, Throwable t) {
                            Toast.makeText(AttendanceActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
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
}