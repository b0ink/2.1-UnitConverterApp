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

public class MainActivity extends AppCompatActivity {

    private Map<String, Double> lengthConversionFactors;
    private Map<String, Double> weightConversionFactors;
    private Map<String, Double> tempConversionFactors;

    public void InitConversionData(){
        lengthConversionFactors = new HashMap<>();
        lengthConversionFactors.put("Centimetres", 1.0);
        lengthConversionFactors.put("Metres", 100.0);
        lengthConversionFactors.put("Kilometres", 100000.0);
        lengthConversionFactors.put("Inches", 2.54);
        lengthConversionFactors.put("Feet", 30.48);
        lengthConversionFactors.put("Yards", 91.44);
        lengthConversionFactors.put("Miles", 160934.4);

        weightConversionFactors = new HashMap<>();
        weightConversionFactors.put("Grams", 1.0);
        weightConversionFactors.put("Kilograms", 1000.0);
        weightConversionFactors.put("Pounds", 453.592);
        weightConversionFactors.put("Ton", 907185.8188);
        weightConversionFactors.put("Ounces", 28.3495);


        // temp conversion too complicated to store formula in map
        tempConversionFactors = new HashMap<>();
        tempConversionFactors.put("Celsius", 1.0);
        tempConversionFactors.put("Fahrenheit", 1.0);
        tempConversionFactors.put("Kelvin", 1.0);

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

    public void PopulateUnits(String unitCategory) {
        unitAdapter.clear();

        // use conversion map's keys as an array of strings
        switch (unitCategory) {
            case "Length":
                unitAdapter.addAll(lengthConversionFactors.keySet().toArray(new String[0]));
                break;
            case "Weight":
                unitAdapter.addAll(weightConversionFactors.keySet().toArray(new String[0]));
                break;
            case "Temperature":
                unitAdapter.addAll(tempConversionFactors.keySet().toArray(new String[0]));
                break;
            default:
                break;
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

    public double convertFromUnitFactors(Map<String, Double> conversionFactors, String inputUnit, String outputUnit, double value) {
        if(!conversionFactors.containsKey(inputUnit) || !conversionFactors.containsKey(outputUnit)){
            throw new IllegalArgumentException("Unknown unit input ("+inputUnit+")/output ("+outputUnit+")");
        }

        double inputFactor = conversionFactors.get(inputUnit);
        double outputFactor = conversionFactors.get(outputUnit);

        // Convert x unit to base value (cm or grams)
        double normalisedValue = value * inputFactor;

        // Convert cm to output unit and return result
        return normalisedValue / outputFactor;
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
            result = convertFromUnitFactors(lengthConversionFactors, inputUnit, outputUnit, value);
        }else if (selectedUnitType == "Weight"){
            result = convertFromUnitFactors(weightConversionFactors, inputUnit, outputUnit, value);
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

        InitConversionData();
        InitSpinners();

        focus = findViewById(R.id.focusableLayout);
        convertBtn = findViewById(R.id.convertBtn);

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

        convertBtn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                // Handle button click
                doConversion(true);
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