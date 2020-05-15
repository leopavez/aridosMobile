package com.ingenieriasantafe.leandro.aridosmobile;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class bluetooth_list extends AppCompatActivity {


    protected static final String TAG = "TAG";
    private BluetoothAdapter mBluetoothAdapter;
    private ArrayAdapter<String> mPairedDevicesArrayAdapter;
    private UUID applicationUUID = UUID
            .fromString("00001101-0000-1000-8000-00805F9B34FB");
    private BluetoothSocket mBluetoothSocket;
    BluetoothDevice mBluetoothDevice;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bluetooth_list);
        mPairedDevicesArrayAdapter = new ArrayAdapter<String>(this, R.layout.device_name);
        ListView mPairedListView = (ListView) findViewById(R.id.paired_devices);
        mPairedListView.setAdapter(mPairedDevicesArrayAdapter);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> mPairedDevices = mBluetoothAdapter.getBondedDevices();
        mPairedListView.setOnItemClickListener(mDeviceClickListener);

        if (mPairedDevices.size() > 0) {
            for (BluetoothDevice mDevice : mPairedDevices) {
                mPairedDevicesArrayAdapter.add(mDevice.getName() + "\n" + mDevice.getAddress());
            }
        } else {
            String mNoDevices = "None Paired";//getResources().getText(R.string.none_paired).toString();
            mPairedDevicesArrayAdapter.add(mNoDevices);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBluetoothAdapter != null) {
            mBluetoothAdapter.cancelDiscovery();
        }
    }

    private AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> mAdapterView, View mView, int mPosition, long mLong) {

            String mDeviceName = ((TextView) mView).getText().toString();
            String mDeviceAddress = mDeviceName.substring(mDeviceName.length() - 17);

            ConfirmPop(mDeviceName, mDeviceAddress);

        }
    };


    public void ConfirmPop(String device, final String address){


        AlertDialog.Builder confirmacion = new AlertDialog.Builder(this);
        confirmacion.setTitle("Confirmar dispositivo: " +device);
        confirmacion.setMessage("¿Estas seguro de vincular esta impresora?");
        confirmacion.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MaskPrinter(address);
                Toast.makeText(getApplicationContext(), "Impresora Configurada", Toast.LENGTH_SHORT).show();

                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            try{

                                SharedPreferences preferences = getSharedPreferences("printer", Context.MODE_PRIVATE);
                                String mask = preferences.getString("mask", "");
                                mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

                                if ((mBluetoothAdapter == null) || (!mBluetoothAdapter.isEnabled())) {
                                    throw new Exception("Bluetooth adapter no esta funcionando o no esta habilitado");
                                }

                                mBluetoothDevice = mBluetoothAdapter.getRemoteDevice(mask);
                                mBluetoothSocket = mBluetoothDevice.createRfcommSocketToServiceRecord(applicationUUID);
                                mBluetoothAdapter.cancelDiscovery();
                                mBluetoothSocket.connect();

                                OutputStream os = mBluetoothSocket.getOutputStream();
                                InputStream is = mBluetoothSocket.getInputStream();

                                Handler mHandler = new Handler(Looper.getMainLooper());


                                String msg = " "+" "+" "+ " "+" "+"IMPRESORA CONFIGURADA"+" "+"\n"+
                                        " " +"\n"+
                                        " " +"\n"+
                                        " " +"\n";
                                os.write(msg.getBytes());
                                mBluetoothSocket.close();

                            }catch (Exception e){
                                e.printStackTrace();
                            }

                        }
                    }).start();

                    Intent intent = new Intent(bluetooth_list.this, AjustesParametros.class);
                    startActivity(intent);
                    finish();

            }
        });
        confirmacion.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        confirmacion.show();
    }

    private void MaskPrinter(String mask){

        SharedPreferences preferences = getSharedPreferences("printer", Context.MODE_PRIVATE);

        SharedPreferences.Editor editor =preferences.edit();
        editor.putString("mask", mask);
        editor.commit();

        }

    }

