package com.zoepapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SiteDetailActivity extends AppCompatActivity {

    ImageView imgSite;
    private Uri photoURI;
    private File photoFile;
//    SiteincidentAdapter adapter;
SiteincidentAdapter adapter;
    RecyclerView recyclerView;
    ImageView imgIncident;
    LinearLayout llNositesAlert;
    MaterialButton btnNewIncident;
    TextView txtSiteName;
    List<ModelSiteIncident> lisIncidents= new ArrayList<>();
    MaterialButton btnRetry;
    int siteId;
    private static final int PERMISSION_REQUEST_CODE = 100;

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                dispatchTakePictureIntent();

            } else {
                Utilities.showCustomSnackbar(SiteDetailActivity.this, "Permissions Denied", Snackbar.LENGTH_SHORT,
                        R.drawable.danger, R.color.white, R.color.color_danger, Gravity.BOTTOM);

            }
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_site_detail);

        imgSite= findViewById(R.id.imgSite);
        llNositesAlert = findViewById(R.id.llNoIncidentsAlert);
        btnNewIncident = findViewById(R.id.btnNewIncident);
        recyclerView = findViewById(R.id.rvIncidents);
        txtSiteName = findViewById(R.id.txtSiteName);
        btnRetry = findViewById(R.id.btnRetry);
        llNositesAlert.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);


        RequestOptions requestOptions = new RequestOptions()
                .fitCenter()
                .placeholder(R.drawable.loading) // A local GIF in res/drawable
                .error(R.drawable.no_image)
                .fallback(R.drawable.no_image)  // Fallback image for errors
                .transform(new RoundedCorners(5)); // Apply rounded corners


        btnNewIncident.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showNewIncidentBottomSheet();
            }
        });

        btnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getUserData(siteId);
            }
        });

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            siteId = extras.getInt("siteId");
            String imageUrl  = extras.getString("siteImage");
            String siteName  = extras.getString("siteName");


            txtSiteName.setText(siteName);

            Glide.with(this)
                    .load(imageUrl)
                    .apply(requestOptions)
                    .into(imgSite);

            getUserData(siteId);
        }


    }


    private void showNewIncidentBottomSheet() {

        photoFile=null;
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_add_incident, null);
        bottomSheetDialog.setContentView(bottomSheetView);

        // Access BottomSheetBehavior
        BottomSheetBehavior<View> behavior = BottomSheetBehavior.from((View) bottomSheetView.getParent());
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED); // Expand fully

        final EditText edtxtFindings = bottomSheetView.findViewById(R.id.edtxtFindings);
        final EditText edtxtTimeFrame = bottomSheetView.findViewById(R.id.edtxtTimeFrame);
        final EditText edtxtRemedialAction = bottomSheetView.findViewById(R.id.edtxtRemedialAction);

        imgIncident = bottomSheetView.findViewById(R.id.imgIncident);





        MaterialButton btnSubmit = bottomSheetView.findViewById(R.id.btnSubmitNewIncident);
        MaterialButton btnClose = bottomSheetView.findViewById(R.id.btnClose);

        MaterialButton tsSeverity1 = bottomSheetView.findViewById(R.id.tsSeverity1);
        MaterialButton tsSeverity2 = bottomSheetView.findViewById(R.id.tsSeverity2);
        MaterialButton tsSeverity3 = bottomSheetView.findViewById(R.id.tsSeverity3);
        MaterialButton tsSeverity4 = bottomSheetView.findViewById(R.id.tsSeverity4);


        imgIncident.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(checkPermissions()){
                    dispatchTakePictureIntent();
                }

            }
        });

        tsSeverity1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggle(tsSeverity1,R.color.selected_button_text_color,R.color.selected_button_stroke_color,R.color.selected_button_color,R.color.unselected_button_text_color,R.color.unselected_button_stroke_color);
            }
        });

        tsSeverity2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggle(tsSeverity2,R.color.selected_button_text_color,R.color.selected_button_stroke_color,R.color.selected_button_color,R.color.unselected_button_text_color,R.color.unselected_button_stroke_color);

            }
        });

        tsSeverity3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggle(tsSeverity3,R.color.selected_button_text_color,R.color.selected_button_stroke_color,R.color.selected_button_color,R.color.unselected_button_text_color,R.color.unselected_button_stroke_color);

            }
        });

        tsSeverity4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggle(tsSeverity4,R.color.selected_button_text_color,R.color.selected_button_stroke_color,R.color.selected_button_color,R.color.unselected_button_text_color,R.color.unselected_button_stroke_color);

            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String findings = edtxtFindings.getText().toString().trim();
                String timeFrame = edtxtTimeFrame.getText().toString().trim();
                String remedialActions = edtxtRemedialAction.getText().toString().trim();
                String severityType="";

                if(tsSeverity1.isChecked()){
                    severityType="1";
                }
                else if(tsSeverity2.isChecked()){
                    severityType="2";
                }
                else if(tsSeverity3.isChecked()){
                    severityType="3";
                }
                else if(tsSeverity4.isChecked()){
                    severityType="4";
                }

                //   String type = edtxtProprtyType.getText().toString().trim();

                if(!findings.equals("") && !timeFrame.equals("") && !remedialActions.equals("")  && !severityType.equals("") ){

                    bottomSheetDialog.dismiss();
                    addReport(findings,timeFrame,severityType,remedialActions);
                }
                else{
                    bottomSheetDialog.hide();
                    Utilities.showCustomSnackbar(SiteDetailActivity.this, "Please enter all values", Snackbar.LENGTH_SHORT,
                            R.drawable.danger, R.color.white, R.color.color_danger, Gravity.TOP);
                    bottomSheetDialog.show();
                }

            }
        });


        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
            }
        });


        bottomSheetDialog.show();
    }

    private void addReport(String findings, String timeFrame, String severity, String remedialActions) {
        MyLoader myLoader = new MyLoader(this);

        JSONObject json = new JSONObject();
        try {
            json.put("findings", findings);
            json.put("timeFrame", timeFrame);
            json.put("severity", severity);
            json.put("remedialActions", remedialActions);
            json.put("siteId",siteId);
        }
        catch (Exception e){

        }

        String url = Global.ADD_INCIDENT_URL;


        ApiClient client = new ApiClient();
        client.post(url, json, photoFile, new ApiClient.ApiExecutionCallback() {
            @Override
            public void onBeforeExecution() {

                myLoader.showIndeterminantLoader("Submitting New Incident");

            }

            @Override
            public void onAfterExecution() {
                myLoader.cancelIndeterminantLoader();

                getUserData(siteId);

            }}, new ApiClient.ApiCallback() {
            @Override
            public void onSuccess(JSONObject responseJson) {
                try {
                    int status = responseJson.getInt("Status");
                    if (status == 200) {
                        // Process the response

                        Utilities.showCustomSnackbar(SiteDetailActivity.this, "Incident Added Successfully!", Snackbar.LENGTH_SHORT,
                                R.drawable.check_circle, R.color.white, R.color.color_sucess, Gravity.BOTTOM);

                        Intent intent = new Intent();
                        intent.putExtra("itemAdded", true);
                        setResult(RESULT_OK, intent);

                    } else {
                        // Handle unsuccessful status codes
                        JSONObject error = responseJson.optJSONObject("Error");
                        String errorDetails = (error != null) ? error.optString("Details", "Unknown error") : "Unknown error";

                        Utilities.showCustomSnackbar(SiteDetailActivity.this, errorDetails, Snackbar.LENGTH_SHORT,
                                R.drawable.danger, R.color.white, R.color.color_danger, Gravity.BOTTOM);

                        //    Log.e("Get User Data Error", "Invalid status code: " + status);
                    }
                } catch (JSONException e) {
                    Utilities.showCustomSnackbar(SiteDetailActivity.this, "1: "+ e.getMessage(), Snackbar.LENGTH_SHORT,
                            R.drawable.danger, R.color.white, R.color.color_danger, Gravity.BOTTOM);

                }
            }

            @Override
            public void onFailure(String errorMessage) {
                Utilities.showCustomSnackbar(SiteDetailActivity.this,"2 :"+ errorMessage, Snackbar.LENGTH_SHORT,
                        R.drawable.danger, R.color.white, R.color.color_danger, Gravity.BOTTOM);

            }
        });
    }

    private void toggle(MaterialButton toggleSelect,int sltBtnTxtColor,int sltBtnStrokeColor,int sltBtnColor,int unSltBtnTextColor,int unSltStrokeColor) {

        LinearLayout viewParent = (LinearLayout) toggleSelect.getParent();

        for (int i = 0; i < viewParent.getChildCount(); i++) {
            View view = viewParent.getChildAt(i);
            MaterialButton button = view.findViewWithTag(toggleSelect.getTag());

            if (button != null) {
                if (button == toggleSelect) {
                    // Selected button
                    button.setBackgroundColor(ContextCompat.getColor(SiteDetailActivity.this, sltBtnColor));
                    button.setStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(SiteDetailActivity.this, sltBtnStrokeColor)));
                    button.setStrokeWidth(5);
                    button.setTextColor(ContextCompat.getColor(SiteDetailActivity.this, sltBtnTxtColor));

                } else {
                    // Other buttons
                    button.setBackgroundColor(ContextCompat.getColor(SiteDetailActivity.this, R.color.color_transparent));
                    button.setTextColor(ContextCompat.getColor(SiteDetailActivity.this, unSltBtnTextColor));
                    button.setStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(SiteDetailActivity.this, unSltStrokeColor)));
                    button.setStrokeWidth(2);
                }
            }
        }


    }

    public void getUserData(int siteId) {
        // Show loader before making the API call
        MyLoader myLoader = new MyLoader(this);
        // Build the URL with UserId as a query parameter
        String url = Global.FACILITY_INCIDENTS_URL + "?siteId=" + siteId;

        // Use the updated ApiClient to make the request
        ApiClient apiClient = new ApiClient();

        apiClient.get(url, new ApiClient.ApiExecutionCallback() {
            @Override
            public void onBeforeExecution() {
                // Called before the request is initiated, show loader here if not shown already
                myLoader.showIndeterminantLoader("Fetching Site Incidents");
                llNositesAlert.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);

            }

            @Override
            public void onAfterExecution() {
                // Ensure cleanup operations and update the UI

                myLoader.cancelIndeterminantLoader();

                adapter.notifyDataSetChanged();
            }
        }, new ApiClient.ApiCallback() {

            @Override
            public void onSuccess(JSONObject responseJson) {
                // Hide the loader once the response is received

                try {
                    int status = responseJson.getInt("Status");
                    if (status == 200) {
                       JSONArray dataArray = responseJson.getJSONArray("Data");

                        // Clear and update the list
                        lisIncidents.clear();
                        for (int i = 0; i < dataArray.length(); i++) {
                            JSONObject inspectionSite = dataArray.getJSONObject(i);

                            int siteIncidentID = inspectionSite.getInt("SiteIncidentID");
                            String findings = inspectionSite.getString("Findings");
                            String severity = inspectionSite.getString("Severity");
                            String image = inspectionSite.getString("Image");
                            String imageThumbnail = inspectionSite.getString("ImageThumbnail");
                            String remedialActions = inspectionSite.getString("RemedialActions");
                            String timeFrame = inspectionSite.getString("TimeFrame");


                            // Create and add the ModelInspectionSite object
                            ModelSiteIncident incident = new ModelSiteIncident(
                                    siteIncidentID, findings, timeFrame, image, imageThumbnail,remedialActions,severity);

                            lisIncidents.add(incident);

                        }
                        llNositesAlert.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                        if(!lisIncidents.isEmpty()){

                            llNositesAlert.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                        }
                    } else {
                        llNositesAlert.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);

                        // Handle unsuccessful status codes
                        JSONObject error = responseJson.optJSONObject("Error");
                        String errorDetails = (error != null) ? error.optString("Details", "Unknown error") : "Unknown error";

                        Utilities.showCustomSnackbar(SiteDetailActivity.this, errorDetails, Snackbar.LENGTH_SHORT,
                                R.drawable.danger, R.color.white, R.color.color_danger, Gravity.BOTTOM);

                        Log.e("Get User Data Error", "Invalid status code: " + status);
                    }
                } catch (JSONException e) {
                    llNositesAlert.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                    Log.e("Get User Data Error", "JSON parsing error: " + e.getMessage());
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                // Hide the loader and handle errors
                Utilities.showCustomSnackbar(SiteDetailActivity.this, "Something went wrong, please try again", Snackbar.LENGTH_SHORT,
                        R.drawable.danger, R.color.white, R.color.color_danger, Gravity.BOTTOM);
                Log.e("Get User Data Error", errorMessage);
                llNositesAlert.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            }


        });
    }


    private boolean checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_CODE);

            return false;
        }
        else{
            return true;
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            try {
                photoFile = createImageFile();
                photoURI = FileProvider.getUriForFile(this,
                        getPackageName() + ".provider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                captureImageLauncher.launch(takePictureIntent);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }

    private final ActivityResultLauncher<Intent> captureImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    try {
                        if (photoFile.exists()) {
                            // Decode the file into a Bitmap
                            //   Bitmap bitmap = BitmapFactory.decodeFile(photoFile.getAbsolutePath());

                            // Set the Bitmap to the ImageView

                            RequestOptions requestOptions = new RequestOptions()
                                    .fitCenter()
                                    .transform(new RoundedCorners(25)); // Apply rounded corners


                            Glide.with(SiteDetailActivity.this)
                                    .load(photoFile)
                                    .apply(requestOptions)
                                    .into(imgIncident);


                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
}