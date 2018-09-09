package com.example.pra.hciapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

public class KeyboardInterface extends AppCompatActivity {

    public EditText text;
    public MainServerActivity serverAct;

    private static final String OUTSIDE_KEYBOARD_MODE = "$$get_outside_keyboard_mode_$$";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keyboard_interface);

        serverAct = new MainServerActivity();
        text = (EditText) findViewById(R.id.textD);


    }

    public void sendText(View view) {

        serverAct.sendMessage(text.getText().toString());
        text.setText("");
    }

    @Override
    public void onBackPressed() {

        serverAct.sendMessage(OUTSIDE_KEYBOARD_MODE);
        super.onBackPressed();
    }

}
