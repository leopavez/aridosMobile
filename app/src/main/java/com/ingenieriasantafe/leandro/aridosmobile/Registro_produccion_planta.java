package com.ingenieriasantafe.leandro.aridosmobile;

public class Registro_produccion_planta {

    int id = 0;
    String tipomaterial;
    String m3;
    String procedencia;
    String planta;
    String username;
    String fecha;
    String hora;
    String estado;

    public Registro_produccion_planta() {

    }

    public Registro_produccion_planta(int id, String tipomaterial, String m3, String procedencia, String planta, String username, String fecha, String hora, String estado) {
        this.id = id;
        this.tipomaterial = tipomaterial;
        this.m3 = m3;
        this.procedencia = procedencia;
        this.planta = planta;
        this.username = username;
        this.fecha = fecha;
        this.hora = hora;
        this.estado = estado;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTipomaterial() {
        return tipomaterial;
    }

    public void setTipomaterial(String tipomaterial) {
        this.tipomaterial = tipomaterial;
    }

    public String getM3() {
        return m3;
    }

    public void setM3(String m3) {
        this.m3 = m3;
    }

    public String getProcedencia() {
        return procedencia;
    }

    public void setProcedencia(String procedencia) {
        this.procedencia = procedencia;
    }

    public String getPlanta() {
        return planta;
    }

    public void setPlanta(String planta) {
        this.planta = planta;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
