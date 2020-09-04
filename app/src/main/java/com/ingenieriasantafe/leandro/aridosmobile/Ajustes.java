package com.ingenieriasantafe.leandro.aridosmobile;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.BoringLayout;
import android.util.Log;
import android.view.View;
import android.view.textclassifier.TextLinks;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import cz.msebera.android.httpclient.Header;

public class Ajustes extends AppCompatActivity implements View.OnClickListener{

    private CardView ajustesgeneralesCard,actualizarpatentes,ajustesParametros, forzarEnvio;
    private TextView txtinfodatos;

    private RequestQueue mRequestQueue;
    private StringRequest mStringRequest;



    ArrayList<Registro_acopio> listaregistrosacopio;
    ArrayList<Registro_salida> listaregistrosalida;
    ArrayList<Registro_produccion_patente> listaregistroproduccionpatente;
    ArrayList<Registro_produccion_planta>listaregistroproduccionplanta;
    Registro_acopio reg_acopio = new Registro_acopio();
    Registro_salida reg_salida = new Registro_salida();
    Registro_produccion_planta reg_prod = new Registro_produccion_planta();
    Registro_produccion_patente reg_prod_patente = new Registro_produccion_patente();

    DatabaseHelper myDB;
    private static final String TAG = Ajustes.class.getName();
    public static final String APIVEHICULOS = "http://santafeinversiones.org/api/vehiculos";
    public static final String APIPLANTAS = "http://santafeinversiones.org/api/plantas";
    public static final String APIOPERADORES = "http://santafeinversiones.org/api/aridos/operadores";
    public static final String APIUnegociosAridos = "http://santafeinversiones.org/api/aridos/all/unegocios";


    public static final String APIEnvioRegistrosAcopio = "http://santafeinversiones.org/api/aridos/registros/entrada";
    public static final String APIEnvioRegistrosProduccionPlanta = "http://santafeinversiones.org/api/aridos/produccion/xplanta";
    public static final String APIEnvioRegistrosProduccionPatente = "http://santafeinversiones.org/api/aridos/produccion/xpatente";
    public static final String APIEnvioRegistrosSalida = "http://santafeinversiones.org/api/aridos/registros/salida";

    ArrayList<String> plantas;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ajustes);
        txtinfodatos = (TextView)findViewById(R.id.txtinfodatos);
        ajustesParametros = (CardView)findViewById(R.id.ajustesparametrosCard);
        ajustesParametros.setOnClickListener(this);
        ajustesgeneralesCard = (CardView)findViewById(R.id.ajustesgeneralesCard);
        ajustesgeneralesCard.setOnClickListener(this);
        actualizarpatentes = (CardView) findViewById(R.id.actualizacionpatentesCard);
        forzarEnvio = (CardView)findViewById(R.id.forzarenvio);
        progressDialog = new ProgressDialog(Ajustes.this);
        actualizarpatentes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressDialog.setTitle("Actualizando Aplicacion");
                progressDialog.setMessage("Descargando datos......");
                progressDialog.setProgressStyle(progressDialog.STYLE_SPINNER);
                progressDialog.setMax(100);
                progressDialog.show();
                progressDialog.setCancelable(false);

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        try{
                            for (int i=0; i<=10; i++){

                                if (i == 1){
                                    DescargaPlantas();
                                }else if(i == 5){
                                    DescargadePatentes();
                                }else if(i==10){
                                    DescargaOperadores();
                                }else if(i==15){
                                    DescargaUnegocios();
                                }
                            }
                            Thread.sleep(10000);

                            progressDialog.dismiss();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(),"Los datos fueron actualizados",Toast.LENGTH_SHORT).show();
                                }
                            });

                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                    }).start();
            }
        });
        plantas = new ArrayList<>();
        myDB = new DatabaseHelper(this);

        SQLiteDatabase db = myDB.getReadableDatabase();
        String estado = "PENDIENTE";


        Cursor acopio = db.rawQuery("SELECT id, patente, m3, planta, chofer, fecha, hora, username,procedencia FROM registros_acopio WHERE estado='" + estado + "'", null);

        Cursor produccion_patente = db.rawQuery("SELECT id, patente, horasmaquina, nam, nah, nat, se, otras, botiquin, extintor, ar, baliza, rt, so, pc, fecha, hora, username, planta, combustible," +
                "operador FROM registros_produccion WHERE estado='" + estado + "'", null);

        Cursor produccion_planta = db.rawQuery("SELECT id, tipomaterial, m3, procedencia, planta, username, fecha, hora FROM prod_planta WHERE estado='" + estado + "'", null);

        Cursor salida = db.rawQuery("SELECT id, patente, m3, planta, chofer, fecha, hora, username,procedencia,tipomaterial,destino FROM registros_salida WHERE estado='" + estado + "'", null);


        txtinfodatos.setText("Acopio: "+acopio.getCount()+ "\n"+
                "Salida: "+salida.getCount()+"\n"+
                "Produccion patente: "+produccion_patente.getCount()+"\n"+
                "Produccion planta: "+produccion_planta.getCount());



        forzarEnvio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressDialog.setTitle("Limpiando la aplicacion");
                progressDialog.setMessage("Sincronizando los datos acumulados");
                progressDialog.setProgressStyle(progressDialog.STYLE_SPINNER);
                progressDialog.setMax(100);
                progressDialog.show();
                progressDialog.setCancelable(false);

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        try{
                            SQLiteDatabase db = myDB.getReadableDatabase();
                            String estado = "PENDIENTE";

                            //ENVIO DATOS DE ACOPIO
                            listaregistrosacopio = new ArrayList<Registro_acopio>();
                            Cursor cursor = db.rawQuery("SELECT id, patente, m3, planta, chofer, fecha, hora, username,procedencia FROM registros_acopio WHERE estado='" + estado + "'", null);

                            listaregistroproduccionpatente = new ArrayList<Registro_produccion_patente>();
                            Cursor cursor1 = db.rawQuery("SELECT id, patente, horasmaquina, nam, nah, nat, se, otras, botiquin, extintor, ar, baliza, rt, so, pc, fecha, hora, username, planta, combustible," +
                                    "operador FROM registros_produccion WHERE estado='" + estado + "'", null);

                            listaregistroproduccionplanta = new ArrayList<Registro_produccion_planta>();
                            Cursor cursor2 = db.rawQuery("SELECT id, tipomaterial, m3, procedencia, planta, username, fecha, hora FROM prod_planta WHERE estado='" + estado + "'", null);

                            listaregistrosalida = new ArrayList<Registro_salida>();
                            Cursor cursor3 = db.rawQuery("SELECT id, patente, m3, planta, chofer, fecha, hora, username,procedencia,tipomaterial,destino FROM registros_salida WHERE estado='" + estado + "'", null);

                            int countregistroacopio = cursor.getCount();
                            int countregistroproduccionpatente = cursor1.getCount();
                            int countregistroproduccionplanta = cursor2.getCount();
                            int countregistrosalida = cursor3.getCount();

                            final int datos = (countregistroacopio+countregistroproduccionpatente+countregistroproduccionplanta+countregistrosalida);

                            if(datos == 0){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(),"No se encontraron datos para enviar",Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                    }
                                });
                            }else{
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        progressDialog.setMessage("Se han encontrado: "+datos+" datos");
                                    }
                                });

                                //ENVIO ACOPIO
                                reg_acopio = new Registro_acopio();

                                for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()){
                                    reg_acopio.setId(cursor.getInt(0));
                                    reg_acopio.setPatente(cursor.getString(1));
                                    reg_acopio.setM3(cursor.getString(2));
                                    reg_acopio.setPlanta(cursor.getString(3));
                                    reg_acopio.setChofer(cursor.getString(4));
                                    reg_acopio.setFecha(cursor.getString(5));
                                    reg_acopio.setHora(cursor.getString(6));
                                    reg_acopio.setUsername(cursor.getString(7));
                                    reg_acopio.setProcedencia(cursor.getString(8));


                                    final int id = reg_acopio.getId();
                                    final String patente = reg_acopio.getPatente().toString();
                                    final String m3 = reg_acopio.getM3().toString();
                                    final String planta = reg_acopio.getPlanta().toString();
                                    final String chofer = reg_acopio.getChofer().toString();
                                    final String fecha = reg_acopio.getFecha().toString();
                                    final String hora = reg_acopio.getHora().toString();
                                    final String username = reg_acopio.getUsername().toString();
                                    final String procedencia = reg_acopio.getProcedencia().toString();


                                    mRequestQueue = Volley.newRequestQueue(getApplicationContext());
                                    mStringRequest = new StringRequest(Request.Method.POST, APIEnvioRegistrosAcopio, new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            String responseapi = new String(response);
                                            Log.i("REQUEST", "" + responseapi.toString());
                                            SQLiteDatabase db = myDB.getReadableDatabase();
                                            db.execSQL("UPDATE registros_acopio SET estado='ENVIADO' WHERE id='" + id + "'");
                                        }
                                    }, new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            Log.i(TAG,"Error de conexion:" +error.toString());
                                        }
                                    }){
                                        @Override
                                        protected Map<String, String> getParams() throws AuthFailureError {
                                            Map<String,String>params = new HashMap<String,String>();
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
                                            return params;
                                        }
                                    };
                                    mRequestQueue.add(mStringRequest);


                                }


                                //ENVIO DATOS PRODUCCIÓN PATENTE

                                reg_prod_patente = new Registro_produccion_patente();

                                for (cursor1.moveToFirst(); !cursor1.isAfterLast(); cursor1.moveToNext()){
                                    reg_prod_patente.setId(cursor1.getInt(0));
                                    reg_prod_patente.setPatente(cursor1.getString(1));
                                    reg_prod_patente.setHorasmaquina(cursor1.getString(2));
                                    reg_prod_patente.setNam(cursor1.getString(3));
                                    reg_prod_patente.setNah(cursor1.getString(4));
                                    reg_prod_patente.setNat(cursor1.getString(5));
                                    reg_prod_patente.setSe(cursor1.getString(6));
                                    reg_prod_patente.setOtras(cursor1.getString(7));
                                    reg_prod_patente.setBotiquin(cursor1.getString(8));
                                    reg_prod_patente.setExtintor(cursor1.getString(9));
                                    reg_prod_patente.setAr(cursor1.getString(10));
                                    reg_prod_patente.setBaliza(cursor1.getString(11));
                                    reg_prod_patente.setRt(cursor1.getString(12));
                                    reg_prod_patente.setSo(cursor1.getString(13));
                                    reg_prod_patente.setPc(cursor1.getString(14));
                                    reg_prod_patente.setHora(cursor1.getString(15));
                                    reg_prod_patente.setFecha(cursor1.getString(16));
                                    reg_prod_patente.setUsername(cursor1.getString(17));
                                    reg_prod_patente.setPlanta(cursor1.getString(18));
                                    reg_prod_patente.setCombustible(cursor1.getString(19));
                                    reg_prod_patente.setOperador(cursor1.getString(20));

                                    final int id = reg_prod_patente.getId();
                                    final String patente = reg_prod_patente.getPatente().toString();
                                    final String horasmaquina = reg_prod_patente.getHorasmaquina().toString();
                                    final String nam = reg_prod_patente.getNam().toString();
                                    final String nah = reg_prod_patente.getNah().toString();
                                    final String nat = reg_prod_patente.getNat().toString();
                                    final String se = reg_prod_patente.getSe().toString();
                                    final String otras = reg_prod_patente.getOtras().toString();
                                    final String botiquin = reg_prod_patente.getBotiquin().toString();
                                    final String extintor = reg_prod_patente.getExtintor().toString();
                                    final String ar = reg_prod_patente.getAr().toString();
                                    final String baliza = reg_prod_patente.getBaliza().toString();
                                    final String rt = reg_prod_patente.getRt().toString();
                                    final String so = reg_prod_patente.getSo().toString();
                                    final String pc = reg_prod_patente.getPc().toString();
                                    final String hora = reg_prod_patente.getHora().toString();
                                    final String fecha = reg_prod_patente.getFecha().toString();
                                    final String username = reg_prod_patente.getUsername().toString();
                                    final String planta = reg_prod_patente.getPlanta().toString();
                                    final String combustible = reg_prod_patente.getCombustible().toString();
                                    final String operador = reg_prod_patente.getOperador().toString();




                                    mRequestQueue = Volley.newRequestQueue(getApplicationContext());
                                    mStringRequest = new StringRequest(Request.Method.POST, APIEnvioRegistrosProduccionPatente, new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            String responseapi = new String(response);
                                            Log.i("REQUEST", "" + responseapi.toString());
                                            SQLiteDatabase db = myDB.getReadableDatabase();
                                            db.execSQL("UPDATE registros_produccion SET estado='ENVIADO' WHERE id='" + id + "'");
                                        }
                                    }, new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            Log.i(TAG,"Error de conexion:" +error.toString());
                                        }
                                    }){
                                        @Override
                                        protected Map<String, String> getParams() throws AuthFailureError {
                                            Map<String,String>params = new HashMap<String,String>();
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
                                            return params;
                                        }
                                    };
                                    mRequestQueue.add(mStringRequest);


                                }

                                //ENVIO DATOS PRODUCCION PLANTA

                                reg_prod = new Registro_produccion_planta();

                                for (cursor2.moveToFirst(); !cursor2.isAfterLast(); cursor2.moveToNext()){
                                    reg_prod.setId(cursor2.getInt(0));
                                    reg_prod.setTipomaterial(cursor2.getString(1));
                                    reg_prod.setM3(cursor2.getString(2));
                                    reg_prod.setProcedencia(cursor2.getString(3));
                                    reg_prod.setPlanta(cursor2.getString(4));
                                    reg_prod.setUsername(cursor2.getString(5));
                                    reg_prod.setFecha(cursor2.getString(6));
                                    reg_prod.setHora(cursor2.getString(7));

                                    final int id = reg_prod.getId();
                                    final String tipomaterial = reg_prod.getTipomaterial().toString();
                                    final String m3 = reg_prod.getM3().toString();
                                    final String planta = reg_prod.getPlanta().toString();
                                    final String username = reg_prod.getUsername().toString();
                                    final String fecha = reg_prod.getFecha().toString();
                                    final String hora = reg_prod.getHora().toString();
                                    final String procedencia = reg_prod.getProcedencia().toString();


                                    mRequestQueue = Volley.newRequestQueue(getApplicationContext());
                                    mStringRequest = new StringRequest(Request.Method.POST, APIEnvioRegistrosProduccionPlanta, new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            String responseapi = new String(response);
                                            Log.i("REQUEST", "" + responseapi.toString());
                                            SQLiteDatabase db = myDB.getReadableDatabase();
                                            db.execSQL("UPDATE prod_planta SET estado='ENVIADO' WHERE id='" + id + "'");
                                        }
                                    }, new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            Log.i(TAG,"Error de conexion:" +error.toString());
                                        }
                                    }){
                                        @Override
                                        protected Map<String, String> getParams() throws AuthFailureError {
                                            Map<String,String>params = new HashMap<String,String>();
                                            String idprod = String.valueOf(id);
                                            params.put("idprodplanta", idprod);
                                            params.put("tipomaterial", tipomaterial);
                                            params.put("m3", m3);
                                            params.put("fecha", fecha);
                                            params.put("hora", hora);
                                            params.put("planta", planta);
                                            params.put("username", username);
                                            params.put("procedencia", procedencia);
                                            return params;
                                        }
                                    };
                                    mRequestQueue.add(mStringRequest);

                                }

                                //ENVIO DATOS SALIDA

                                reg_salida = new Registro_salida();
                                for (cursor3.moveToNext(); !cursor3.isAfterLast(); cursor3.moveToNext()){

                                    reg_salida.setId(cursor3.getInt(0));
                                    reg_salida.setPatente(cursor3.getString(1));
                                    reg_salida.setM3(cursor3.getString(2));
                                    reg_salida.setPlanta(cursor3.getString(3));
                                    reg_salida.setChofer(cursor3.getString(4));
                                    reg_salida.setFecha(cursor3.getString(5));
                                    reg_salida.setHora(cursor3.getString(6));
                                    reg_salida.setUsername(cursor3.getString(7));
                                    reg_salida.setProcedencia(cursor3.getString(8));
                                    reg_salida.setTipomaterial(cursor3.getString(9));
                                    reg_salida.setDestino(cursor3.getString(10));

                                    final int id = reg_salida.getId();
                                    final String patente = reg_salida.getPatente().toString();
                                    final String m3 = reg_salida.getM3().toString();
                                    final String planta = reg_salida.getPlanta().toString();
                                    final String chofer = reg_salida.getChofer().toString();
                                    final String fecha = reg_salida.getFecha().toString();
                                    final String hora = reg_salida.getHora().toString();
                                    final String username = reg_salida.getUsername().toString();
                                    final String procedencia = reg_salida.getProcedencia().toString();
                                    final String tipomaterial = reg_salida.getTipomaterial().toString();
                                    final String destino = reg_salida.getDestino().toString();

                                    mRequestQueue = Volley.newRequestQueue(getApplicationContext());
                                    mStringRequest = new StringRequest(Request.Method.POST, APIEnvioRegistrosSalida, new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            String responseapi = new String(response);
                                            Log.i("REQUEST", "" + responseapi.toString());
                                            SQLiteDatabase db = myDB.getReadableDatabase();
                                            db.execSQL("UPDATE registros_salida SET estado='ENVIADO' WHERE id='" + id + "'");
                                        }
                                    }, new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            Log.i(TAG,"Error de conexion:" +error.toString());
                                        }
                                    }){
                                        @Override
                                        protected Map<String, String> getParams() throws AuthFailureError {
                                            Map<String,String>params = new HashMap<String,String>();
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
                                            return params;
                                        }
                                    };
                                    mRequestQueue.add(mStringRequest);
                                }
                                Thread.sleep(5000);
                                progressDialog.dismiss();

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(),"Proceso terminado",Toast.LENGTH_SHORT).show();
                                    }
                                });
                                Intent refresh = new Intent(Ajustes.this,Ajustes.class);
                                startActivity(refresh);
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    }
                }).start();
            }
        });

    }


    public void onClick(View v){
        Intent i;
        Intent intent = getIntent();
        switch (v.getId()){
            case R.id.ajustesgeneralesCard : i = new Intent(this,Ajustesgenerales.class);startActivity(i);overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);break;
            case R.id.ajustesparametrosCard : i = new Intent(this,AjustesParametros.class);startActivity(i);overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);break;
            default:break;
        }
    }

    private void DescargadePatentes(){
        mRequestQueue = Volley.newRequestQueue(this);
        mStringRequest = new StringRequest(Request.Method.GET, APIVEHICULOS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    String json;
                    json = response.toString();
                    JSONArray jsonArray = null;
                    jsonArray = new JSONArray(json);
                    Vehiculos vehiculos = new Vehiculos();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        vehiculos.id = jsonObject.getString("id");
                        vehiculos.patente = jsonObject.getString("patente");
                        vehiculos.marca = jsonObject.getString("marca");
                        vehiculos.propietario = jsonObject.getString("propietario");
                        vehiculos.tipo = jsonObject.getString("tipo");
                        vehiculos.m3 = jsonObject.getString("m3");

                        SQLiteDatabase db = myDB.getWritableDatabase();

                        if (myDB.ExisteVehiculo(vehiculos.id ) == false){
                            //LA PATENTE NO SE ENCUENTRA REGISTRADA
                            myDB.RegistroVehiculos(vehiculos.id,vehiculos.patente,vehiculos.tipo,vehiculos.marca,
                                    vehiculos.propietario,vehiculos.m3);

                        }else{
                            Cursor cursor = db.rawQuery("SELECT id, patente, tipo, marca, propietario, m3 FROM vehiculos WHERE id ='"+vehiculos.id+"'",null);
                            if (cursor.moveToFirst() == true){
                                String patente = cursor.getString(1);
                                String tipo = cursor.getString(2);
                                String marca = cursor.getString(3);
                                String propietario = cursor.getString(4);
                                String m3 = cursor.getString(5);

                                if (patente != vehiculos.patente){
                                    db.execSQL("UPDATE vehiculos SET patente='"+vehiculos.patente+"' WHERE id='"+vehiculos.id+"'");
                                }
                                if (tipo != vehiculos.tipo){
                                    db.execSQL("UPDATE vehiculos SET tipo='"+vehiculos.tipo+"' WHERE id='"+vehiculos.id+"'");
                                }
                                if (marca != vehiculos.marca){
                                    db.execSQL("UPDATE vehiculos SET marca='"+vehiculos.marca+"' WHERE id='"+vehiculos.id+"'");
                                }
                                if (propietario != vehiculos.propietario){
                                    db.execSQL("UPDATE vehiculos SET propietario='"+vehiculos.propietario+"' WHERE id='"+vehiculos.id+"'");
                                }
                                if (m3 != vehiculos.m3){
                                    db.execSQL("UPDATE vehiculos SET m3='"+vehiculos.m3+"' WHERE id='"+vehiculos.id+"'");
                                }

                            }


                        }
                    }

                }catch (JSONException e){
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        mRequestQueue.add(mStringRequest);
    }


    private void DescargaOperadores(){
        mRequestQueue = Volley.newRequestQueue(this);
        mStringRequest = new StringRequest(Request.Method.GET, APIOPERADORES, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    String json;

                    json = response.toString();
                    JSONArray jsonArray = null;
                    jsonArray = new JSONArray(json);
                    Operadores op = new Operadores();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        op.nombre = jsonObject.getString("nombre");
                        op.id_web = jsonObject.getString("id");

                        SQLiteDatabase db = myDB.getWritableDatabase();

                        if (myDB.ExisteOperador(op.id_web) == false){
                            //EL OPERADOR NO SE ENCUENTRA
                            myDB.RegistroOperadores(op.id_web, op.nombre);

                        }else{
                            Cursor cursor = db.rawQuery("SELECT id, nombre FROM operadores WHERE id_web ='"+op.id_web+"'",null);
                            if (cursor.moveToFirst() == true){
                                String nombre = cursor.getString(1);

                                if (nombre != op.nombre){
                                    db.execSQL("UPDATE operadores SET nombre='"+op.nombre+"' WHERE id_web='"+op.id_web+"'");
                                }
                            }
                        }
                    }
                }
                catch (JSONException e){
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        mRequestQueue.add(mStringRequest);
    }


    private void DescargaPlantas(){
        mRequestQueue = Volley.newRequestQueue(this);
        mStringRequest = new StringRequest(Request.Method.GET, APIPLANTAS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    String json;

                    json = response.toString();
                    JSONArray jsonArray = null;
                    jsonArray = new JSONArray(json);
                    plantas_list plants = new plantas_list();

                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        plants.id = jsonObject.getString("id");
                        plants.nombre = jsonObject.getString("nombre");
                        plants.ubicacion = jsonObject.getString("ubicacion");
                        SQLiteDatabase db = myDB.getWritableDatabase();


                        if (myDB.ExistePlanta(plants.id ) == false){
                            //LA PATENTE NO SE ENCUENTRA REGISTRADA
                            myDB.RegistrarListadoPlantas(plants.id,plants.nombre,plants.ubicacion);

                        }else{
                            Cursor cursor = db.rawQuery("SELECT id, nombre, ubicacion FROM plantas_list WHERE id ='"+plants.id+"'",null);
                                if (cursor.moveToFirst() == true) {
                                    String id = cursor.getString(0);
                                    String nombre = cursor.getString(1);
                                    String ubicacion = cursor.getString(2);
                                    if (nombre != plants.nombre) {
                                        db.execSQL("UPDATE plantas_list SET nombre='"+plants.nombre+"' WHERE id='"+plants.id+"'");
                                    }
                                    if (ubicacion != plants.ubicacion){
                                        db.execSQL("UPDATE plantas_list SET ubicacion='"+plants.ubicacion+"' WHERE id='"+plants.id+"'");
                                    }


                                }


                        }

                    }

                }catch (JSONException e){
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        mRequestQueue.add(mStringRequest);
    }




    private void DescargaUnegocios(){
        mRequestQueue = Volley.newRequestQueue(this);
        mStringRequest = new StringRequest(Request.Method.GET, APIUnegociosAridos, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    String json;

                    Log.i("TaaaAG",response);
                    json = response.toString();
                    JSONArray jsonArray = null;
                    jsonArray = new JSONArray(json);
                    Unegocios unegocios = new Unegocios();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        unegocios.id = jsonObject.getString("id");
                        unegocios.nombre = jsonObject.getString("nombre");
                        unegocios.estado = jsonObject.getString("estado");

                        SQLiteDatabase db = myDB.getWritableDatabase();

                        Cursor cursor = db.rawQuery("SELECT id, nombre, estado FROM unegocios WHERE id ='"+unegocios.id+"'",null);

                        if (cursor.getCount() <=0){
                            //NO SE ENCUENTRA LA UNEGOCIO Y SE REGISTRA
                            myDB.RegistroUnegocios(unegocios.id,unegocios.nombre,unegocios.estado);

                        }else{
                            if (cursor.moveToFirst() == true){
                                String id = cursor.getString(0);
                                String nombre = cursor.getString(1);
                                if (unegocios.estado.toString().equals("INACTIVO")){

                                    db.execSQL("DELETE FROM unegocios WHERE id ='"+unegocios.id+"'");

                                }else{
                                    if(id != unegocios.id){
                                        db.execSQL("UPDATE unegocios SET id='"+unegocios.id+"' WHERE id='"+unegocios.id+"'");
                                    }
                                    if(nombre != unegocios.nombre){
                                        db.execSQL("UPDATE unegocios SET nombre='"+unegocios.nombre+"' WHERE id='"+unegocios.id+"'");
                                    }
                                }
                            }
                        }

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        mRequestQueue.add(mStringRequest);
    }






}