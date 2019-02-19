package com.example.leandro.aridosmobile;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AjustesParametros extends AppCompatActivity implements View.OnClickListener {

    CardView cardplanta, cardimpresora;
    DatabaseHelper myDB;
    RequestQueue mRequestQueue;
    StringRequest mStringRequest;
    private static final String TAG = AjustesParametros.class.getName();
    public static final String APIPLANTAS = "http://192.168.1.189:8000/api/plantas";

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ajustesparametros);
        myDB = new DatabaseHelper(this);
        cardimpresora = (CardView) findViewById(R.id.ajustesImpresoraCard);
        cardimpresora.setOnClickListener(this);
        cardplanta = (CardView)findViewById(R.id.ajustesPlantasCard);
        cardplanta.setOnClickListener(this);
        TareaenSegundoPlano sp = new TareaenSegundoPlano();
        sp.execute();

    }

    public void onClick(View v){
        Intent i;
        Intent intent = getIntent();
        switch (v.getId()){
            case R.id.ajustesImpresoraCard : i = new Intent(this,bluetooth_list.class);startActivity(i);overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);break;
            case R.id.ajustesPlantasCard : i = new Intent(this,Seleccionplanta.class);startActivity(i);overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);break;
            default:break;
        }
    }


    public class TareaenSegundoPlano extends AsyncTask<Void, Integer, Boolean> {
        @Override
        protected Boolean doInBackground(Void... voids) {
            DescargaPlantas();
            return true;
        }
    }


    private void DescargaPlantas(){
        mRequestQueue = Volley.newRequestQueue(this);
        mStringRequest = new StringRequest(Request.Method.GET, APIPLANTAS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    String json;
                    Log.i(TAG,"Respuesta Success a la API: "+response.toString());

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

                            Log.i(TAG,"PLANTA AGREGADO CORRECTAMENTE: "+plants.nombre);
                        }else{
                            Log.i(TAG,"LA PLANTA YA SE ENCUENTRA EN EL LISTADO");
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
