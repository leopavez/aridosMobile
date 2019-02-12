package com.example.leandro.aridosmobile;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.annotation.RequiresApi;
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


EditText usuario;
EditText password;
Button ingresar;

NotificationCompat.Builder notificacion;
private static final int idUnica = 1;

private static final String TAG = MainActivity.class.getName();

private RequestQueue mRequestQueue;
private StringRequest mStringRequest;
DatabaseHelper myDB;



public static final String APIUsuarios = "http://http://002ec26b.ngrok.io/api/aridos/users";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        usuario = (EditText) findViewById(R.id.txtusuario);
        password = (EditText)findViewById(R.id.txtpassword);
        ingresar = (Button)findViewById(R.id.btninicio);

        myDB = new DatabaseHelper(this);

        notificacion = new NotificationCompat.Builder(this);
        notificacion.setAutoCancel(true);

        DescargaUsuarios();


        ingresar.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
              if (usuario.getText().toString().equals("") || password.getText().toString().equals("")){
                  Toast.makeText(getApplicationContext(),"Campos vacios",Toast.LENGTH_SHORT).show();
              }else{
                 /** NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(MainActivity.this, "0")
                          .setSmallIcon(R.mipmap.ic_launcher)
                          .setContentTitle("Santafe Mobile")
                          .setContentText("Tienes nuevas patentes asignadas!")
                          .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                          // Set the intent that will fire when the user taps the notification
                          .setAutoCancel(true);

                  initChannels(MainActivity.this);
                  Intent nuevoform= new Intent(MainActivity.this, Menu.class);;
                  startActivity(nuevoform);

                  NotificationManagerCompat notificationManager = NotificationManagerCompat.from(MainActivity.this);

                  // notificationId is a unique int for each notification that you must define
                  notificationManager.notify(idUnica, mBuilder.build())
                  **/

                  Intent nuevoform= new Intent(MainActivity.this, Menu.class);;
                  startActivity(nuevoform);
                  }
            }
        });
    }


    /**
    public void initChannels(Context context) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, "0")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Titulo")
                .setContentText("Contenido de la notificación")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        createNotificationChannel();
    }

     **/
    /**
        private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.app_name);
            String description = getString(R.string.bottom_sheet_behavior);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("0", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

     **/



    public void DescargaUsuarios(){
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
                            myDB.RegistroUsuarios(users.log_id,users.nombre,users.apellido,users.username,users.password,users.estado);
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
}
