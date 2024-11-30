package com.example.intent.Parent;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.intent.Model.Student;
import com.example.intent.R;
import com.example.intent.StudentAdapter;

import java.util.ArrayList;
import java.util.List;

public class HomePageActivity extends AppCompatActivity {
    private RecyclerView rvStudents;
    private StudentAdapter studentAdapter;
    private List<Student> studentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_page);

        // Xử lý insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Thiết lập RecyclerView và Adapter
        rvStudents = findViewById(R.id.rvStudents);
        studentList = getStudentList(); // Thay bằng phương thức lấy dữ liệu thực tế
        studentAdapter = new StudentAdapter(studentList);
        rvStudents.setLayoutManager(new LinearLayoutManager(this));
        rvStudents.setAdapter(studentAdapter);
    }

    private List<Student> getStudentList() {
        List<Student> list = new ArrayList<>();
        list.add(new Student("Nguyen Van A", "10A1"));
        list.add(new Student("Tran Thi B", "10A2"));
        return list;
    }

    private void updateStudentList(List<Student> newList) {
        if (studentAdapter != null) {
            studentAdapter.updateStudentList(newList);
        } else {
            Log.e("HomePageActivity", "Adapter chưa được khởi tạo.");
        }
    }
}
