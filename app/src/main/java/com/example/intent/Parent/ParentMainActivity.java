package com.example.intent.Parent;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.intent.Api.ApiService;
import com.example.intent.Api.RetrofitClient;
import com.example.intent.ChangePasswordActivity;
import com.example.intent.LoginActivity;
import com.example.intent.Model.Student;
import com.example.intent.R;
import com.example.intent.StudentAdapter;
import com.example.intent.Token.TokenManager;
import com.google.gson.Gson;

import java.util.List;

public class ParentMainActivity extends AppCompatActivity {
    private Button btnAddStudent, btnExtension, btnLogOut;
    private TextView txtNameStudent, txtClassStudent;
    private TabHost myTab;
    private ImageView imgNextToActivityTracking, imgNextToAttendance, imgNextToMenu,
            imgNextToPayMentHP, imgNextToLearningCorner, imgNextToHealth, imgNextToService,
            imgNextToStudentDiary, imgNextToPayment, imgNextToAccountInformation, imgNextToChangePW;

    private TokenManager tokenManager;
    private ApiService apiService;

    private RecyclerView rvStudents;
    private StudentAdapter studentAdapter;
    private List<Student> studentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeComponents();
        setupClickListeners();

        loadStudentData();
    }

    private void initializeComponents() {
        myTab = findViewById(R.id.myTab);
        btnAddStudent = findViewById(R.id.btnAddStudent);
        btnExtension = findViewById(R.id.btnExtension);
        btnLogOut = findViewById(R.id.btnLogOut);
        imgNextToActivityTracking = findViewById(R.id.imgNextToActivityTracking);
        imgNextToAttendance = findViewById(R.id.imgNextToAttendance);
        imgNextToMenu = findViewById(R.id.imgNextToMenu);
        imgNextToPayMentHP = findViewById(R.id.imgNextToPaymentHP);
        imgNextToLearningCorner = findViewById(R.id.imgNextToLearningCorner);
        imgNextToHealth = findViewById(R.id.imgNextToHealth);
        imgNextToService = findViewById(R.id.imgNextToService);
        imgNextToStudentDiary = findViewById(R.id.imgNextToStudentDiary);
        imgNextToPayment = findViewById(R.id.imgNextToPayment);
        imgNextToAccountInformation = findViewById(R.id.imgNextToAccountInformation);
        imgNextToChangePW = findViewById(R.id.imgNextToChangePW);
        txtNameStudent = findViewById(R.id.txtNameStudent);
        txtClassStudent = findViewById(R.id.txtClassStudent);

        tokenManager = new TokenManager(this);
        apiService = RetrofitClient.getInstance().createService(ApiService.class);

        setupTabs();
    }

    private void setupTabs() {
        myTab.setup();

        addTab("t1", R.id.tab1, R.drawable.ic_people);
        addTab("t2", R.id.tab2, R.drawable.ic_extension);
        addTab("t3", R.id.tab3, R.drawable.ic_home);
        addTab("t4", R.id.tab4, R.drawable.ic_notifications);
        addTab("t5", R.id.tab5, R.drawable.ic_settings);
    }

    private void addTab(String tabTag, int contentId, int iconResourceId) {
        TabHost.TabSpec spec = myTab.newTabSpec(tabTag);
        spec.setContent(contentId);
        spec.setIndicator("", getResources().getDrawable(iconResourceId));
        myTab.addTab(spec);
    }

    private void setupClickListeners() {
        btnAddStudent.setOnClickListener(v ->
                startActivity(new Intent(this, AddStudentActivity.class))
        );

        btnExtension.setOnClickListener(v ->
                myTab.setCurrentTab(1)
        );

        btnLogOut.setOnClickListener(v ->
                showLogoutConfirmDialog()
        );

        setupNavigationListeners();
    }

    private void setupNavigationListeners() {
        imgNextToPayment.setOnClickListener(v ->
                startActivity(new Intent(this, PaymentActivity.class))
        );

        imgNextToActivityTracking.setOnClickListener(v ->
                startActivity(new Intent(this, ActivityTrackingActivity.class))
        );

        imgNextToAttendance.setOnClickListener(v ->
                startActivity(new Intent(this, AttendenceActivity.class))
        );

        imgNextToMenu.setOnClickListener(v ->
                startActivity(new Intent(this, MenuActivity.class))
        );

        imgNextToPayMentHP.setOnClickListener(v ->
                startActivity(new Intent(this, PaymentActivity.class))
        );

        imgNextToLearningCorner.setOnClickListener(v ->
                startActivity(new Intent(this, LearningCornerActivity.class))
        );

        imgNextToHealth.setOnClickListener(v ->
                startActivity(new Intent(this, HealthActivity.class))
        );

        imgNextToService.setOnClickListener(v ->
                startActivity(new Intent(this, ServiceActivity.class))
        );

        imgNextToStudentDiary.setOnClickListener(v ->
                startActivity(new Intent(this, StudentDiaryActivity.class))
        );

        imgNextToAccountInformation.setOnClickListener(v ->
                startActivity(new Intent(this, AccountInformationActivity.class))
        );

        imgNextToChangePW.setOnClickListener(v ->
                startActivity(new Intent(this, ChangePasswordActivity.class))
        );
    }

    private void showLogoutConfirmDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Đăng xuất")
                .setMessage("Bạn có chắc chắn muốn đăng xuất khỏi tài khoản không?")
                .setPositiveButton("Có", (dialog, which) -> {
                    Intent intent = new Intent(this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("Không", (dialog, which) -> dialog.dismiss())
                .show();
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
