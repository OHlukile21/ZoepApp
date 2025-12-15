package com.zoepapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
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

public class SiteActivity extends AppCompatActivity {


    FacilityAdapter adapter;
    RecyclerView recyclerView;
    private Uri photoURI;
    private File photoFile;
    String userId;
    String bcaId;
    ImageView imgSite;
    RelativeLayout rlMenu;
    TextView txtUsername,txtRole;
    LinearLayout llNositesAlert;
    ImageView imgBell;
    SwipeRefreshLayout swipeRefreshLayout;
    List<ModelFacility> listfacilities= new ArrayList<>();
    MaterialButton btnMenu,btnNewReport,btnRetry;
    private static final int PERMISSION_REQUEST_CODE = 100;
    private static final int ADD_ITEM_REQUEST_CODE = 200;
    private boolean shouldRefreshList = false;

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                dispatchTakePictureIntent();

            } else {
                Utilities.showCustomSnackbar(SiteActivity.this, "Permissions Denied", Snackbar.LENGTH_SHORT,
                        R.drawable.danger, R.color.white, R.color.color_danger, Gravity.BOTTOM);

            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_ITEM_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null && data.getBooleanExtra("itemAdded", false)) {
                shouldRefreshList = true; // Mark that the list should be refreshed
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (shouldRefreshList) {
            getUserData(Integer.parseInt(bcaId));
            shouldRefreshList = false; // Reset the flag
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sites);

        llNositesAlert = findViewById(R.id.llNoSitesAlert);
        btnMenu = findViewById(R.id.btnMenu);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        btnRetry = findViewById(R.id.btnRetry);
        btnNewReport = findViewById(R.id.btnNewReport);
        rlMenu = findViewById(R.id.rlMenu);
        imgBell = findViewById(R.id.imgBell);
        txtUsername = findViewById(R.id.txtUsername);
        txtRole = findViewById(R.id.txtRole);
        recyclerView = findViewById(R.id.rvSites);
        llNositesAlert.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);

        adapter = new FacilityAdapter(SiteActivity.this, listfacilities, new OnSiteClickListener() {
            @Override
            public void OnSiteClick(ModelFacility site) {
                Intent intent= new Intent(SiteActivity.this, FacilityDetailActivity.class);
                intent.putExtra("facilityId",site.getFacilityID());
                intent.putExtra("facilityName",site.getName());
                intent.putExtra("facilityImage","");
                startActivityForResult(intent, ADD_ITEM_REQUEST_CODE);
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);



        userId = SharedPrefsHelper.getValue(SiteActivity.this,SharedPrefsHelper.KEY_USER_ID);
        bcaId = SharedPrefsHelper.getValue(SiteActivity.this,SharedPrefsHelper.KEY_BCA_ID);
        String username = SharedPrefsHelper.getValue(SiteActivity.this,SharedPrefsHelper.KEY_USER_NAME);
        String role = SharedPrefsHelper.getValue(SiteActivity.this,SharedPrefsHelper.KEY_USER_ROLE);


        if(bcaId!=null && !bcaId.equalsIgnoreCase("")){
            txtUsername.setText(username);
            txtRole.setText(role);

            getUserData(Integer.parseInt(bcaId));
        }
        else{
            finish();
        }

        swipeRefreshLayout.setColorSchemeResources(
                R.color.color_primary, // Replace with your desired colors
                R.color.color_primary,
                R.color.color_primary
        );
        btnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getUserData(Integer.parseInt(bcaId));
            }
        });

        rlMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopUpMenu();
            }
        });
        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopUpMenu();
            }
        });

        imgBell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAlertsScreen();
            }
        });

        btnNewReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNewReportBottomSheet();
            }
        });

        swipeRefreshLayout.setOnRefreshListener(() -> {

            getUserData(Integer.parseInt(bcaId));
        });

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

    private void logout() {
        SharedPrefsHelper.clearSharedPrefs(SiteActivity.this);
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void openAlertsScreen() {
        Toast.makeText(SiteActivity.this, "Opening Alerts...", Toast.LENGTH_SHORT).show();

        // TODO: Replace with your real AlertsActivity when ready
        Intent intent = new Intent(SiteActivity.this, AlertsActivity.class);
        startActivity(intent);
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }

    public void getUserData(int bcaId) {
        // Show loader before making the API call
        MyLoader myLoader = new MyLoader(this);
        // Build the URL with UserId as a query parameter
        String url = Global.USER_FACILITIES_URL + "?bcaId=" + bcaId;

        // Use the updated ApiClient to make the request
        ApiClient apiClient = new ApiClient();

        apiClient.get(url, new ApiClient.ApiExecutionCallback() {
            @Override
            public void onBeforeExecution() {
                // Called before the request is initiated, show loader here if not shown already
                myLoader.showIndeterminantLoader("Fetching User Facilities");
                llNositesAlert.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
            }

            @Override
            public void onAfterExecution() {
                // Ensure cleanup operations and update the UI

                myLoader.cancelIndeterminantLoader();

                swipeRefreshLayout.setRefreshing(false);
                // Notify the adapter regardless of success or failure
                adapter.notifyDataSetChanged();
            }
        }, new ApiClient.ApiCallback() {

            @Override
            public void onSuccess(JSONObject responseJson) {
                // Hide the loader once the response is received

                try {
                    int status = responseJson.getInt("Status");
                    if (status == 200) {
                        // Process the response
                        //      JSONObject dataObject = responseJson.getJSONObject("Data");
                        JSONArray dataArray = responseJson.getJSONArray("Data");

                        // Clear and update the list
                        listfacilities.clear();
                        for (int i = 0; i < dataArray.length(); i++) {
                            JSONObject facility = dataArray.getJSONObject(i);

                            int facilityID = facility.getInt("FacilityID");
                            String name = facility.getString("Name");
                            String address = facility.getString("Address");
                            //  String image = inspectionSite.getString("Image");
                            // String imageThumbnail = inspectionSite.getString("ImageThumbnail");
                            String type = facility.getString("Type");
                            //  LocalDate date = Utilities.convertStrToDate(inspectionSite.getString("CreatedDate"));
                            // String nextInspectionDate = Utilities.formatDate(LocalDate.now().plusMonths(12));
                            // Handle optional NumberOfDefects
                            int numOfIncidents = 0;
                            // String lastInspectionDate = "Never";
                            if (!facility.isNull("NumberOfIncidents")) {
                                numOfIncidents = facility.getInt("NumberOfIncidents");

                            }


                            // Create and add the ModelInspectionSite object
                            ModelFacility modelFacility = new ModelFacility(
                                    facilityID, name, address,numOfIncidents, type
                            );

                            listfacilities.add(modelFacility);
                        }
                        llNositesAlert.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                        if(!listfacilities.isEmpty()){
                            llNositesAlert.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                        }
                    } else {
                        llNositesAlert.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                        // Handle unsuccessful status codes
                        JSONObject error = responseJson.optJSONObject("Error");
                        String errorDetails = (error != null) ? error.optString("Details", "Unknown error") : "Unknown error";

                        Utilities.showCustomSnackbar(SiteActivity.this, errorDetails, Snackbar.LENGTH_SHORT,
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
                Utilities.showCustomSnackbar(SiteActivity.this, "Something went wrong, please try again", Snackbar.LENGTH_SHORT,
                        R.drawable.danger, R.color.white, R.color.color_danger, Gravity.BOTTOM);
                Log.e("Get User Data Error", errorMessage);
                llNositesAlert.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
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
                    button.setBackgroundColor(ContextCompat.getColor(SiteActivity.this, sltBtnColor));
                    button.setStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(SiteActivity.this, sltBtnStrokeColor)));
                    button.setStrokeWidth(5);
                    button.setTextColor(ContextCompat.getColor(SiteActivity.this, sltBtnTxtColor));

                } else {
                    // Other buttons
                    button.setBackgroundColor(ContextCompat.getColor(SiteActivity.this, R.color.color_transparent));
                    button.setTextColor(ContextCompat.getColor(SiteActivity.this, unSltBtnTextColor));
                    button.setStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(SiteActivity.this, unSltStrokeColor)));
                    button.setStrokeWidth(2);
                }
            }
        }


    }

    private void addReport(String name, String address, String type) {
        MyLoader myLoader = new MyLoader(this);

        JSONObject json = new JSONObject();
        try {
            json.put("name", name);
            json.put("address", address);
            json.put("type", type);
            json.put("userId", SharedPrefsHelper.getValue(SiteActivity.this, SharedPrefsHelper.KEY_USER_ID));
        }
        catch (Exception e){

        }
        String url = Global.ADD_SITE_URL;


        ApiClient client = new ApiClient();
        client.post(url, json, photoFile, new ApiClient.ApiExecutionCallback() {
            @Override
            public void onBeforeExecution() {
                myLoader.showIndeterminantLoader("Submitting New Report");
            }

            @Override
            public void onAfterExecution() {
                myLoader.cancelIndeterminantLoader();

                getUserData(Integer.parseInt(bcaId));

            }}, new ApiClient.ApiCallback() {
            @Override
            public void onSuccess(JSONObject responseJson) {
                try {
                    int status = responseJson.getInt("Status");
                    if (status == 200) {
                        // Process the response
                        String dataString = responseJson.getString("Data");

                        Utilities.showCustomSnackbar(SiteActivity.this, "Site Created Successfully!", Snackbar.LENGTH_SHORT,
                                R.drawable.check_circle, R.color.white, R.color.color_sucess, Gravity.BOTTOM);


                    } else {
                        // Handle unsuccessful status codes
                        JSONObject error = responseJson.optJSONObject("Error");
                        String errorDetails = (error != null) ? error.optString("Details", "Unknown error") : "Unknown error";

                        Utilities.showCustomSnackbar(SiteActivity.this, errorDetails, Snackbar.LENGTH_SHORT,
                                R.drawable.danger, R.color.white, R.color.color_danger, Gravity.BOTTOM);

                        //    Log.e("Get User Data Error", "Invalid status code: " + status);
                    }
                } catch (JSONException e) {
                    Utilities.showCustomSnackbar(SiteActivity.this, "Something went wrong, please try again", Snackbar.LENGTH_SHORT,
                            R.drawable.danger, R.color.white, R.color.color_danger, Gravity.BOTTOM);

                }
            }

            @Override
            public void onFailure(String errorMessage) {
                Utilities.showCustomSnackbar(SiteActivity.this, "Something went wrong, please try again", Snackbar.LENGTH_SHORT,
                        R.drawable.danger, R.color.white, R.color.color_danger, Gravity.BOTTOM);

            }
        });
    }

    @SuppressLint("RestrictedApi")
    private void showPopUpMenu(){
        MenuBuilder menuBuilder =new MenuBuilder(SiteActivity.this);
        MenuInflater inflater = new MenuInflater(SiteActivity.this);
        inflater.inflate(R.menu.main_menu, menuBuilder);
        MenuPopupHelper optionsMenu = new MenuPopupHelper(SiteActivity.this, menuBuilder, btnMenu);
        optionsMenu.setForceShowIcon(true);
        menuBuilder.setCallback(new MenuBuilder.Callback() {
            @Override
            public boolean onMenuItemSelected(MenuBuilder menu, MenuItem item) {
                logout();
                return true;
            }
            @Override
            public void onMenuModeChange(MenuBuilder menu) {}
        });

        optionsMenu.show();
    }


    private void showNewReportBottomSheet() {
        photoFile=null;
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_add_report, null);
        bottomSheetDialog.setContentView(bottomSheetView);

        final EditText edtxtName = bottomSheetView.findViewById(R.id.edtxtName);
        final EditText edtxtAddress = bottomSheetView.findViewById(R.id.edtxtAddress);
        //final EditText edtxtProprtyType = bottomSheetView.findViewById(R.id.edtxtProprtyType);
        imgSite = bottomSheetView.findViewById(R.id.imgSite);





        MaterialButton btnSubmit = bottomSheetView.findViewById(R.id.btnSubmitNewReport);
        MaterialButton btnClose = bottomSheetView.findViewById(R.id.btnClose);

        MaterialButton tsRetail = bottomSheetView.findViewById(R.id.tsRetail);
        MaterialButton tsOffice = bottomSheetView.findViewById(R.id.tsOffice);
        MaterialButton tsSpecialized = bottomSheetView.findViewById(R.id.tsSpecialized);
        MaterialButton tsIndustrial = bottomSheetView.findViewById(R.id.tsIndustrial);

        imgSite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(checkPermissions()){
                    dispatchTakePictureIntent();
                }

            }
        });

        tsRetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggle(tsRetail,R.color.selected_button_text_color,R.color.selected_button_stroke_color,R.color.selected_button_color,R.color.unselected_button_text_color,R.color.unselected_button_stroke_color);
            }
        });

        tsOffice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggle(tsOffice,R.color.selected_button_text_color,R.color.selected_button_stroke_color,R.color.selected_button_color,R.color.unselected_button_text_color,R.color.unselected_button_stroke_color);

            }
        });

        tsIndustrial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggle(tsIndustrial,R.color.selected_button_text_color,R.color.selected_button_stroke_color,R.color.selected_button_color,R.color.unselected_button_text_color,R.color.unselected_button_stroke_color);

            }
        });

        tsSpecialized.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggle(tsSpecialized,R.color.selected_button_text_color,R.color.selected_button_stroke_color,R.color.selected_button_color,R.color.unselected_button_text_color,R.color.unselected_button_stroke_color);

            }
        });


        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = edtxtName.getText().toString().trim();
                String address = edtxtAddress.getText().toString().trim();
                String propertyType="";

                if(tsRetail.isChecked()){
                    propertyType="1";
                }
                else if(tsIndustrial.isChecked()){
                    propertyType="2";
                }
                else if(tsOffice.isChecked()){
                    propertyType="3";
                }
                else if(tsSpecialized.isChecked()){
                    propertyType="4";
                }


                if(!name.equals("") && !address.equals("") && !propertyType.equals("") ){

                    bottomSheetDialog.dismiss();
                    addReport(name,address,propertyType);
                }
                else{
                    bottomSheetDialog.hide();
                    Utilities.showCustomSnackbar(SiteActivity.this, "Please enter all values", Snackbar.LENGTH_SHORT,
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


                            Glide.with(SiteActivity.this)
                                    .load(photoFile)
                                    .apply(requestOptions)
                                    .into(imgSite);


                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
}