package pe.carlos.undcbusestudiante;

import java.util.ArrayList;
import java.util.List;

public class Ruta {
    private String nombre;
    private String turno;
    private String puntoRecojo;
    private List<Salida> salidas; // Lista de salidas asociadas a esta ruta



    public void setSalidas(List<Salida> salidas) {
        this.salidas = salidas;
    }

    public Ruta() {
        // Constructor vacío requerido por Firebase
    }

    // Constructor
    public Ruta(String nombre, String turno, String puntoRecojo) {
        this.nombre = nombre;
        this.turno = turno;
        this.puntoRecojo = puntoRecojo;
        this.salidas = new ArrayList<>();
    }
    // Métodos para agregar y obtener salidas
    public void agregarSalida(Salida salida) {
        salidas.add(salida);
    }

    public List<Salida> getSalidas() {
        return salidas;
    }

    // Getters y setters
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

    public String getPuntoRecojo() {
        return puntoRecojo;
    }

    public void setPuntoRecojo(String puntoRecojo) {
        this.puntoRecojo = puntoRecojo;
    }
}
