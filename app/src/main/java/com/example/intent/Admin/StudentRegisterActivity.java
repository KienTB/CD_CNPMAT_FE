package com.example.intent.Admin;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StudentRegisterActivity extends AppCompatActivity {
    private EditText edtName, edtClass, edtUserId, edtAddress, edtTeacherId;
    private TextView tvBirthDate;
    private RadioGroup radioGroupGender;
    private Button btnRegister;
    private ImageView imgBackToExtension;
    private TokenManager tokenManager;
    private ApiService apiService;

    private String birthDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_register);

        initViews();

        tvBirthDate.setOnClickListener(v -> openDatePickerDialog());

        imgBackToExtension.setOnClickListener(v -> finish());

        btnRegister.setOnClickListener(v -> processFormFields());
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
    }

    private void openDatePickerDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_calendar);

        CalendarView calendarView = dialog.findViewById(R.id.calendarView);
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            birthDate = dayOfMonth + "/" + (month + 1) + "/" + year;
            tvBirthDate.setText(birthDate);
            dialog.dismiss();
            Toast.makeText(this, "Ngày sinh đã chọn: " + birthDate, Toast.LENGTH_SHORT).show();
        });

        dialog.show();
    }

    private void processFormFields() {
        if (!validateName() || !validateBirthDate() || !validateClass() || !validateUserId() || !validateAddress() || !validateTeacherId()) {
            return;
        }

        try {
            String name = edtName.getText().toString();
            String birthDate = convertDateFormat(tvBirthDate.getText().toString());
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

    private String convertDateFormat(String inputDate) throws Exception {
        SimpleDateFormat inputFormat = new SimpleDateFormat("d/M/yyyy");
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = inputFormat.parse(inputDate);
        return outputFormat.format(date);
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

    private void showSuccessDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Thêm học sinh thành công")
                .setMessage("Bạn đã thêm học sinh thành công!")
                .setPositiveButton("OK", (dialog, which) -> {
                    Intent intent = new Intent(StudentRegisterActivity.this, StudentManagementActivity.class);
                    startActivity(intent);
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
}
