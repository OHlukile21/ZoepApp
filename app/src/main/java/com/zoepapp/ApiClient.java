package com.zoepapp;

import android.os.Handler;
import android.os.Looper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ApiClient {

    private static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)  // Set connection timeout
            .readTimeout(60, TimeUnit.SECONDS)     // Set read timeout
            .writeTimeout(60, TimeUnit.SECONDS)    // Set write timeout
            .build();

    // Define a callback interface to handle responses
    public interface ApiCallback {
        void onSuccess(JSONObject responseJson);
        void onFailure(String errorMessage);
    }

    // Define pre and post-execution callbacks
    public interface ApiExecutionCallback {
        void onBeforeExecution();
        void onAfterExecution();
    }

    // Generic method for making requests
    private void makeRequest(String url, String method, JSONObject jsonRequestBody, File imageFile,
                             final ApiExecutionCallback executionCallback, final ApiCallback apiCallback) {

        // Pre-execution logic (main thread)
        new Handler(Looper.getMainLooper()).post(() -> {
            if (executionCallback != null) {
                executionCallback.onBeforeExecution();
            }
        });

        // Prepare the request body
        RequestBody requestBody;

        if ("POST".equalsIgnoreCase(method)) {
            // Create a multipart body builder
            MultipartBody.Builder bodyBuilder = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM);

            // Add the image file if present
            if (imageFile != null) {
                bodyBuilder.addFormDataPart("image", imageFile.getName(),
                        RequestBody.create(MediaType.parse("image/*"), imageFile));
            }

            // Add JSON key-value pairs to the multipart request, if present
            if (jsonRequestBody != null) {
                jsonRequestBody.keys().forEachRemaining(key -> {
                    try {
                        String value = jsonRequestBody.getString(key);
                        bodyBuilder.addFormDataPart(key, value);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                });
            }

            // Build the multipart body
            requestBody = bodyBuilder.build();
        } else {
            requestBody = null; // No body for GET or other non-POST methods
        }

// Prepare the request
        Request.Builder requestBuilder = new Request.Builder().url(url);

        if ("POST".equalsIgnoreCase(method) && requestBody != null) {
            requestBuilder.post(requestBody);
        } else if ("GET".equalsIgnoreCase(method)) {
            requestBuilder.get();
        }

        Request request = requestBuilder.build();


        // Make the request asynchronously
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Post-execution logic (main thread)
                new Handler(Looper.getMainLooper()).post(() -> {
                    if (executionCallback != null) {
                        executionCallback.onAfterExecution();
                    }
                    apiCallback.onFailure("Request failed: " + e.getMessage());
                });
            }




            @Override
            public void onResponse(Call call, Response response) throws IOException {


                if (response.isSuccessful()) {
                    try {
                        String responseBody = response.body().string();
                        JSONObject jsonResponse = new JSONObject(responseBody);

                        new Handler(Looper.getMainLooper()).post(() -> {
                            if (executionCallback != null) {
                                executionCallback.onAfterExecution();
                            }
                            apiCallback.onSuccess(jsonResponse); // Pass the successful response
                        });
                    }
                    catch (Exception e) {
                        new Handler(Looper.getMainLooper()).post(() -> {
                            if (executionCallback != null) {
                                executionCallback.onAfterExecution();
                            }
                            apiCallback.onFailure(e.getMessage());
                        });
                    }
                }
                else{
                    new Handler(Looper.getMainLooper()).post(() -> {
                        if (executionCallback != null) {
                            executionCallback.onAfterExecution();
                        }
                        apiCallback.onFailure("failed reponse");
                    });
                }










            }
        });
    }

    // Utility method for GET requests
    public void get(String url, ApiExecutionCallback executionCallback, ApiCallback apiCallback) {
        makeRequest(url, "GET", null, null, executionCallback, apiCallback);
    }

    // Utility method for POST requests with JSON only
    public void post(String url, JSONObject requestBody, ApiExecutionCallback executionCallback, ApiCallback apiCallback) {
        makeRequest(url, "POST", requestBody, null, executionCallback, apiCallback);
    }

    // Utility method for POST requests with JSON and image
    public void post(String url, JSONObject requestBody, File imageFile, ApiExecutionCallback executionCallback, ApiCallback apiCallback) {
        makeRequest(url, "POST", requestBody, imageFile, executionCallback, apiCallback);
    }
}