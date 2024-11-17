package com.example.intent;

import static android.app.ProgressDialog.show;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    Button btnAddStudent, btnExtension, btnLogOut;
    TabHost myTab;
    ImageView imgNextToPayment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addControl();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnAddStudent = findViewById(R.id.btnAddStudent);
        btnAddStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddStudentActivity.class);
                startActivity(intent);
            }
        });

        btnExtension = findViewById(R.id.btnExtension);
        btnExtension.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View view) {
                myTab.setCurrentTab(1);
            }
        });

        btnLogOut = findViewById(R.id.btnLogOut);
        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSuccessDialog();
            }
        });

        imgNextToPayment = findViewById(R.id.imgNextToPayment);
        imgNextToPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PaymentActivity.class);
                startActivity(intent);
            }
        });

    }

    private void showSuccessDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Đăng xuất")
                .setMessage("Bạn có chắc chắn muốn đăng xuất khỏi tài khoản không?")
                .setPositiveButton("Có", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .setNegativeButton("Không", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    private void addControl() {

        myTab = findViewById(R.id.myTab);
        myTab.setup();
        TabHost.TabSpec spec1, spec2, spec3, spec4, spec5;
        //ứng với mỗi tab con thực hiên 4 việc
        spec1 = myTab.newTabSpec("t1");
        spec1.setContent(R.id.tab1);
        spec1.setIndicator("", getResources().getDrawable(R.drawable.ic_people));
        myTab.addTab(spec1);

        spec2 = myTab.newTabSpec("t2");
        spec2.setContent(R.id.tab2);
        spec2.setIndicator("", getResources().getDrawable(R.drawable.ic_extension));
        myTab.addTab(spec2);

        spec3 = myTab.newTabSpec("t3");
        spec3.setContent(R.id.tab3);
        spec3.setIndicator("", getResources().getDrawable(R.drawable.ic_home));
        myTab.addTab(spec3);

        spec4 = myTab.newTabSpec("t4");
        spec4.setContent(R.id.tab4);
        spec4.setIndicator("", getResources().getDrawable(R.drawable.ic_notifications));
        myTab.addTab(spec4);

        spec5 = myTab.newTabSpec("t5");
        spec5.setContent(R.id.tab5);
        spec5.setIndicator("", getResources().getDrawable(R.drawable.ic_settings));
        myTab.addTab(spec5);
    }
}