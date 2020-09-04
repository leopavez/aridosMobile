package com.ingenieriasantafe.leandro.aridosmobile;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class Ajustesgenerales extends AppCompatActivity {

    Switch voucher;

    EditText cantidadVouchertxt;
    TextView maximoacumulados;
    Button GuardarNVoucher;
    Button actualizarMaxAcumulado;

    ProgressDialog progressDialog;

    private RequestQueue mRequestQueue;
    private StringRequest mStringRequest;

    public static final String APINumeroMaxAcumulados = "http://santafeinversiones.org/api/aridos-app/config/numerodatos";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ajustesgenerales);
        voucher = (Switch)findViewById(R.id.switchVoucher);
        cantidadVouchertxt = (EditText)findViewById(R.id.txtnVoucher);
        GuardarNVoucher = (Button)findViewById(R.id.btnguardarnvoucher);
        actualizarMaxAcumulado = (Button)findViewById(R.id.btnactualizarmaxacumulado);
        progressDialog = new ProgressDialog(Ajustesgenerales.this);
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

        actualizarMaxAcumulado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progressDialog.setTitle("Actualizando Aplicacion");
                progressDialog.setMessage("Actualizando maximo acumulado...");
                progressDialog.setProgressStyle(progressDialog.STYLE_SPINNER);
                progressDialog.setMax(100);
                progressDialog.show();
                progressDialog.setCancelable(false);

                ActualizacionMaxAcumulado();
            }
        });



    }

    private void ActualizacionMaxAcumulado(){

        mRequestQueue = Volley.newRequestQueue(this);
        mStringRequest = new StringRequest(Request.Method.GET, APINumeroMaxAcumulados, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try{
                    String json;
                    Log.i("MaxACUMULADO",response);
                    json = response.toString();
                    SharedPreferences preferences = getSharedPreferences("maxacumulados", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor =preferences.edit();
                    editor.putString("max", json);
                    editor.commit();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try{
                                progressDialog.dismiss();

                            }catch (Exception e){
                                e.printStackTrace();
                            }
                            Toast.makeText(Ajustesgenerales.this, "Actualizado correctamente", Toast.LENGTH_SHORT).show();
                            Intent refresh = new Intent(Ajustesgenerales.this,Ajustesgenerales.class);
                            startActivity(refresh);
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                            finish();
                        }
                    });

                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                        Toast.makeText(Ajustesgenerales.this, "Hubo un problema al actualizar el maximo acumulado", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
        mRequestQueue.add(mStringRequest);
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
