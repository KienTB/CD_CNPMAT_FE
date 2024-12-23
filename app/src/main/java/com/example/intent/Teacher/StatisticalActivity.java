package com.example.intent.Teacher;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.intent.R;

public class StatisticalActivity extends AppCompatActivity {
    private ImageView imgBackToExtension;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_statistical);
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
    }
    public void openStatisticalAttendance(View view) {
        Intent intent = new Intent(this, StatisticalAttendanceActivity.class);
        startActivity(intent);
    }

    public void openStatisticalScore(View view) {
        Intent intent = new Intent(this, StatisticalGradeActivity.class);
        startActivity(intent);
    }
}