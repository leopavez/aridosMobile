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
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.w3c.dom.Text;

import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import cz.msebera.android.httpclient.Header;

public class Produccion_planta extends AppCompatActivity {

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

    public static final String APIEnvioRegistrosProduccionPlanta = "http://santafeinversiones.org/api/aridos/produccion/xplanta";
    Registro_produccion_planta reg_prod = new Registro_produccion_planta();
    ArrayList<Registro_produccion_planta> listaregistroproduccionplanta;
    DatabaseHelper myDB;

    Spinner tipomaterial;
    TextInputEditText m3, procedencia;
    Button generarproduccion;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.produccion_planta);
        tipomaterial = (Spinner)findViewById(R.id.tipomaterialSpinner);
        m3 = (TextInputEditText)findViewById(R.id.txtm3ProduccionPlanta);
        procedencia = (TextInputEditText)findViewById(R.id.txtm3ProcedenciaProduccionPlanta);
        generarproduccion = (Button)findViewById(R.id.btnproduccion);
        progressDialog = new ProgressDialog(Produccion_planta.this);
        myDB = new DatabaseHelper(this);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.materiales_string,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tipomaterial.setAdapter(adapter);

        //BLUETOOTH
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        generarproduccion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tipomaterial.getItemAtPosition(tipomaterial.getSelectedItemPosition()).toString().equals("Seleccione material") ||
                        m3.getText().toString().equals("") || procedencia.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(), "Error, verifique los campos", Toast.LENGTH_SHORT).show();
                }else{
                    progressDialog.setTitle("Generando produccion");
                    progressDialog.setMessage("Espere un momento......");
                    progressDialog.setProgressStyle(progressDialog.STYLE_SPINNER);
                    progressDialog.setMax(100);
                    progressDialog.show();
                    progressDialog.setCancelable(false);

                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            try{

                                SharedPreferences preferences1 = getSharedPreferences("plantaApp", Context.MODE_PRIVATE);
                                final String planta = preferences1.getString("NAME_PLANTA", "");

                                SharedPreferences preferences = getSharedPreferences("printer", Context.MODE_PRIVATE);
                                String mask = preferences.getString("mask", "");

                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                                SimpleDateFormat horaformat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
                                Date date = new Date();
                                final String fecha = dateFormat.format(date);
                                final String hora = horaformat.format(date);

                                SharedPreferences preferences2 = getSharedPreferences("credenciales", Context.MODE_PRIVATE);
                                String name = preferences2.getString("NAME","NO EXISTE LA CREDENCIAL");
                                String lastname = preferences2.getString("LASTNAME", "NO EXISTE LA CREDENCIAL");

                                final String username = name + " "+lastname;



                                Boolean registro =myDB.registroProduccionPlanta(tipomaterial.getItemAtPosition(tipomaterial.getSelectedItemPosition()).toString(),m3.getText().toString(),procedencia.getText().toString(),planta,
                                        username,fecha,hora,"PENDIENTE");

                                if (registro == true){


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
                                            " " + "Tipo material: "+tipomaterial.getItemAtPosition(tipomaterial.getSelectedItemPosition()).toString()+"\n"+
                                            " " + "M3: "+m3.getText().toString()+"\n"+
                                            " " + "Procedencia: "+procedencia.getText().toString().toUpperCase()+"\n"+
                                            " " + "Planta: "+planta+"\n"+
                                            " " + "Usuario: "+username+"\n"+
                                            " " +"\n"+
                                            " " +"\n"+
                                            " " +"\n"+
                                            " "+" "+" "+" "+" "+" "+" "+"Produccion de planta"+" "+" "+" "+"\n"+
                                            " " +"\n"+
                                            " " +"\n"+
                                            " " +"\n";
                                    os.write(msg.getBytes());
                                    Thread.sleep(3000);
                                    mBluetoothSocket.close();
                                    //ENVIO PRODUCCION PLANTA
                                    SQLiteDatabase db = myDB.getReadableDatabase();
                                    String estado = "PENDIENTE";
                                    listaregistroproduccionplanta = new ArrayList<Registro_produccion_planta>();
                                    Cursor cursor3 = db.rawQuery("SELECT id, tipomaterial, m3, procedencia, planta, username, fecha, hora FROM prod_planta WHERE estado='" + estado + "'", null);


                                    while (cursor3.moveToNext()) {
                                        reg_prod = new Registro_produccion_planta();
                                        reg_prod.setId(cursor3.getInt(0));
                                        reg_prod.setTipomaterial(cursor3.getString(1));
                                        reg_prod.setM3(cursor3.getString(2));
                                        reg_prod.setProcedencia(cursor3.getString(3));
                                        reg_prod.setPlanta(cursor3.getString(4));
                                        reg_prod.setUsername(cursor3.getString(5));
                                        reg_prod.setFecha(cursor3.getString(6));
                                        reg_prod.setHora(cursor3.getString(7));
                                        listaregistroproduccionplanta.add(reg_prod);
                                    }
                                    for (int i = 0; i < listaregistroproduccionplanta.size(); i++) {

                                        final int id = listaregistroproduccionplanta.get(i).getId();
                                        final String tipomaterial = listaregistroproduccionplanta.get(i).getTipomaterial().toString();
                                        final String m3 = listaregistroproduccionplanta.get(i).getM3().toString();
                                        final String plantaenvio = listaregistroproduccionplanta.get(i).getPlanta().toString();
                                        final String usernameenvio = listaregistroproduccionplanta.get(i).getUsername().toString();
                                        final String fechaenvio = listaregistroproduccionplanta.get(i).getFecha().toString();
                                        final String horaenvio = listaregistroproduccionplanta.get(i).getHora().toString();
                                        final String procedencia = listaregistroproduccionplanta.get(i).getProcedencia().toString();


                                        listaregistroproduccionplanta.remove(i);

                                        final RequestParams params = new RequestParams();
                                        String idprod = String.valueOf(id);
                                        params.put("idprodplanta", idprod);
                                        params.put("tipomaterial", tipomaterial);
                                        params.put("m3", m3);
                                        params.put("fecha", fechaenvio);
                                        params.put("hora", horaenvio);
                                        params.put("planta", plantaenvio);
                                        params.put("username", usernameenvio);
                                        params.put("procedencia", procedencia);

                                        Handler handler = new Handler(Looper.getMainLooper());
                                        Runnable runnable = new Runnable() {
                                            @Override
                                            public void run() {
                                                AsyncHttpClient client = new AsyncHttpClient();
                                                client.setMaxRetriesAndTimeout(0,1500);
                                                client.post(APIEnvioRegistrosProduccionPlanta, params, new AsyncHttpResponseHandler() {
                                                    @Override
                                                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                                                        if (statusCode==200){
                                                            String response = new String(responseBody).toUpperCase();
                                                            Log.i("TAG", "ENVIO OK" + response);
                                                            SQLiteDatabase db = myDB.getReadableDatabase();

                                                            db.execSQL("UPDATE prod_planta SET estado='ENVIADO' WHERE id='" + id + "'");
                                                        }
                                                    }

                                                    @Override
                                                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                                        Log.i("REQUEST FAIL",""+error.toString());
                                                    }
                                                });
                                            }
                                        };
                                        handler.post(runnable);

                                    }

                                    Thread.sleep(3000);
                                    progressDialog.dismiss();

                                    Intent intent = new Intent(Produccion_planta.this, Menu.class);
                                    startActivity(intent);
                                    finish();

                                }else if (registro == false) {
                                    Thread.sleep(4000);
                                    progressDialog.dismiss();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), "OCURRIO UN PROBLEMA AL GENERAR EL REPORTE", Toast.LENGTH_SHORT).show();

                                    }
                                });
                                }
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }
            }
        });
    }
}
