package com.example.intent.Token;

import android.content.Context;
import android.content.SharedPreferences;

public class TokenManager {
    private static final String PREF_NAME = "AuthPrefs";
    private static final String KEY_TOKEN = "jwt_token";
    private static final String KEY_REFRESH_TOKEN = "refresh_token";
    private static final String KEY_STUDENT_DATA = "student_data";

    private final SharedPreferences prefs;
    private Context context;

    public TokenManager(Context context) {
        this.context = context;
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveToken(String token) {
        prefs.edit().putString(KEY_TOKEN, token).apply();
    }

    public String getToken() {
        return prefs.getString(KEY_TOKEN, null);
    }

    public void saveRefeshToken(String refreshToken) {
        prefs.edit().putString(KEY_REFRESH_TOKEN, refreshToken).apply();
    }

    public String getRefreshToken() {
        return prefs.getString(KEY_REFRESH_TOKEN, null);
    }

    public void deleteToken() {
        prefs.edit().remove(KEY_TOKEN).apply();
    }

    public void deleteRefreshToken() {
        prefs.edit().remove(KEY_REFRESH_TOKEN).apply();
    }

    public boolean hasToken() {
        return getToken() != null;
    }

    // Lưu thông tin học sinh dưới dạng JSON string
    public void saveStudentData(String studentJson) {
        prefs.edit().putString(KEY_STUDENT_DATA, studentJson).apply();
    }

    // Lấy thông tin học sinh
    public String getStudentData() {
        return prefs.getString(KEY_STUDENT_DATA, null);
    }

    // Xóa thông tin học sinh
    public void deleteStudentData() {
        prefs.edit().remove(KEY_STUDENT_DATA).apply();
    }
}
