package com.ingenieriasantafe.leandro.aridosmobile;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class Report_produccion extends AppCompatActivity {


    private AutoCompleteTextView pt, ope;
    private static final int REQUEST_ENABLE_BT = 2;
    Button siguiente;
    TextInputEditText horasmaquina;
    TextInputEditText combustible;
    BluetoothAdapter mBluetoothAdapter;

    DatabaseHelper myDB;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reportproduccion);
        pt = (AutoCompleteTextView) findViewById(R.id.patenteproduccion);
        ope = (AutoCompleteTextView) findViewById(R.id.OperadoresProduccion);
        siguiente = (Button) findViewById(R.id.btnsiguiente1);
        horasmaquina = (TextInputEditText) findViewById(R.id.txthorasmaquina);
        combustible = (TextInputEditText)findViewById(R.id.txtCombustible);
        myDB = new DatabaseHelper(this);

        //BLUETOOTH
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }


        siguiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SQLiteDatabase db = myDB.getWritableDatabase();

                Cursor cursor = db.rawQuery("SELECT id FROM vehiculos WHERE LOWER(patente) ='"+pt.getText().toString().toLowerCase()+"'",null);

                if (cursor.moveToFirst()==true){
                    if (pt.getText().toString().equals("") || horasmaquina.getText().toString().equals("") || ope.getText().toString().equals("")){

                        Toast.makeText(getApplicationContext(),"Debes llenar todos los campos",Toast.LENGTH_SHORT).show();

                    }else{
                        Intent intent = new Intent(Report_produccion.this, Report_produccionStep2.class);
                        intent.putExtra("patente",pt.getText().toString());
                        intent.putExtra("horasmaquina", horasmaquina.getText().toString());
                        intent.putExtra("combustible",combustible.getText().toString());
                        intent.putExtra("operador",ope.getText().toString());
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                        finish();

                    }
                }else{
                    Toast.makeText(Report_produccion.this, "La patente no esta registrada en el sistema", Toast.LENGTH_SHORT).show();
                }
            }
        });

        LoadDataPatentes();
        LoadDataOperadores();

    }

    private void LoadDataPatentes(){
        List<Vehiculos> patents = new ArrayList<Vehiculos>();
        final PatentesSearchAdapter patentesSearchAdapter = new PatentesSearchAdapter(getApplicationContext(),patents);
        pt.setThreshold(1);
        pt.setAdapter(patentesSearchAdapter);
    }


    private void LoadDataOperadores(){
        List<Operadores> op = new ArrayList<Operadores>();
        final OperadorSearchAdapter operadorSearchAdapter = new OperadorSearchAdapter(getApplicationContext(),op);
        ope.setThreshold(1);
        ope.setAdapter(operadorSearchAdapter);
    }
}
