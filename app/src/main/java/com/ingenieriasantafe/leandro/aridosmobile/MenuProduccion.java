package com.ingenieriasantafe.leandro.aridosmobile;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.AutoCompleteTextView;

public class MenuProduccion extends AppCompatActivity implements View.OnClickListener {


    private static final String TAG = MenuProduccion.class.getName();
    CardView reportproduccion, produccionplanta;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menuproduccion);
        reportproduccion = (CardView) findViewById(R.id.rproduccion);
        produccionplanta = (CardView)findViewById(R.id.produccionplanta);
        produccionplanta.setOnClickListener(this);
        reportproduccion.setOnClickListener(this);
    }

    public void onClick(View v){
        Intent i;
        Intent intent = getIntent();
        switch (v.getId()){
            case R.id.rproduccion : i = new Intent(this,Report_produccion.class);startActivity(i);overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);break;
            case R.id.produccionplanta: i = new Intent(this,Produccion_planta.class);startActivity(i);overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);break;
            default:break;
        }
    }
}
