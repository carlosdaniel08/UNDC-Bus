package pe.carlos.undcbusestudiante.Class;

public class Historial {
    private String fecha;
    private String actividad;
    private String userId;
    private String nombreUsuario;
    private String tipoUsuario;

    public Historial() {
        // Constructor vacío necesario para Firebase
    }

    public Historial(String fecha, String actividad, String userId, String nombreUsuario, String tipoUsuario) {
        this.fecha = fecha;
        this.actividad = actividad;
        this.userId = userId;
        this.nombreUsuario = nombreUsuario;
        this.tipoUsuario = tipoUsuario;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getActividad() {
        return actividad;
    }

    public void setActividad(String actividad) {
        this.actividad = actividad;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getTipoUsuario() {
        return tipoUsuario;
    }

    public void setTipoUsuario(String tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }
}
