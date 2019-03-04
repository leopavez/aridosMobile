package com.example.leandro.aridosmobile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Menu extends AppCompatActivity implements View.OnClickListener{


    private CardView acopio_card, produccion_card, reportsalida_card, ajustes_card;
    Button wizard;


    ArrayList<Registro_acopio> listaregistrosacopio;
    Registro_acopio reg_acopio = new Registro_acopio();
    DatabaseHelper myDB;
    public static final String APIEnvioRegistrosAcopio = "http://192.168.1.189:8000/api/aridos/registros/entrada";
    private RequestQueue mRequestQueue;
    private StringRequest mStringRequest;
    private static final String TAG = Report_entrada.class.getName();

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

        cargarCredenciales();

        wizard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Menu.this, Pager.class);
                startActivity(i);

            }
        });

    }


    public void onClick(View v){
        Intent i;
        Intent intent = getIntent();
        switch (v.getId()){
            case R.id.acopiocard : i = new Intent(this,MenuAcopio.class);startActivity(i);overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);break;
            case R.id.produccioncard : i = new Intent(this,MenuProduccion.class);startActivity(i);overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);break;
            case R.id.reportsalida : i = new Intent(this,MenuSalida.class);startActivity(i);overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);break;
            case R.id.ajustescard: i = new Intent(this,Ajustes.class); startActivity(i);overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);break;
            default:break;
        }
    }


    public void cargarCredenciales(){
        SharedPreferences preferences = getSharedPreferences("credenciales", Context.MODE_PRIVATE);

        String name = preferences.getString("NAME","NO EXISTE LA CREDENCIAL");
        String lastname = preferences.getString("LASTNAME", "NO EXISTE LA CREDENCIAL");

        Toast.makeText(this, "Bienvenido: "+name+" "+lastname, Toast.LENGTH_SHORT).show();


    }


    private void Enviodatos(){
        SQLiteDatabase db = myDB.getReadableDatabase();
        String estado = "PENDIENTE";

        listaregistrosacopio= new ArrayList<Registro_acopio>();
        Cursor cursor = db.rawQuery("SELECT id, patente, m3, planta, chofer, fecha, hora, username FROM registros_acopio WHERE estado='"+estado+"'",null);

        while (cursor.moveToNext()){
            reg_acopio = new Registro_acopio();
            reg_acopio.setId(cursor.getInt(0));
            reg_acopio.setPatente(cursor.getString(1));
            reg_acopio.setM3(cursor.getString(2));
            reg_acopio.setPlanta(cursor.getString(3));
            reg_acopio.setChofer(cursor.getString(4));
            reg_acopio.setFecha(cursor.getString(5));
            reg_acopio.setHora(cursor.getString(6));
            reg_acopio.setUsername(cursor.getString(7));


            listaregistrosacopio.add(reg_acopio);

        }
        for (int i=0; i<listaregistrosacopio.size(); i++){


            final int id = listaregistrosacopio.get(i).getId();
            final String patente = listaregistrosacopio.get(i).getPatente().toString();
            final String m3 = listaregistrosacopio.get(i).getM3().toString();
            final String planta = listaregistrosacopio.get(i).getPlanta().toString();
            final String chofer = listaregistrosacopio.get(i).getChofer().toString();
            final String fecha = listaregistrosacopio.get(i).getFecha().toString();
            final String hora = listaregistrosacopio.get(i).getHora().toString();
            final String username = listaregistrosacopio.get(i).getUsername().toString();

            mRequestQueue = Volley.newRequestQueue(this);
            mStringRequest = new StringRequest(Request.Method.POST, APIEnvioRegistrosAcopio, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    Log.i("MSG", "Respuesta aceptada de la API: " + response);

                    SQLiteDatabase db = myDB.getReadableDatabase();

                    db.execSQL("UPDATE registros_acopio SET estado='ENVIADO' WHERE id='"+id+"'");
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    Log.i(TAG,"Error de conexion con la API: "+error.toString());
                }
            }){
                @Override
                protected java.util.Map<String, String> getParams() throws AuthFailureError {

                    String nvale = String.valueOf(id);

                    Map<String, String> params = new HashMap<String, String>();
                    params.put("nrovale",nvale);
                    params.put("patente",patente);
                    params.put("m3",m3);
                    params.put("fecha",fecha);
                    params.put("hora",hora);
                    params.put("planta",planta);
                    params.put("chofer",chofer);
                    params.put("user",username);
                    return params;
                }
            };
            mRequestQueue.add(mStringRequest);
        }
    }

    public void EjecucionEnvioSP() {
        try {
            Enviodatos();
            Thread.sleep(1800000);
            Log.i(TAG,"ENTRO AL ENVIO DE DATOS");
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }

    public class SegundoPlano extends AsyncTask<Void,Integer,Boolean>
    {

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

}
