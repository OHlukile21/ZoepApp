package com.zoepapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    MaterialButton btnLogin;
    TextInputEditText edtxtEmail, edtxtPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnLogin = findViewById(R.id.btnLogin);
        edtxtEmail = findViewById(R.id.edtxtEmail);
        edtxtPassword = findViewById(R.id.edtxtPassword);

        // ---------------------------------------------------------
        // ⭐ STEP 3 — AUTO CHECK IF LAST LOGIN WAS MORE THAN 24 HOURS AGO
        // ---------------------------------------------------------
        String userId = SharedPrefsHelper.getValue(this, SharedPrefsHelper.KEY_USER_ID);
        String lastLoginTimeStr = SharedPrefsHelper.getValue(this, "LAST_LOGIN_TIME");

        if (userId != null && !userId.equals("") && lastLoginTimeStr != null && !lastLoginTimeStr.equals("")) {

            long lastLoginTime = Long.parseLong(lastLoginTimeStr);
            long now = System.currentTimeMillis();

            long hoursSinceLastLogin = (now - lastLoginTime) / (1000 * 60 * 60);

            if (hoursSinceLastLogin < 24) {
                // ⭐ VALID SESSION — AUTO LOG THE USER IN
                Intent intent = new Intent(LoginActivity.this, SiteActivity.class);
                startActivity(intent);
                finish();
                return;
            } else {
                // ⭐ EXPIRED — FORCE LOGOUT
                SharedPrefsHelper.clearSharedPrefs(this);
            }
        }

        // ---------------------------------------------------------
        // LOGIN BUTTON
        // ---------------------------------------------------------
        btnLogin.setOnClickListener(v -> {
            String email = edtxtEmail.getText().toString().trim();
            String userPassword = edtxtPassword.getText().toString().trim();

            Utilities.closeKeyboard(LoginActivity.this, btnLogin);

            if (!email.equals("") && !userPassword.equals("")) {
                if (Utilities.isValidEmail(email)) {
                    if (Utilities.checkConnectivity(LoginActivity.this)) {
                        login(email, userPassword);
                    } else {
                        Utilities.showCustomSnackbar(LoginActivity.this,
                                "No internet connection!",
                                Snackbar.LENGTH_SHORT,
                                R.drawable.danger, R.color.white,
                                R.color.color_danger, Gravity.BOTTOM);
                    }
                } else {
                    Utilities.showCustomSnackbar(LoginActivity.this,
                            "Please enter a valid email",
                            Snackbar.LENGTH_SHORT,
                            R.drawable.danger, R.color.white,
                            R.color.color_danger, Gravity.BOTTOM);
                }
            } else {
                Utilities.showCustomSnackbar(LoginActivity.this,
                        "Please enter all values",
                        Snackbar.LENGTH_SHORT,
                        R.drawable.danger, R.color.white,
                        R.color.color_danger, Gravity.BOTTOM);
            }
        });
    }

    // ---------------------------------------------------------
    // LOGIN FUNCTION
    // ---------------------------------------------------------
    public void login(String email, String password) {
        MyLoader myLoader = new MyLoader(this);

        JSONObject requestBodyJson = new JSONObject();
        try {
            requestBodyJson.put("Email", email);
            requestBodyJson.put("Password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiClient apiClient = new ApiClient();
        apiClient.post(Global.LOGIN_URL, requestBodyJson,
                new ApiClient.ApiExecutionCallback() {
                    @Override
                    public void onBeforeExecution() {
                        myLoader.showIndeterminantLoader("Logging In");
                    }

                    @Override
                    public void onAfterExecution() {
                        myLoader.cancelIndeterminantLoader();
                    }
                },
                new ApiClient.ApiCallback() {
                    @Override
                    public void onSuccess(JSONObject responseJson) {

                        try {
                            int status = responseJson.getInt("Status");

                            if (status == 200) {

                                JSONObject dataObj = responseJson.getJSONObject("Data");
                                JSONObject userObj = dataObj.getJSONObject("User");

                                int userId = userObj.getInt("UserId");
                                int bcaId = userObj.getInt("BcaId");
                                String userEmail = userObj.getString("Email");
                                String roleName = userObj.getString("RoleName");
                                String userName = userObj.getString("UserName");

                                SharedPrefsHelper.setValue(LoginActivity.this, SharedPrefsHelper.KEY_USER_ID, String.valueOf(userId));
                                SharedPrefsHelper.setValue(LoginActivity.this, SharedPrefsHelper.KEY_EMAIL, userEmail);
                                SharedPrefsHelper.setValue(LoginActivity.this, SharedPrefsHelper.KEY_USER_NAME, userName);
                                SharedPrefsHelper.setValue(LoginActivity.this, SharedPrefsHelper.KEY_USER_ROLE, roleName);
                                SharedPrefsHelper.setValue(LoginActivity.this, SharedPrefsHelper.KEY_BCA_ID, String.valueOf(bcaId));

                                // ---------------------------------------------------------
                                // ⭐ STEP 2 — SAVE LOGIN TIME
                                // ---------------------------------------------------------
                                long now = System.currentTimeMillis();
                                SharedPrefsHelper.setValue(LoginActivity.this, "LAST_LOGIN_TIME", String.valueOf(now));

                                Intent intent = new Intent(LoginActivity.this, SiteActivity.class);
                                startActivity(intent);
                                finish();

                            } else {
                                JSONObject error = responseJson.getJSONObject("Error");
                                String errorDetails = error.optString("Details", "Unknown error");

                                Utilities.showCustomSnackbar(LoginActivity.this,
                                        errorDetails,
                                        Snackbar.LENGTH_SHORT,
                                        R.drawable.danger,
                                        R.color.white,
                                        R.color.color_danger,
                                        Gravity.BOTTOM);

                                Log.e("Login Error", "Invalid status code: " + status);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        Utilities.showCustomSnackbar(LoginActivity.this,
                                "Something went wrong, please try again",
                                Snackbar.LENGTH_SHORT,
                                R.drawable.danger,
                                R.color.white,
                                R.color.color_danger,
                                Gravity.BOTTOM);
                    }
                });
    }
}
