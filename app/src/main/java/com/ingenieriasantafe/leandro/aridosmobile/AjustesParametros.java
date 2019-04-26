package com.ingenieriasantafe.leandro.aridosmobile;

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

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ajustesparametros);
        myDB = new DatabaseHelper(this);
        cardimpresora = (CardView) findViewById(R.id.ajustesImpresoraCard);
        cardimpresora.setOnClickListener(this);
        cardplanta = (CardView)findViewById(R.id.ajustesPlantasCard);
        cardplanta.setOnClickListener(this);


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



}
