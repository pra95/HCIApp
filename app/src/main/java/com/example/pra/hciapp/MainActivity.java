package com.example.pra.hciapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private String hostName;

    public String debugString = "debug";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(debugString, "MAIN ONCREATE");


    }

    public void setIPToNext(View view) {

        String receivedIP = getIP();
        Log.d(debugString, "receivedIP = " + receivedIP);

        //just for fun.
        Boolean ans = checkIfCorrect(receivedIP);
        Log.d(debugString, "validating ans = " + ans);

        startNextActivity(receivedIP);

    }

    public void startScan(View view) {

        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
        integrator.setPrompt("Place the QR code inside the view finder to scan it.");
        integrator.setCameraId(0);
        integrator.setBeepEnabled(false);
        integrator.setBarcodeImageEnabled(false);
        integrator.initiateScan();
    }

    public void startNextActivity(String receivedIP) {

        TextView invalidText = (TextView) findViewById(R.id.validationText);
        if(checkIfCorrect(receivedIP)) {

            invalidText.setText("");

            Intent intent = new Intent(this, MainServerActivity.class);
            intent.putExtra("server-ip-address", receivedIP);
            startActivity(intent);

        }
        else {

            invalidText.setText("Invalid IP address");
        }

    }

    private String getIP() {

        EditText fieldText = (EditText) findViewById(R.id.serverIPText);

        return fieldText.getText().toString();

    }

    private Boolean checkIfCorrect(String ipAddress) {

        Pattern pattern;
        Matcher matcher;

        final String IPADDRESS_PATTERN = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";

        pattern = Pattern.compile(IPADDRESS_PATTERN);
        matcher = pattern.matcher(ipAddress);
        return matcher.matches();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if(result != null) {

            String receivedIP = result.getContents();
            if(receivedIP == null) {

                Toast.makeText(this, "Scan Canceled", Toast.LENGTH_SHORT).show();
            }
            else {

                Toast.makeText(this, receivedIP, Toast.LENGTH_LONG).show();
                Log.d("debug" , receivedIP);
                startNextActivity(receivedIP);
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(debugString, "MAIN ONPAUSE");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(debugString, "MAIN ONRESTART");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(debugString, "MAIN ONRESUME");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(debugString, "MAIN ONSTOP");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(debugString, "MAIN ONDESTROY");
    }

    public void onBackPressed() {

        //super.onBackPressed();
        FireMissilesDialogFragment dialogues = new FireMissilesDialogFragment();
        dialogues.show(getFragmentManager().beginTransaction(), "dialog");
    }

}
