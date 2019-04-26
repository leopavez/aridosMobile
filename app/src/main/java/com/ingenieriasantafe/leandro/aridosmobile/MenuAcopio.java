package com.ingenieriasantafe.leandro.aridosmobile;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;

public class MenuAcopio extends AppCompatActivity implements View.OnClickListener {


    private CardView reporte_entrada,resumen;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menuacopio);
        reporte_entrada = (CardView) findViewById(R.id.rentrada);
        resumen = (CardView) findViewById(R.id.resumendiario);
        reporte_entrada.setOnClickListener(this);
        resumen.setOnClickListener(this);

    }


    public void onClick(View v){
        Intent i;
        Intent intent = getIntent();
        switch (v.getId()){
            case R.id.rentrada : i = new Intent(this,Report_entrada.class);startActivity(i);overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);break;
            case R.id.resumendiario : i = new Intent(this,Resumenes_diarios_acopio.class);startActivity(i);overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);break;
         default:break;
        }
    }

}
