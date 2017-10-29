package com.evasler.clientapp;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class settings extends AppCompatActivity {

    int amountOfMappers;
    SharedPreferences mapperPrefs, generalPrefs;
    EditText reducerIpET, amountOfMappersET;
    ArrayList<EditText> mapperIpEtArray;
    ArrayList<EditText> mapperPortEtArray;
    ArrayList<LinearLayout> mapperIpLayouts;
    ArrayList<LinearLayout> mapperPortLayouts;
    SharedPreferences.Editor mapEditor, generalEditor;
    EditText insertServerIpEt = (EditText) findViewById(R.id.insertServerIP);
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mapperPrefs = getSharedPreferences("mapperIPs", MODE_PRIVATE);
        generalPrefs = getSharedPreferences("preferences", MODE_PRIVATE);

        mapperIpEtArray = new ArrayList<>();
        mapperPortEtArray = new ArrayList<>();
        mapperIpLayouts = new ArrayList<>();
        mapperPortLayouts = new ArrayList<>();

        mapEditor = mapperPrefs.edit();
        generalEditor = generalPrefs.edit();

        String reducerIp = generalPrefs.getString("reducerIp", null);
        amountOfMappers = generalPrefs.getInt("amountOfMappers", 1);

        reducerIpET = (EditText) findViewById(R.id.reducerIP);
        amountOfMappersET = (EditText) findViewById(R.id.amountOfMappers);

        if (reducerIp != null) {

            reducerIpET.setText(reducerIp);
        }

        amountOfMappersET.setText(String.valueOf(amountOfMappers));

        updateMappers(false);

        Button updateBt = (Button) findViewById(R.id.updateBt);
        Button applyBt = (Button) findViewById(R.id.applyBt);

        assert updateBt != null;
        updateBt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                updateMappers(true);
            }
        });

        assert applyBt != null;
        applyBt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                save();
                settings.this.finish();
            }
        });

    }

    void updateMappers(boolean updating) {

        LinearLayout mapperLinear = (LinearLayout) findViewById(R.id.mapperLinear);

        if (updating) {

            assert mapperLinear != null;
            mapperLinear.removeAllViews();

            mapEditor.clear();
            mapEditor.commit();

            mapperIpLayouts.clear();
            mapperPortLayouts.clear();
            mapperIpEtArray.clear();
            mapperPortEtArray.clear();

            amountOfMappers = amountOfMappersET.getText().toString().equals("") ? 0 : Integer.parseInt(amountOfMappersET.getText().toString());
        }

        LinearLayout.LayoutParams ETparams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ETparams.gravity = Gravity.CENTER;
        ETparams.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 160, getResources().getDisplayMetrics());

        generalEditor.putInt("amountOfMappers", amountOfMappers);
        generalEditor.commit();

        for (int i = 0; i < amountOfMappers; i++) {

            mapperIpEtArray.add(new EditText(this));
            mapperIpEtArray.get(i).setId(i);
            mapperIpEtArray.get(i).setText(mapperPrefs.getString("mapperIp" + i, null));

            TextView text = new TextView(this);
            text.setText(String.format("Mapper %d IP:", i + 1));
            text.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12, getResources().getDisplayMetrics()));

            mapperIpLayouts.add(new LinearLayout(this));
            mapperIpLayouts.get(i).addView(text);
            mapperIpLayouts.get(i).addView(mapperIpEtArray.get(i), ETparams);
        }

        for (int i = 0; i < amountOfMappers; i++) {

            mapperPortEtArray.add(new EditText(this));
            mapperPortEtArray.get(i).setId(i + 100);
            mapperPortEtArray.get(i).setText(mapperPrefs.getString("mapperPort" + i, null));

            TextView text = new TextView(this);
            text.setText(String.format("Mapper %d Port:", i + 1));
            text.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12, getResources().getDisplayMetrics()));

            mapperPortLayouts.add(new LinearLayout(this));
            mapperPortLayouts.get(i).addView(text);
            mapperPortLayouts.get(i).addView(mapperPortEtArray.get(i), ETparams);
        }

        for (int i = 0; i < amountOfMappers; i++) {

            assert mapperLinear != null;
            mapperLinear.addView(mapperIpLayouts.get(i));
            mapperLinear.addView(mapperPortLayouts.get(i));
        }
    }

    void save () {

        if (amountOfMappers == 0) {

            Toast.makeText(this, "Invalid amount of Mappers", Toast.LENGTH_SHORT).show();
            return;
        }

        if (checkIP(reducerIpET.getText().toString())) {

            generalEditor.putString("reducerIp", reducerIpET.getText().toString());
            generalEditor.commit();
        } else {

            Toast.makeText(this, "Invalid Reducer IP", Toast.LENGTH_SHORT).show();
            return;
        }
        if (checkIP(insertServerIpEt.getText().toString())) {

            generalEditor.putString("insertServerIp", insertServerIpEt.getText().toString());
            generalEditor.commit();
        } else {

            Toast.makeText(this, "Invalid Insert Server IP", Toast.LENGTH_SHORT).show();
            return;
        }
        for (int i = 0; i < amountOfMappers; i++) {

            if (checkIP(mapperIpEtArray.get(i).getText().toString())) {

                mapEditor.putString("mapperIp" + i, mapperIpEtArray.get(i).getText().toString());
                mapEditor.putString("mapperPort" + i, mapperPortEtArray.get(i).getText().toString());
                mapEditor.commit();
            } else {

                Toast.makeText(this, "Invalid Mapper " + (i + 1) + " IP", Toast.LENGTH_SHORT).show();
                return;
            }
        }
    }

    boolean checkIP(String IP) {

        if (IP.equals("")) {

            return false;
        }

        int counter = 0;
        int dotPosition[] = new int[3];
        Character currentChar;

        for (int i = 0; i < IP.length(); i++) {

            currentChar = IP.charAt(i);

            if (!currentChar.equals('.') && !Character.isDigit(currentChar)) {

                return false;
            }

            if (currentChar.equals('.')) {

                dotPosition[counter] = i;
                counter++;
            }

            if (counter > 3) {

                return false;
            }
        }

        if (counter < 3) {

            return false;
        }

        for (int i = 0; i < 4; i++) {

            String IPtoken = IP.substring( i == 0 ? 0 : dotPosition[i - 1] + 1, i == 3 ? IP.length() : dotPosition[i]);
            Log.d("test", IPtoken);
            if (Integer.parseInt(IPtoken) > 255) {

                return false;
            }
        }

        return true;
    }
}
