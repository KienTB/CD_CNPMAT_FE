package com.example.intent;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.intent.Api.ApiResponse;
import com.example.intent.Api.ApiService;
import com.example.intent.Api.RetrofitClient;
import com.example.intent.Request.OtpVerificationRequest;
import com.example.intent.Token.TokenManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResetPasswordActivity extends AppCompatActivity {
    private EditText etOtp1, etOtp2, etOtp3, etOtp4, etOtp5, etOtp6;
    private Button btnSubmitOtp;
    private ImageView imgBackToExtension;
    private ApiService apiService;
    private TokenManager tokenManager;
    private String phoneNumber;
    private Long userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_reset_password);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeViews();
        setupData();
        setupOtpInputs();
        setupClickListeners();
    }

    private void initializeViews() {
        etOtp1 = findViewById(R.id.etOtp1);
        etOtp2 = findViewById(R.id.etOtp2);
        etOtp3 = findViewById(R.id.etOtp3);
        etOtp4 = findViewById(R.id.etOtp4);
        etOtp5 = findViewById(R.id.etOtp5);
        etOtp6 = findViewById(R.id.etOtp6);
        btnSubmitOtp = findViewById(R.id.btnSubmitOtp);
        imgBackToExtension = findViewById(R.id.imgBackToExtension);
    }

    private void setupData() {
        apiService = RetrofitClient.getInstance().createService(ApiService.class);
        tokenManager = new TokenManager(this);

        phoneNumber = getIntent().getStringExtra("phone_number");
        userId = getIntent().getLongExtra("user_id", -1);

        if (phoneNumber == null || userId == -1) {
            Toast.makeText(this, "Dữ liệu không hợp lệ", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void setupOtpInputs() {
        EditText[] otpFields = {etOtp1, etOtp2, etOtp3, etOtp4, etOtp5, etOtp6};

        for (int i = 0; i < otpFields.length; i++) {
            final EditText currentField = otpFields[i];
            final EditText nextField = (i < otpFields.length - 1) ? otpFields[i + 1] : null;

            currentField.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() == 1 && nextField != null) {
                        nextField.requestFocus();
                    }
                    checkOtpComplete();
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });
        }
    }

    private void checkOtpComplete() {
        String otp = getOtpFromInputs();
        btnSubmitOtp.setEnabled(otp.length() == 6);
    }

    private void setupClickListeners() {
        btnSubmitOtp.setOnClickListener(v -> verifyOtp());
        imgBackToExtension.setOnClickListener(v -> finish());
    }

    private void verifyOtp() {
        String otp = getOtpFromInputs();
        OtpVerificationRequest request = new OtpVerificationRequest(userId, otp);

        btnSubmitOtp.setEnabled(false); // Disable nút khi đang gửi request

        apiService.verifyOtp(request).enqueue(new Callback<ApiResponse<String>>() {
            @Override
            public void onResponse(Call<ApiResponse<String>> call, Response<ApiResponse<String>> response) {
                btnSubmitOtp.setEnabled(true); // Enable lại nút sau khi có response

                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    // Nếu verify thành công
                    Intent intent = new Intent(ResetPasswordActivity.this, NewPasswordActivity.class);
                    intent.putExtra("user_id", userId);
                    intent.putExtra("otp", otp);
                    startActivity(intent);
                    finish();
                } else {
                    // Xử lý các trường hợp lỗi
                    String errorMessage = "Mã OTP không đúng";
                    if (response.body() != null && response.body().getMessage() != null) {
                        // Lấy message lỗi từ server nếu có
                        errorMessage = response.body().getMessage();
                    }
                    Toast.makeText(ResetPasswordActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<String>> call, Throwable t) {
                btnSubmitOtp.setEnabled(true); // Enable lại nút khi có lỗi
                Toast.makeText(ResetPasswordActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getOtpFromInputs() {
        StringBuilder otp = new StringBuilder();
        otp.append(etOtp1.getText().toString());
        otp.append(etOtp2.getText().toString());
        otp.append(etOtp3.getText().toString());
        otp.append(etOtp4.getText().toString());
        otp.append(etOtp5.getText().toString());
        otp.append(etOtp6.getText().toString());
        return otp.toString();
    }
}