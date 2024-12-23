package com.example.intent.Parent;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.intent.Model.Student;
import com.example.intent.R;
import com.example.intent.Token.TokenManager;
import com.google.gson.Gson;

public class StudentDiaryActivity extends AppCompatActivity {
    ImageView imgBackToExtension;
    TextView tvStudentName, tvStudentId, tvStudentBirthDate, tvStudentGender, tvStudentClass, tvStudentAddress;
    private TokenManager tokenManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_student_diary);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        imgBackToExtension = findViewById(R.id.imgBackToExtension);
        imgBackToExtension.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tvStudentName = findViewById(R.id.tvStudentName);
        tvStudentId = findViewById(R.id.tvStudentId);
        tvStudentBirthDate = findViewById(R.id.tvStudentBirthDate);
        tvStudentGender = findViewById(R.id.tvStudentGender);
        tvStudentClass = findViewById(R.id.tvStudentClass);
        tvStudentAddress = findViewById(R.id.tvStudentAddress);

        tokenManager = new TokenManager(this);

        String studentJson = tokenManager.getStudentData();
        if (studentJson != null) {
            Gson gson = new Gson();
            Student student = gson.fromJson(studentJson, Student.class);

            tvStudentName.setText(student.getName());
            tvStudentId.setText(String.valueOf(student.getStudentId()));
            tvStudentBirthDate.setText(student.getBirthDate());
            tvStudentGender.setText(student.getGender());
            tvStudentClass.setText(student.getClass_name());
            tvStudentAddress.setText(student.getAddress());
        } else {
            tvStudentName.setText("Không có dữ liệu học sinh.");
        }
    }
}