package com.example.leandro.aridosmobile;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "aridos.db";

    //USUARIOS
    public static final int ID = 0;
    public static final String LOG_ID = "log_id";
    public static final String NOMBRE = "nombre";
    public static final String APELLIDO = "apellido";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String ESTADO = "estado";


    final String CREAR_TABLA_USUARIOS = "CREATE TABLE usuarios (id INTEGER PRIMARY KEY AUTOINCREMENT";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL(CREAR_TABLA_USUARIOS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS usuarios");

        db.execSQL(CREAR_TABLA_USUARIOS);
    }

    public boolean RegistroUsuarios(String log_id, String nombre, String apellido, String username, String password, String estado){

        try {

            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues contentValues = new ContentValues();

            contentValues.put(LOG_ID, log_id );
            contentValues.put(NOMBRE,nombre);
            contentValues.put(APELLIDO,apellido);
            contentValues.put(USERNAME, username);
            contentValues.put(PASSWORD,password);
            contentValues.put(ESTADO, estado);

            db.insert("usuarios",null,contentValues);
            db.close();
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }


}
