package com.example.leandro.aridosmobile;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
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

public class Seleccionplanta extends AppCompatActivity {

    DatabaseHelper myDB;

    ListView lista;

    ArrayList<plantas_list> listadeplantas;
    ArrayList<String> listadeldia;

    private static final String TAG = Seleccionplanta.class.getName();


    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.seleccionplanta);
        myDB = new DatabaseHelper(this);
        consultarListadoPlantas();
        lista = (ListView) findViewById(R.id.listadeplantas);
        ArrayAdapter adaptador= new ArrayAdapter(this, android.R.layout.simple_list_item_1,listadeldia);
        lista.setAdapter(adaptador);


       lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
           @Override
           public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

               GuardarPlantaSeleccionada(listadeplantas.get(position).getId(),listadeplantas.get(position).getNombre(),
                       listadeplantas.get(position).getUbicacion());

           }
       });


    }



    private void consultarListadoPlantas(){
        SQLiteDatabase db = myDB.getWritableDatabase();
        plantas_list plantas = null;

        listadeplantas= new ArrayList<plantas_list>();
        Cursor cursor = db.rawQuery("SELECT id, nombre, ubicacion FROM plantas_list" ,null);

        while (cursor.moveToNext()){
            plantas= new plantas_list();
            plantas.setId(cursor.getString(0));
            plantas.setNombre(cursor.getString(1));
            plantas.setUbicacion(cursor.getString(2));
            listadeplantas.add(plantas);

        }

        obtenerlistado();
    }


    private void obtenerlistado(){
        listadeldia = new ArrayList<String>();

        for (int i=0; i<listadeplantas.size(); i++){
            listadeldia.add("Nombre: "+listadeplantas.get(i).getNombre()+"\n"+
                    "Ubicación: "+listadeplantas.get(i).getUbicacion());

        }


    }

    private void GuardarPlantaSeleccionada(final String id, final String nombre, final String ubicación){


        AlertDialog.Builder confirmacion = new AlertDialog.Builder(this);
        confirmacion.setTitle("Confirmar la planta: " +nombre);
        confirmacion.setMessage("¿Estas seguro de guardar esta planta?");
        confirmacion.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                SharedPreferences preferences = getSharedPreferences("plantaApp", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor =preferences.edit();
                editor.putString("ID_PLANTA", id);
                editor.putString("NAME_PLANTA", nombre);
                editor.putString("UBICACION",ubicación);
                editor.commit();

                Toast.makeText(getApplicationContext(), "Planta configurada: "+nombre, Toast.LENGTH_SHORT).show();
                Intent i = new Intent(Seleccionplanta.this, AjustesParametros.class);
                startActivity(i);

            }
        });
        confirmacion.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        confirmacion.show();
    }

}
