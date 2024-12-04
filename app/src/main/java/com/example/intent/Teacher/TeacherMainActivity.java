package com.example.intent.Teacher;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.intent.Api.ApiResponse;
import com.example.intent.Api.ApiService;
import com.example.intent.Api.RetrofitClient;
import com.example.intent.ChangePasswordActivity;
import com.example.intent.LoginActivity;
import com.example.intent.Model.Grade;
import com.example.intent.Parent.AccountInformationActivity;
import com.example.intent.Parent.AddStudentActivity;
import com.example.intent.Parent.ParentMainActivity;
import com.example.intent.R;
import com.example.intent.StudentAdapter;
import com.example.intent.Token.TokenManager;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TeacherMainActivity extends AppCompatActivity {
    private TabHost myTab;
    private Button btnLogOut;
    private TextView txtName, txtPhone;
    private ImageView imgNextToAccountInformation, imgNextToChangePW,
            imgStudentList, imgGrade, imgClassManagement,
            imgLectureNotes, imgTeachingSchedule, imgEvaluation, imgReport;

    private CalendarView calendarView;

    private TokenManager tokenManager;
    private ApiService apiService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_teacher_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initializeComponents();
        setupClickListeners();
        setupTabListener();
    }
    private void initializeComponents() {
        myTab = findViewById(R.id.myTab);
        btnLogOut = findViewById(R.id.btnLogOut);
        txtName = findViewById(R.id.txtName);
        txtPhone = findViewById(R.id.txtPhone);

        imgNextToChangePW = findViewById(R.id.imgNextToChangePW);
        imgNextToAccountInformation = findViewById(R.id.imgNextToAccountInformation);
        imgStudentList = findViewById(R.id.imgStudentList);
        imgGrade = findViewById(R.id.imgGrade);
        imgClassManagement = findViewById(R.id.imgClassManagement);
        imgLectureNotes = findViewById(R.id.imgLectureNotes);
        imgTeachingSchedule = findViewById(R.id.imgTeachingSchedule);
        imgEvaluation = findViewById(R.id.imgEvaluation);
        imgReport = findViewById(R.id.imgReport);

        calendarView = findViewById(R.id.calendarView);

        long currentTimeMillis = System.currentTimeMillis();

        calendarView.setDate(currentTimeMillis, true, true);

        tokenManager = new TokenManager(this);
        apiService = RetrofitClient.getInstance().createService(ApiService.class);

        setupTabs();
    }

    private void setupTabs() {
        myTab.setup();

        addTab("t1", R.id.tab1, R.drawable.ic_home);
        addTab("t2", R.id.tab2, R.drawable.ic_notifications);
        addTab("t3", R.id.tab3, R.drawable.ic_settings);
    }

    private void addTab(String tabTag, int contentId, int iconResourceId) {
        TabHost.TabSpec spec = myTab.newTabSpec(tabTag);
        spec.setContent(contentId);
        spec.setIndicator("", getResources().getDrawable(iconResourceId));
        myTab.addTab(spec);
    }

    private void setupClickListeners() {
        btnLogOut.setOnClickListener(v ->
                showLogoutConfirmDialog()
        );

        imgNextToAccountInformation.setOnClickListener(v ->
                startActivity(new Intent(this, AccountInformationActivity.class)));

        imgNextToChangePW.setOnClickListener(v ->
                startActivity(new Intent(this, ChangePasswordActivity.class))
        );

        imgStudentList.setOnClickListener(v ->
                startActivity(new Intent(this, StudentListActivity.class))
        );

        imgGrade.setOnClickListener(v ->
                startActivity(new Intent(this, ScoreActivity.class))
        );

        imgClassManagement.setOnClickListener(v ->
                startActivity(new Intent(this, ClassManagementActivity.class))
        );

        imgLectureNotes.setOnClickListener(v ->
                startActivity(new Intent(this, LectureNoteActivity.class))
        );

        imgTeachingSchedule.setOnClickListener(v ->
                startActivity(new Intent(this, TeachingScheduleActivity.class))
        );

        imgEvaluation.setOnClickListener(v ->
                startActivity(new Intent(this, EvaluateActivity.class))
        );

        imgReport.setOnClickListener(v ->
                startActivity(new Intent(this, StatisticalActivity.class))
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

    private void setupTabListener() {
        myTab.setOnTabChangedListener(tabId -> {
            if ("t3".equals(tabId)) {
                fetchUserProfile();
            }
        });
    }

    private void fetchUserProfile() {
        String token = tokenManager.getToken();

        if (token == null || token.isEmpty()) {
            Toast.makeText(this, "Authentication token is missing", Toast.LENGTH_SHORT).show();
            return;
        }

        apiService.getUserProfile("Bearer " + token)
                .enqueue(new Callback<ApiResponse<Map<String, Object>>>() {
                    @Override
                    public void onResponse(
                            Call<ApiResponse<Map<String, Object>>> call,
                            Response<ApiResponse<Map<String, Object>>> response
                    ) {
                        handleUserProfileResponse(response);
                    }

                    @Override
                    public void onFailure(
                            Call<ApiResponse<Map<String, Object>>> call,
                            Throwable t
                    ) {
                        Toast.makeText(
                                TeacherMainActivity.this,
                                "An error occurred: " + t.getMessage(),
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
    }

    private void handleUserProfileResponse(Response<ApiResponse<Map<String, Object>>> response) {
        if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
            Map<String, Object> userProfile = response.body().getData();

            String name = (String) userProfile.get("name");
            String phone = (String) userProfile.get("phoneNumber");

            txtName.setText(name != null ? name : "N/A");
            txtPhone.setText(phone != null ? phone : "N/A");
        } else {
            Toast.makeText(this, "Failed to fetch user profile", Toast.LENGTH_SHORT).show();
        }
    }
}