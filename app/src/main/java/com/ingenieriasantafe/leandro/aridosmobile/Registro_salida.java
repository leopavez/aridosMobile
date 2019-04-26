package com.ingenieriasantafe.leandro.aridosmobile;

public class Registro_salida {
    int id = 0;
    String patente;
    String m3;
    String planta;
    String chofer;
    String username;
    String fecha;
    String hora;
    String estado;
    String procedencia;
    String tipomaterial;
    String destino;

    public Registro_salida() {

    }

    public Registro_salida(int id, String patente, String m3, String planta, String chofer, String username, String fecha, String hora, String estado, String procedencia, String tipomaterial, String destino) {
        this.id = id;
        this.patente = patente;
        this.m3 = m3;
        this.planta = planta;
        this.chofer = chofer;
        this.username = username;
        this.fecha = fecha;
        this.hora = hora;
        this.estado = estado;
        this.procedencia = procedencia;
        this.tipomaterial = tipomaterial;
        this.destino = destino;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPatente() {
        return patente;
    }

    public void setPatente(String patente) {
        this.patente = patente;
    }

    public String getM3() {
        return m3;
    }

    public void setM3(String m3) {
        this.m3 = m3;
    }

    public String getPlanta() {
        return planta;
    }

    public void setPlanta(String planta) {
        this.planta = planta;
    }

    public String getChofer() {
        return chofer;
    }

    public void setChofer(String chofer) {
        this.chofer = chofer;
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

    public String getProcedencia() {
        return procedencia;
    }

    public void setProcedencia(String procedencia) {
        this.procedencia = procedencia;
    }

    public String getTipomaterial() {
        return tipomaterial;
    }

    public void setTipomaterial(String tipomaterial) {
        this.tipomaterial = tipomaterial;
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }
}