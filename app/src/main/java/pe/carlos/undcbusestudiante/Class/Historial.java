package pe.carlos.undcbusestudiante.Class;

public class Historial {
    private String correo;
    private String idUsuario;
    private String nombre;
    private String tipoUsuario;
    private String fecha;
    private String accion;

    public Historial(String correo, String idUsuario, String nombre, String tipoUsuario, String fecha, String accion) {
        this.correo = correo;
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.tipoUsuario = tipoUsuario;
        this.fecha = fecha;
        this.accion = accion;
    }

    // Constructor sin argumentos requerido por Firebase
    public Historial() {
    }


    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTipoUsuario() {
        return tipoUsuario;
    }

    public void setTipoUsuario(String tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getAccion() {
        return accion;
    }

    public void setAccion(String accion) {
        this.accion = accion;
    }
}
