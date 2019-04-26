package com.ingenieriasantafe.leandro.aridosmobile;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class Report_produccionStep3 extends AppCompatActivity {


    CheckBox checkBotiquin, checkExtintor, checkAlarmaRetroceso, checkBaliza, checkRevisionTecnica, checkSeguroObligatorio, checkPermisoCirculacion;
    String botiquin, extintor, alarmaretroceso, baliza, revisiontecnica, seguroobligatorio, permisocirculacion;
    Button siguiuente;


    private static final String TAG = Report_entrada.class.getName();
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    Button mScan, mPrint, mDisc;
    BluetoothAdapter mBluetoothAdapter;
    private UUID applicationUUID = UUID
            .fromString("00001101-0000-1000-8000-00805F9B34FB");
    private ProgressDialog mBluetoothConnectProgressDialog;
    private BluetoothSocket mBluetoothSocket;
    BluetoothDevice mBluetoothDevice;

    DatabaseHelper myDB= new DatabaseHelper(this);


    String patente,horasmaquina,nivelaceitemotor,nivelaceitehidraulico,nivelaceitetransmision,sistemaelectrico,otrasrevisiones,combustible,operador;
    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reportproduccionstep3);
        checkBotiquin = (CheckBox) findViewById(R.id.checkBotiquin);
        checkExtintor = (CheckBox)findViewById(R.id.checkExtintor);
        checkAlarmaRetroceso = (CheckBox)findViewById(R.id.checkAlarmaRetroceso);
        checkBaliza = (CheckBox) findViewById(R.id.checkBaliza);
        checkRevisionTecnica = (CheckBox) findViewById(R.id.checkRevisionTecnica);
        checkSeguroObligatorio = (CheckBox)findViewById(R.id.checkSeguroObligatorio);
        checkPermisoCirculacion = (CheckBox)findViewById(R.id.checkPermisoCirculacion);
        siguiuente = (Button)findViewById(R.id.btnsiguiente3);

        Bundle parametros = this.getIntent().getExtras();
        patente = parametros.getString("Patente");
        horasmaquina = parametros.getString("HorasMaquina");
        nivelaceitemotor = parametros.getString("NivelAceiteMotor");
        nivelaceitehidraulico = parametros.getString("NivelAceiteHidraulico");
        nivelaceitetransmision = parametros.getString("NivelAceiteTransmision");
        sistemaelectrico = parametros.getString("SistemaElectrico");
        otrasrevisiones = parametros.getString("OtrasRevisiones");
        combustible = parametros.getString("Combustible");
        operador = parametros.getString("Operador");


        siguiuente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (checkBotiquin.isChecked()==true){
                    botiquin = "SI";

                }else{
                    botiquin = "NO";
                }
                if(checkExtintor.isChecked()==true){
                    extintor = "SI";

                }else{
                    extintor = "NO";

                }
                if (checkAlarmaRetroceso.isChecked()==true){
                    alarmaretroceso = "SI";

                }else{
                    alarmaretroceso = "NO";
                }
                if (checkBaliza.isChecked()==true){
                    baliza = "SI";

                }else{
                    baliza = "NO";

                }
                if (checkRevisionTecnica.isChecked()==true){
                    revisiontecnica = "SI";

                }else{
                    revisiontecnica = "NO";

                }
                if (checkSeguroObligatorio.isChecked()==true){
                    seguroobligatorio = "SI";

                }else{
                    seguroobligatorio = "NO";

                }
                if (checkPermisoCirculacion.isChecked()==true){
                    permisocirculacion = "SI";

                }else{
                    permisocirculacion = "NO";
                }

                Intent intent = new Intent(Report_produccionStep3.this, Report_produccionStep4.class);
                intent.putExtra("NivelAceiteMotor",nivelaceitemotor);
                intent.putExtra("NivelAceiteHidraulico", nivelaceitehidraulico);
                intent.putExtra("NivelAceiteTransmision",nivelaceitetransmision);
                intent.putExtra("SistemaElectrico",sistemaelectrico);
                intent.putExtra("OtrasRevisiones",otrasrevisiones);
                intent.putExtra("Patente",patente);
                intent.putExtra("HorasMaquina",horasmaquina);
                intent.putExtra("Botiquin",botiquin);
                intent.putExtra("Extintor", extintor);
                intent.putExtra("AlarmaRetroceso",alarmaretroceso);
                intent.putExtra("Baliza",baliza);
                intent.putExtra("RevisionTecnica",revisiontecnica);
                intent.putExtra("SeguroObligatorio",seguroobligatorio);
                intent.putExtra("PermisoCirculacion",permisocirculacion);
                intent.putExtra("Combustible",combustible);
                intent.putExtra("Operador",operador);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                finish();


            }
        });





    }


}
