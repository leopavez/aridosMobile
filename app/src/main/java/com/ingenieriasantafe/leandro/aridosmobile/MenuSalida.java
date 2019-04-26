package com.ingenieriasantafe.leandro.aridosmobile;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;

public class MenuSalida extends AppCompatActivity implements View.OnClickListener {


    private static final String TAG = MenuProduccion.class.getName();
    CardView reportsalida, resumensalida;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menusalida);
        reportsalida = (CardView) findViewById(R.id.rsalida);
        resumensalida = (CardView)findViewById(R.id.resumensalida);
        resumensalida.setOnClickListener(this);
        reportsalida.setOnClickListener(this);

    }

    public void onClick(View v){
        Intent i;
        Intent intent = getIntent();
        switch (v.getId()){
            case R.id.rsalida : i = new Intent(this,Report_salida.class);startActivity(i);overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);break;
            case R.id.resumensalida : i = new Intent(this,Resumen_salida.class);startActivity(i);overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);break;
            default:break;
        }
    }
}
