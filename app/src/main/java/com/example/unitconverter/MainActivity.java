package com.example.unitconverter;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.view.View;

import com.google.android.material.tabs.TabLayout;



public class MainActivity extends AppCompatActivity {

    Spinner spinnerInputType;
    Spinner spinnerOutputType;
    Button convertBtn;
    EditText inputText;
    EditText outputText;
    LinearLayout focus;
    TabLayout unitTypeSelector;
    String selectedUnitType;
//    ArrayAdapter<String> adapterLength;
//    ArrayAdapter<String> adapterWeight;
//    ArrayAdapter<String> adapterTemp;

    ArrayAdapter<String> unitAdapter;

//    List<String> unitOptions;

    public void PopulateUnits(String unitCategory) {
        unitAdapter.clear();
        if (unitCategory == "Length") {
            unitAdapter.addAll("Centimetres", "Metres", "Kilometres", "Inches", "Feet", "Yards", "Miles");
        } else if (unitCategory == "Weight") {
            unitAdapter.addAll("Kilograms", "Grams", "Pounds", "Ton");
        } else if (unitCategory == "Temperature") {
            unitAdapter.addAll("Celsius", "Fahrenheit", "Kelvin");
        }
        unitAdapter.notifyDataSetChanged();
        spinnerInputType.setSelection(0);
        spinnerOutputType.setSelection(1);
        selectedUnitType = unitCategory;
        inputText.setText("");
        outputText.setText("");
        inputText.requestFocus();
    }

    public void InitSpinners() {
        spinnerInputType = (Spinner) findViewById(R.id.conversionOptionsInput);
        spinnerOutputType = (Spinner) findViewById(R.id.conversionOptionsOutput);
        inputText = findViewById(R.id.inputValue);
        outputText = findViewById(R.id.outputValue);
        unitAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        unitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        PopulateUnits("Length");
        spinnerInputType.setAdapter(unitAdapter);
        spinnerOutputType.setAdapter(unitAdapter);
    }


    public double convertLength(String inputUnit, String outputUnit, double value) {
        double cm = 0.0;
        // Convert input unit value to cm
        switch (inputUnit.toLowerCase()) {
            case "centimetres": cm = value;break;
            case "inches": cm = value * 2.54;break;
            case "feet": cm = value * 30.48;break;
            case "yards": cm = value * 91.44;break;
            case "miles": cm = value * 160934.4;break;
            case "metres": cm = value * 100.0;break;
            case "kilometres": cm = value * 100000.0;break;
            default: cm = value;break;
        }

        // Convert cm to output unit
        switch (outputUnit.toLowerCase()) {
            case "centimetres": return cm;
            case "inches": return cm / 2.54;
            case "feet": return cm / 30.48;
            case "yard": return cm / 91.44;
            case "mile": return cm / 160934.4;
            case "metres": return cm / 100.0;
            case "kilometres": return cm / 100000.0;
            default: return cm;
        }
    }

    public double convertWeight(String inputUnit, String outputUnit, double value) {
        double grams = 0.0;

        // Convert input unit value to grams
        switch (inputUnit.toLowerCase()) {
            case "grams": grams = value;break;
            case "kilograms": grams = value * 1000;break;
            case "pounds": grams = value * 453.592;break;
            case "ounces": grams = value * 28.3495;break;
            case "ton": grams = value * 907185.8188;break;
            default: grams = value;break;
        }

        // Convert grams to output unit
        switch (outputUnit.toLowerCase()) {
            case "grams": return grams;
            case "kilograms": return grams / 1000;
            case "pounds": return grams / 453.592;
            case "ounces": return grams / 28.3495;
            case "ton": return grams / 907185.8188;
            default: return grams;
        }
    }

    public double convertTemperature(String inputUnit, String outputUnit, double value) {
        double result = 0.0;

        // Convert input unit value to Celsius
        switch (inputUnit.toLowerCase()) {
            case "celsius": result = value; break;
            case "fahrenheit": result = (value - 32) * 5 / 9; break;
            case "kelvin": result = value - 273.15; break;
            default: result = value; break;
        }

        // Convert Celsius to output unit
        switch (outputUnit.toLowerCase()) {
            case "celsius": return result;
            case "fahrenheit": return (result * 9 / 5) + 32;
            case "kelvin": return result + 273.15;
            default: return result;
        }
    }


    public void doConversion(Boolean printErrors){
        String inputUnit = spinnerInputType.getSelectedItem().toString();
        String outputUnit = spinnerOutputType.getSelectedItem().toString();

        ArrayList<String> items = new ArrayList<String>();
        for (int i = 0; i < unitAdapter.getCount(); i++) {
            items.add(unitAdapter.getItem(i));
        }

        // Force select input type
        if (!items.contains(inputUnit)) {
            spinnerInputType.performClick();
            if(printErrors) Toast.makeText(MainActivity.this, "Input unit required", Toast.LENGTH_SHORT).show();
            return;
        }

        // Force input unit value
        String inputValue = inputText.getText().toString();
        if (inputValue.isEmpty()) {
            inputText.requestFocus();
            return;
        }

        // Force output unit type
        if (!items.contains(outputUnit)) {
            spinnerOutputType.performClick();
            if(printErrors) Toast.makeText(MainActivity.this, "Output unit required", Toast.LENGTH_SHORT).show();
            return;
        }

        double value = Double.parseDouble(inputValue);
        double result = 0.0;
        if(selectedUnitType == "Length"){
            result = convertLength(inputUnit, outputUnit, value);
        }else if (selectedUnitType == "Weight"){
            result = convertWeight(inputUnit, outputUnit, value);
        }else if (selectedUnitType == "Temperature"){
            result = convertTemperature(inputUnit, outputUnit, value);
        }else{
            Toast.makeText(MainActivity.this, "error", Toast.LENGTH_SHORT).show();
        }

        outputText.setText(Double.toString(result));

        inputText.clearFocus();
        outputText.requestFocus();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        InitSpinners();
        focus = findViewById(R.id.focusableLayout);

        convertBtn = findViewById(R.id.convertBtn);

        spinnerInputType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                doConversion(false);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });

        spinnerOutputType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                doConversion(true);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });

        convertBtn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                // Handle button click
                doConversion(true);
//                focus.requestFocus();
            }
        });


        unitTypeSelector = findViewById(R.id.UnitTypeTab);

        unitTypeSelector.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int unitType = tab.getPosition();
                // if unitType == 0: unit = length
                // if unitType == 1: unit = weight
                // if unitType == 2: unit = temperature
                List<String> categories = Arrays.asList("Length", "Weight", "Temperature");
                PopulateUnits(categories.get(unitType));
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

    }
}