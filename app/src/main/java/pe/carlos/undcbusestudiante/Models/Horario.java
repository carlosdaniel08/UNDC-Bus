package pe.carlos.undcbusestudiante.Models;

import java.util.List;

public class Horario {
    private String nombre;
    private String turno;
    private List<Paradero> salida; // Lista de paraderos para la SALIDA
    private List<Paradero> retorno; // Lista de paraderos para el RETORNO

    public List<Paradero> getSalida() {
        return salida;
    }

    public void setSalida(List<Paradero> salida) {
        this.salida = salida;
    }

    public List<Paradero> getRetorno() {
        return retorno;
    }

    public void setRetorno(List<Paradero> retorno) {
        this.retorno = retorno;
    }

    public Horario() {} // Constructor vacío para Firebase

    public Horario(String nombre, String turno) {
        this.nombre = nombre;
        this.turno = turno;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTurno() {
        return turno;
    }

    public void setTurno(String turno) {
        this.turno = turno;
    }
}
