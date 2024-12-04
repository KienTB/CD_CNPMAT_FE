package com.example.intent.Parent;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.intent.Api.ApiService;
import com.example.intent.Api.RetrofitClient;
import com.example.intent.Model.Student;
import com.example.intent.R;
import com.example.intent.StudentAdapter;
import com.example.intent.Token.TokenManager;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class HomePageActivity extends AppCompatActivity {
    private TextView txtNameStudent, txtClassStudent;
    private TokenManager tokenManager;
    private ApiService apiService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_page);
        initializeComponents();

        loadStudentData();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void initializeComponents() {
        txtNameStudent = findViewById(R.id.txtNameStudent);
        txtClassStudent = findViewById(R.id.txtClassStudent);

        tokenManager = new TokenManager(this);
        apiService = RetrofitClient.getInstance().createService(ApiService.class);

    }

    private void loadStudentData() {
        String studentJson = tokenManager.getStudentData();
        if (studentJson != null) {
            Gson gson = new Gson();
            Student student = gson.fromJson(studentJson, Student.class);

            txtNameStudent.setText(student.getName());
            txtClassStudent.setText(student.getClass_name());
        } else {
            txtNameStudent.setText("Không có dữ liệu học sinh.");
            txtClassStudent.setText("");
        }
    }

}
