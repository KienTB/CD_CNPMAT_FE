package com.example.intent.Admin;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.intent.Api.ApiResponse;
import com.example.intent.Api.ApiService;
import com.example.intent.Api.RetrofitClient;
import com.example.intent.Helper.StringHelper;
import com.example.intent.Model.Student;
import com.example.intent.R;
import com.example.intent.Request.StudentRegisterRequest;
import com.example.intent.Token.TokenManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StudentRegisterActivity extends AppCompatActivity {
    private EditText edtName,tvBirthDate, edtClass, edtUserId, edtAddress, edtTeacherId;
    private RadioGroup radioGroupGender;
    private Button btnRegister;
    private ImageView imgBackToExtension;
    private TokenManager tokenManager;
    private ApiService apiService;
    private String scheduleDate;

    private String birthDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_register);

        initViews();

        imgBackToExtension.setOnClickListener(v -> finish());

        btnRegister.setOnClickListener(v -> processFormFields());

        tvBirthDate.setOnClickListener(v -> showDatePickerDialog());

        edtClass.setOnClickListener(v -> showClassSelectionDialog());
        edtUserId.setOnClickListener(v -> fetchUserIds());
        edtTeacherId.setOnClickListener(v -> fetchTeacherIds());

        edtAddress.setOnClickListener(v -> showAddressSelectionDialog());
    }

    private void initViews() {
        edtName = findViewById(R.id.edtName);
        tvBirthDate = findViewById(R.id.tvBirthDate);
        edtClass = findViewById(R.id.edtClass);
        edtUserId = findViewById(R.id.edtUserId);
        edtAddress = findViewById(R.id.edtAddress);
        edtTeacherId = findViewById(R.id.edtTeacherId);
        radioGroupGender = findViewById(R.id.radioGroupGender);
        btnRegister = findViewById(R.id.btnRegister);
        imgBackToExtension = findViewById(R.id.imgBackToExtension);

        tokenManager = new TokenManager(this);
        apiService = RetrofitClient.getInstance().createService(ApiService.class);

        imgBackToExtension.setOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            finish();
        });
    }

    private void showClassSelectionDialog() {
        String[] classes = {"12a1", "12a2", "12a3", "12a4", "12a5", "12a6", "12a7", "12a8"};
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Chọn lớp học")
                .setItems(classes, (dialog, which) -> edtClass.setText(classes[which]))
                .show();
    }

    private void showAddressSelectionDialog() {
        String[] classes = {"Phú La, Hà Đông, Hà Nội", "Kiến Hưng, Hà Đông, Hà Nội", "La Khê, Hà Đông, Hà Nội", "Mộ Lao, Hà Đông, Hà Nội", "Nguyễn Trãi, Hà Đông, Hà Nội", "Quang Trung, Hà Đông, Hà Nội", "Vạn Phúc, Hà Đông, Hà Nội", "Văn Quán, Hà Đông, Hà Nội"};
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Chọn địa chỉ")
                .setItems(classes, (dialog, which) -> edtAddress.setText(classes[which]))
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
                String formattedDate = convertDateFormat(rawDate);
                tvBirthDate.setText(formattedDate);

                birthDate = formattedDate;
            } catch (Exception e) {
                Toast.makeText(this, "Lỗi chuyển đổi định dạng ngày: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("StudentRegisterActivity", "Date conversion error", e);
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



    private void processFormFields() {
        if (!validateName() || !validateBirthDate() || !validateClass() || !validateUserId() || !validateAddress() || !validateTeacherId()) {
            return;
        }

        try {
            String name = edtName.getText().toString();
            if (birthDate == null || birthDate.isEmpty()) {
                Toast.makeText(this, "Vui lòng chọn ngày sinh", Toast.LENGTH_SHORT).show();
                return;
            }
            String gender = getSelectedGender();
            String class_name = edtClass.getText().toString();
            Long userId = Long.parseLong(edtUserId.getText().toString());
            String address = edtAddress.getText().toString();
            Long teacherId = Long.parseLong(edtTeacherId.getText().toString());

            StudentRegisterRequest studentRegisterRequest = new StudentRegisterRequest(name, birthDate, gender, class_name, userId, address, teacherId);
            registerStudentUser(studentRegisterRequest);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "User ID và Teacher ID phải là số hợp lệ", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void registerStudentUser(StudentRegisterRequest studentRegisterRequest) {
        String token = tokenManager.getToken();
        Call<ApiResponse<Student>> call = apiService.registerStudent("Bearer " + token, studentRegisterRequest);

        call.enqueue(new Callback<ApiResponse<Student>>() {
            @Override
            public void onResponse(Call<ApiResponse<Student>> call, Response<ApiResponse<Student>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        showSuccessDialog();
                    } else {
                        Toast.makeText(StudentRegisterActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(StudentRegisterActivity.this, "Registration failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Student>> call, Throwable t) {
                Toast.makeText(StudentRegisterActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void    showSuccessDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Thêm học sinh thành công")
                .setMessage("Bạn đã thêm học sinh thành công!")
                .setPositiveButton("OK", (dialog, which) -> {
                    setResult(RESULT_OK);
                    finish();
                })
                .show();
    }

    private String getSelectedGender() {
        int selectedId = radioGroupGender.getCheckedRadioButtonId();
        if (selectedId == R.id.radioMale) {
            return "Nam";
        } else if (selectedId == R.id.radioFemale) {
            return "Nữ";
        }
        return "Khác";
    }

    private boolean validateName() {
        String name = edtName.getText().toString();
        if (name.isEmpty()) {
            edtName.setError("Tên không được để trống");
            return false;
        }
        edtName.setError(null);
        return true;
    }

    private boolean validateBirthDate() {
        if (birthDate == null || birthDate.isEmpty()) {
            tvBirthDate.setError("Ngày sinh không được để trống");
            return false;
        }
        tvBirthDate.setError(null);
        return true;
    }

    private boolean validateClass() {
        String class_name = edtClass.getText().toString();
        if (class_name.isEmpty()) {
            edtClass.setError("Lớp không được để trống");
            return false;
        }
        edtClass.setError(null);
        return true;
    }

    private boolean validateUserId() {
        String userId = edtUserId.getText().toString();
        if (userId.isEmpty()) {
            edtUserId.setError("Mã người dùng không được để trống");
            return false;
        }
        edtUserId.setError(null);
        return true;
    }

    private boolean validateAddress() {
        String address = edtAddress.getText().toString();
        if (address.isEmpty()) {
            edtAddress.setError("Địa chỉ không được để trống");
            return false;
        }
        edtAddress.setError(null);
        return true;
    }

    private boolean validateTeacherId() {
        String teacherId = edtTeacherId.getText().toString();
        if (teacherId.isEmpty()) {
            edtTeacherId.setError("Mã giáo viên không được để trống");
            return false;
        }
        edtTeacherId.setError(null);
        return true;
    }

    private void fetchUserIds() {
        String token = tokenManager.getToken();
        Call<ApiResponse<List<Long>>> call = apiService.getAllUserIdParent("Bearer " + token);

        call.enqueue(new Callback<ApiResponse<List<Long>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Long>>> call, Response<ApiResponse<List<Long>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<Long> userIds = response.body().getData();
                    showUserIdSelectionDialog(userIds);
                } else {
                    Toast.makeText(StudentRegisterActivity.this, "Không thể lấy danh sách User ID", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Long>>> call, Throwable t) {
                Toast.makeText(StudentRegisterActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showUserIdSelectionDialog(List<Long> userIds) {
        String[] userIdArray = userIds.stream().map(String::valueOf).toArray(String[]::new);

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Chọn mã phụ huynh")
                .setItems(userIdArray, (dialog, which) -> edtUserId.setText(userIdArray[which]))
                .show();
    }

    private void fetchTeacherIds() {
        String token = tokenManager.getToken();
        Call<ApiResponse<List<Long>>> call = apiService.getAllUserIdTeacher("Bearer " + token);

        call.enqueue(new Callback<ApiResponse<List<Long>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Long>>> call, Response<ApiResponse<List<Long>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<Long> userIds = response.body().getData();
                    showTeacherIdSelectionDialog(userIds);
                } else {
                    Toast.makeText(StudentRegisterActivity.this, "Không thể lấy danh sách User ID", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Long>>> call, Throwable t) {
                Toast.makeText(StudentRegisterActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showTeacherIdSelectionDialog(List<Long> userIds) {
        String[] userIdArray = userIds.stream().map(String::valueOf).toArray(String[]::new);

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Chọn mã giáo viên")
                .setItems(userIdArray, (dialog, which) -> edtTeacherId.setText(userIdArray[which]))
                .show();
    }
}
