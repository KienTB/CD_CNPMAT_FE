package com.example.intent.Teacher;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.SearchView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.intent.Api.ApiService;
import com.example.intent.Api.RetrofitClient;
import com.example.intent.Adapter.GradeTeacherAdapter;
import com.example.intent.Model.Grade;
import com.example.intent.R;
import com.example.intent.Token.TokenManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StatisticalGradeDetailActivity extends AppCompatActivity {
    private ImageView imgBackToExtension;
    private RecyclerView rvGrade;
    private GradeTeacherAdapter gradeTeacherAdapter;
    private List<Grade> gradeList = new ArrayList<>();
    private TokenManager tokenManager;
    private ApiService apiService;
    private SearchView searchView;
    private Long studentId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_statistical_grade_detail);

        imgBackToExtension = findViewById(R.id.imgBackToExtension);
        rvGrade = findViewById(R.id.rvGrade);
        searchView = findViewById(R.id.searchView);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tokenManager = new TokenManager(this);
        apiService = RetrofitClient.getInstance().createService(ApiService.class);

        imgBackToExtension.setOnClickListener(v -> finish());

        rvGrade.setLayoutManager(new LinearLayoutManager(this));
        gradeTeacherAdapter = new GradeTeacherAdapter(gradeList, this::onGradeSelected);
        rvGrade.setAdapter(gradeTeacherAdapter);

        Intent intent = getIntent();
        studentId = intent.getLongExtra("studentId", -1);

        if (studentId != -1) {
            loadAttendanceData(studentId);
        }
        setupSearchView();
    }

    private void onGradeSelected(Grade grade) {
        Long gradeId = grade.getGradeId();
        long safeGradeId = (gradeId != null) ? gradeId : -1L;

        Intent intent = new Intent(this, EditAndDeleteGradeActivity.class);
        intent.putExtra("gradeId", safeGradeId);
        intent.putExtra("studentId", studentId);
        intent.putExtra("subject", grade.getSubject());
        intent.putExtra("score", String.valueOf(grade.getScore()));
        intent.putExtra("term", grade.getTerm());
        startActivity(intent);
        Log.d("DEBUG", "Grade ID: " + safeGradeId);
        Log.d("DEBUG", "Student ID: " + studentId);
        Log.d("DEBUG", "Subject: " + grade.getSubject());
        Log.d("DEBUG", "Score: " + grade.getScore());
        Log.d("DEBUG", "Term: " + grade.getTerm());
    }

    private void setupSearchView() {
        searchView.setOnQueryTextFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                searchView.clearFocus();
                showTermSelectionDialog();
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterGradeByTerm(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    gradeTeacherAdapter.updateData(gradeList);
                } else {
                    filterGradeByTerm(newText);
                }
                return true;
            }
        });
    }

    private void showTermSelectionDialog() {
        String[] terms = {"Giữa kỳ", "Cuối kỳ", "Tổng kết"};
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Chọn kỳ học")
                .setItems(terms, (dialog, which) -> {
                    searchView.setQuery(terms[which], true);
                    filterGradeByTerm(terms[which]);
                })
                .show();
    }


    private void filterGradeByTerm(String term) {
        List<Grade> filteredList = new ArrayList<>();
        for (Grade grade : gradeList) {
            if (grade.getTerm().equalsIgnoreCase(term)) {
                filteredList.add(grade);
            }
        }
        gradeTeacherAdapter.updateData(filteredList);
    }

    private void loadAttendanceData(Long studentId) {
        String token = tokenManager.getToken();

        apiService.getGradesForTeacher("Bearer " + token, studentId)
                .enqueue(new Callback<List<Grade>>() {
                    @Override
                    public void onResponse(Call<List<Grade>> call, Response<List<Grade>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            gradeList.clear();
                            gradeList.addAll(response.body());
                            for (Grade grade : gradeList) {
                                Log.d("API_DEBUG", "Schedule ID: " + grade.getGradeId());
                                if (grade.getStudent() != null) {
                                    Log.d("API_DEBUG", "Student ID: " + grade.getStudent().getStudentId());
                                    Log.d("API_DEBUG", "Student Name: " + grade.getStudent().getName());
                                } else {
                                    Log.d("API_DEBUG", "Student is null");
                                }
                            }
                            runOnUiThread(() -> gradeTeacherAdapter.notifyDataSetChanged());
                        } else {
                            Log.e("API_DEBUG", "Error: " + response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Grade>> call, Throwable t) {
                        Log.e("API_DEBUG", "API Call Failed", t);
                    }
                });
    }

}