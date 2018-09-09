package com.example.pra.hciapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class GameModeActivity extends AppCompatActivity {

    Spinner noButtons;
    private static final String OUTSIDE_GAME_MODE = "$$get_outside_game_mode_$$";
    private static final String OUTSIDE_GESTURE_MODE = "$$get_outside_gesture_mode_$$";
    private static final String INSIDE_GESTURE_MODE = "$$gesture_mode_selected$$";
    private static final String INSIDE_GAMING_MODE = "$$gaming_mode_selected$$";




    MainServerActivity serA;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_mode);

        serA = new MainServerActivity();
        Log.d("debugString", "Inside onCreate of GameModeActivity");
        //readFile();
        Log.d("debugString", "THE CONCEPT OF THIS" + this);
        noButtons = (Spinner) findViewById(R.id.buttonCount);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.count_array, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        noButtons.setAdapter(adapter);
        noButtons.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String item = parent.getItemAtPosition(position).toString();
                Log.d("debugString", item);
                activityStarter(item);
                //serA.sendMessage(INSIDE_GAMING_MODE);


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }




    @Override
    protected void onResume() {

        noButtons.setSelection(0);
        super.onResume();
    }

    public void activityStarter(String data) {

        Intent intent;
        switch (data) {

            case "NONE":
                break;
            case "GESTURE":
                serA.sendMessage(OUTSIDE_GAME_MODE);
                serA.sendMessage(INSIDE_GESTURE_MODE);
                Log.d("debugString", "Going to grsture activity");
                intent = new Intent(this, GestureModeActivity.class);
                intent.putExtra("isInGame", true);
                startActivity(intent);
                //Log.d("debugString", "comkiung back from gesture activity ");
                //serA.sendMessage(OUTSIDE_GESTURE_MODE);
                //serA.sendMessage(INSIDE_GAMING_MODE);
                break;
            case "BUTTONS":
                intent = new Intent(this, GameController.class);
                startActivity(intent);
                break;
            /*case "3 KEY":
                intent = new Intent(this, ThreeButtonCon.class);
                startActivity(intent);
                break;
            case "4 KEY":
                intent = new Intent(this, FourButtonCon.class);
                startActivity(intent);
                break;
            case "5 KEY":
                intent = new Intent(this, FiveButtonCon.class);
                startActivity(intent);
                break;
            case "6 KEY":
                intent = new Intent(this, OneButtonCon.class);
                startActivity(intent);
                break;*/

        }
    }

    @Override
    public void onBackPressed() {

        serA.sendMessage(OUTSIDE_GAME_MODE);
        super.onBackPressed();
    }
}
