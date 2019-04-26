package com.ingenieriasantafe.leandro.aridosmobile;

public class Operadores {


    String id;
    String id_web;
    String nombre;


    public Operadores(){

    }

    public Operadores(String id, String id_web, String nombre) {
        this.id = id;
        this.id_web = id_web;
        this.nombre = nombre;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId_web() {
        return id_web;
    }

    public void setId_web(String id_web) {
        this.id_web = id_web;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}

