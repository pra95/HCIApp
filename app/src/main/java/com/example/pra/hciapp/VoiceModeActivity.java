package com.example.pra.hciapp;

import android.content.Intent;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

public class VoiceModeActivity extends AppCompatActivity {

    private EditText sendField;
    private TextView resultMsg;
    private MainServerActivity serverAct;


    private static final int SPEECH_REQUEST_CODE = 89;

    private static final String OUTSIDE_VOICE_MODE = "$$get_outside_voice_mode_$$";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_mode);

        sendField = (EditText) findViewById(R.id.sendMsgField);
        resultMsg = (TextView) findViewById(R.id.resultText);
        serverAct = new MainServerActivity();
    }

    public void sendVoiceMessages(View view) {

        String message = sendField.getText().toString();
        serverAct.sendMessage(message);
        sendField.setText("");


    }

    public void voiceButtonClicked(View view) {

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        startActivityForResult(intent, SPEECH_REQUEST_CODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {

            List<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String spokenText = results.get(0);
            resultMsg.setText(spokenText);
            serverAct.sendMessage(spokenText);

        }
    }

    @Override
    public void onBackPressed() {

        serverAct.sendMessage(OUTSIDE_VOICE_MODE);

        super.onBackPressed();
    }
}
