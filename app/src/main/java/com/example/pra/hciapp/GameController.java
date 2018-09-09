package com.example.pra.hciapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;

public class GameController extends AppCompatActivity {

    //visible at first
    public RelativeLayout layP;
    public Button set;
    public Button but1;
    public Button but2;
    public Button but3;
    public Button but4;
    public Button but5;
    public Button but6;

    //secondary
    public LinearLayout lay;
    public Spinner sp1;
    public Spinner sp2;
    public Spinner sp3;
    public Spinner sp4;
    public Spinner sp5;
    public Spinner sp6;
    public Button done;

    //button representation
    public String val1 = "ENTER";
    public String val2 = "LEFT";
    public String val3 = "RIGHT";
    public String val4 = "UP";
    public String val5 = "DOWN";
    public String val6 = "SPACE";

    MainServerActivity serverAct;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_controller);

        serverAct = new MainServerActivity();



        //primary
        layP = (RelativeLayout) findViewById(R.id.relativeL);
        set = (Button) findViewById(R.id.setting);
        but1 = (Button) findViewById(R.id.press1);
        but2 = (Button) findViewById(R.id.press2);
        but3 = (Button) findViewById(R.id.press3);
        but4 = (Button) findViewById(R.id.press4);
        but5 = (Button) findViewById(R.id.press5);
        but6 = (Button) findViewById(R.id.press6);


        but1.setText(val1);
        but2.setText(val2);
        but3.setText(val3);
        but4.setText(val4);
        but5.setText(val5);
        but6.setText(val6);

        //setting up primary
        but1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                serverAct.sendMessage(val1);
            }
        });
        but2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                serverAct.sendMessage(val2);
            }
        });
        but3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                serverAct.sendMessage(val3);
            }
        });
        but4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                serverAct.sendMessage(val4);
            }
        });
        but5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                serverAct.sendMessage(val5);
            }
        });
        but6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                serverAct.sendMessage(val6);
            }
        });

        //secondary
        lay = (LinearLayout) findViewById(R.id.linearL);
        sp1 = (Spinner) findViewById(R.id.spn1);
        sp2 = (Spinner) findViewById(R.id.spn2);
        sp3 = (Spinner) findViewById(R.id.spn3);
        sp4 = (Spinner) findViewById(R.id.spn4);
        sp5 = (Spinner) findViewById(R.id.spn5);
        sp6 = (Spinner) findViewById(R.id.spn6);
        done = (Button) findViewById(R.id.doneBtn);
        lay.setVisibility(View.INVISIBLE);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.key_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp1.setAdapter(adapter);
        sp2.setAdapter(adapter);
        sp3.setAdapter(adapter);
        sp4.setAdapter(adapter);
        sp5.setAdapter(adapter);
        sp6.setAdapter(adapter);


        sp1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                val1 = parent.getItemAtPosition(position).toString();
                but1.setText(val1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        sp2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                val2 = parent.getItemAtPosition(position).toString();
                but2.setText(val2);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        sp3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                val3 = parent.getItemAtPosition(position).toString();
                but3.setText(val3);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        sp4.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                val4 = parent.getItemAtPosition(position).toString();
                but4.setText(val4);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        sp5.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                val5 = parent.getItemAtPosition(position).toString();
                but5.setText(val5);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        sp6.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                val6 = parent.getItemAtPosition(position).toString();
                but6.setText(val6);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

    public void doSet(View view) {

        but1.setVisibility(View.INVISIBLE);
        but2.setVisibility(View.INVISIBLE);
        but3.setVisibility(View.INVISIBLE);
        but4.setVisibility(View.INVISIBLE);
        but5.setVisibility(View.INVISIBLE);
        but6.setVisibility(View.INVISIBLE);
        set.setVisibility(View.INVISIBLE);
        lay.setVisibility(View.VISIBLE);
    }

    public void settDone(View view) {

        but1.setVisibility(View.VISIBLE);
        but2.setVisibility(View.VISIBLE);
        but3.setVisibility(View.VISIBLE);
        but4.setVisibility(View.VISIBLE);
        but5.setVisibility(View.VISIBLE);
        but6.setVisibility(View.VISIBLE);
        set.setVisibility(View.VISIBLE);
        lay.setVisibility(View.INVISIBLE);
    }
}
