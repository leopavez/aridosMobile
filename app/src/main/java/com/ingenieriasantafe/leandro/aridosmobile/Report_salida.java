package com.ingenieriasantafe.leandro.aridosmobile;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class Report_salida extends AppCompatActivity {

    Spinner tipomaterial;
    AutoCompleteTextView patente;
    EditText m3, nombrechofer, procedencia;
    Spinner destinomaterial;
    Button GenerarSalida;
    ProgressDialog progressDialog;

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

    ArrayList<Unegocios> unegocioslist;
    ArrayList<String> listaunegocios;


    DatabaseHelper myDB;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report_salida);
        tipomaterial = (Spinner) findViewById(R.id.tipomaterialSalidaSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.materiales_string,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tipomaterial.setAdapter(adapter);
        patente = (AutoCompleteTextView)findViewById(R.id.patenteSalida);
        m3 = (EditText)findViewById(R.id.m3Salida);
        nombrechofer = (EditText) findViewById(R.id.choferSalida);
        procedencia = (EditText) findViewById(R.id.procedenciaSalida);
        GenerarSalida = (Button)findViewById(R.id.btngenerarSalida);
        destinomaterial = (Spinner)findViewById(R.id.destinomaterialsalida);
        progressDialog = new ProgressDialog(Report_salida.this);

        myDB = new DatabaseHelper(this);
        GenerarSalida.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SQLiteDatabase db = myDB.getReadableDatabase();
                String pt = patente.getText().toString();

                Cursor cursor = db.rawQuery("SELECT id FROM vehiculos WHERE LOWER(patente) ='"+pt.toLowerCase()+"'",null);

                if (cursor.moveToFirst() == true){

                    ingresoSalida();
                }else{
                    Toast.makeText(Report_salida.this, "La patente no se encuentra en el sistema", Toast.LENGTH_SHORT).show();
                }
            }
        });

        LoadDataPatentes();
        CargadeUnegocios();

        ArrayAdapter<CharSequence> adaptador = new ArrayAdapter(this,android.R.layout.simple_spinner_item,listaunegocios);
        adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        destinomaterial.setAdapter(adaptador);


    }

    private void LoadDataPatentes(){
        List<Vehiculos> patents = new ArrayList<Vehiculos>();
        final PatentesSearchAdapter patentesSearchAdapter = new PatentesSearchAdapter(getApplicationContext(),patents);
        patente.setThreshold(1);
        patente.setAdapter(patentesSearchAdapter);

        patente.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {



                m3.setText(patentesSearchAdapter.getItem(position).getM3());
            }
        });
    }

    private void CargadeUnegocios(){
        SQLiteDatabase db = myDB.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT nombre FROM unegocios",null);
        unegocioslist = new ArrayList<Unegocios>();
        Unegocios unegocios = null;

        while (cursor.moveToNext()){
            unegocios = new Unegocios();
            unegocios.setNombre(cursor.getString(0));

            unegocioslist.add(unegocios);
        }

        listaunegocios = new ArrayList<String>();
        listaunegocios.add("SELECCIONE DESTINO");

        for (int i=0; i<unegocioslist.size();i++){

            listaunegocios.add(unegocioslist.get(i).getNombre());
        }


    }

    private void ingresoSalida(){
        if (patente.getText().toString().equals("") || m3.getText().toString().equals("") ||
                nombrechofer.getText().toString().equals("") || procedencia.getText().toString().equals("")){
            Toast.makeText(getApplicationContext(), "Error, favor complete los campos",Toast.LENGTH_SHORT).show();
        }else{
            if (tipomaterial.getSelectedItem().toString().equals("Seleccione material") || destinomaterial.getSelectedItem().toString().equals("SELECCIONE DESTINO")){
                Toast.makeText(getApplicationContext(), "Error, favor seleccione un material o destino valido",Toast.LENGTH_SHORT).show();
            }else{

                SharedPreferences preferences1 = getSharedPreferences("plantaApp", Context.MODE_PRIVATE);
                final String planta = preferences1.getString("NAME_PLANTA", "");

                SharedPreferences preferences = getSharedPreferences("credenciales", Context.MODE_PRIVATE);
                String name = preferences.getString("NAME","NO EXISTE LA CREDENCIAL");
                String lastname = preferences.getString("LASTNAME", "NO EXISTE LA CREDENCIAL");

                final String username = name + " "+lastname;

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                SimpleDateFormat horaformat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
                Date date = new Date();
                final String fecha = dateFormat.format(date);
                final String hora = horaformat.format(date);

                Toast.makeText(this, "DESTINO SELECCIONADO: "+destinomaterial.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
                Boolean registro = myDB.RegistroSalida(patente.getText().toString(),m3.getText().toString(),tipomaterial.getSelectedItem().toString(),planta,nombrechofer.getText().toString(),
                        username,fecha,hora,"PENDIENTE",procedencia.getText().toString(),destinomaterial.getSelectedItem().toString());

                if (registro == true) {
                    progressDialog.setTitle("Generando salida");
                    progressDialog.setMessage("Espere un momento......");
                    progressDialog.setProgressStyle(progressDialog.STYLE_SPINNER);
                    progressDialog.setMax(100);
                    progressDialog.show();
                    progressDialog.setCancelable(false);

                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            try {

                                SQLiteDatabase db = myDB.getWritableDatabase();
                                Cursor cursor = db.rawQuery("SELECT id FROM registros_salida ORDER BY id DESC",null);
                                if (cursor.moveToFirst() == true){
                                    String nvale = cursor.getString(0);

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

                                    Handler mHandler =  new Handler(Looper.getMainLooper());

                                    String msg = " "+" "+" "+" "+" "+" "+" "+"Aridos Santa Fe "+" "+" "+" "+"\n"+
                                            " " + " "+" "+""+" "+" " +" "+" "+" "+""+" "+"\n"+
                                            " " +"\n"+
                                            " " + "Fecha: "+fecha+" "+""+hora+"\n"+
                                            " " + "N Vale: "+nvale+"\n"+
                                            " " + "Patente: "+patente.getText().toString().toUpperCase()+"\n"+
                                            " " + "Tipo material: "+tipomaterial.getSelectedItem().toString()+"\n"+
                                            " " + "M3: "+m3.getText().toString()+"\n"+
                                            " " + "Procedencia: "+procedencia.getText().toString().toUpperCase()+"\n"+
                                            " " + "Destino: "+destinomaterial.getSelectedItem().toString().toUpperCase()+"\n"+
                                            " " + "Chofer: "+nombrechofer.getText().toString().toUpperCase()+"\n"+
                                            " " + "Usuario: "+username+"\n"+
                                            " " + "Planta: "+planta+"\n"+
                                            " " +"\n"+
                                            " " +"\n"+
                                            " " + "Nombre:........................" +"\n"+
                                            " " +"\n"+
                                            " " +"\n"+
                                            " " + "Firma:........................." +"\n"+
                                            " " +"\n"+
                                            " " +"\n"+
                                            " "+" "+" "+" "+" "+" "+" "+"Report Salida"+" "+" "+" "+"\n"+
                                            " "+" "+" "+" "+" "+" "+" "+"Copia Obra "+" "+" "+" "+"\n"+
                                            " " +"\n"+
                                            " " +"\n"+
                                            " " +"\n";
                                    os.write(msg.getBytes());
                                    Thread.sleep(5000);

                                    String msg2 = " "+" "+" "+" "+" "+" "+" "+"Aridos Santa Fe "+" "+" "+" "+"\n"+
                                            " " + " "+" "+""+" "+" " +" "+" "+" "+""+" "+"\n"+
                                            " " +"\n"+
                                            " " + "Fecha: "+fecha+" "+""+hora+"\n"+
                                            " " + "N Vale: "+nvale+"\n"+
                                            " " + "Patente: "+patente.getText().toString().toUpperCase()+"\n"+
                                            " " + "Tipo material: "+tipomaterial.getSelectedItem().toString()+"\n"+
                                            " " + "M3: "+m3.getText().toString()+"\n"+
                                            " " + "Procedencia: "+procedencia.getText().toString().toUpperCase()+"\n"+
                                            " " + "Destino: "+destinomaterial.getSelectedItem().toString().toUpperCase()+"\n"+
                                            " " + "Chofer: "+nombrechofer.getText().toString().toUpperCase()+"\n"+
                                            " " + "Usuario: "+username+"\n"+
                                            " " + "Planta: "+planta+"\n"+
                                            " " +"\n"+
                                            " " +"\n"+
                                            " " + "Nombre:........................" +"\n"+
                                            " " +"\n"+
                                            " " +"\n"+
                                            " " + "Firma:........................." +"\n"+
                                            " " +"\n"+
                                            " " +"\n"+ " "+" "+" "+" "+" "+" "+" "+"Report Salida"+" "+" "+" "+"\n"+
                                            " "+" "+" "+" "+" "+" "+"Copia conductor"+" "+" "+" "+"\n"+
                                            " " +"\n"+
                                            " " +"\n"+
                                            " " +"\n";

                                    os.write(msg2.getBytes());
                                    Thread.sleep(5000);
                                    String msg3 = " "+" "+" "+" "+" "+" "+" "+"Aridos Santa Fe "+" "+" "+" "+"\n"+
                                            " " + " "+" "+""+" "+" " +" "+" "+" "+""+" "+"\n"+
                                            " " +"\n"+
                                            " " + "Fecha: "+fecha+" "+""+hora+"\n"+
                                            " " + "N Vale: "+nvale+"\n"+
                                            " " + "Patente: "+patente.getText().toString().toUpperCase()+"\n"+
                                            " " + "Tipo material: "+tipomaterial.getSelectedItem().toString()+"\n"+
                                            " " + "M3: "+m3.getText().toString()+"\n"+
                                            " " + "Procedencia: "+procedencia.getText().toString().toUpperCase()+"\n"+
                                            " " + "Destino: "+destinomaterial.getSelectedItem().toString().toUpperCase()+"\n"+
                                            " " + "Chofer: "+nombrechofer.getText().toString().toUpperCase()+"\n"+
                                            " " + "Usuario: "+username+"\n"+
                                            " " + "Planta: "+planta+"\n"+
                                            " " +"\n"+
                                            " " +"\n"+
                                            " " + "Nombre:........................" +"\n"+
                                            " " +"\n"+
                                            " " +"\n"+
                                            " " + "Firma:........................." +"\n"+
                                            " " +"\n"+
                                            " " +"\n"+ " "+" "+" "+" "+" "+" "+" "+"Report Salida"+" "+" "+" "+"\n"+
                                            " "+" "+" "+" "+" "+" "+"Copia Jefe planta "+" "+" "+" "+"\n"+
                                            " " +"\n"+
                                            " " +"\n"+
                                            " " +"\n";
                                    os.write(msg3.getBytes());
                                    mBluetoothSocket.close();
                                    Thread.sleep(3000);
                                    progressDialog.dismiss();
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {

                                            Toast.makeText(getApplicationContext(), "Salida generada correctamente", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                    Intent intent = new Intent(Report_salida.this, Menu.class);
                                    startActivity(intent);
                                    finish();


                                }

                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }


                        }
                    }).start();

                }
                if (registro == false){

                    progressDialog.setTitle("Generando salida");
                    progressDialog.setMessage("Oh! no se pudo guardar......");
                    progressDialog.setProgressStyle(progressDialog.STYLE_SPINNER);
                    progressDialog.setMax(100);
                    progressDialog.show();
                    progressDialog.setCancelable(false);

                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            try {
                                Thread.sleep(3000);
                                progressDialog.dismiss();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(),"ERROR AL GENERAR SALIDA",Toast.LENGTH_LONG).show();
                                    }
                                });
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }
            }
        }
    }
}
