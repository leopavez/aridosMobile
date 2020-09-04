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
import android.support.annotation.ColorInt;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import cz.msebera.android.httpclient.Header;

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

    ProgressDialog progressDialog;


    ArrayList<Registro_acopio> listaregistrosacopio;
    ArrayList<Registro_salida> listaregistrosalida;
    ArrayList<Registro_produccion_patente> listaregistroproduccionpatente;
    ArrayList<Registro_produccion_planta> listaregistroproduccionplanta;
    Registro_acopio reg_acopio = new Registro_acopio();
    Registro_salida reg_salida = new Registro_salida();
    Registro_produccion_planta reg_prod = new Registro_produccion_planta();
    Registro_produccion_patente reg_prod_patente = new Registro_produccion_patente();
    public static final String APIEnvioRegistrosAcopio = "http://santafeinversiones.org/api/aridos/registros/entrada";
    public static final String APIEnvioRegistrosProduccionPlanta = "http://santafeinversiones.org/api/aridos/produccion/xplanta";
    public static final String APIEnvioRegistrosProduccionPatente = "http://santafeinversiones.org/api/aridos/produccion/xpatente";
    public static final String APIEnvioRegistrosSalida = "http://santafeinversiones.org/api/aridos/registros/salida";

    private RequestQueue mRequestQueue;
    private StringRequest mStringRequest;


    Menu menu = new Menu();


    DatabaseHelper myDB= new DatabaseHelper(this);

    EditText m3, chofername, procedencia;
    private AutoCompleteTextView pt;
    Button registro;
    Switch voucher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reportentrada);
        voucher = (Switch)findViewById(R.id.switchVoucher);
        m3 = (EditText) findViewById(R.id.txtm3entrada);
        chofername = (EditText)findViewById(R.id.txtnchofer);
        registro = (Button)findViewById(R.id.btnregistroentrada);
        pt = findViewById(R.id.patentesreporteentrada);
        procedencia = (EditText)findViewById(R.id.txtprocedencia);
        EstadoVoucherImp();

        //BLUETOOTH
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }



        progressDialog = new ProgressDialog(Report_entrada.this);
        registro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (pt.getText().toString().equals("") || m3.getText().toString().equals("")
                        || chofername.getText().toString().equals("") || procedencia.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(),"Debes llenar todos los campos",Toast.LENGTH_SHORT).show();
                }else{
                    SharedPreferences preferencesvoucher = getSharedPreferences("Voucher", Context.MODE_PRIVATE);
                    String estado = preferencesvoucher.getString("estado","false");

                    if (estado.toString().equals("false")){

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

                        Boolean registro = myDB.Registro_acopio(pt.getText().toString(),m3.getText().toString(), plantaname,chofername.getText().toString(),
                                username,fecha,hora,"PENDIENTE",procedencia.getText().toString());
                        if (registro == true){

                            progressDialog.setTitle("Report Entrada - Guardando");
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
                                                Intent intent = new Intent(Report_entrada.this, Menu.class);
                                                startActivity(intent);
                                                finish();
                                                Toast.makeText(getApplicationContext(),"DATOS GUARDADOS CORRECTAMENTE",Toast.LENGTH_LONG).show();
                                            }
                                        });
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }).start();
                        }
                        if (registro==false){
                            progressDialog.setTitle("Report Entrada - Cancelando");
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
                                                Intent intent = new Intent(Report_entrada.this, Menu.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                        });
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }

                                }
                            }).start();
                        }

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

                        Boolean registro =  myDB.Registro_acopio(pt.getText().toString(),m3.getText().toString(), plantaname,chofername.getText().toString(),
                                username,fecha,hora,"PENDIENTE",procedencia.getText().toString());

                        if (registro==true){

                            progressDialog.setTitle("Report Entrada - Imprimiendo");
                            progressDialog.setMessage("Espere un momento......");
                            progressDialog.setProgressStyle(progressDialog.STYLE_SPINNER);
                            progressDialog.setMax(100);
                            progressDialog.show();
                            progressDialog.setCancelable(false);

                            new Thread(new Runnable() {
                                @Override
                                public void run() {

                                    String patenteIMP = pt.getText().toString();
                                    String m3IMP = m3.getText().toString();
                                    String choferIMP = chofername.getText().toString();
                                    SharedPreferences preferences1 = getSharedPreferences("Cvoucher", Context.MODE_PRIVATE);
                                    String cantidad = preferences1.getString("cantidad","1");
                                    SQLiteDatabase db = myDB.getWritableDatabase();
                                    Cursor cursor = db.rawQuery("SELECT id FROM registros_acopio ORDER BY id DESC",null);
                                    if (cursor.moveToFirst()){
                                        String nvale = cursor.getString(0);
                                        ImprimirVoucher(cantidad,nvale,patenteIMP,m3IMP,choferIMP,procedencia.getText().toString());
                                    }
                                    try {
                                        Thread.sleep(10000);
                                        progressDialog.dismiss();
                                        Intent intent = new Intent(Report_entrada.this, Menu.class);
                                        startActivity(intent);
                                        finish();
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }).start();
                        }if (registro == false){
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
                                                Intent intent = new Intent(Report_entrada.this, Menu.class);
                                                startActivity(intent);
                                                finish();
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

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            //ENVIO ACOPIO
                            SQLiteDatabase db = myDB.getReadableDatabase();
                            String estado = "PENDIENTE";
                            listaregistrosacopio = new ArrayList<Registro_acopio>();
                            Cursor cursor = db.rawQuery("SELECT id, patente, m3, planta, chofer, fecha, hora, username,procedencia FROM registros_acopio WHERE estado='" + estado + "'ORDER BY id ASC", null);


                            while (cursor.moveToNext()) {
                                reg_acopio = new Registro_acopio();
                                reg_acopio.setId(cursor.getInt(0));
                                reg_acopio.setPatente(cursor.getString(1));
                                reg_acopio.setM3(cursor.getString(2));
                                reg_acopio.setPlanta(cursor.getString(3));
                                reg_acopio.setChofer(cursor.getString(4));
                                reg_acopio.setFecha(cursor.getString(5));
                                reg_acopio.setHora(cursor.getString(6));
                                reg_acopio.setUsername(cursor.getString(7));
                                reg_acopio.setProcedencia(cursor.getString(8));

                                listaregistrosacopio.add(reg_acopio);

                            }

                            Log.i("LISTA",""+listaregistrosacopio.size());
                            for (int i = 0; i < listaregistrosacopio.size(); i++) {
                                final int id = listaregistrosacopio.get(i).getId();
                                final String patente = listaregistrosacopio.get(i).getPatente().toString();
                                final String m3 = listaregistrosacopio.get(i).getM3().toString();
                                final String planta = listaregistrosacopio.get(i).getPlanta().toString();
                                final String chofer = listaregistrosacopio.get(i).getChofer().toString();
                                final String fecha = listaregistrosacopio.get(i).getFecha().toString();
                                final String hora = listaregistrosacopio.get(i).getHora().toString();
                                final String username = listaregistrosacopio.get(i).getUsername().toString();
                                final String procedencia = listaregistrosacopio.get(i).getProcedencia().toString();

                                listaregistrosacopio.remove(i);

                                final RequestParams params = new RequestParams();
                                String nvale = String.valueOf(id);
                                params.put("nrovale", nvale);
                                params.put("patente", patente);
                                params.put("m3", m3);
                                params.put("fecha", fecha);
                                params.put("hora", hora);
                                params.put("planta", planta);
                                params.put("chofer", chofer);
                                params.put("user", username);
                                params.put("procedencia", procedencia);

                                Handler handler = new Handler(Looper.getMainLooper());
                                Runnable runnable = new Runnable() {
                                    @Override
                                    public void run() {
                                        AsyncHttpClient client = new AsyncHttpClient();
                                        client.setMaxRetriesAndTimeout(0,1500);
                                        client.post(APIEnvioRegistrosAcopio, params, new AsyncHttpResponseHandler() {
                                            @Override
                                            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                                if (statusCode== 200){
                                                    String response = new String(responseBody);
                                                    Log.i("REQUEST", "" + response.toString());
                                                    SQLiteDatabase db = myDB.getReadableDatabase();
                                                    db.execSQL("UPDATE registros_acopio SET estado='ENVIADO' WHERE id='" + id + "'");
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

                            //ENVIO PRODUCCION X PATENTE

                            listaregistroproduccionpatente = new ArrayList<Registro_produccion_patente>();
                            Cursor cursor2 = db.rawQuery("SELECT id, patente, horasmaquina, nam, nah, nat, se, otras, botiquin, extintor, ar, baliza, rt, so, pc, fecha, hora, username, planta, combustible," +
                                    "operador FROM registros_produccion WHERE estado='" + estado + "'", null);
                            while (cursor2.moveToNext()) {
                                reg_prod_patente = new Registro_produccion_patente();
                                reg_prod_patente.setId(cursor2.getInt(0));
                                reg_prod_patente.setPatente(cursor2.getString(1));
                                reg_prod_patente.setHorasmaquina(cursor2.getString(2));
                                reg_prod_patente.setNam(cursor2.getString(3));
                                reg_prod_patente.setNah(cursor2.getString(4));
                                reg_prod_patente.setNat(cursor2.getString(5));
                                reg_prod_patente.setSe(cursor2.getString(6));
                                reg_prod_patente.setOtras(cursor2.getString(7));
                                reg_prod_patente.setBotiquin(cursor2.getString(8));
                                reg_prod_patente.setExtintor(cursor2.getString(9));
                                reg_prod_patente.setAr(cursor2.getString(10));
                                reg_prod_patente.setBaliza(cursor2.getString(11));
                                reg_prod_patente.setRt(cursor2.getString(12));
                                reg_prod_patente.setSo(cursor2.getString(13));
                                reg_prod_patente.setPc(cursor2.getString(14));
                                reg_prod_patente.setHora(cursor2.getString(15));
                                reg_prod_patente.setFecha(cursor2.getString(16));
                                reg_prod_patente.setUsername(cursor2.getString(17));
                                reg_prod_patente.setPlanta(cursor2.getString(18));
                                reg_prod_patente.setCombustible(cursor2.getString(19));
                                reg_prod_patente.setOperador(cursor2.getString(20));


                                listaregistroproduccionpatente.add(reg_prod_patente);

                            }
                            for (int i = 0; i < listaregistroproduccionpatente.size(); i++) {


                                final int id = listaregistroproduccionpatente.get(i).getId();
                                final String patente = listaregistroproduccionpatente.get(i).getPatente().toString();
                                final String horasmaquina = listaregistroproduccionpatente.get(i).getHorasmaquina().toString();
                                final String nam = listaregistroproduccionpatente.get(i).getNam().toString();
                                final String nah = listaregistroproduccionpatente.get(i).getNah().toString();
                                final String nat = listaregistroproduccionpatente.get(i).getNat().toString();
                                final String se = listaregistroproduccionpatente.get(i).getSe().toString();
                                final String otras = listaregistroproduccionpatente.get(i).getOtras().toString();
                                final String botiquin = listaregistroproduccionpatente.get(i).getBotiquin().toString();
                                final String extintor = listaregistroproduccionpatente.get(i).getExtintor().toString();
                                final String ar = listaregistroproduccionpatente.get(i).getAr().toString();
                                final String baliza = listaregistroproduccionpatente.get(i).getBaliza().toString();
                                final String rt = listaregistroproduccionpatente.get(i).getRt().toString();
                                final String so = listaregistroproduccionpatente.get(i).getSo().toString();
                                final String pc = listaregistroproduccionpatente.get(i).getPc().toString();
                                final String hora = listaregistroproduccionpatente.get(i).getHora().toString();
                                final String fecha = listaregistroproduccionpatente.get(i).getFecha().toString();
                                final String username = listaregistroproduccionpatente.get(i).getUsername().toString();
                                final String planta = listaregistroproduccionpatente.get(i).getPlanta().toString();
                                final String combustible = listaregistroproduccionpatente.get(i).getCombustible().toString();
                                final String operador = listaregistroproduccionpatente.get(i).getOperador().toString();

                                listaregistroproduccionpatente.remove(i);

                                final RequestParams params = new RequestParams();
                                String idprod = String.valueOf(id);
                                params.put("idprodpatente", idprod);
                                params.put("patente", patente);
                                params.put("horasmaquina", horasmaquina);
                                params.put("nam", nam);
                                params.put("nah", nah);
                                params.put("nat", nat);
                                params.put("se", se);
                                params.put("otras", otras);
                                params.put("botiquin", botiquin);
                                params.put("extintor", extintor);
                                params.put("ar", ar);
                                params.put("baliza", baliza);
                                params.put("rt", rt);
                                params.put("so", so);
                                params.put("pc", pc);
                                params.put("hora", hora);
                                params.put("fecha", fecha);
                                params.put("username", username);
                                params.put("planta", planta);
                                params.put("combustible", combustible);
                                params.put("operador", operador);

                                Handler handler = new Handler(Looper.getMainLooper());
                                Runnable runnable = new Runnable() {
                                    @Override
                                    public void run() {
                                        AsyncHttpClient client = new AsyncHttpClient();
                                        client.setMaxRetriesAndTimeout(0,1500);
                                        client.post(APIEnvioRegistrosProduccionPatente, params, new AsyncHttpResponseHandler() {
                                            @Override
                                            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                                if (statusCode == 200){
                                                    String response = new String(responseBody).toUpperCase();
                                                    Log.i("TAG", "ENVIO OK" + response);
                                                    SQLiteDatabase db = myDB.getReadableDatabase();
                                                    db.execSQL("UPDATE registros_produccion SET estado='ENVIADO' WHERE id='" + id + "'");
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

                            //ENVIO PRODUCCION PLANTA
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
                                final String planta = listaregistroproduccionplanta.get(i).getPlanta().toString();
                                final String username = listaregistroproduccionplanta.get(i).getUsername().toString();
                                final String fecha = listaregistroproduccionplanta.get(i).getFecha().toString();
                                final String hora = listaregistroproduccionplanta.get(i).getHora().toString();
                                final String procedencia = listaregistroproduccionplanta.get(i).getProcedencia().toString();


                                listaregistroproduccionplanta.remove(i);

                                final RequestParams params = new RequestParams();
                                String idprod = String.valueOf(id);
                                params.put("idprodplanta", idprod);
                                params.put("tipomaterial", tipomaterial);
                                params.put("m3", m3);
                                params.put("fecha", fecha);
                                params.put("hora", hora);
                                params.put("planta", planta);
                                params.put("username", username);
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

                            //ENVIO SALIDA
                            listaregistrosalida = new ArrayList<Registro_salida>();
                            Cursor cursor4 = db.rawQuery("SELECT id, patente, m3, planta, chofer, fecha, hora, username,procedencia,tipomaterial,destino FROM registros_salida WHERE estado='" + estado + "'", null);


                            while (cursor4.moveToNext()) {
                                reg_salida = new Registro_salida();
                                reg_salida.setId(cursor4.getInt(0));
                                reg_salida.setPatente(cursor4.getString(1));
                                reg_salida.setM3(cursor4.getString(2));
                                reg_salida.setPlanta(cursor4.getString(3));
                                reg_salida.setChofer(cursor4.getString(4));
                                reg_salida.setFecha(cursor4.getString(5));
                                reg_salida.setHora(cursor4.getString(6));
                                reg_salida.setUsername(cursor4.getString(7));
                                reg_salida.setProcedencia(cursor4.getString(8));
                                reg_salida.setTipomaterial(cursor4.getString(9));
                                reg_salida.setDestino(cursor4.getString(10));

                                listaregistrosalida.add(reg_salida);

                            }

                            for (int i = 0; i < listaregistrosalida.size(); i++) {


                                final int id = listaregistrosalida.get(i).getId();
                                final String patente = listaregistrosalida.get(i).getPatente().toString();
                                final String m3 = listaregistrosalida.get(i).getM3().toString();
                                final String planta = listaregistrosalida.get(i).getPlanta().toString();
                                final String chofer = listaregistrosalida.get(i).getChofer().toString();
                                final String fecha = listaregistrosalida.get(i).getFecha().toString();
                                final String hora = listaregistrosalida.get(i).getHora().toString();
                                final String username = listaregistrosalida.get(i).getUsername().toString();
                                final String procedencia = listaregistrosalida.get(i).getProcedencia().toString();
                                final String tipomaterial = listaregistrosalida.get(i).getTipomaterial().toString();
                                final String destino = listaregistrosalida.get(i).getDestino().toString();

                                listaregistrosalida.remove(i);

                                final RequestParams params = new RequestParams();
                                String nvale = String.valueOf(id);
                                params.put("nrovale", nvale);
                                params.put("patente", patente);
                                params.put("m3", m3);
                                params.put("fecha", fecha);
                                params.put("hora", hora);
                                params.put("planta", planta);
                                params.put("chofer", chofer);
                                params.put("user", username);
                                params.put("procedencia", procedencia);
                                params.put("tipomaterial", tipomaterial);
                                params.put("destino", destino);



                                Handler handler = new Handler(Looper.getMainLooper());
                                Runnable runnable = new Runnable() {
                                    @Override
                                    public void run() {
                                        AsyncHttpClient client = new AsyncHttpClient();
                                        client.setMaxRetriesAndTimeout(0,1500);
                                        client.post(APIEnvioRegistrosSalida, params, new AsyncHttpResponseHandler() {
                                            @Override
                                            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                                if (statusCode==200){
                                                    String response = new String(responseBody).toUpperCase();
                                                    Log.i("TAG", "ENVIO OK" + response);
                                                    SQLiteDatabase db = myDB.getReadableDatabase();
                                                    db.execSQL("UPDATE registros_salida SET estado='ENVIADO' WHERE id='" + id + "'");
                                                }
                                            }

                                            @Override
                                            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                                try{
                                                    String response = new String(responseBody).toUpperCase();
                                                    Log.i("REQUEST FAIL",""+error.toString()+" AA "+response);
                                                }catch (Exception e){
                                                    e.printStackTrace();
                                                }
                                            }
                                        });
                                    }
                                };
                                handler.post(runnable);
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }).start();

                SQLiteDatabase db = myDB.getReadableDatabase();

                String estado = "PENDIENTE";

                Cursor acopio = db.rawQuery("SELECT id, patente, m3, planta, chofer, fecha, hora, username,procedencia FROM registros_acopio WHERE estado='" + estado + "'", null);
                Cursor produccion_patente = db.rawQuery("SELECT id, patente, horasmaquina, nam, nah, nat, se, otras, botiquin, extintor, ar, baliza, rt, so, pc, fecha, hora, username, planta, combustible," +
                        "operador FROM registros_produccion WHERE estado='" + estado + "'", null);
                Cursor produccion_planta = db.rawQuery("SELECT id, tipomaterial, m3, procedencia, planta, username, fecha, hora FROM prod_planta WHERE estado='" + estado + "'", null);
                Cursor salida = db.rawQuery("SELECT id, patente, m3, planta, chofer, fecha, hora, username,procedencia,tipomaterial,destino FROM registros_salida WHERE estado='" + estado + "'", null);

                SharedPreferences preferences = getSharedPreferences("maxacumulados", Context.MODE_PRIVATE);
                String maxdatosacumulados = preferences.getString("max", "SIN MAXIMO");

                int countregistroacopio = acopio.getCount();
                int countregistroproduccionpatente = produccion_patente.getCount();
                int countregistroproduccionplanta = produccion_planta.getCount();
                int countregistrosalida = salida.getCount();

                final int datos = (countregistroacopio+countregistroproduccionpatente+countregistroproduccionplanta+countregistrosalida);

                if (maxdatosacumulados.equals("SIN MAXIMO")){
                    if(datos>15){
                        DynamicToast.makeWarning(getApplicationContext(), "SE ALCANZO EL NUMERO MAXIMO DE DATOS ACUMULADOS, POR FAVOR SINCRONIZA LOS DATOS LO ANTES POSIBLE").show();
                    }
                }else if(datos> Integer.parseInt(maxdatosacumulados)) {
                    DynamicToast.makeWarning(getApplicationContext(), "SE ALCANZO EL NUMERO MAXIMO DE DATOS ACUMULADOS, POR FAVOR SINCRONIZA LOS DATOS LO ANTES POSIBLE").show();
                }
            }
        });
        LoadDataPatentes();
        voucher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (voucher.isChecked()){
                    SwitchEstado("true");
                    Toast.makeText(getApplicationContext(),"La impresión de Voucher se encuentra habilitada",Toast.LENGTH_SHORT).show();
                }else{
                    SwitchEstado("false");
                    Toast.makeText(getApplicationContext(),"La impresión de Voucher fue deshabilitada",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void SwitchEstado(String value){
        SharedPreferences preferences = getSharedPreferences("Voucher", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("estado",value);
        editor.commit();

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

    private void EstadoVoucherImp(){
        SharedPreferences preferences = getSharedPreferences("Voucher", Context.MODE_PRIVATE);
        String estado = preferences.getString("estado","false");

        if (estado.toString().equals("true")){
            voucher.setChecked(true);

        }else if (estado.toString().equals("false")){
            voucher.setChecked(false);
        }else{
            voucher.setChecked(false);
        }

    }

    private void ImprimirVoucher(String cantidad, String nvale,String patente, String m3imp, String chofer,String procedencia){
        try {
            int nvoucher = Integer.parseInt(cantidad);
            SharedPreferences preferences = getSharedPreferences("printer", Context.MODE_PRIVATE);
            String mask = preferences.getString("mask", "");
            SharedPreferences preferences1 = getSharedPreferences("plantaApp", Context.MODE_PRIVATE);
            String plantaname = preferences1.getString("NAME_PLANTA", "");

            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

            if ((mBluetoothAdapter == null) || (!mBluetoothAdapter.isEnabled())) {
                throw new Exception("Bluetooth adapter no esta funcionando o no esta habilitado");
            }

            mBluetoothDevice = mBluetoothAdapter.getRemoteDevice(mask);
            mBluetoothSocket = mBluetoothDevice.createRfcommSocketToServiceRecord(applicationUUID);
            mBluetoothAdapter.cancelDiscovery();
            mBluetoothSocket.connect();

            Log.i("TAG","METODO DE IMPRIMIR");

            OutputStream os = mBluetoothSocket.getOutputStream();
            InputStream is = mBluetoothSocket.getInputStream();

            Handler mHandler =  new Handler(Looper.getMainLooper());

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat horaformat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
            Date date = new Date();
            String fecha = dateFormat.format(date);
            String hora = horaformat.format(date);

            for (int i = 0; i <= nvoucher-1; i++) {

                String msg = " "+" "+" "+ " "+" "+" "+" "+" "+" "+" Aridos Santa Fe "+" "+"\n"+
                        " " + " "+" "+""+" "+" " +" "+" "+" "+""+" "+"\n"+
                        " " +"\n"+
                        " " + "N vale: "+nvale+"\n"+
                        " " + "Patente: "+patente+"\n"+
                        " " + "Cantidad M3: "+m3imp+"\n"+
                        " " + "Procedencia: "+procedencia+"\n"+
                        " " + "Chofer: "+chofer+"\n"+
                        " " + "Fecha: "+fecha+"\n"+
                        " " + "Hora: "+hora+"\n"+
                        " " + "Planta: "+plantaname+"\n"+
                        " " +"\n"+
                        " " +"\n"+
                        " " + "KM:............................" +"\n"+
                        " " +"\n"+
                        " " +"\n"+
                        " " + "Nombre:........................" +"\n"+
                        " " +"\n"+
                        " " +"\n"+
                        " " + "Firma:........................." +"\n"+
                        " " +"\n"+
                        " " + " "+" "+" Acopio - Report entrada "+" "+" "+"\n"+
                        " " +"\n"+
                        " " +"\n"+
                        " " +"\n";
                os.write(msg.getBytes());
            }
            mBluetoothSocket.close();

       }catch (Exception e){

        }


    }

}
