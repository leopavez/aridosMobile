package com.ingenieriasantafe.leandro.aridosmobile;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
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
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class Ajustes extends AppCompatActivity implements View.OnClickListener{

    private CardView ajustesgeneralesCard,actualizarpatentes,ajustesParametros, limpiarDatos;

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

    ArrayList<String> plantas;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ajustes);
        ajustesParametros = (CardView)findViewById(R.id.ajustesparametrosCard);
        ajustesParametros.setOnClickListener(this);
        ajustesgeneralesCard = (CardView)findViewById(R.id.ajustesgeneralesCard);
        ajustesgeneralesCard.setOnClickListener(this);
        actualizarpatentes = (CardView) findViewById(R.id.actualizacionpatentesCard);
        limpiarDatos = (CardView)findViewById(R.id.limpiardatos);
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

        limpiarDatos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressDialog.setTitle("Limpiando la aplicacion");
                progressDialog.setMessage("Eliminando datos enviados......");
                progressDialog.setProgressStyle(progressDialog.STYLE_SPINNER);
                progressDialog.setMax(100);
                progressDialog.show();
                progressDialog.setCancelable(false);

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        try{


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

                                if(fechaactual.equals(fecha)){
                                }else{
                                    reg_acopio = new Registro_acopio();
                                    reg_acopio.setId(cursor.getInt(0));
                                    listaregistrosacopio.add(reg_acopio);
                                }
                            }
                            for (int i = 0; i < listaregistrosacopio.size(); i++) {

                                db.execSQL("DELETE FROM registros_acopio WHERE id=" + listaregistrosacopio.get(i).getId() + "");
                                Log.i("ACOPIO","ELIMINADO"+listaregistrosacopio.get(i).getId());
                            }
                            //PRODUCCION PLANTA
                            while (cursor1.moveToNext()) {

                                String fecha = cursor1.getString(1);
                                if (fechaactual.equals(fecha)){
                                }else{
                                    reg_prod = new Registro_produccion_planta();
                                    reg_prod.setId(cursor1.getInt(0));
                                    listaregistroproduccionplanta.add(reg_prod);
                                }
                            }
                            for (int i = 0; i < listaregistroproduccionplanta.size(); i++) {

                                db.execSQL("DELETE FROM prod_planta WHERE id=" + listaregistroproduccionplanta.get(i).getId() + "");
                                Log.i("PPLANTA","ELIMINADO"+listaregistroproduccionplanta.get(i).getId());
                            }
                            //PRODUCCION X PATENTE
                            while (cursor2.moveToNext()) {

                                String fecha = cursor2.getString(1);

                                if (fechaactual.equals(fecha)){
                                }else{
                                    reg_prod_patente = new Registro_produccion_patente();
                                    reg_prod_patente.setId(cursor2.getInt(0));
                                    listaregistroproduccionpatente.add(reg_prod_patente);
                                }
                            }
                            for (int i = 0; i < listaregistroproduccionpatente.size(); i++) {

                                db.execSQL("DELETE FROM registros_produccion WHERE id=" + listaregistroproduccionpatente.get(i).getId() + "");
                                Log.i("PPATENTE","ELIMINADO"+listaregistroproduccionpatente.get(i).getId());
                            }
                            //SALIDA DE MATERIAL
                            while (cursor3.moveToNext()) {

                                String fecha = cursor3.getString(1);

                                if (fechaactual.equals(fecha)){
                                }else{
                                    reg_salida = new Registro_salida();
                                    reg_salida.setId(cursor3.getInt(0));
                                    listaregistrosalida.add(reg_salida);
                                }
                            }
                            for (int i = 0; i < listaregistrosalida.size(); i++) {

                                db.execSQL("DELETE FROM registros_salida WHERE id=" + listaregistrosalida.get(i).getId() + "");
                                Log.i("SALIDA","ELIMINADO"+listaregistrosalida.get(i).getId());
                            }




                            Thread.sleep(5000);
                            progressDialog.dismiss();

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(),"Limpieza completada",Toast.LENGTH_SHORT).show();
                                }
                            });

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