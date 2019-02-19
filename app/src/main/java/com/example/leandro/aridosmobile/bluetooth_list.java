package com.example.leandro.aridosmobile;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Set;

public class bluetooth_list extends AppCompatActivity {


    protected static final String TAG = "TAG";
    private BluetoothAdapter mBluetoothAdapter;
    private ArrayAdapter<String> mPairedDevicesArrayAdapter;

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
                Toast.makeText(getApplicationContext(),"Impresora Configurada",Toast.LENGTH_SHORT).show();

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

