package com.example.pra.hciapp;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class GestureModeActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager mSensorManager;

    private Sensor accelerometerSensor;

    private TextView commandG;

    private Button pressB;
    private Button trainB;

    private MainServerActivity serverAct;

    private static final String OUTSIDE_GESTURE_MODE = "$$get_outside_gesture_mode_$$";
    private static final String SENSOR_READ_FINISHED = "$$sensor_readings_over$$";
    private static final String TRAINING_DATA_COMING = "$$get_ready_for_training$$";
    private static final String TRAINING_DATA_OVER = "$$training_readings_over$$";
    private static final String DISCARD_TRAINING_DATA = "$$discard_training_data$$";
    private static final String INSIDE_GAMING_MODE = "$$gaming_mode_selected$$";


    private static final float ALPHA = 0.3f;

    private double preX = 0;
    private double preY = 0;
    private double preZ = 0;

    private static int trainCount = -1;

    private static boolean trainingStarted = false;
    private static boolean flag = false;

    private boolean isGamin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gesture_mode);

        serverAct = new MainServerActivity();

        Intent intent = getIntent();
        isGamin = intent.getBooleanExtra("isInGame", false);

        mSensorManager = (SensorManager) getSystemService(getApplicationContext().SENSOR_SERVICE);

        if(mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {

            accelerometerSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }
        else {

            Toast.makeText(this, "The sensor is not supported", Toast.LENGTH_LONG);
        }

        commandG = (TextView) findViewById(R.id.instructionC);

        trainB = (Button) findViewById(R.id.new_ges);

        pressB = (Button) findViewById(R.id.gesBtn);
        pressB.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:
                        registerSens();
                        break;
                    case MotionEvent.ACTION_UP:
                        unregisterSens();
                        serverAct.sendMessage(SENSOR_READ_FINISHED);
                        if(trainingStarted) {

                            trainCount--;
                            updateText();
                        }
                        break;
                }
                return false;
            }
        });

    }

    private void registerSens() {

        mSensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void unregisterSens() {

        mSensorManager.unregisterListener(this);
        preX = preY = preZ = 0;
        flag = false;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {


        if(flag) {

            preX = preX + ALPHA * (event.values[0] - preX);
            preY = preY + ALPHA * (event.values[1] - preY);
            preZ = preZ + ALPHA * (event.values[2] - preZ);
        }
        else {

            preX = event.values[0];
            preY = event.values[1];
            preZ = event.values[2];
            flag = true;
        }

        sendSensorReadings(preX + " " + preY + " " + preZ);
        Log.d("debugString", preX + " " + preY + " " + preZ);

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.d("debugString", "ACCURACY CHANGED");
    }

    public void trainMe(View view) {

        serverAct.sendMessage(TRAINING_DATA_COMING);

        trainB.setVisibility(View.INVISIBLE);
        trainingStarted = true;
        trainCount = 5;
        commandG.setText("Please perform the gesture");

    }

    public void updateText() {

        if(trainCount > 0) {

            commandG.setText("Please perform the gesture " + trainCount + " more time/s.");
        }
        else {

            trainCount = -1;
            trainingStarted = false;
            commandG.setText("");
            trainB.setVisibility(View.VISIBLE);
            serverAct.sendMessage(TRAINING_DATA_OVER);
            getGestureDetails();
        }

    }

    public void getGestureDetails() {

        Intent intent = new Intent(this, GestureDetailActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {

        if(trainingStarted) {

            serverAct.sendMessage(DISCARD_TRAINING_DATA);
        }

        serverAct.sendMessage(OUTSIDE_GESTURE_MODE);
        if(isGamin)
            serverAct.sendMessage(INSIDE_GAMING_MODE);

        super.onBackPressed();
    }

    private void sendSensorReadings(String reading) {

        serverAct.sendMessage(reading);
    }
}
