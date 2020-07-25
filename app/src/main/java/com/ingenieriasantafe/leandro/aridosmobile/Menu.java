package com.ingenieriasantafe.leandro.aridosmobile;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.CrossProcessCursor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import cz.msebera.android.httpclient.Header;

public class Menu extends AppCompatActivity implements View.OnClickListener {


    private CardView acopio_card, produccion_card, reportsalida_card, ajustes_card;
    Button wizard;

    ArrayList<Registro_acopio> listaregistrosacopio;
    ArrayList<Registro_salida> listaregistrosalida;
    ArrayList<Registro_produccion_patente> listaregistroproduccionpatente;
    ArrayList<Registro_produccion_planta> listaregistroproduccionplanta;
    Registro_acopio reg_acopio = new Registro_acopio();
    Registro_salida reg_salida = new Registro_salida();
    Registro_produccion_planta reg_prod = new Registro_produccion_planta();
    Registro_produccion_patente reg_prod_patente = new Registro_produccion_patente();
    DatabaseHelper myDB;
    public static final String APIEnvioRegistrosAcopio = "http://santafeinversiones.org/api/aridos/registros/entrada";
    public static final String APIEnvioRegistrosProduccionPlanta = "http://santafeinversiones.org/api/aridos/produccion/xplanta";
    public static final String APIEnvioRegistrosProduccionPatente = "http://santafeinversiones.org/api/aridos/produccion/xpatente";
    public static final String APIEnvioRegistrosSalida = "http://santafeinversiones.org/api/aridos/registros/salida";
    public static final String APIEnvioNotificacionAcumulados="http://new.santafeinversiones.org/api/aridos-app/mensaje";

    private RequestQueue mRequestQueue;
    private StringRequest mStringRequest;
    private static final String TAG = Menu.class.getName();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu);
        wizard = (Button) findViewById(R.id.btnwizard);
        acopio_card = (CardView) findViewById(R.id.acopiocard);
        produccion_card = (CardView) findViewById(R.id.produccioncard);
        reportsalida_card = (CardView) findViewById(R.id.reportsalida);
        ajustes_card = (CardView) findViewById(R.id.ajustescard);
        acopio_card.setOnClickListener(this);
        produccion_card.setOnClickListener(this);
        reportsalida_card.setOnClickListener(this);
        ajustes_card.setOnClickListener(this);
        myDB = new DatabaseHelper(this);

        SegundoPlano sp = new SegundoPlano();
        sp.execute();

        wizard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Menu.this, Pager.class);
                startActivity(i);

            }
        });

    }


    public void onClick(View v) {
        Intent i;
        Intent intent = getIntent();
        switch (v.getId()) {
            case R.id.acopiocard:
                i = new Intent(this, MenuAcopio.class);
                startActivity(i);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
            case R.id.ajustescard:
                i = new Intent(this, Ajustes.class);
                startActivity(i);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
            case R.id.produccioncard:
                i = new Intent(this, MenuProduccion.class);
                startActivity(i);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
            case R.id.reportsalida:
                i = new Intent(this, MenuSalida.class);
                startActivity(i);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;

            default:
                break;
        }
    }

    private void EnviodatosAcopio() {
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

    }

    public void EjecucionEnvioSP() {
        try {
            Eliminaciondedatos();
            EnviodatosAcopio();
            EnvioDatosProduccionPlanta();
            EnvioDatosProduccionXPatente();
            EnviodatosSalida();
            EnvionotificacionMaximoAcumulado();
            Thread.sleep(1200000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public class SegundoPlano extends AsyncTask<Void, Integer, Boolean> {

        @Override
        protected Boolean doInBackground(Void... voids) {
            EjecucionEnvioSP();
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            SegundoPlano sp = new SegundoPlano();
            sp.execute();
        }
    }

    private void EnvionotificacionMaximoAcumulado(){
        SQLiteDatabase db = myDB.getReadableDatabase();

        String estado = "PENDIENTE";

        Cursor acopio = db.rawQuery("SELECT id, patente, m3, planta, chofer, fecha, hora, username,procedencia FROM registros_acopio WHERE estado='" + estado + "'", null);
        Cursor produccion_patente = db.rawQuery("SELECT id, patente, horasmaquina, nam, nah, nat, se, otras, botiquin, extintor, ar, baliza, rt, so, pc, fecha, hora, username, planta, combustible," +
                "operador FROM registros_produccion WHERE estado='" + estado + "'", null);
        Cursor produccion_planta = db.rawQuery("SELECT id, tipomaterial, m3, procedencia, planta, username, fecha, hora FROM prod_planta WHERE estado='" + estado + "'", null);
        Cursor salida = db.rawQuery("SELECT id, patente, m3, planta, chofer, fecha, hora, username,procedencia,tipomaterial,destino FROM registros_salida WHERE estado='" + estado + "'", null);

        int countregistroacopio = acopio.getCount();
        int countregistroproduccionpatente = produccion_patente.getCount();
        int countregistroproduccionplanta = produccion_planta.getCount();
        int countregistrosalida = salida.getCount();

        final int datos = (countregistroacopio+countregistroproduccionpatente+countregistroproduccionplanta+countregistrosalida);

        SharedPreferences preferences = getSharedPreferences("maxacumulados", Context.MODE_PRIVATE);
        String maxdatosacumulados = preferences.getString("max", "SIN MAXIMO");

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat horaformat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        String fecha = dateFormat.format(date);
        String hora = horaformat.format(date);
        SharedPreferences preferences1 = getSharedPreferences("plantaApp", Context.MODE_PRIVATE);
        String planta = preferences1.getString("NAME_PLANTA", "");

        if (maxdatosacumulados.equals("SIN MAXIMO")){

        }else if (datos>=Integer.parseInt(maxdatosacumulados)){


            //NOTIFICACION ANDROID

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "1")
                    .setSmallIcon(R.drawable.transporte)
                    .setContentTitle("DATOS ACUMULADOS")
                    .setContentText("Por favor sincroniza los datos acumulados")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            notificationManager.notify(1, builder.build());

            final RequestParams params = new RequestParams();
            params.put("planta",planta);
            params.put("mensaje","Porfavor contacta al operador del P.O.S para que sincronice los datos acumulados");
            params.put("numerodatos",datos);
            params.put("fecha",fecha);
            params.put("hora",hora);

            Handler handler = new Handler(Looper.getMainLooper());
            Runnable runnable = new Runnable() {
                @Override
                public void run() {

                    AsyncHttpClient client = new AsyncHttpClient();
                    client.setMaxRetriesAndTimeout(0,1500);
                    client.post(APIEnvioNotificacionAcumulados, params, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            if (statusCode == 200){
                                String response = new String(responseBody).toUpperCase();
                                Log.i("TAG", "ENVIO OK " + response);
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

        }else{
            Log.i("DATOS ACUMULADOS", "NO SE ALCANZA LA CANTIDAD DE DATOS");
        }
    }

    private void EnvioDatosProduccionXPatente() {
        SQLiteDatabase db = myDB.getReadableDatabase();
        String estado = "PENDIENTE";
        listaregistroproduccionpatente = new ArrayList<Registro_produccion_patente>();
        Cursor cursor = db.rawQuery("SELECT id, patente, horasmaquina, nam, nah, nat, se, otras, botiquin, extintor, ar, baliza, rt, so, pc, fecha, hora, username, planta, combustible," +
                "operador FROM registros_produccion WHERE estado='" + estado + "'", null);
        while (cursor.moveToNext()) {
            reg_prod_patente = new Registro_produccion_patente();
            reg_prod_patente.setId(cursor.getInt(0));
            reg_prod_patente.setPatente(cursor.getString(1));
            reg_prod_patente.setHorasmaquina(cursor.getString(2));
            reg_prod_patente.setNam(cursor.getString(3));
            reg_prod_patente.setNah(cursor.getString(4));
            reg_prod_patente.setNat(cursor.getString(5));
            reg_prod_patente.setSe(cursor.getString(6));
            reg_prod_patente.setOtras(cursor.getString(7));
            reg_prod_patente.setBotiquin(cursor.getString(8));
            reg_prod_patente.setExtintor(cursor.getString(9));
            reg_prod_patente.setAr(cursor.getString(10));
            reg_prod_patente.setBaliza(cursor.getString(11));
            reg_prod_patente.setRt(cursor.getString(12));
            reg_prod_patente.setSo(cursor.getString(13));
            reg_prod_patente.setPc(cursor.getString(14));
            reg_prod_patente.setHora(cursor.getString(15));
            reg_prod_patente.setFecha(cursor.getString(16));
            reg_prod_patente.setUsername(cursor.getString(17));
            reg_prod_patente.setPlanta(cursor.getString(18));
            reg_prod_patente.setCombustible(cursor.getString(19));
            reg_prod_patente.setOperador(cursor.getString(20));


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
    }


    private void EnvioDatosProduccionPlanta() {
        SQLiteDatabase db = myDB.getReadableDatabase();
        String estado = "PENDIENTE";

        listaregistroproduccionplanta = new ArrayList<Registro_produccion_planta>();
        Cursor cursor = db.rawQuery("SELECT id, tipomaterial, m3, procedencia, planta, username, fecha, hora FROM prod_planta WHERE estado='" + estado + "'", null);


        while (cursor.moveToNext()) {
            reg_prod = new Registro_produccion_planta();
            reg_prod.setId(cursor.getInt(0));
            reg_prod.setTipomaterial(cursor.getString(1));
            reg_prod.setM3(cursor.getString(2));
            reg_prod.setProcedencia(cursor.getString(3));
            reg_prod.setPlanta(cursor.getString(4));
            reg_prod.setUsername(cursor.getString(5));
            reg_prod.setFecha(cursor.getString(6));
            reg_prod.setHora(cursor.getString(7));
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
    }


    private void EnviodatosSalida() {
        SQLiteDatabase db = myDB.getReadableDatabase();
        String estado = "PENDIENTE";

        listaregistrosalida = new ArrayList<Registro_salida>();
        Cursor cursor = db.rawQuery("SELECT id, patente, m3, planta, chofer, fecha, hora, username,procedencia,tipomaterial,destino FROM registros_salida WHERE estado='" + estado + "'", null);


        while (cursor.moveToNext()) {
            reg_salida = new Registro_salida();
            reg_salida.setId(cursor.getInt(0));
            reg_salida.setPatente(cursor.getString(1));
            reg_salida.setM3(cursor.getString(2));
            reg_salida.setPlanta(cursor.getString(3));
            reg_salida.setChofer(cursor.getString(4));
            reg_salida.setFecha(cursor.getString(5));
            reg_salida.setHora(cursor.getString(6));
            reg_salida.setUsername(cursor.getString(7));
            reg_salida.setProcedencia(cursor.getString(8));
            reg_salida.setTipomaterial(cursor.getString(9));
            reg_salida.setDestino(cursor.getString(10));

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

    }

    public void Eliminaciondedatos() {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date date = new Date();
        String fechaactual = dateFormat.format(date);

        SQLiteDatabase db = myDB.getWritableDatabase();
        String estado = "ENVIADO";
        listaregistrosacopio = new ArrayList<Registro_acopio>();
        listaregistroproduccionplanta = new ArrayList<Registro_produccion_planta>();
        listaregistroproduccionpatente = new ArrayList<Registro_produccion_patente>();
        listaregistrosalida = new ArrayList<Registro_salida>();

        Cursor cursor = db.rawQuery("SELECT id, fecha FROM registros_acopio WHERE estado='" + estado + "'", null);
        Cursor cursor1 = db.rawQuery("SELECT id, fecha FROM prod_planta WHERE estado='" + estado + "'", null);
        Cursor cursor2 = db.rawQuery("SELECT id, fecha FROM registros_produccion WHERE estado='" + estado + "'", null);
        Cursor cursor3 = db.rawQuery("SELECT id, fecha FROM registros_salida WHERE estado='" + estado + "'", null);


        //ACOPIO
        while (cursor.moveToNext()) {

            String fecha = cursor.getString(1);
            if (fechaactual.equals(fecha)) {
            } else {
                reg_acopio = new Registro_acopio();
                reg_acopio.setId(cursor.getInt(0));
                listaregistrosacopio.add(reg_acopio);
            }
        }

        for (int i = 0; i < listaregistrosacopio.size(); i++) {

            db.execSQL("DELETE FROM registros_acopio WHERE id=" + listaregistrosacopio.get(i).getId() + "");
        }

        //PRODUCCION PLANTA
        while (cursor1.moveToNext()) {

            String fecha = cursor1.getString(1);
            if (fechaactual.equals(fecha)) {

            } else {
                reg_prod = new Registro_produccion_planta();
                reg_prod.setId(cursor1.getInt(0));
                listaregistroproduccionplanta.add(reg_prod);
            }
        }

        for (int i = 0; i < listaregistroproduccionplanta.size(); i++) {

            db.execSQL("DELETE FROM prod_planta WHERE id=" + listaregistroproduccionplanta.get(i).getId() + "");
        }

        //PRODUCCION X PATENTE
        while (cursor2.moveToNext()) {

            String fecha = cursor2.getString(1);
            if (fechaactual.equals(fecha)) {

            } else {
                reg_prod_patente = new Registro_produccion_patente();
                reg_prod_patente.setId(cursor2.getInt(0));
                listaregistroproduccionpatente.add(reg_prod_patente);
            }
        }

        for (int i = 0; i < listaregistroproduccionpatente.size(); i++) {

            db.execSQL("DELETE FROM registros_produccion WHERE id=" + listaregistroproduccionpatente.get(i).getId() + "");
        }

        //SALIDA DE MATERIAL
        while (cursor3.moveToNext()) {
            String fecha = cursor3.getString(1);
            if (fechaactual.equals(fecha)) {

            } else {
                reg_salida = new Registro_salida();
                reg_salida.setId(cursor3.getInt(0));
                listaregistrosalida.add(reg_salida);
            }
        }
        for (int i = 0; i < listaregistrosalida.size(); i++) {

            db.execSQL("DELETE FROM registros_salida WHERE id=" + listaregistrosalida.get(i).getId() + "");
        }
    }
}
