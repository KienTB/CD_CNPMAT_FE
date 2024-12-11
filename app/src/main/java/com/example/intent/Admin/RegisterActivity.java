package com.example.intent.Admin;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.intent.Api.ApiResponse;
import com.example.intent.Api.ApiService;
import com.example.intent.Api.RetrofitClient;
import com.example.intent.Helper.StringHelper;
import com.example.intent.LoginActivity;
import com.example.intent.Model.User;
import com.example.intent.R;
import com.example.intent.Request.RegisterRequest;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    EditText edtName, edtPhoneNumber, edtEmail, edtPassword, edtAddress, edtConfirm;
    Button btnRegister;
    RadioGroup radioGroupRole;
    ImageView imgBackToExtension;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        edtName = findViewById(R.id.edtName);
        edtPhoneNumber = findViewById(R.id.edtPhoneNumber);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        edtAddress = findViewById(R.id.edtAddress);
        edtConfirm = findViewById(R.id.edtConfirm);
        radioGroupRole = findViewById(R.id.radioGroupRole);
        imgBackToExtension = findViewById(R.id.imgBackToExtension);
        imgBackToExtension.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnRegister = findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                processFormFields();
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void processFormFields() {
        if (!validateName() || !validatePhoneNumber() || !validateEmail() || !validatePasswordAndConfirm() || !validateAddress() || !validateRole()) {
            return;
        }

        String phoneNumber = edtPhoneNumber.getText().toString();
        String password = edtPassword.getText().toString();
        String email = edtEmail.getText().toString();
        String name = edtName.getText().toString();
        String address = edtAddress.getText().toString();
        String role = getSelectedRole();

        RegisterRequest registerRequest = new RegisterRequest(phoneNumber, password, email, name, address, role);
        registerUser(registerRequest);
    }

    private String getSelectedRole() {
        int selectedId = radioGroupRole.getCheckedRadioButtonId();
        if (selectedId == R.id.radioTeacher) {
            return "teacher";
        } else if (selectedId == R.id.radioParent) {
            return "parent";
        }
        return null;
    }

        public void registerUser(RegisterRequest registerRequest) {
        ApiService apiService = RetrofitClient.getInstance().createService(ApiService.class);
        Call<ApiResponse<User>> call = apiService.register(registerRequest);

        call.enqueue(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        showSuccessDialog();
                    } else {
                        Toast.makeText(RegisterActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(RegisterActivity.this, "Registration failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                Toast.makeText(RegisterActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showSuccessDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Đăng ký thành công")
                .setMessage("Bạn đã đăng ký thành công. Hãy đăng nhập!")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .show();
    }

    public boolean validateName() {
        String name =  edtName.getText().toString();
        if (name.isEmpty()) {
            edtName.setError("Name cannot be empty");
        } else {
            edtName.setError(null);
            return true;
        }
        return false;
    }

    public boolean validatePhoneNumber() {
        String name = edtName.getText().toString();
        if (name.isEmpty()) {
            edtName.setError("PhoneNumber cannot be empty");
        } else {
            edtName.setError(null);
            return true;
        }
        return false;
    }

    public boolean validateEmail() {
        String email = edtEmail.getText().toString();
        if (email.isEmpty()) {
            edtEmail.setError("Email cannot be empty");
            return false;
        } else if (!StringHelper.regexEmailValidationPattern(email)) {
            edtEmail.setError("please enter a valid email");
            return false;
        }
            edtEmail.setError(null);
            return true;
        }

    public boolean validatePasswordAndConfirm() {
        String password = edtPassword.getText().toString();
        String confirm = edtConfirm.getText().toString();
        if (password.isEmpty()) {
            edtPassword.setError("Password cannot be empty");
            edtConfirm.setError("Confirm fields cannot be empty");
            return false;
        } else if (edtPassword.equals(edtConfirm)) {
            edtPassword.setError("password do not match");
            return false;
        } else if (confirm.isEmpty()) {
            edtConfirm.setError("confirm fields cannot be empty");
            return false;
        } else {
            edtPassword.setError(null);
            edtConfirm.setError(null);
            return true;
        }
    }

    public boolean validateAddress() {
        String address = edtAddress.getText().toString();
        if (address.isEmpty()) {
            edtAddress.setError("Address cannot be empty");
        } else {
            edtAddress.setError(null);
            return true;
        }
        return false;
    }

    public boolean validateRole() {
        if (radioGroupRole.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Please select a role", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

}