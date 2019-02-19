package com.example.leandro.aridosmobile;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "aridos.db";
    public static final String TABLA_USUARIOS = "usuarios";
    public static final String TABLA_PLANTA_APP = "planta";
    public static final String TABLA_LIST_PLANTAS = "plantas_list";
    public static final String TABLA_VEHICULOS = "vehiculos";

    //USUARIOS
    public static final int ID = 0;
    public static final String LOG_ID = "log_id";
    public static final String NOMBRE = "nombre";
    public static final String APELLIDO = "apellido";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String ESTADO = "estado";

    //PLANTA APLICACION
    public static final String ID_PLANTA = "id";
    public static final String NOMBRE_PLANTA = "nombre";
    public static final String UBICACION = "ubicacion";

    //PLANTAS
    public static final String ID_LOG_PLANTAS = "id";
    public static final String NOMBRE_LOG_PLANTA = "nombre";
    public static final String UBICACION_PLANTA = "ubicacion";

    //VEHICULOS
    public static final String ID_VEHICULOS = "id";
    public static final String PATENTE = "patente";
    public static final String TIPO = "tipo";
    public static final String PROPIETARIO = "propietario";
    public static final String MARCA = "marca";
    public static final String M3 = "m3";

    //IMPRESORA - PARAMETROS
    public static final String ID_IMPRESORA = "id";
    public static final String MASCARA = "mascara";

    //REGISTROS - ACOPIO

    public static final String ID_REGISTRO_ACOPIO = "id";
    public static final String PATENTE_REGISTRO_ACOPIO = "patente";
    public static final String M3_REGISTRO_ACOPIO = "m3";
    public static final String PLANTA_REGISTRO_ACOPIO = "planta";
    public static final String CHOFER_REGISTRO_ACOPIO = "chofer";
    public static final String USERNAME_REGISTRO_ACOPIO ="username";
    public static final String FECHA_REGISTRO_ACOPIO = "fecha";
    public static final String HORA_REGISTRO_ACOPIO = "hora";
    public static final String ESTADO_REGISTRO_ACOPIO = "estado";




    final String CREAR_TABLA_USUARIOS = "CREATE TABLE usuarios (id INTEGER PRIMARY KEY AUTOINCREMENT,log_id TEXT unique, nombre TEXT, apellido TEXT, username TEXT, password TEXT, estado TEXT)";
    final String CREAR_TABLA_PARAMETROS_PLANTA = "CREATE TABLE planta (id TEXT, nombre TEXT, UBICACION text)";
    final String CREAR_TABLA_PARAMETROS_IMPRESORA = "CREATE TABLE impresora (id INTEGER PRIMARY KEY AUTOINCREMENT, mascara TEXT)";
    final String CREAR_TABLA_LISTADO_PLANTAS = "CREATE TABLE plantas_list (id TEXT unique, nombre TEXT, ubicacion TEXT)";
    final String CREAR_TABLA_VEHICULOS = "CREATE TABLE vehiculos (id TEXT unique, patente TEXT unique, tipo TEXT, propietario TEXT, marca TEXT, m3 TEXT)";
    final String CREAR_TABLA_REGISTROS_ACOPIO = "CREATE TABLE registros_acopio (id INTEGER PRIMARY KEY AUTOINCREMENT, patente TEXT, m3 TEXT, planta TEXT, chofer TEXT, username TEXT, fecha TEXT, hora TEXT, estado TEXT)";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL(CREAR_TABLA_USUARIOS);
        db.execSQL(CREAR_TABLA_PARAMETROS_PLANTA);
        db.execSQL(CREAR_TABLA_LISTADO_PLANTAS);
        db.execSQL(CREAR_TABLA_VEHICULOS);
        db.execSQL(CREAR_TABLA_PARAMETROS_IMPRESORA);
        db.execSQL(CREAR_TABLA_REGISTROS_ACOPIO);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS usuarios");
        db.execSQL("DROP TABLE IF EXISTS planta");
        db.execSQL("DROP TABLE IF EXISTS plantas_list");
        db.execSQL("DROP TABLE IF EXISTS vehiculos");
        db.execSQL("DROP TABLE IF EXISTS impresora");
        db.execSQL("DROP TABLE IF EXISTS registros_acopio");

        db.execSQL(CREAR_TABLA_USUARIOS);
        db.execSQL(CREAR_TABLA_PARAMETROS_PLANTA);
        db.execSQL(CREAR_TABLA_LISTADO_PLANTAS);
        db.execSQL(CREAR_TABLA_VEHICULOS);
        db.execSQL(CREAR_TABLA_PARAMETROS_IMPRESORA);
        db.execSQL(CREAR_TABLA_REGISTROS_ACOPIO);
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


    public boolean LoginUsers(String username, String password){

        String[] columns = {
                LOG_ID
        };
        SQLiteDatabase db = this.getReadableDatabase();

        String selection = USERNAME + " = ? " + " AND " + PASSWORD + " = ? ";

        String[] selectionArgs = {username, password};

        Cursor cursor = db.query(TABLA_USUARIOS,
                columns,
                selection,
                selectionArgs,
                null,
                null,
                null);

        int cursorCount = cursor.getCount();

        cursor.close();
        db.close();
        if (cursorCount > 0 ){
            return true;
        }
        return false;
    }

    public boolean RegistrarListadoPlantas(String id, String nombre, String ubicacion){

        try {

            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues contentValues = new ContentValues();

            contentValues.put(ID_LOG_PLANTAS, id );
            contentValues.put(NOMBRE_LOG_PLANTA,nombre);
            contentValues.put(UBICACION_PLANTA,ubicacion);

            db.insert("plantas_list",null,contentValues);
            db.close();
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public boolean RegistroVehiculos(String id, String patente, String tipo, String marca, String propietario, String m3){

        try {

            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues contentValues = new ContentValues();

            contentValues.put(ID_VEHICULOS, id );
            contentValues.put(PATENTE,patente);
            contentValues.put(TIPO,tipo);
            contentValues.put(MARCA,marca);
            contentValues.put(PROPIETARIO,propietario);
            contentValues.put(M3,m3);

            db.insert("vehiculos",null,contentValues);
            db.close();
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public boolean Registro_acopio(String patente, String m3, String planta, String chofer, String username, String fecha, String hora, String estado){

        try {

            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues contentValues = new ContentValues();

            contentValues.put(PATENTE_REGISTRO_ACOPIO, patente );
            contentValues.put(M3_REGISTRO_ACOPIO,m3);
            contentValues.put(PLANTA_REGISTRO_ACOPIO,planta);
            contentValues.put(CHOFER_REGISTRO_ACOPIO,chofer);
            contentValues.put(USERNAME_REGISTRO_ACOPIO,username);
            contentValues.put(FECHA_REGISTRO_ACOPIO,fecha);
            contentValues.put(HORA_REGISTRO_ACOPIO,hora);
            contentValues.put(ESTADO_REGISTRO_ACOPIO, estado);

            db.insert("registros_acopio",null,contentValues);
            db.close();
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public boolean ExisteVehiculo(String id){
        boolean VehiculoExiste = false;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            Cursor cursor = db.rawQuery("SELECT patente FROM vehiculos WHERE id = '"+id+"'",null);

            if (cursor.getCount()<=0){

                db.close();
                VehiculoExiste = false;
            }else{

                db.close();
                VehiculoExiste = true;
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        return VehiculoExiste;
    }

    public boolean ExistePlanta(String id){
        boolean PlantaExiste = false;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            Cursor cursor = db.rawQuery("SELECT nombre FROM plantas_list WHERE id = '"+id+"'",null);

            if (cursor.getCount()<=0){

                db.close();
                PlantaExiste = false;
            }else{

                db.close();
                PlantaExiste = true;
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        return PlantaExiste;
    }


    public List<Vehiculos> search (String keyword){
        List<Vehiculos> vh = null;
        try {
            SQLiteDatabase db = getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT patente, m3 FROM vehiculos where patente like ?"
            , new String[] {"%" + keyword + "%"});
            if (cursor.moveToFirst()){
                vh = new ArrayList<Vehiculos>();
                do {
                    Vehiculos vehiculos = new Vehiculos();
                    vehiculos.setPatente(cursor.getString(0));
                    vehiculos.setM3(cursor.getString(1));
                    vh.add(vehiculos);
                }while (cursor.moveToNext());
            }
        }catch (Exception e){
            vh = null;
        }
        return vh;
    }

}
