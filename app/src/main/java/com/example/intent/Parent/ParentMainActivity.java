package com.example.intent.Parent;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.intent.Api.ApiResponse;
import com.example.intent.Api.ApiService;
import com.example.intent.Api.RetrofitClient;
import com.example.intent.ChangePasswordActivity;
import com.example.intent.InformationProductActivity;
import com.example.intent.LoginActivity;
import com.example.intent.Model.Notification;
import com.example.intent.Model.Student;
import com.example.intent.NotificationAdapter;
import com.example.intent.R;
import com.example.intent.StudentAdapter;
import com.example.intent.Teacher.TeacherMainActivity;
import com.example.intent.Token.TokenManager;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ParentMainActivity extends AppCompatActivity {
    private Button btnAddStudent, btnExtension, btnLogOut;
    private TextView txtNameStudent, txtClassStudent, txtName, txtPhone;
    private TabHost myTab;
    private ImageView imgNextToActivityTracking, imgNextToAttendance, imgNextToMenu,
            imgNextToPayMentHP, imgNextToLearningCorner, imgNextToHealth, imgNextToService,
            imgNextToStudentDiary, imgNextToPayment, imgNextToAccountInformation, imgNextToChangePW,
            imgNextToInformationProduct;

    private TokenManager tokenManager;
    private ApiService apiService;

    private RecyclerView rvStudents, recyclerViewNotifications;
    private StudentAdapter studentAdapter;
    private NotificationAdapter notificationAdapter;
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
        imgNextToInformationProduct = findViewById(R.id.imgNextToInformationProduct);
        imgNextToAccountInformation = findViewById(R.id.imgNextToAccountInformation);
        imgNextToChangePW = findViewById(R.id.imgNextToChangePW);
        txtNameStudent = findViewById(R.id.txtNameStudent);
        txtClassStudent = findViewById(R.id.txtClassStudent);
        txtName = findViewById(R.id.txtName);
        txtPhone = findViewById(R.id.txtPhone);

        recyclerViewNotifications = findViewById(R.id.recyclerViewNotifications);

        recyclerViewNotifications.setLayoutManager(new LinearLayoutManager(this));

        notificationAdapter = new NotificationAdapter(new ArrayList<>(), notification -> {
            Toast.makeText(this, "Clicked: " + notification.getTitle(), Toast.LENGTH_SHORT).show();
        });


        recyclerViewNotifications.setAdapter(notificationAdapter);

        tokenManager = new TokenManager(this);
        apiService = RetrofitClient.getInstance().createService(ApiService.class);

        setupTabs();
        setupTabListener();
        fetchNotifications();
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

        imgNextToInformationProduct.setOnClickListener(v ->
                startActivity(new Intent(this, InformationProductActivity.class))
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

        if (studentJson != null && !studentJson.isEmpty()) {
            Gson gson = new Gson();
            Student student = gson.fromJson(studentJson, Student.class);

            txtNameStudent.setText(student.getName());
            txtClassStudent.setText(student.getClass_name());
        } else {
            txtNameStudent.setText("Họ và tên");
            txtClassStudent.setText("Lớp");
        }
    }

    private void setDefaultStudentData() {
        txtNameStudent.setText("Họ và tên");
        txtClassStudent.setText("Lớp");
    }

    private void setupTabListener() {
        myTab.setOnTabChangedListener(tabId -> {
            if ("t5".equals(tabId)) {
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
                                ParentMainActivity.this,
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

    private void fetchNotifications() {
        String token = tokenManager.getToken();

        String bearerToken = "Bearer " + token;

        ApiService apiService = RetrofitClient.getInstance().createService(ApiService.class);
        Call<ApiResponse<List<Notification>>> call = apiService.getNotifications(bearerToken);

        call.enqueue(new Callback<ApiResponse<List<Notification>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Notification>>> call, Response<ApiResponse<List<Notification>>> response) {

                if (response.isSuccessful() && response.body() != null) {
                    List<Notification> notifications = response.body().getData();
                    notificationAdapter.updateData(notifications);
                } else {
                    Toast.makeText(ParentMainActivity.this, "Không thể lấy thông báo", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Notification>>> call, Throwable t) {
                Toast.makeText(ParentMainActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
