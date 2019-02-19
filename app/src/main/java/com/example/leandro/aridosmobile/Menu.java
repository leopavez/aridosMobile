package com.example.leandro.aridosmobile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class Menu extends AppCompatActivity implements View.OnClickListener{


    private CardView acopio_card, produccion_card, reportsalida_card, ajustes_card;
    Button wizard;

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

        Toast.makeText(this, "El nombre es: "+name+" "+lastname, Toast.LENGTH_SHORT).show();


    }




}
