package pe.carlos.undcbusestudiante;

public class Salida {
    private String paraderosalida;
    private String horariosalida;
    public Salida(){
        // Constructor vacío requerido por Firebase
    }

    // Constructor
    public Salida(String paraderoSalida, String horarioSalida) {
        this.paraderosalida = paraderoSalida;
        this.horariosalida = horarioSalida;
    }

    public String getParaderosalida() {
        return paraderosalida;
    }

    public void setParaderosalida(String paraderosalida) {
        this.paraderosalida = paraderosalida;
    }

    public String getHorariosalida() {
        return horariosalida;
    }

    public void setHorariosalida(String horariosalida) {
        this.horariosalida = horariosalida;
    }
}
