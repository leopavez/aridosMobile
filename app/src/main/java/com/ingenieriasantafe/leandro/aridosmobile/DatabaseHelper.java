package com.ingenieriasantafe.leandro.aridosmobile;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

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


    //REGISTROS - ACOPIO

    public static final String ID_REGISTRO_ACOPIO = "id";
    public static final String PATENTE_REGISTRO_ACOPIO = "patente";
    public static final String M3_REGISTRO_ACOPIO = "m3";
    public static final String PLANTA_REGISTRO_ACOPIO = "planta";
    public static final String CHOFER_REGISTRO_ACOPIO = "chofer";
    public static final String USERNAME_REGISTRO_ACOPIO ="username";
    public static final String PROCEDENCIA_REGISTRO_ACOPIO = "procedencia";
    public static final String FECHA_REGISTRO_ACOPIO = "fecha";
    public static final String HORA_REGISTRO_ACOPIO = "hora";
    public static final String ESTADO_REGISTRO_ACOPIO = "estado";

    //REGISTROS - PRODUCCION
    public static final String ID_REGISTRO_PRODUCCION= "id";
    public static final String PATENTE_REGISTRO_PRODUCCION = "patente";
    public static final String HM_REGISTRO_PRODUCCION = "horasmaquina";
    public static final String COMB_REGISTRO_PRODUCCION = "combustible";
    public static final String OPERADOR_REGISTRO_PRODUCCION ="operador";
    public static final String NAM_REGISTRO_PRODUCCION = "nam";
    public static final String NAH_REGISTRO_PRODUCCION = "nah";
    public static final String NAT_REGISTRO_PRODUCCION = "nat";
    public static final String SE_REGISTRO_PRODUCCION = "se";
    public static final String OR_REGISTRO_PRODUCCION = "otras";
    public static final String BOTIQUIN_REGISTRO_PRODUCCION = "botiquin";
    public static final String EXTINTOR_REGISTRO_PRODUCCION = "extintor";
    public static final String AR_REGISTRO_PRODUCCION = "ar";
    public static final String BALIZA_REGISTRO_PRODUCCION = "baliza";
    public static final String RT_REGISTRO_PRODUCCION = "rt";
    public static final String SO_REGISTRO_PRODUCCION = "so";
    public static final String PC_REGISTRO_PRODUCCION = "pc";
    public static final String FECHA_REGISTRO_PRODUCCION = "fecha";
    public static final String HORA_REGISTRO_PRODUCCION = "hora";
    public static final String USERNAME_REGISTRO_PRODUCCION = "username";
    public static final String PLANTA_REGISTRO_PRODUCCION = "planta";
    public static final String ESTADO_REGISTRO_PRODUCCION = "estado";

    //OPERADORES
    public static final String ID_OPERADORES = "id";
    public static final String ID_OPERADOR_WEB = "id_web";
    public static final String NOMBRE_OPERADOR = "nombre";


    //PRODUCCION - PRODUCCION PLANTA REGISTROS
    public static final String ID_PPLANTA = "id";
    public static final String TIPO_MATERIAL = "tipomaterial";
    public static final String M3_PRODUCCION_PLANTA = "m3";
    public static final String PLANTA_P_PLANTA = "planta";
    public static final String PROCEDENCIA_P_PLANTA ="procedencia";
    public static final String USERNAME_P_PLANTA ="username";
    public static final String FECHA_P_PLANTA = "fecha";
    public static final String HORA_P_PLANTA = "hora";
    public static final String ESTADO_P_PLANTA = "estado";

    //SALIDA - REGISTROS DE SALIDA
    public static final String ID_SALIDA ="id";
    public static final String PATENTE_REGISTRO_SALIDA = "patente";
    public static final String M3_REGISTRO_SALIDA = "m3";
    public static final String TIPOMATERIAL_REGISTRO_SALIDA = "tipomaterial";
    public static final String PLANTA_REGISTRO_SALIDA = "planta";
    public static final String CHOFER_REGISTRO_SALIDA = "chofer";
    public static final String USERNAME_REGISTRO_SALIDA ="username";
    public static final String PROCEDENCIA_REGISTRO_SALIDA = "procedencia";
    public static final String FECHA_REGISTRO_SALIDA = "fecha";
    public static final String HORA_REGISTRO_SALIDA = "hora";
    public static final String ESTADO_REGISTRO_SALIDA = "estado";
    public static final String DESTINO_REGISTROS_SALIDA ="destino";



    final String CREAR_TABLA_USUARIOS = "CREATE TABLE usuarios (id INTEGER PRIMARY KEY AUTOINCREMENT,log_id TEXT unique, nombre TEXT, apellido TEXT, username TEXT, password TEXT, estado TEXT)";
    final String CREAR_TABLA_LISTADO_PLANTAS = "CREATE TABLE plantas_list (id TEXT unique, nombre TEXT, ubicacion TEXT)";
    final String CREAR_TABLA_VEHICULOS = "CREATE TABLE vehiculos (id TEXT unique, patente TEXT unique, tipo TEXT, propietario TEXT, marca TEXT, m3 TEXT)";
    final String CREAR_TABLA_REGISTROS_ACOPIO = "CREATE TABLE registros_acopio (id INTEGER PRIMARY KEY AUTOINCREMENT, patente TEXT, m3 TEXT, planta TEXT, chofer TEXT,username TEXT, fecha TEXT, hora TEXT, estado TEXT, procedencia TEXT)";
    final String CREAR_TABLA_REGISTROS_PRODUCCION = "CREATE TABLE registros_produccion (id INTEGER PRIMARY KEY AUTOINCREMENT, patente TEXT, horasmaquina TEXT, nam TEXT,nah TEXT," +
            "nat TEXT, se TEXT, otras TEXT, botiquin TEXT, extintor TEXT, ar TEXT, baliza TEXT, rt TEXT, so TEXT, pc TEXT, fecha TEXT, hora TEXT, username TEXT, planta TEXT, combustible TEXT," +
            "operador TEXT, estado TEXT)";
    final String CREAR_TABLA_OPERADORES = "CREATE TABLE operadores(id INTEGER PRIMARY KEY AUTOINCREMENT, id_web TEXT,  nombre TEXT)";
    final String CREAR_TABLA_PRODUCCION_PLANTA = "CREATE TABLE prod_planta(id INTEGER PRIMARY KEY AUTOINCREMENT, tipomaterial TEXT, m3 TEXT, planta TEXT, procedencia TEXT, username TEXT,fecha TEXT, hora TEXT, estado TEXT)";
    final String CREAR_TABLA_REGISTROS_SALIDA = "CREATE TABLE registros_salida (id INTEGER PRIMARY KEY AUTOINCREMENT, patente TEXT, m3 TEXT, tipomaterial TEXT, planta TEXT, chofer TEXT,username TEXT, fecha TEXT, hora TEXT, estado TEXT, procedencia TEXT, destino TEXT)";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null,8);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL(CREAR_TABLA_USUARIOS);
        db.execSQL(CREAR_TABLA_LISTADO_PLANTAS);
        db.execSQL(CREAR_TABLA_VEHICULOS);
        db.execSQL(CREAR_TABLA_REGISTROS_ACOPIO);
        db.execSQL(CREAR_TABLA_REGISTROS_PRODUCCION);
        db.execSQL(CREAR_TABLA_OPERADORES);
        db.execSQL(CREAR_TABLA_PRODUCCION_PLANTA);
        db.execSQL(CREAR_TABLA_REGISTROS_SALIDA);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS usuarios");
        db.execSQL("DROP TABLE IF EXISTS planta");
        db.execSQL("DROP TABLE IF EXISTS plantas_list");
        db.execSQL("DROP TABLE IF EXISTS vehiculos");
        db.execSQL("DROP TABLE IF EXISTS registros_acopio");
        db.execSQL("DROP TABLE IF EXISTS registros_produccion");
        db.execSQL("DROP TABLE IF EXISTS operadores");
        db.execSQL("DROP TABLE IF EXISTS prod_planta");
        db.execSQL("DROP TABLE IF EXISTS registros_salida");

        db.execSQL(CREAR_TABLA_USUARIOS);
        db.execSQL(CREAR_TABLA_LISTADO_PLANTAS);
        db.execSQL(CREAR_TABLA_VEHICULOS);
        db.execSQL(CREAR_TABLA_REGISTROS_ACOPIO);
        db.execSQL(CREAR_TABLA_REGISTROS_PRODUCCION);
        db.execSQL(CREAR_TABLA_OPERADORES);
        db.execSQL(CREAR_TABLA_PRODUCCION_PLANTA);
        db.execSQL(CREAR_TABLA_REGISTROS_SALIDA);
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

    public boolean RegistroOperadores(String id_web, String nombre){

        try {

            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues contentValues = new ContentValues();


            contentValues.put(ID_OPERADOR_WEB, id_web);
            contentValues.put(NOMBRE_OPERADOR, nombre );

            db.insert("operadores",null,contentValues);
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

    public boolean registroProduccionPlanta(String tipomaterial, String m3, String planta, String procedencia, String username, String fecha, String hora, String estado){

        try {

            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues contentValues = new ContentValues();

            contentValues.put(TIPO_MATERIAL, tipomaterial );
            contentValues.put(M3_PRODUCCION_PLANTA,m3);
            contentValues.put(PLANTA_P_PLANTA,planta);
            contentValues.put(PROCEDENCIA_P_PLANTA, procedencia);
            contentValues.put(USERNAME_P_PLANTA,username);
            contentValues.put(FECHA_P_PLANTA,fecha);
            contentValues.put(HORA_P_PLANTA,hora);
            contentValues.put(ESTADO_P_PLANTA, estado);

            int resultado = (int) db.insert("prod_planta",null,contentValues);
            db.close();
            if (resultado == -1){
                return false;
            }else{
                return true;
            }
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public boolean Registro_acopio(String patente, String m3, String planta, String chofer, String username, String fecha, String hora, String estado, String procedencia){

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
            contentValues.put(PROCEDENCIA_REGISTRO_ACOPIO, procedencia);

            int resultado = (int) db.insert("registros_acopio",null,contentValues);
            db.close();
            if (resultado == -1){
                return false;
            }else{
                return true;
            }
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public boolean RegistroSalida(String patente, String m3, String tipomaterial, String planta, String chofer, String username, String fecha, String hora, String estado, String procedencia, String destino){

        try {

            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues contentValues = new ContentValues();

            contentValues.put(PATENTE_REGISTRO_SALIDA, patente );
            contentValues.put(M3_REGISTRO_SALIDA,m3);
            contentValues.put(TIPOMATERIAL_REGISTRO_SALIDA,tipomaterial);
            contentValues.put(PLANTA_REGISTRO_SALIDA,planta);
            contentValues.put(CHOFER_REGISTRO_SALIDA,chofer);
            contentValues.put(USERNAME_REGISTRO_SALIDA,username);
            contentValues.put(FECHA_REGISTRO_SALIDA,fecha);
            contentValues.put(HORA_REGISTRO_SALIDA,hora);
            contentValues.put(ESTADO_REGISTRO_SALIDA, estado);
            contentValues.put(PROCEDENCIA_REGISTRO_SALIDA, procedencia);
            contentValues.put(DESTINO_REGISTROS_SALIDA, destino);

            int resultado = (int) db.insert("registros_salida",null,contentValues);
            db.close();
            if (resultado == -1){
                return false;
            }else{
                return true;
            }
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public boolean Registro_Produccion(String patente, String horasmaquina, String combustible, String operador, String nam, String nah, String nat, String se,
                                       String otras, String botiquin, String extintor, String ar, String baliza, String rt, String so, String pc,
                                       String fecha, String hora, String username, String planta, String estado){

        try {

            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues contentValues = new ContentValues();

            contentValues.put(PATENTE_REGISTRO_PRODUCCION, patente );
            contentValues.put(HM_REGISTRO_PRODUCCION,horasmaquina);
            contentValues.put(COMB_REGISTRO_PRODUCCION, combustible);
            contentValues.put(OPERADOR_REGISTRO_PRODUCCION,operador);
            contentValues.put(NAM_REGISTRO_PRODUCCION,nam);
            contentValues.put(NAH_REGISTRO_PRODUCCION,nah);
            contentValues.put(NAT_REGISTRO_PRODUCCION,nat);
            contentValues.put(SE_REGISTRO_PRODUCCION, se);
            contentValues.put(OR_REGISTRO_PRODUCCION,otras);
            contentValues.put(BOTIQUIN_REGISTRO_PRODUCCION, botiquin);
            contentValues.put(EXTINTOR_REGISTRO_PRODUCCION,extintor);
            contentValues.put(AR_REGISTRO_PRODUCCION,ar);
            contentValues.put(BALIZA_REGISTRO_PRODUCCION, baliza);
            contentValues.put(RT_REGISTRO_PRODUCCION,rt);
            contentValues.put(SO_REGISTRO_PRODUCCION,so);
            contentValues.put(PC_REGISTRO_PRODUCCION,pc);
            contentValues.put(FECHA_REGISTRO_PRODUCCION, fecha);
            contentValues.put(HORA_REGISTRO_PRODUCCION,hora);
            contentValues.put(USERNAME_REGISTRO_PRODUCCION,username);
            contentValues.put(PLANTA_REGISTRO_PRODUCCION,planta);
            contentValues.put(ESTADO_REGISTRO_PRODUCCION,estado);

            int resultado = (int) db.insert("registros_produccion",null,contentValues);
            db.close();
            if (resultado == -1){
                return false;
            }else{
                return true;
            }
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public boolean ExisteVehiculo(String id){
        boolean VehiculoExiste = false;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT patente FROM vehiculos WHERE id = '"+id+"'",null);        try {

            if (cursor.getCount()<=0){

                VehiculoExiste = false;
            }else{

                VehiculoExiste = true;
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return VehiculoExiste;
    }

    public boolean ExisteOperador(String id){
        boolean OperadorExiste = false;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            Cursor cursor = db.rawQuery("SELECT id FROM operadores WHERE id = '"+id+"'",null);

            if (cursor.getCount()<=0){

                OperadorExiste = false;
            }else{

                OperadorExiste = true;
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        return OperadorExiste;
    }

    public boolean ExistePlanta(String id){
        boolean PlantaExiste = false;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            Cursor cursor = db.rawQuery("SELECT nombre FROM plantas_list WHERE id = '"+id+"'",null);

            if (cursor.getCount()<=0){

                PlantaExiste = false;
            }else{

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


    public List<Operadores> searchOperador (String keyword){
        List<Operadores> vh = null;
        try {
            SQLiteDatabase db = getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT nombre FROM operadores where nombre like ?"
                    , new String[] {"%" + keyword + "%"});
            if (cursor.moveToFirst()){
                vh = new ArrayList<Operadores>();
                do {
                    Operadores op = new Operadores();
                    op.setNombre(cursor.getString(0));
                    vh.add(op);
                }while (cursor.moveToNext());
            }
        }catch (Exception e){
            vh = null;
        }
        return vh;
    }
}
