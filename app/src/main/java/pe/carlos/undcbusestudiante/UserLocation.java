package pe.carlos.undcbusestudiante;

public class UserLocation {
    private double latitude;
    private double longitude;

    public UserLocation() {
        // Constructor vacío requerido por Firebase Database
    }

    public UserLocation(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}