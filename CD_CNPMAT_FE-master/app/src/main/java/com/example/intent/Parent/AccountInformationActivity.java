package com.example.intent.Parent;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
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
import com.example.intent.R;
import com.example.intent.Token.TokenManager;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccountInformationActivity extends AppCompatActivity {
    ImageView imgBack;
    private TokenManager tokenManager;
    private ApiService apiService;

    private TextView txtNameAI, txtEmailAI, txtPhoneAI, txtAddressAI;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_information);
        initializeComponents();

        fetchAccountInformation();
    }

    private void initializeComponents() {
        txtNameAI = findViewById(R.id.txtNameAI);
        txtEmailAI = findViewById(R.id.txtEmailAI);
        txtPhoneAI = findViewById(R.id.txtPhoneAI);
        txtAddressAI = findViewById(R.id.txtAddressAI);
        imgBack = findViewById(R.id.imgBack);

        tokenManager = new TokenManager(this);
        apiService = RetrofitClient.getInstance().createService(ApiService.class);

        imgBack.setOnClickListener(v -> finish());
    }

    private void fetchAccountInformation() {
        String token = tokenManager.getToken();

        if (token == null || token.isEmpty()) {
            Toast.makeText(this, "Authentication token is missing", Toast.LENGTH_SHORT).show();
            return;
        }

        apiService.getUserProfile("Bearer " + token).enqueue(new Callback<ApiResponse<Map<String, Object>>>() {
            @Override
            public void onResponse(Call<ApiResponse<Map<String, Object>>> call, Response<ApiResponse<Map<String, Object>>> response) {
                handleAccountInfoResponse(response);
            }

            @Override
            public void onFailure(Call<ApiResponse<Map<String, Object>>> call, Throwable t) {
                Toast.makeText(
                        AccountInformationActivity.this,
                        "Error fetching account info: " + t.getMessage(),
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }

    private void handleAccountInfoResponse(Response<ApiResponse<Map<String, Object>>> response) {
        if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
            Map<String, Object> accountInfo = response.body().getData();

            String name = (String) accountInfo.get("name");
            String email = (String) accountInfo.get("email");
            String phone = (String) accountInfo.get("phoneNumber");
            String address = (String) accountInfo.get("address");

            txtNameAI.setText(name != null ? name : "N/A");
            txtEmailAI.setText(email != null ? email : "N/A");
            txtPhoneAI.setText(phone != null ? phone : "N/A");
            txtAddressAI.setText(address != null ? address : "N/A");
        } else {
            Toast.makeText(this, "Failed to fetch account information", Toast.LENGTH_SHORT).show();
        }
    }
}