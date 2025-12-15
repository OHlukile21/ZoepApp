package com.zoepapp;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

public class AddIncidentActivity extends AppCompatActivity {

    Spinner spinnerDiscipline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_incident);

        spinnerDiscipline = findViewById(R.id.spinnerDiscipline);

        String[] disciplines = {
                "Electrical",
                "Plumbing",
                "Structural",
                "Fire",
                "Health & Safety",
                "Other"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                disciplines
        );

        spinnerDiscipline.setAdapter(adapter);
    }
}