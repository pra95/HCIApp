package com.example.pra.hciapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

public class GestureDetailActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {


    private TextView name;
    private TextView action;
    private MainServerActivity serverAct;
    String itemSelect = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gesture_detail);

        name = (TextView) findViewById(R.id.nameText);
        //action = (TextView) findViewById(R.id.actText);

        serverAct = new MainServerActivity();

        Spinner spinner = (Spinner) findViewById(R.id.spinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.key_array, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

    }



    public void sendData(View view) {

        String nameVal = name.getText().toString();
        if(nameVal.length() == 0) {

            return;
        }
        else {

            serverAct.sendMessage(nameVal);
        }

        String actionVal = itemSelect;
        if(actionVal.length() == 0) {

            return;
        }
        else {

            serverAct.sendMessage(actionVal);
        }
        finish();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        itemSelect = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
