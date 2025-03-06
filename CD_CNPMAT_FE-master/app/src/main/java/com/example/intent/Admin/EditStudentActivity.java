package com.example.intent.Admin;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.intent.Api.ApiResponse;
import com.example.intent.Api.ApiService;
import com.example.intent.Api.RetrofitClient;
import com.example.intent.Model.Student;
import com.example.intent.R;
import com.example.intent.Request.UpdateStudentRequest;
import com.example.intent.Token.TokenManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditStudentActivity extends AppCompatActivity {

    private ImageView imgBack, imgStudentAvatar;
    private EditText edtName, edtBirthDate, edtGender, edtClass, edtUserId, edtAddress, edtTeacherId;
    private Button btnConfirm;
    private TokenManager tokenManager;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_student);

        imgBack = findViewById(R.id.imgBack);
        imgStudentAvatar = findViewById(R.id.imgStudentAvatar);
        edtName = findViewById(R.id.edtName);
        edtBirthDate = findViewById(R.id.edtBirthDate);
        edtGender = findViewById(R.id.edtGender);
        edtClass = findViewById(R.id.edtClass);
        edtUserId = findViewById(R.id.edtUserId);
        edtAddress = findViewById(R.id.edtAddress);
        edtTeacherId = findViewById(R.id.edtTeacherId);
        btnConfirm = findViewById(R.id.btnConfirm);

        edtBirthDate.setOnClickListener(v -> showDatePickerDialog());
        edtAddress.setOnClickListener(v -> showAddressSelectionDialog());
        edtClass.setOnClickListener(v -> showClassSelectionDialog());

        imgBack.setOnClickListener(v -> finish());

        Long studentId = getIntent().getLongExtra("studentId", -1);
        String name = getIntent().getStringExtra("name");
        String birthDate = getIntent().getStringExtra("birthDate");
        String class_name = getIntent().getStringExtra("class_name");
        String gender = getIntent().getStringExtra("gender");
        String address = getIntent().getStringExtra("address");
        Long userId = getIntent().getLongExtra("userId", -1);
        Long teacherId = getIntent().getLongExtra("teacherId", -1);

        edtName.setText(name);
        edtBirthDate.setText(birthDate);
        edtClass.setText(class_name);
        edtGender.setText(gender);
        edtAddress.setText(address);
        edtUserId.setText(String.valueOf(userId));
        edtTeacherId.setText(String.valueOf(teacherId));

        tokenManager = new TokenManager(this);
        apiService = RetrofitClient.getInstance().createService(ApiService.class);

        btnConfirm.setOnClickListener(v -> {
            String updatedName = edtName.getText().toString().trim();
            String updatedBirthDate = edtBirthDate.getText().toString().trim();
            String updatedClass = edtClass.getText().toString().trim();
            String updatedGender = edtGender.getText().toString().trim();
            String updatedAddress = edtAddress.getText().toString().trim();
            Long updatedUserId = Long.valueOf(edtUserId.getText().toString());
            Long updatedTeacherId = Long.valueOf(edtTeacherId.getText().toString());

            if (TextUtils.isEmpty(updatedName)) {
                Toast.makeText(this, "Vui lòng nhập họ và tên!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(updatedAddress)) {
                Toast.makeText(this, "Vui lòng nhập địa chỉ!", Toast.LENGTH_SHORT).show();
                return;
            }

            updateStudentInfo(studentId, updatedName, updatedBirthDate, updatedClass, updatedGender, updatedAddress, updatedUserId, updatedTeacherId);
        });

        imgBack.setOnClickListener(v -> finish());
    }

    private void showAddressSelectionDialog() {
        String[] classes = {"Phú La, Hà Đông, Hà Nội", "Kiến Hưng, Hà Đông, Hà Nội", "La Khê, Hà Đông, Hà Nội", "Mộ Lao, Hà Đông, Hà Nội", "Nguyễn Trãi, Hà Đông, Hà Nội", "Quang Trung, Hà Đông, Hà Nội", "Vạn Phúc, Hà Đông, Hà Nội", "Văn Quán, Hà Đông, Hà Nội"};
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Chọn địa chỉ")
                .setItems(classes, (dialog, which) -> edtAddress.setText(classes[which]))
                .show();
    }

    private void showClassSelectionDialog() {
        String[] classes = {"12a1", "12a2", "12a3", "12a4", "12a5", "12a6", "12a7", "12a8"};
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Chọn lớp học")
                .setItems(classes, (dialog, which) -> edtClass.setText(classes[which]))
                .show();
    }

    private void updateStudentInfo(Long studentId, String name, String birthDate, String class_name, String gender, String address, Long userId, Long teacherId) {
        UpdateStudentRequest request = new UpdateStudentRequest(name, birthDate, gender, class_name, userId, address, teacherId);
        String token = "Bearer " + tokenManager.getToken();

        apiService.updateStudent(token, studentId, request).enqueue(new Callback<ApiResponse<Student>>() {
            @Override
            public void onResponse(Call<ApiResponse<Student>> call, Response<ApiResponse<Student>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(EditStudentActivity.this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(EditStudentActivity.this, "Cập nhật thất bại!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Student>> call, Throwable t) {
                Toast.makeText(EditStudentActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year1, month1, dayOfMonth) -> {
            try {
                String selectedDate = dayOfMonth + "/" + (month1 + 1) + "/" + year1;
                String convertedDate = convertDateFormat(selectedDate);
                edtBirthDate.setText(convertedDate);
            } catch (Exception e) {
                Toast.makeText(this, "Lỗi định dạng ngày!", Toast.LENGTH_SHORT).show();
            }
        }, year, month, day);

        datePickerDialog.show();
    }

    private String convertDateFormat(String inputDate) throws Exception {
        SimpleDateFormat inputFormat = new SimpleDateFormat("d/M/yyyy");
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = inputFormat.parse(inputDate);
        return outputFormat.format(date);
    }
}
