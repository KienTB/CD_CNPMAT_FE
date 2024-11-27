package com.example.intent;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.intent.Api.ApiResponse;
import com.example.intent.Api.ApiService;
import com.example.intent.Api.RetrofitClient;
import com.example.intent.Parent.AccountInformationActivity;
import com.example.intent.Parent.AddStudentActivity;
import com.example.intent.Parent.PaymentActivity;
import com.example.intent.Token.TokenManager;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    // UI Components
    private Button btnAddStudent, btnExtension, btnLogOut;
    private TextView txtName, txtPhone,
                    txtNameAI, txtEmailAI, txtPhoneAI, txtAddressAI;
    private TabHost myTab;
    private ImageView imgNextToPayment, imgNextToPayMentHP,
            imgNextToAccountInformation, imgNextToChangePW;

    // API and Token Management
    private TokenManager tokenManager;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize components
        initializeComponents();
        setupTabListener();
        setupClickListeners();
    }

    private void initializeComponents() {
        // Initialize views
        myTab = findViewById(R.id.myTab);
        btnAddStudent = findViewById(R.id.btnAddStudent);
        btnExtension = findViewById(R.id.btnExtension);
        btnLogOut = findViewById(R.id.btnLogOut);
        imgNextToPayment = findViewById(R.id.imgNextToPayment);
        imgNextToPayMentHP = findViewById(R.id.imgNextToPaymentHP);
        imgNextToAccountInformation = findViewById(R.id.imgNextToAccountInformation);
        imgNextToChangePW = findViewById(R.id.imgNextToChangePW);
        txtName = findViewById(R.id.txtName);
        txtPhone = findViewById(R.id.txtPhone);

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

    private void setupTabListener() {
        myTab.setOnTabChangedListener(tabId -> {
            if ("t5".equals(tabId)) {
                fetchUserProfile();
            }
        });
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

        imgNextToPayMentHP.setOnClickListener(v ->
                startActivity(new Intent(this, PaymentActivity.class))
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
                                MainActivity.this,
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