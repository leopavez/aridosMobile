package com.example.leandro.aridosmobile;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import jp.wasabeef.blurry.Blurry;

public class MainActivity extends AppCompatActivity {


TextInputEditText usuario;
TextInputEditText password;
Button ingresar;

TextInputLayout lusers;
TextInputLayout lpass;

NotificationCompat.Builder notificacion;
private static final int idUnica = 1;

private static final String TAG = MainActivity.class.getName();

private RequestQueue mRequestQueue;
private StringRequest mStringRequest;
DatabaseHelper myDB;

private InputValidation inputValidation;


public static final String APIUsuarios = "http://192.168.1.189:8000/api/aridos/users";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lusers = (TextInputLayout) findViewById(R.id.textinputLayoutusers);
        lpass = (TextInputLayout) findViewById(R.id.textInputLayoutpass);
        this.DescargaUsuarios();
        usuario = (TextInputEditText) findViewById(R.id.txtusuario);
        password = (TextInputEditText) findViewById(R.id.txtpassword);
        ingresar = (Button) findViewById(R.id.btninicio);

        inputValidation = new InputValidation(this);

        myDB = new DatabaseHelper(this);


        notificacion = new NotificationCompat.Builder(this);
        notificacion.setAutoCancel(true);

        ingresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                guardarCredenciales();
                verifyFromSQLite();
            }
        });

    }

    private void verifyFromSQLite(){
        if (!inputValidation.isInputEditTextFilled((TextInputEditText) usuario, lusers, getString(R.string.error_message_error))){
            return;
        }
        if (!inputValidation.isInputEditTextFilled((TextInputEditText) password, lpass, getString(R.string.error_message_error))){
            return;
        }
        if (myDB.LoginUsers(usuario.getText().toString().trim(), password.getText().toString().trim())){

            Intent intent = new Intent(MainActivity.this, Menu.class);
            startActivity(intent);
            finish();

        } else{
            Toast.makeText(this, "Usuario o password incorrectos", Toast.LENGTH_SHORT).show();
        }
    }

    private void DescargaUsuarios(){
        mRequestQueue = Volley.newRequestQueue(this);
        mStringRequest = new StringRequest(Request.Method.GET, APIUsuarios, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    String json;
                    Log.i(TAG, "Response: " + response.toString());

                    json = response.toString();
                    JSONArray jsonArray = null;
                    jsonArray = new JSONArray(json);
                    Usuarios users = new Usuarios();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        users.log_id = jsonObject.getString("id");
                        users.nombre = jsonObject.getString("nombre");
                        users.apellido = jsonObject.getString("apellido");
                        users.username = jsonObject.getString("username");
                        users.password = jsonObject.getString("password");
                        users.estado = jsonObject.getString("estado");

                        SQLiteDatabase db = myDB.getWritableDatabase();

                        Cursor cursor = db.rawQuery("SELECT log_id, estado FROM usuarios WHERE log_id ='"+users.log_id+"'",null);

                        if (cursor.getCount() <=0){
                            //NO SE ENCUENTRA EL USUARIO Y SE REGISTRA
                            myDB.RegistroUsuarios(users.log_id,users.nombre,users.apellido,users.username.toLowerCase(),users.password,users.estado);

                            Log.i(TAG,"USUARIO AGREGADO");
                        }else{
                            if (cursor.moveToFirst() == true){
                                String estadoUsuario = cursor.getString(1);
                                if (users.estado.toString().equals(estadoUsuario)){

                                }else{
                                    db.execSQL("UPDATE usuarios SET estado='"+users.estado.toString()+"' WHERE id='"+users.log_id+"'");
                                }
                            }
                        }

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG,"Error de conexion; "+error.toString());
            }
        });
        mRequestQueue.add(mStringRequest);
        }


        private void guardarCredenciales(){

            SharedPreferences preferences = getSharedPreferences("credenciales", Context.MODE_PRIVATE);
            String username = usuario.getText().toString();


            SQLiteDatabase db = myDB.getWritableDatabase();

            Cursor cursor = db.rawQuery("SELECT log_id, nombre, apellido FROM usuarios WHERE username ='"+username+"'",null);

            if (cursor.moveToFirst() == true){

                String log_idusers = cursor.getString(0);
                String nameusers = cursor.getString(1);
                String lastnameusers = cursor.getString(2);

                SharedPreferences.Editor editor =preferences.edit();
                editor.putString("LOG_ID", log_idusers);
                editor.putString("NAME", nameusers);
                editor.putString("LASTNAME",lastnameusers);

                editor.commit();
            }

        }

}
