package com.example.leandro.aridosmobile;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

public class Report_entrada extends AppCompatActivity {

    Spinner spinner;


    private static final String[] patentes = new String[]{
            "KHXP21","NJ2421","VT2171"

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reportentrada);


        AutoCompleteTextView pt = findViewById(R.id.patentesreporteentrada);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,patentes);

        pt.setAdapter(adapter);

    }
}
