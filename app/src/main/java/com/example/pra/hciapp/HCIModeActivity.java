package com.example.pra.hciapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class HCIModeActivity extends AppCompatActivity {

    private MainServerActivity serverActivity;

    //constants for modes
    private static final String INSIDE_VOICE_MODE = "$$voice_mode_selected$$";
    private static final String INSIDE_GESTURE_MODE = "$$gesture_mode_selected$$";
    private static final String INSIDE_IMAGE_MODE = "$$image_mode_selected$$";
    private static final String INSIDE_GAMING_MODE = "$$gaming_mode_selected$$";
    private static final String INSIDE_KEYBOARD_MODE = "$$keyboard_mode_selected$$";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hcimode);

        serverActivity = new MainServerActivity();
    }

    public void pressedVoiceMode(View view) {

        sendModeSelectionMessage(INSIDE_VOICE_MODE);
        Intent intent = new Intent(this, VoiceModeActivity.class);
        startActivity(intent);

    }

    public void pressedGestureMode(View view) {

        sendModeSelectionMessage(INSIDE_GESTURE_MODE);
        Intent intent = new Intent(this, GestureModeActivity.class);
        startActivity(intent);

    }

    public void pressedImageMode(View view) {

        sendModeSelectionMessage(INSIDE_IMAGE_MODE);
        Intent intent = new Intent(this, ImageModeActivity.class);
        startActivity(intent);
    }

    public void pressedGameMode(View view) {

        sendModeSelectionMessage(INSIDE_GAMING_MODE);
        Intent intent = new Intent(this, GameModeActivity.class);
        startActivity(intent);
    }

    public void startKeyboard(View view) {

        sendModeSelectionMessage(INSIDE_KEYBOARD_MODE);
        Intent intent = new Intent(this, KeyboardInterface.class);
        startActivity(intent);
    }

    private void sendModeSelectionMessage(String message) {

        serverActivity.sendMessage(message);
    }
}
