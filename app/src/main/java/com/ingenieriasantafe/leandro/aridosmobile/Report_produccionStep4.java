package com.ingenieriasantafe.leandro.aridosmobile;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class Report_produccionStep4 extends AppCompatActivity {


    TextInputEditText observaciones;

    DatabaseHelper myDB;
    ProgressDialog progressDialog;
    Button finalizar;

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


    String patente,horasmaquina,nivelaceitemotor,nivelaceitehidraulico,nivelaceitetransmision,sistemaelectrico,otrasrevisiones,combustible,operador,
    botiquin,extintor,alarmaretroceso,seguroobligatorio,permisocirculacion,revisiontecnica,baliza;
    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reportproduccionstep4);
        observaciones = (TextInputEditText)findViewById(R.id.txtObservaciones);
        finalizar = (Button) findViewById(R.id.btnfinalizar);
        myDB = new DatabaseHelper(this);
        progressDialog = new ProgressDialog(Report_produccionStep4.this);

        Bundle parametros = this.getIntent().getExtras();
        patente = parametros.getString("Patente");
        horasmaquina = parametros.getString("HorasMaquina");
        nivelaceitemotor = parametros.getString("NivelAceiteMotor");
        nivelaceitehidraulico = parametros.getString("NivelAceiteHidraulico");
        nivelaceitetransmision = parametros.getString("NivelAceiteTransmision");
        sistemaelectrico = parametros.getString("SistemaElectrico");
        otrasrevisiones = parametros.getString("OtrasRevisiones");
        combustible = parametros.getString("Combustible");
        operador = parametros.getString("Operador");
        botiquin = parametros.getString("Botiquin");
        extintor = parametros.getString("Extintor");
        alarmaretroceso = parametros.getString("AlarmaRetroceso");
        seguroobligatorio = parametros.getString("SeguroObligatorio");
        permisocirculacion = parametros.getString("PermisoCirculacion");
        revisiontecnica = parametros.getString("RevisionTecnica");
        baliza = parametros.getString("Baliza");

        finalizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try{

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

                    Boolean registro = myDB.Registro_Produccion(patente,horasmaquina,combustible,operador,nivelaceitemotor,nivelaceitehidraulico,nivelaceitetransmision,sistemaelectrico,
                            otrasrevisiones,botiquin,extintor,alarmaretroceso,baliza,revisiontecnica,seguroobligatorio,permisocirculacion,fecha,hora,username,planta,"PENDIENTE");

                    if (registro == true){

                        progressDialog.setTitle("Guardando informacion");
                        progressDialog.setMessage("Espere un momento......");
                        progressDialog.setProgressStyle(progressDialog.STYLE_SPINNER);
                        progressDialog.setMax(100);
                        progressDialog.show();
                        progressDialog.setCancelable(false);

                        new Thread(new Runnable() {
                            @Override
                            public void run() {

                                try {


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
                                                " " + "F/H: "+fecha+" "+""+hora+"\n"+
                                                " " + "Patente: "+patente+"\n"+
                                                " " + "HM: "+horasmaquina+"\n"+
                                                " " + "Combustible: "+combustible+"\n"+
                                                " " + "Operador: "+operador+"\n"+
                                                " " + "Usuario: "+username+"\n"+
                                                " " + "Planta: "+planta+"\n"+
                                                " " + "Observaciones: "+"\n"+
                                                " " +"\n"
                                                +observaciones.getText().toString()+"\n"+
                                                " " +"\n"+
                                                " " +"\n"+
                                                " " + "Jefe de planta" +"\n"+
                                                " " +"\n"+
                                                " " + "Nombre:........................" +"\n"+
                                                " " +"\n"+
                                                " " +"\n"+
                                                " " + "Firma:........................." +"\n"+
                                                " " +"\n"+
                                                " " +"\n"+
                                                " "+" "+" "+" "+" "+" "+" "+"Report Produccion"+" "+" "+" "+"\n"+
                                                " "+" "+" "+" "+" "+" "+" "+" "+"Copia Operador "+" "+" "+" "+"\n"+
                                                " " +"\n"+
                                                " " +"\n"+
                                                " " +"\n";
                                        os.write(msg.getBytes());
                                        Thread.sleep(6000);

                                        String msg2 = " "+" "+" "+" "+" "+" "+" "+"Aridos Santa Fe "+" "+" "+" "+"\n"+
                                            " " + " "+" "+""+" "+" " +" "+" "+" "+""+" "+"\n"+
                                            " " +"\n"+
                                            " " + "F/H: "+fecha+" "+""+hora+"\n"+
                                            " " + "Patente: "+patente+"\n"+
                                            " " + "HM: "+horasmaquina+"\n"+
                                            " " + "Combustible: "+combustible+"\n"+
                                            " " + "Operador: "+operador+"\n"+
                                            " " + "Usuario: "+username+"\n"+
                                            " " + "Planta: "+planta+"\n"+
                                            " " + "Observaciones: "+"\n"+
                                            " " +"\n"
                                            +observaciones.getText().toString()+"\n"+
                                            " " +"\n"+
                                            " " +"\n"+
                                            " " + "Operador" +"\n"+
                                            " " +"\n"+
                                            " " + "Nombre:........................" +"\n"+
                                            " " +"\n"+
                                            " " +"\n"+
                                            " " + "Firma:........................." +"\n"+
                                            " " +"\n"+
                                            " " +"\n"+ " "+" "+" "+" "+" "+" "+" "+"Report Produccion"+" "+" "+" "+"\n"+
                                            " "+" "+" "+" "+" "+" "+" "+"Copia Jefe planta "+" "+" "+" "+"\n"+
                                            " " +"\n"+
                                            " " +"\n"+
                                            " " +"\n";

                                    os.write(msg2.getBytes());
                                    mBluetoothSocket.close();
                                    Thread.sleep(3000);
                                    progressDialog.dismiss();

                                    Intent intent = new Intent(Report_produccionStep4.this, Menu.class);
                                    startActivity(intent);
                                    finish();


                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }


                            }
                        }).start();


                    }if (registro == false){
                        progressDialog.setTitle("Estamos trabajando");
                        progressDialog.setMessage("Espere un momento......");
                        progressDialog.setProgressStyle(progressDialog.STYLE_SPINNER);
                        progressDialog.setMax(100);
                        progressDialog.show();
                        progressDialog.setCancelable(false);

                        new Thread(new Runnable() {
                            @Override
                            public void run() {

                                try {
                                    Thread.sleep(4000);
                                    progressDialog.dismiss();
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(getApplicationContext(),"ERROR AL GUARDAR DATOS",Toast.LENGTH_LONG).show();
                                        }
                                    });
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }

                            }
                        }).start();



                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });




    }
}
