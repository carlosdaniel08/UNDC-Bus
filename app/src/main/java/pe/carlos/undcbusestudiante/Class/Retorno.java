package pe.carlos.undcbusestudiante.Class;

public class Retorno {
    private String paraderoretorno;
    private String horarioretorno;
    public Retorno(){
        // Constructor vacío requerido por Firebase
    }

    // Constructor
    public Retorno(String paraderoRetorno, String horarioRetorno) {
        this.paraderoretorno = paraderoRetorno;
        this.horarioretorno = horarioRetorno;
    }

    public String getParaderoretorno() {
        return paraderoretorno;
    }

    public void setParaderoretorno(String paraderoretorno) {
        this.paraderoretorno = paraderoretorno;
    }

    public String getHorarioretorno() {
        return horarioretorno;
    }

    public void setHorarioretorno(String horarioretorno) {
        this.horarioretorno = horarioretorno;
    }
}
