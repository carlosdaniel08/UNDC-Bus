package pe.carlos.undcbusestudiante.Models;

public class Paradero {
    private String nombre;
    private String hora;

    // Constructor vacío requerido para Firebase
    public Paradero() {}

    public Paradero(String nombre, String hora) {
        this.nombre = nombre;
        this.hora = hora;
    }

    // Getter para el nombre
    public String getNombre() {
        return nombre;
    }

    // Setter para el nombre
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    // Getter para la hora
    public String getHora() {
        return hora;
    }

    // Setter para la hora
    public void setHora(String hora) {
        this.hora = hora;
    }
}
