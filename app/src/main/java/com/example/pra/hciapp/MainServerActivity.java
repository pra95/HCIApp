package com.example.pra.hciapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.concurrent.ExecutionException;

public class MainServerActivity extends AppCompatActivity {

    private static String hostName = "localhost";
    private static final int port = 8539;

    public static Socket socket = null;
    private static BufferedReader br;
    private static BufferedWriter bw;

    private static final String CLOSE_CONNECTION_MESSAGE = "$$CLOSE_CONNECTION$$";

    private static final int START_CONNECTING = 0;
    private static final int CONNECTED_TO_SERVER = 1;
    private static final int FAILED_TO_CONNECT = 2;
    private static final int DISCONNECTED_TO_SERVER = 3;

    private static int currentConnectionStatus = -1;

    Thread serverThread;

    TextView connectionStatus;

    public String debugString = "debug";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_server);

        Log.d(debugString, "SERVER ONCREATE");

        Intent intent = getIntent();
        hostName = intent.getStringExtra("server-ip-address");

        Log.d(debugString, "received ip adress = " + hostName);

        connectionStatus = (TextView) findViewById(R.id.severConnectStatus);

        serverThread = new Thread() {

            @Override
            public void run() {

                Boolean threadSuccess = false;

                try {

                    //connecting to server.
                    Log.d(debugString, "trying to connect");
                    setConnectionStatusMessage(START_CONNECTING);

                    socket = new Socket(hostName, port);
                    threadSuccess = true;

                    Log.d(debugString, "Connected to server");
                    setConnectionStatusMessage(CONNECTED_TO_SERVER);

                    br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

                    //receiving messages from server
                    String data;
                    while((data = br.readLine()) != null) {

                        Log.d(debugString, "inside while");
                        if(data.equals(CLOSE_CONNECTION_MESSAGE)) {

                            sendCloseConnectionMessage();
                            break;
                        }

                        Log.d(debugString, "message from server: " + data);
                    }

                    //closing connection
                    br.close();
                    bw.close();
                    socket.close();
                    hostName = null;

                    setConnectionStatusMessage(DISCONNECTED_TO_SERVER);

                } catch (IOException e) {

                    Log.d(debugString, "ERROR");
                    Log.e(debugString, e.getMessage());
                    e.printStackTrace();
                    //setConnectionStatusMessage(DISCONNECTED_TO_SERVER);
                    Log.d(debugString, "ERROR");

                }
                catch (Exception e) {

                    Log.e(debugString, e.getMessage());
                    e.printStackTrace();
                }

                if(!threadSuccess) {

                    setConnectionStatusMessage(FAILED_TO_CONNECT);
                }

            }
        };
        serverThread.start();

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(debugString, "SERVER ONPAUSE");

    }

    private void setConnectionStatusMessage(int statusCode) {

        final int innerStatusCode = statusCode;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                switch(innerStatusCode) {

                    case START_CONNECTING:
                        currentConnectionStatus = START_CONNECTING;
                        connectionStatus.setText(getString(R.string.conn_state_connecting) + hostName + getString(R.string.triple_dot));
                        break;

                    case CONNECTED_TO_SERVER:
                        currentConnectionStatus = CONNECTED_TO_SERVER;
                        connectionStatus.setText(getString(R.string.conn_state_connected));
                        Toast.makeText(MainServerActivity.this, "Connected to Server", Toast.LENGTH_SHORT).show();
                        performSuccessfulConnectionOperations();
                        break;

                    case FAILED_TO_CONNECT:
                        currentConnectionStatus = FAILED_TO_CONNECT;
                        connectionStatus.setText(getString(R.string.conn_state_failed) + hostName);
                        Toast.makeText(MainServerActivity.this, "Failed to Connect", Toast.LENGTH_SHORT).show();
                        break;

                    case DISCONNECTED_TO_SERVER:
                        currentConnectionStatus = DISCONNECTED_TO_SERVER;
                        connectionStatus.setText(getString(R.string.conn_state_disconnected));
                        Toast.makeText(MainServerActivity.this, "Server Disconnected", Toast.LENGTH_SHORT).show();
                        break;


                    default:
                        connectionStatus.setText(getString(R.string.conn_status_default_message));
                        break;

                }


            }
        });

    }

    private void sendMessageToServer(String message) {

        try {
            //bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            bw.write(message);
            bw.newLine();
            bw.flush();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void sendMessage(String message) {

        sendMessageToServer(message);
    }

    private void sendCloseConnectionMessage() {

        sendMessageToServer(CLOSE_CONNECTION_MESSAGE);

    }

    private void performSuccessfulConnectionOperations() {

        Intent intent = new Intent(this, HCIModeActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(debugString, "SERVER ONRESTART");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(debugString, "SERVER ONRESUME");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(debugString, "SERVER ONSTOP");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(debugString, "SERVER ONDESTROY");


    }

    @Override
    public void onBackPressed() {

        Log.d(debugString, "backbutton is pressed");
        Log.d(debugString, "Thread state is: " + serverThread.isAlive());


        if(serverThread.isAlive()) {

            //alive
            if(currentConnectionStatus == START_CONNECTING) {

                Toast.makeText(this, "Wait for server to connect", Toast.LENGTH_SHORT).show();
            }
            else if(currentConnectionStatus == CONNECTED_TO_SERVER) {

                sendCloseConnectionMessage();
                super.onBackPressed();
            }
        }
        else {

            super.onBackPressed();
        }

    }


}


