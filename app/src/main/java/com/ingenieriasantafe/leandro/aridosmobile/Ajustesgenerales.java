package com.ingenieriasantafe.leandro.aridosmobile;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class Ajustesgenerales extends AppCompatActivity {

    Switch voucher;

    EditText cantidadVouchertxt;
    TextView maximoacumulados;
    Button GuardarNVoucher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ajustesgenerales);
        voucher = (Switch)findViewById(R.id.switchVoucher);
        cantidadVouchertxt = (EditText)findViewById(R.id.txtnVoucher);
        GuardarNVoucher = (Button)findViewById(R.id.btnguardarnvoucher);
        EstadoVoucher();
        AsignarCantidadVoucher();
        MaximoAcumulados();
        voucher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (voucher.isChecked()){
                    SwitchEstado("true");
                    Toast.makeText(getApplicationContext(),"La impresión de Voucher se encuentra habilitada",Toast.LENGTH_SHORT).show();
                }else{
                    SwitchEstado("false");
                    Toast.makeText(getApplicationContext(),"La impresión de Voucher fue deshabilitada",Toast.LENGTH_SHORT).show();
                }
            }
        });

        GuardarNVoucher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cantidadVouchertxt.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(), "Debes escribir una cantidad (1-9)",Toast.LENGTH_SHORT).show();
                }else{
                    String cantidad = cantidadVouchertxt.getText().toString();
                    SharedCantidadNVoucher(cantidad);
                    Toast.makeText(getApplicationContext(), "Se configuraron "+cantidad+" Voucher",Toast.LENGTH_SHORT).show();

                }
            }
        });



    }

    private void MaximoAcumulados(){

        maximoacumulados = (TextView)findViewById(R.id.txtmaxacumulados);
        SharedPreferences preferences = getSharedPreferences("maxacumulados", Context.MODE_PRIVATE);
        String maxacumulados = preferences.getString("max","SIN MAXIMO");

        maximoacumulados.setText("Se notificara al alcanzar: "+maxacumulados+" datos acumulados");

    }
    private void EstadoVoucher(){
        SharedPreferences preferences = getSharedPreferences("Voucher", Context.MODE_PRIVATE);

        String estado = preferences.getString("estado","false");

        if (estado.toString().equals("true")){
            voucher.setChecked(true);

        }else if (estado.toString().equals("false")){
            voucher.setChecked(false);
        }else{
            voucher.setChecked(false);
        }

    }

    private void AsignarCantidadVoucher(){
        SharedPreferences preferences = getSharedPreferences("Cvoucher",Context.MODE_PRIVATE);
        String cantidadVoucher = preferences.getString("cantidad","1");
        if (cantidadVoucher.toString().equals("0")){
            cantidadVouchertxt.setText("1");
        }else{
            cantidadVouchertxt.setText(cantidadVoucher);
        }
    }

    public void SwitchEstado(String value){
        SharedPreferences preferences = getSharedPreferences("Voucher", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("estado",value);
        editor.commit();

    }

    public void SharedCantidadNVoucher(String cantidad){
        SharedPreferences preferences = getSharedPreferences("Cvoucher", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("cantidad",cantidad);
        editor.commit();

    }


}
