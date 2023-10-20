package pe.carlos.undcbusestudiante.Class;

public class UserLocation {
    private double latitude;
    private double longitude;
    private float rotation;

    public UserLocation(double latitude, double longitude, float rotation) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.rotation = rotation;
    }

    public float getRotation() {
        return rotation;
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
    }

    private String driverName; // Agrega el nombre del conductor

    public UserLocation(String driverName) {
        this.driverName = driverName;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public UserLocation() {
        // Constructor vacío requerido por Firebase Database
    }



    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}