package com.example.leandro.aridosmobile;

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
    public static final String APIVEHICULOS = "http://192.168.1.189:8000/api/vehiculos";
    ArrayList<String> plantas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ajustes);
        ajustesParametros = (CardView)findViewById(R.id.ajustesparametrosCard);
        ajustesParametros.setOnClickListener(this);
        ajustesgeneralesCard = (CardView)findViewById(R.id.ajustesgeneralesCard);
        ajustesgeneralesCard.setOnClickListener(this);
        actualizarpatentes = (CardView) findViewById(R.id.actualizacionpatentesCard);
        actualizarpatentes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TareaenSegundoPlano tsp = new TareaenSegundoPlano();
                tsp.execute();

                Toast.makeText(getApplicationContext(), "Patentes Actualizadas", Toast.LENGTH_SHORT).show();
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

    public class TareaenSegundoPlano extends AsyncTask<Void, Integer, Boolean> {


        @Override
        protected Boolean doInBackground(Void... voids) {
                DescargadePatentes();
            return true;
        }
    }




    private void DescargadePatentes(){
        mRequestQueue = Volley.newRequestQueue(this);
        mStringRequest = new StringRequest(Request.Method.GET, APIVEHICULOS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    String json;
                    Log.i(TAG,"Respuesta Success a la API: "+response.toString());
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
                            Log.i(TAG,"VEHICULO AGREGADO CORRECTAMENTE: "+vehiculos.patente);
                        }else{
                            Log.i(TAG,"EL VEHICULO YA SE ENCUENTRA EN EL LISTADO");
                        }
                    }

                }catch (JSONException e){
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG, "Error de conexión con la API: "+error.toString());
            }
        });
        mRequestQueue.add(mStringRequest);
    }


}