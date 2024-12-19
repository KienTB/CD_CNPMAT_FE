package com.example.intent.Teacher;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.intent.R;

public class StudentInfomationActivity extends AppCompatActivity {

    private ImageView imgBack;
    private TextView tvStudentName, tvStudentId, tvStudentClass,
            tvStudentBirthDate, tvStudentGender, tvStudentAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_detail);

        initializeViews();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            displayStudentDetails(extras);
        }

        imgBack.setOnClickListener(v -> finish());
    }

    private void initializeViews() {
        imgBack = findViewById(R.id.imgBack);
        tvStudentName = findViewById(R.id.tvStudentName);
        tvStudentId = findViewById(R.id.tvStudentId);
        tvStudentClass = findViewById(R.id.tvStudentClass);
        tvStudentBirthDate = findViewById(R.id.tvStudentBirthDate);
        tvStudentGender = findViewById(R.id.tvStudentGender);
        tvStudentAddress = findViewById(R.id.tvStudentAddress);
    }

    private void displayStudentDetails(Bundle extras) {
        tvStudentName.setText(extras.getString("name", "Chưa cập nhật"));
        tvStudentId.setText(String.format("Mã học sinh: %s",
                String.valueOf(extras.getLong("studentId", 0))));
        tvStudentClass.setText(String.format("Lớp: %s",
                extras.getString("class_name", "Chưa cập nhật")));
        tvStudentBirthDate.setText(String.format("Ngày sinh: %s",
                extras.getString("birthDate", "Chưa cập nhật")));
        tvStudentGender.setText(String.format("Giới tính: %s",
                extras.getString("gender", "Chưa cập nhật")));
        tvStudentAddress.setText(String.format("Địa chỉ: %s",
                extras.getString("address", "Chưa cập nhật")));
    }
}