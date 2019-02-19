package com.example.leandro.aridosmobile;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

public class Resumenes_diarios_acopio extends AppCompatActivity {

 private static final String TAG = Report_entrada.class.getName();
 private AutoCompleteTextView pt;
 Button xpatente;
 @Override
 protected void onCreate(Bundle savedInstanceState) {
  super.onCreate(savedInstanceState);
  setContentView(R.layout.resumen_diario_acopio);
  pt = findViewById(R.id.patentesResumenAcopio);
  xpatente = (Button)findViewById(R.id.btnresumenxpatenteacopio);

  LoadDataPatentes();

 }



 private void LoadDataPatentes(){
  List<Vehiculos> patents = new ArrayList<Vehiculos>();
  final PatentesSearchAdapter patentesSearchAdapter = new PatentesSearchAdapter(getApplicationContext(),patents);
  pt.setThreshold(1);
  pt.setAdapter(patentesSearchAdapter);


 }
}
