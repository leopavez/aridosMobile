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

import java.util.ArrayList;

public class Ajustes extends AppCompatActivity implements View.OnClickListener{

    private CardView ajustesgeneralesCard,actualizarpatentes,ajustesParametros;

    private RequestQueue mRequestQueue;
    private StringRequest mStringRequest;

    DatabaseHelper myDB;
    private static final String TAG = Ajustes.class.getName();
    public static final String APIVEHICULOS = "http://santafeinversiones.org/api/vehiculos";
    public static final String APIPLANTAS = "http://santafeinversiones.org/api/plantas";
    public static final String APIOPERADORES = "http://santafeinversiones.org/api/aridos/operadores";

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





}