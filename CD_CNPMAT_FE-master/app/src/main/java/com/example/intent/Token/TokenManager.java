package com.example.intent.Token;

import android.content.Context;
import android.content.SharedPreferences;

public class TokenManager {
    private static final String PREF_NAME = "AuthPrefs";
    private static final String KEY_TOKEN = "jwt_token";
    private static final String KEY_REFRESH_TOKEN = "refresh_token";
    private static final String KEY_STUDENT_DATA = "student_data";
    private static final String KEY_TEACHER_ID = "teacher_id";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_STUDENT_ID = "student_id";

    public void clearStudentData() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(KEY_STUDENT_DATA);
        editor.remove(KEY_STUDENT_ID);
        editor.apply();
    }

    public long getStudentId() {
        return prefs.getLong(KEY_STUDENT_ID, -1L);
    }

    public void saveStudentId(long studentId) {
        prefs.edit().putLong(KEY_STUDENT_ID, studentId).apply();
    }

    public void saveUserId(long userId) {
        prefs.edit().putLong(KEY_USER_ID, userId).apply();
    }

    public long getUserId() {
        return prefs.getLong(KEY_USER_ID, -1L);
    }

    public void deleteUserId() {
        prefs.edit().remove(KEY_USER_ID).apply();
    }

    public void saveTeacherId(Integer teacherId) {
            prefs.edit().putLong(KEY_TEACHER_ID, teacherId.longValue()).apply();
    }

    public Long getTeacherId() {
        return prefs.getLong(KEY_TEACHER_ID, -1L);
    }

    public void deleteTeacherId() {
        prefs.edit().remove(KEY_TEACHER_ID).apply();
    }

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

    public void saveStudentData(String studentJson) {
        prefs.edit().putString(KEY_STUDENT_DATA, studentJson).apply();
    }

    public String getStudentData() {
        return prefs.getString(KEY_STUDENT_DATA, null);
    }

    public void deleteStudentData() {
        prefs.edit().remove(KEY_STUDENT_DATA).apply();
    }
}
