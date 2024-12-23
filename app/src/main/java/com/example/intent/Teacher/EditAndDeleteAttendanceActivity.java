package com.example.intent.Teacher;

import android.app.DatePickerDialog;
import android.content.Intent;
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
import com.example.intent.Model.Schedule;
import com.example.intent.R;
import com.example.intent.Request.UpdateScheduleRequest;
import com.example.intent.Token.TokenManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditAndDeleteAttendanceActivity extends AppCompatActivity {

    private EditText etActivity, etDate;
    private RadioGroup radioGroupStatus;
    private RadioButton radioPresent, radioAbsent, radioLate;
    private Button btnEdit, btnDelete;
    private ImageView imgBackToExtension;

    private TokenManager tokenManager;
    private ApiService apiService;
    private Long studentId;
    private Long scheduleId;
    private String attendanceStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_and_delete_attendance);

        imgBackToExtension = findViewById(R.id.imgBackToExtension);
        etActivity = findViewById(R.id.etActivity);
        etDate = findViewById(R.id.etDate);
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
        scheduleId = intent.getLongExtra("scheduleId", -1);
        studentId = intent.getLongExtra("studentId", -1);

        String activity = intent.getStringExtra("activity");
        String receivedScheduleDate = intent.getStringExtra("scheduleDate");
        String status = intent.getStringExtra("status");

        etActivity.setText(activity != null ? activity : "");
        etDate.setText(receivedScheduleDate != null ? receivedScheduleDate : "");

        switch (status) {
            case "Có mặt":
                radioPresent.setChecked(true);
                attendanceStatus = "Có mặt";
                break;
            case "Vắng mặt":
                radioAbsent.setChecked(true);
                attendanceStatus = "Vắng mặt";
                break;
            case "Tới trễ":
                radioLate.setChecked(true);
                attendanceStatus = "Tới trễ";
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

        etDate.setOnClickListener(v -> showDatePickerDialog());

        etActivity.setOnClickListener(v -> showSubjectSelectionDialog());

        btnEdit.setOnClickListener(v -> updateAttendance());

        btnDelete.setOnClickListener(v -> deleteAttendance());

        btnEdit.setOnClickListener(v -> showConfirmDialog("Cập nhật", "Bạn có chắc muốn cập nhật dữ liệu này?", () -> updateAttendance()));
        btnDelete.setOnClickListener(v -> showConfirmDialog("Xóa", "Bạn có chắc muốn xóa dữ liệu này?", () -> deleteAttendance()));

    }

    private void showConfirmDialog(String title, String message, Runnable onConfirmAction) {
        new android.app.AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Đồng ý", (dialog, which) -> onConfirmAction.run())
                .setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void updateAttendance() {
        if (studentId != -1 && scheduleId != -1) {
            String token = tokenManager.getToken();
            String activity = etActivity.getText().toString().trim();
            String scheduleDate = etDate.getText().toString().trim();

            if (activity.isEmpty() || scheduleDate.isEmpty() || attendanceStatus == null) {
                Toast.makeText(this, "Vui lòng nhập đủ thông tin!", Toast.LENGTH_SHORT).show();
                return;
            }

            UpdateScheduleRequest updateRequest = new UpdateScheduleRequest();
            updateRequest.setActivity(activity);
            updateRequest.setScheduleDate(scheduleDate);
            updateRequest.setStatus(attendanceStatus);

            apiService.updateSchedule("Bearer " + token, studentId, scheduleId, updateRequest)
                    .enqueue(new Callback<ApiResponse<Schedule>>() {
                        @Override
                        public void onResponse(Call<ApiResponse<Schedule>> call, Response<ApiResponse<Schedule>> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                Toast.makeText(EditAndDeleteAttendanceActivity.this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(EditAndDeleteAttendanceActivity.this, "Cập nhật thất bại!", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ApiResponse<Schedule>> call, Throwable t) {
                            Toast.makeText(EditAndDeleteAttendanceActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(this, "Không tìm thấy dữ liệu để cập nhật.", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteAttendance() {
        if (studentId != -1 && scheduleId != -1) {
            String token = tokenManager.getToken();
            apiService.deleteSchedule("Bearer " + token, studentId, scheduleId)
                    .enqueue(new Callback<ApiResponse<Void>>() {
                        @Override
                        public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(EditAndDeleteAttendanceActivity.this, "Xóa thành công", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(EditAndDeleteAttendanceActivity.this, "Xóa thất bại", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                            Toast.makeText(EditAndDeleteAttendanceActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(this, "Không tìm thấy dữ liệu để xóa.", Toast.LENGTH_SHORT).show();
        }
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, selectedYear, selectedMonth, selectedDay) -> {
            String rawDate = String.format(Locale.getDefault(), "%d/%d/%d", selectedDay, selectedMonth + 1, selectedYear);
            try {
                String formattedDate = convertDateFormat(rawDate);
                etDate.setText(formattedDate);
            } catch (Exception e) {
                Toast.makeText(this, "Lỗi chuyển đổi ngày: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("AttendanceActivity", "Date conversion failed", e);
            }
        }, year, month, day);

        datePickerDialog.show();
    }

    private void showSubjectSelectionDialog() {
        String[] subjects = {"Toán", "Văn", "Anh", "Lý", "Hóa", "Sinh", "Sử", "Địa"};
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Chọn môn học")
                .setItems(subjects, (dialog, which) -> etActivity.setText(subjects[which]))
                .show();
    }

    private String convertDateFormat(String inputDate) throws Exception {
        SimpleDateFormat inputFormat = new SimpleDateFormat("d/M/yyyy", Locale.getDefault());
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date date = inputFormat.parse(inputDate);
        return outputFormat.format(date);
    }
}
