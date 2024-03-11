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

import java.util.HashMap;
import java.util.Map;
import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {

    Map<String, Map<String, Double>> unitCategories;

    public void initConversionData(){

        Map<String, Double> lengthConversionFactors = new HashMap<>();
        lengthConversionFactors.put("Centimetres", 1.0);
        lengthConversionFactors.put("Metres", 100.0);
        lengthConversionFactors.put("Kilometres", 100000.0);
        lengthConversionFactors.put("Inches", 2.54);
        lengthConversionFactors.put("Feet", 30.48);
        lengthConversionFactors.put("Yards", 91.44);
        lengthConversionFactors.put("Miles", 160934.4);

        Map<String, Double> weightConversionFactors = new HashMap<>();
        weightConversionFactors.put("Grams", 1.0);
        weightConversionFactors.put("Kilograms", 1000.0);
        weightConversionFactors.put("Pounds", 453.592);
        weightConversionFactors.put("Ton", 907185.8188);
        weightConversionFactors.put("Ounces", 28.3495);


        // temp conversion too complicated to store formula in map
        Map<String, Double> tempConversionFactors = new HashMap<>();
        tempConversionFactors.put("Celsius", 1.0);
        tempConversionFactors.put("Fahrenheit", 1.0);
        tempConversionFactors.put("Kelvin", 1.0);

        unitCategories = new HashMap<>();
        unitCategories.put("Length", lengthConversionFactors);
        unitCategories.put("Weight", weightConversionFactors);
        unitCategories.put("Temperature", tempConversionFactors);
    }

    Spinner spinnerInputType;
    Spinner spinnerOutputType;
    Button convertBtn;
    EditText inputText;
    EditText outputText;
    LinearLayout focus;
    TabLayout unitTypeSelector;
    String selectedUnitType;

    ArrayAdapter<String> unitAdapter;

    public void populateUnits(String unitCategory) {
        unitAdapter.clear();

        // use conversion map's keys as an array of strings
        if(!unitCategories.containsKey(unitCategory)){
            throw new IllegalArgumentException("Invalid unit category: " + unitCategory);
        }

        unitAdapter.addAll(unitCategories.get(unitCategory).keySet().toArray(new String[0]));

        unitAdapter.notifyDataSetChanged();
        inputText.setText("");
        outputText.setText("");
        spinnerInputType.setSelection(0);
        spinnerOutputType.setSelection(1);
        selectedUnitType = unitCategory;

        inputText.requestFocus();
    }

    public void initSpinners() {
        spinnerInputType = (Spinner) findViewById(R.id.conversionOptionsInput);
        spinnerOutputType = (Spinner) findViewById(R.id.conversionOptionsOutput);

        inputText = findViewById(R.id.inputValue);
        outputText = findViewById(R.id.outputValue);

        unitAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        unitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        spinnerInputType.setAdapter(unitAdapter);
        spinnerOutputType.setAdapter(unitAdapter);

        populateUnits("Length");

    }

    public double convertFromUnitFactors(Map<String, Double> conversionFactors, String inputUnit, String outputUnit, double value) {
        if(!conversionFactors.containsKey(inputUnit) || !conversionFactors.containsKey(outputUnit)){
            throw new IllegalArgumentException("Unknown unit input ("+inputUnit+")/output ("+outputUnit+")");
        }

        if(conversionFactors.containsKey("Celsius")){
            return convertTemperature(inputUnit, outputUnit, value);
        }

        double inputFactor = conversionFactors.get(inputUnit);
        double outputFactor = conversionFactors.get(outputUnit);

        // Convert x unit to base value (cm or grams)
        double normalisedValue = value * inputFactor;

        // Convert normalised value to output unit and return result
        return normalisedValue / outputFactor;
    }

    public double convertTemperature(String inputUnit, String outputUnit, double value) {
        double result = 0.0;

        // Convert input unit value to Celsius
        switch (inputUnit.toLowerCase()) {
            case "fahrenheit": result = (value - 32) * 5 / 9; break;
            case "kelvin": result = value - 273.15; break;
            default: result = value; break;
        }

        // Convert Celsius to output unit
        switch (outputUnit.toLowerCase()) {
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
        result = convertFromUnitFactors(unitCategories.get(selectedUnitType), inputUnit, outputUnit, value);

        DecimalFormat df = new DecimalFormat("#.#####");
        outputText.setText(df.format(result));

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


        initConversionData();
        initSpinners();

        focus = findViewById(R.id.focusableLayout);

        // Handle conversion on button click
        convertBtn = findViewById(R.id.convertBtn);
        convertBtn.setOnClickListener(v -> doConversion(true));

        AdapterView.OnItemSelectedListener spinnerItemSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (parentView == spinnerInputType) {
                    doConversion(false);
                } else if (parentView == spinnerOutputType) {
                    doConversion(true);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {

            }
        };

        spinnerInputType.setOnItemSelectedListener(spinnerItemSelectedListener);
        spinnerOutputType.setOnItemSelectedListener(spinnerItemSelectedListener);


        unitTypeSelector = findViewById(R.id.UnitTypeTab);
        unitTypeSelector.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                populateUnits((String)tab.getText());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

    }
}