package com.example.intent.Parent;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.SearchView;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendence);

        imgBackToExtension = findViewById(R.id.imgBackToExtension);
        rvAttendance = findViewById(R.id.rvAttendance);
        searchView = findViewById(R.id.searchView);

        tokenManager = new TokenManager(this);
        apiService = RetrofitClient.getInstance().createService(ApiService.class);

        imgBackToExtension.setOnClickListener(v -> finish());

        rvAttendance.setLayoutManager(new LinearLayoutManager(this));
        attendanceAdapter = new AttendanceAdapter(scheduleList);
        rvAttendance.setAdapter(attendanceAdapter);

        String studentDataJson = tokenManager.getStudentData();
        if (studentDataJson != null) {
            Long studentId = parseStudentIdFromJson(studentDataJson);
            if (studentId != null) {
                loadAttendanceData(studentId);
            }
        }
        setupSearchView();
    }

    private void setupSearchView() {
        searchView.setOnQueryTextFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                showDatePickerDialog();
                searchView.clearFocus();
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterAttendanceByDate(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    attendanceAdapter.updateData(scheduleList);
                } else {
                    filterAttendanceByDate(newText);
                }
                return true;
            }
        });
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
                searchView.setQuery(formattedDate, true);
                filterAttendanceByDate(formattedDate);
            } catch (Exception e) {
                Toast.makeText(this, "Lỗi chuyển đổi định dạng ngày: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("StatisticalDetail", "Date conversion error", e);
            }
        }, year, month, day);

        datePickerDialog.show();
    }

    private String convertDateFormat(String inputDate) throws Exception {
        SimpleDateFormat inputFormat = new SimpleDateFormat("d/M/yyyy", Locale.getDefault());
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
        Date date = inputFormat.parse(inputDate);
        return outputFormat.format(date);
    }

    private void filterAttendanceByDate(String date) {
        if (!date.matches("\\d{4}/\\d{2}/\\d{2}")) {
            Toast.makeText(this, "Định dạng ngày không hợp lệ. Vui lòng nhập yyyy/MM/dd", Toast.LENGTH_SHORT).show();
            return;
        }

        List<Schedule> filteredList = new ArrayList<>();
        for (Schedule schedule : scheduleList) {
            String formattedDate = formatDate(schedule.getScheduleDate());
            if (formattedDate.equals(date)) {
                filteredList.add(schedule);
            }
        }

        if (filteredList.isEmpty()) {
            Toast.makeText(this, "Không tìm thấy lịch điểm danh cho ngày " + date, Toast.LENGTH_SHORT).show();
        } else {
            attendanceAdapter.updateData(filteredList);
        }
    }

    private String formatDate(String date) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
            Date parsedDate = inputFormat.parse(date);
            return outputFormat.format(parsedDate);
        } catch (Exception e) {
            e.printStackTrace();
            return date;
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
                            Log.e("API_DEBUG", "Error: " + response.message());
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
}
