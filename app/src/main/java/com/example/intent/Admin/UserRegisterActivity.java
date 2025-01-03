package com.example.intent.Admin;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.example.intent.Model.User;
import com.example.intent.R;
import com.example.intent.Request.RegisterRequest;
import com.example.intent.Token.TokenManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserRegisterActivity extends AppCompatActivity {

    EditText edtName, edtPhoneNumber, edtEmail, edtPassword, edtAddress, edtConfirm;
    Button btnRegister;
    RadioGroup radioGroupRole;
    ImageView imgBackToExtension;
    private TokenManager tokenManager;
    private ApiService apiService;

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

        edtPassword.setText("123456");
        edtConfirm.setText("123456");

        tokenManager = new TokenManager(this);
        apiService = RetrofitClient.getInstance().createService(ApiService.class);

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

        edtAddress.setOnClickListener(v -> showAddressSelectionDialog());
    }

    private void showAddressSelectionDialog() {
        String[] classes = {"Phú La, Hà Đông, Hà Nội", "Kiến Hưng, Hà Đông, Hà Nội", "La Khê, Hà Đông, Hà Nội", "Mộ Lao, Hà Đông, Hà Nội", "Nguyễn Trãi, Hà Đông, Hà Nội", "Quang Trung, Hà Đông, Hà Nội", "Vạn Phúc, Hà Đông, Hà Nội", "Văn Quán, Hà Đông, Hà Nội"};
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Chọn địa chỉ")
                .setItems(classes, (dialog, which) -> edtAddress.setText(classes[which]))
                .show();
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

        RegisterRequest registerRequest = new RegisterRequest(phoneNumber, password, name, email, address, role);
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
        String token = "Bearer " + tokenManager.getToken();
            Log.d("RegisterRequest", "phoneNumber: " + registerRequest.getPhoneNumber());
            Log.d("RegisterRequest", "password: " + registerRequest.getPassword());
            Log.d("RegisterRequest", "name: " + registerRequest.getName());
            Log.d("RegisterRequest", "email: " + registerRequest.getEmail());
            Log.d("RegisterRequest", "address: " + registerRequest.getAddress());
            Log.d("RegisterRequest", "role: " + registerRequest.getRole());
        Call<ApiResponse<User>> call = apiService.register(token, registerRequest);

        call.enqueue(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        showSuccessDialog();
                    } else {
                        Toast.makeText(UserRegisterActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(UserRegisterActivity.this, "Registration failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                Toast.makeText(UserRegisterActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showSuccessDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Thêm người dùng thành công")
                .setMessage("Bạn đã thêm người dùng thành công!")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(UserRegisterActivity.this, UserManagementActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
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
        String phoneNumber = edtPhoneNumber.getText().toString();
        if (phoneNumber.isEmpty()) {
            edtPhoneNumber.setError("PhoneNumber cannot be empty");
        } else {
            edtPhoneNumber.setError(null);
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
            return false;
        } else if (!password.equals(confirm)) {
            edtPassword.setError("Passwords do not match");
            edtConfirm.setError("Passwords do not match");
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