package net.londatiga.android.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Created by Allen Sanford on 11/9/2014.
 */
public class HeatMain extends Activity {
    BluetoothSocket mSocket;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private final String TAG = "HeatMain";
    private BluetoothDevice paired = PairedDevice.getInstance(null);
    private final double INIT_GOAL = 85.0;
    private double temp = INIT_GOAL;
    Button goalTemp, currTemp;
    RadioButton power;
    Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.heat_on);
        setupButtons();
        setupConnection();
        if(mSocket != null) {
            reset();
            mHandler = new Handler();
            mHandler.postDelayed(mRunnable, 500);
        }
    }

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                InputStream io = mSocket.getInputStream();
                currTemp.setText(Float.toString(io.read()));
            } catch (IOException e1) {
                Log.d(TAG, paired.getName() + " did not write.");
            }
            mHandler.postDelayed(mRunnable, 500);
            }
        };



    protected  void onPause(){
        super.onPause();
        if (!power.isChecked() && mSocket!=null){
            reset();
        }
    }
    protected  void onStop(){
        super.onStop();
        if (!power.isChecked() && mSocket!=null){
            reset();
        }
    }


    private void setupButtons() {
        //1. get reference to buttons
        Button btnUp = (Button) findViewById(R.id.btnDown);
        Button btnDown = (Button) findViewById(R.id.btnUp);
        ImageButton toBlue = (ImageButton) findViewById(R.id.btnBluetooth);
        power = (RadioButton) findViewById(R.id.Power);
        goalTemp = (Button) findViewById(R.id.btnGoal);
        currTemp = (Button) findViewById(R.id.btnTemp);

        btnUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSocket.isConnected()) {
                    try {
                        Toast.makeText(HeatMain.this, "Temperature Increased", Toast.LENGTH_SHORT).show();
                        OutputStream io = mSocket.getOutputStream();
                        io.write('0');
                        temp ++;
                        goalTemp.setText(Double.toString(temp));
                    } catch (IOException e1) {
                        Log.d(TAG, paired.getName() + " did not write.");
                    }
                }
            }});


        btnDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mSocket.isConnected()) {
                    try {
                        Toast.makeText(HeatMain.this, "Temperature Decreased", Toast.LENGTH_SHORT).show();
                        OutputStream io = mSocket.getOutputStream();
                        temp --;
                        goalTemp.setText(Double.toString(temp));
                        io.write('1');
                    }catch(IOException e1){
                        Log.d(TAG, paired.getName()+" did not write.");
                    }
                }
            }
        });

        power.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                power.setChecked(!power.isChecked());
                if(mSocket.isConnected()) {
                    reset();
                }
             }
        });
        toBlue.setOnClickListener(toBluetooth);
    };

    private void setupConnection(){
        if(paired == null){
            Toast.makeText(getApplicationContext(), "Please unpair and repair again.", Toast.LENGTH_SHORT).show();

        }
        else if(paired.getBondState() == paired.BOND_BONDED){
            Log.d(TAG, paired.getName());
            try{
                mSocket =paired.createInsecureRfcommSocketToServiceRecord(MY_UUID);
             } catch(IOException e1){
                Log.d(TAG,"Socket not created");
                e1.printStackTrace();
            }
            try {
                mSocket.connect();
            }catch(IOException e2){
                try{
                    mSocket.close();
                    Log.d(TAG,"Cannot connect");
                }catch(IOException e3){
                    Log.d(TAG,"Socket not closed");
                    e3.printStackTrace();
                }
            }
        }
    }

    private void reset(){
        if(mSocket.isConnected()) {
            try {
                OutputStream io = mSocket.getOutputStream();
                temp = INIT_GOAL;
                io.write('3');
                goalTemp.setText(Double.toString(temp));
                power.setChecked(false);
            }catch(IOException e1){
                Log.d(TAG, paired.getName()+" did not write.");
            }
        }
    }

    private View.OnClickListener toBluetooth = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            doButton();
        }
        public void doButton(){
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }
    };
}
