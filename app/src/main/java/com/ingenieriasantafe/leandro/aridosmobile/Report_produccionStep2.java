package com.ingenieriasantafe.leandro.aridosmobile;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

public class Report_produccionStep2 extends AppCompatActivity {

    Button siguiente;

    CheckBox checkAceiteMotor, checkAceiteHidraulico, checkAceiteTransmision, checkSistemaElectrico, checkOtrasRevisiones;
    String checkaceitemotor, checkaceitehidraulico, checkaceitetransmision, checksistemaelectrico, checkotrasrevisiones;
    String patente, horasmaquina, combustible,operador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reportproduccionstep2);
        checkAceiteMotor = (CheckBox) findViewById(R.id.checkAceiteMotor);
        checkAceiteHidraulico = (CheckBox) findViewById(R.id.checkAceiteHidraulico);
        checkAceiteTransmision = (CheckBox) findViewById(R.id.checkAceiteTransmision);
        checkSistemaElectrico = (CheckBox) findViewById(R.id.checkSistemaElectrico);
        checkOtrasRevisiones = (CheckBox) findViewById(R.id.checkOtrasRevisiones);

        Bundle parametros = this.getIntent().getExtras();
        patente = parametros.getString("patente");
        horasmaquina = parametros.getString("horasmaquina");
        combustible = parametros.getString("combustible");
        operador = parametros.getString("operador");


        siguiente = (Button)findViewById(R.id.btnsiguiente2);
        siguiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (checkAceiteMotor.isChecked()==true){
                  checkaceitemotor = "SI";

                }else{
                    checkaceitemotor = "NO";
                }
                if(checkAceiteHidraulico.isChecked()==true){
                    checkaceitehidraulico = "SI";

                }else{
                    checkaceitehidraulico = "NO";

                }
                if (checkAceiteTransmision.isChecked()==true){
                    checkaceitetransmision = "SI";

                }else{
                    checkaceitetransmision = "NO";
                }
                if (checkSistemaElectrico.isChecked()==true){
                    checksistemaelectrico = "SI";

                }else{
                    checksistemaelectrico = "NO";

                }
                if (checkOtrasRevisiones.isChecked()==true){
                    checkotrasrevisiones = "SI";

                }else{
                    checkotrasrevisiones = "NO";

                }

                Intent intent = new Intent(Report_produccionStep2.this, Report_produccionStep3.class);
                intent.putExtra("NivelAceiteMotor",checkaceitemotor);
                intent.putExtra("NivelAceiteHidraulico", checkaceitehidraulico);
                intent.putExtra("NivelAceiteTransmision",checkaceitetransmision);
                intent.putExtra("SistemaElectrico",checksistemaelectrico);
                intent.putExtra("OtrasRevisiones",checkotrasrevisiones);
                intent.putExtra("Patente",patente);
                intent.putExtra("HorasMaquina",horasmaquina);
                intent.putExtra("Combustible",combustible);
                intent.putExtra("Operador",operador);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                finish();

            }
        });
    }
}
