package com.example.controlrc;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener {
    private final String MAC = "00:21:13:00:04:84";
    private final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
    private Button forward, back, right, left, connect;
    private boolean found;
    private BluetoothAdapter adapter;
    private Set<BluetoothDevice> devices;
    private BluetoothDevice device;
    private BluetoothSocket socket;
    private OutputStream out;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        forward = (Button) findViewById(R.id.forward);
        back = (Button) findViewById(R.id.back);
        right = (Button) findViewById(R.id.right);
        left = (Button) findViewById(R.id.left);
        connect = (Button) findViewById(R.id.connect);


        forward.setOnTouchListener(this);
        back.setOnTouchListener(this);
        right.setOnTouchListener(this);
        left.setOnTouchListener(this);


        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(BTinit()){
                    BTconnect();
                }
            }
        });
    }

    private boolean BTinit(){
        found = false;
        adapter = BluetoothAdapter.getDefaultAdapter();
        if(adapter == null){
            Toast.makeText(getApplicationContext(),"Urządzenie nie obsługuje bluetooth",Toast.LENGTH_SHORT).show();
            return found;
        }
        if(!adapter.isEnabled()){
            Intent enableBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBT,0);
        }
        try{
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        devices = adapter.getBondedDevices();
        if(devices.isEmpty()){
            Toast.makeText(getApplicationContext(),"Brak sparowanych urządzeń",Toast.LENGTH_SHORT).show();
        }else{
            for(BluetoothDevice i : devices){
                if(i.getAddress().equals(MAC)){
                    device = i;
                    found = true;
                    Toast.makeText(getApplicationContext(),"ZNALEZIONO DYSK Z MAC",Toast.LENGTH_SHORT).show();
                    break;
                }
            }
        }
        return found;
    }


    @SuppressLint("MissingPermission")
    private void BTconnect(){
        boolean conected = true;
        try {
            socket = device.createInsecureRfcommSocketToServiceRecord(MY_UUID);
            Toast.makeText(getApplicationContext(),"UTWORZONO SOCKET",Toast.LENGTH_SHORT).show();
            socket.connect();
            Toast.makeText(getApplicationContext(),"Połączenie udane",Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            conected = false;
            Toast.makeText(getApplicationContext(),"Połączenie nie udane "+e.getMessage(),Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        if(conected){
            try {
                out = socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (view.getId()){
            case R.id.forward:
                sleep();
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    move("1");
                }else if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                    move("5");
                }
                break;
            case R.id.back:
                sleep();
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    move("2");
                }else if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                    move("5");
                }
                break;
            case R.id.right:
                sleep();
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    move("3");
                }else if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                    move("5");
                }
                break;
            case R.id.left:
                sleep();
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    move("4");
                }else if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                    move("5");
                }
                break;
        }
        return false;
    }

    private void sleep(){
        try {
            Thread.sleep(25);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void move(String c){
        try{
            out.write(c.getBytes());
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}