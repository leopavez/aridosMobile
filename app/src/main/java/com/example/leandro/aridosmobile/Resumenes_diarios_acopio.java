package com.example.leandro.aridosmobile;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
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
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ProgressBar;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.IllegalFormatCodePointException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class Resumenes_diarios_acopio extends AppCompatActivity {

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


    private static final String TAG = Report_entrada.class.getName();
    private AutoCompleteTextView pt;
    Button xpatente;

    ProgressDialog progress;

 @Override
 protected void onCreate(Bundle savedInstanceState) {
  super.onCreate(savedInstanceState);
  setContentView(R.layout.resumen_diario_acopio);
  pt = findViewById(R.id.patentesResumenAcopio);
  xpatente = (Button)findViewById(R.id.btnresumenxpatenteacopio);
  xpatente.setOnClickListener(new View.OnClickListener() {
   @Override
   public void onClick(View v) {
       ProgressDialog progressDialog = new ProgressDialog(Resumenes_diarios_acopio.this );
      new Thread(new Runnable() {
          @Override
          public void run() {

              for (int i=0; i<=2;i++){
                  try{
                  Log.i(TAG,"FOR: "+i);
                  ImprimirResumenXPatente(pt.getText().toString());
                  Thread.sleep(5000);
                  }catch (Exception e){
                      e.printStackTrace();
                  }
              }
          }
      }).start();
   }
  });
  LoadDataPatentes();

 }
 private void LoadDataPatentes(){
  List<Vehiculos> patents = new ArrayList<Vehiculos>();
  final PatentesSearchAdapter patentesSearchAdapter = new PatentesSearchAdapter(getApplicationContext(),patents);
  pt.setThreshold(1);
  pt.setAdapter(patentesSearchAdapter);
 }

 private void ImprimirResumenXPatente(String patente){

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

         Handler mHandler =  new Handler(Looper.getMainLooper());

         SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
         Date date = new Date();
         String fecha = dateFormat.format(date);

         SQLiteDatabase db = myDB.getWritableDatabase();

         Cursor cursor = db.rawQuery("SELECT id, fecha, hora, patente, m3, planta, chofer,username FROM registros_acopio WHERE patente ='"+patente+"' AND fecha ='"+fecha+"' ORDER BY id asc", null);

         if (cursor.moveToFirst()){

             int totalm3 = 0;

             for (int i = 0; i<=cursor.getCount(); cursor.moveToNext()){
                 i = i+1;
                 String fechaAcopio = cursor.getString(1);
                 String horaAcopio = cursor.getString(2);
                 String m3 = cursor.getString(4);
                 String planta = cursor.getString(5);
                 String chofer = cursor.getString(6);
                 String usuario = cursor.getString(7);
                 int m3vuelta = Integer.parseInt(m3);

                 totalm3 = totalm3 + m3vuelta;

                 if (i == 1){

                     String msg = " "+" "+" "+ " "+" "+" "+" "+" "+" "+" Aridos Santa Fe "+" "+"\n"+
                             " " + " "+" "+""+" "+" "+ " "+" "+" "+" "+" "+" "+" "+" "+pt.getText().toString()+" "+" "+""+" "+"\n"+
                             " " +"\n"+
                             " " + "Fecha: "+fechaAcopio+"\n"+
                             " " + "Hora: "+horaAcopio+"\n"+
                             " " + "Cantidad M3: "+m3+"\n"+
                             " " + "Usuario: "+usuario+"\n"+
                             " " +"\n"+
                             " " + " "+" "+"--------------------------"+" "+" "+"\n"+
                             " " +"\n";
                     os.write(msg.getBytes());

                     }
                     else if (i>1 && i<=cursor.getCount()){


                         String msg2 =
                                 " " + "Fecha: "+fechaAcopio+"\n"+
                                 " " + "Hora: "+horaAcopio+"\n"+
                                 " " + "Cantidad M3: "+m3+"\n"+
                                 " " + "Usuario: "+usuario+"\n"+
                                 " " +"\n"+
                                 " " + " "+" "+"--------------------------"+" "+" "+"\n"+
                                 " " +"\n";
                         os.write(msg2.getBytes());

                 }if (i == cursor.getCount()){
                     String msg3 = " " + " "+" "+""+" "+" "+ " "+" "+" "+" "+" "+" "+" "+" Resumen: "+" "+" "+""+" "+"\n"+
                             " " + " "+" "+""+" "+" " +" "+" "+" "+""+" "+"\n"+
                             " " + "Chofer: "+chofer+"\n"+
                             " " + "Total de vueltas: "+i+"\n"+
                             " " + "Patente: "+pt.getText().toString()+"\n"+
                             " " + "Total M3: "+totalm3+"\n"+
                             " " + "Planta: "+planta+"\n"+
                             " " +"\n"+
                             " " +"\n"+
                             " " +"\n"+
                             " " + "RUT:..........................." +"\n"+
                             " " +"\n"+
                             " " +"\n"+
                             " " + "Nombre:........................" +"\n"+
                             " " +"\n"+
                             " " +"\n"+
                             " " + "Firma:........................." +"\n"+
                             " " +"\n"+
                             " " +"\n";
                     os.write(msg3.getBytes());
                 }

             }
             mBluetoothSocket.close();
         }
     }catch (Exception e) {
         Log.e("Report_entrada","Error: "+e);
     }
 }
}
