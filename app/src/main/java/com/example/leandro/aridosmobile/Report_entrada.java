package com.example.leandro.aridosmobile;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class Report_entrada extends AppCompatActivity {

    private static final String TAG = Report_entrada.class.getName();
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    Button mScan, mPrint, mDisc;
    BluetoothAdapter mBluetoothAdapter;
    private UUID applicationUUID = UUID
            .fromString("00001101-0000-1000-8000-00805F9B34FB");
    private ProgressDialog mBluetoothConnectProgressDialog;
    private BluetoothSocket mBluetoothSocket;
    BluetoothDevice mBluetoothDevice;

    DatabaseHelper myDB= new DatabaseHelper(this);

    EditText m3, chofername;
    private AutoCompleteTextView pt;
    Button registro;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reportentrada);
        m3 = (EditText) findViewById(R.id.txtm3entrada);
        chofername = (EditText)findViewById(R.id.txtnchofer);
        registro = (Button)findViewById(R.id.btnregistroentrada);
        pt = findViewById(R.id.patentesreporteentrada);

        registro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pt.getText().toString().equals("") || m3.getText().toString().equals("")
                        || chofername.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(),"Debes llenar todos los campos",Toast.LENGTH_SHORT).show();
                }else{
                    SharedPreferences preferences1 = getSharedPreferences("plantaApp", Context.MODE_PRIVATE);
                    String plantaname = preferences1.getString("NAME_PLANTA", "");

                    SharedPreferences preferences = getSharedPreferences("credenciales", Context.MODE_PRIVATE);

                    String name = preferences.getString("NAME","NO EXISTE LA CREDENCIAL");
                    String lastname = preferences.getString("LASTNAME", "NO EXISTE LA CREDENCIAL");

                    String username = name + " "+lastname;

                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    SimpleDateFormat horaformat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
                    Date date = new Date();
                    String fecha = dateFormat.format(date);
                    String hora = horaformat.format(date);

                    myDB.Registro_acopio(pt.getText().toString(),m3.getText().toString(), plantaname,chofername.getText().toString(),
                            username,fecha,hora,"PENDIENTE");
                    EstadoVoucher();
                }
            }
        });
        LoadDataPatentes();
    }

    private void LoadDataPatentes(){
        List<Vehiculos> patents = new ArrayList<Vehiculos>();
        final PatentesSearchAdapter patentesSearchAdapter = new PatentesSearchAdapter(getApplicationContext(),patents);
        pt.setThreshold(1);
        pt.setAdapter(patentesSearchAdapter);

        pt.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                m3.setText(patentesSearchAdapter.getItem(position).getM3());
            }
        });
    }

    private void EstadoVoucher(){
        SharedPreferences preferences = getSharedPreferences("Voucher", Context.MODE_PRIVATE);
        String estado = preferences.getString("estado","Sin estado");

        if (estado.toString().equals("true")){
            TareaenSegundoPlano sp = new TareaenSegundoPlano();
            sp.execute();
            Intent intent = new Intent(this,Report_entrada.class);
            startActivity(intent);
        }else if (estado.toString().equals("false")){
            Toast.makeText(getApplicationContext(), "Registro de entrada ingresado",Toast.LENGTH_SHORT).show();
        }
    }

    private void ImprimirVoucher(String cantidad, String nvale){
        try {
            int nvoucher = Integer.parseInt(cantidad);
            SharedPreferences preferences = getSharedPreferences("printer", Context.MODE_PRIVATE);
            String mask = preferences.getString("mask", "");
            SharedPreferences preferences1 = getSharedPreferences("plantaApp", Context.MODE_PRIVATE);
            String plantaname = preferences1.getString("NAME_PLANTA", "");
            String idplanta = preferences1.getString("ID_PLANTA","");

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

            Handler mHandler =  new Handler(Looper.getMainLooper());

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat horaformat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
            Date date = new Date();
            String fecha = dateFormat.format(date);
            String hora = horaformat.format(date);

            for (int i = 0; i <= nvoucher-1; i++) {

                String msg = " "+" "+" "+" Aridos Santa Fe "+" "+"\n"+
                        " " + " "+" "+""+" "+" " +" "+" "+" "+""+" "+"\n"+
                        " " +"\n"+
                        " " + "N vale: "+nvale+"\n"+
                        " " + "Patente: "+pt.getText().toString()+"\n"+
                        " " + "Cantidad M3: "+m3.getText().toString()+"\n"+
                        " " + "Chofer: "+chofername.getText().toString()+"\n"+
                        " " + "Fecha: "+fecha+"\n"+
                        " " + "Hora: "+hora+"\n"+
                        " " + "Planta: "+plantaname+"\n"+
                        " " +"\n"+
                        " " + " "+" "+" Acopio - Report entrada "+" "+" "+"\n"+
                        " " +"\n"+
                        " " +"\n"+
                        " " +"\n";
                os.write(msg.getBytes());
                Thread.sleep(6000);
            }
            mBluetoothSocket.close();
        }catch (Exception e){
            Log.e("Report_entrada","Error: "+e);
        }

    }

    public class TareaenSegundoPlano extends AsyncTask<Void, Integer, Boolean> {
        @Override
        protected Boolean doInBackground(Void... voids) {
            SharedPreferences preferences1 = getSharedPreferences("Cvoucher", Context.MODE_PRIVATE);
            String cantidad = preferences1.getString("cantidad","1");
            SQLiteDatabase db = myDB.getWritableDatabase();
            Cursor cursor = db.rawQuery("SELECT id FROM registros_acopio ORDER BY id DESC LIMIT 1",null);
            if (cursor.moveToFirst()){
                String nvale = cursor.getString(0);
                ImprimirVoucher(cantidad,nvale);
            }
            return true;
        }
    }


}
