package pe.carlos.undcbusestudiante;

import android.app.*;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;
import android.os.Looper;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import com.google.android.gms.location.*;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LocationForegroundService extends Service {

    private static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "LocationChannel";

    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannel();

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult != null) {
                    Location location = locationResult.getLastLocation();
                    // Obtén la instancia de FirebaseAuth y el usuario actual.
                    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                    FirebaseUser currentUser = firebaseAuth.getCurrentUser();

                    if (currentUser != null) {
                        String uid = currentUser.getUid();

                        // Obtén la referencia a la base de datos de Firebase.
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

                        // Crea un objeto con la latitud y longitud.
                        UserLocation userLocation = new UserLocation(location.getLatitude(), location.getLongitude());

                        // Guarda la ubicación en Firebase.
                        databaseReference.child("users").child(uid).child("location").setValue(userLocation);

                        // Aquí puedes también actualizar la UI si es necesario.
                        // Pero recuerda, estás en un servicio, así que no puedes actualizar la UI directamente.
                        // Podrías enviar un broadcast o usar un LiveData para informar a las Activities/Observers sobre la nueva ubicación.
                    }
                }
            }
        };

        startLocationUpdates();
    }

    private void startLocationUpdates() {
        LocationRequest locationRequest = LocationRequest.create()
                .setInterval(1000)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        try {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        } catch (SecurityException e) {
            // Manejar excepción.
            // Por ejemplo, podrías mostrar un mensaje al usuario informándole de que la aplicación necesita el permiso de ubicación para funcionar correctamente.
            Toast.makeText(this, "Permiso de ubicación necesario", Toast.LENGTH_SHORT).show();
        }
    }

    private void createNotificationChannel() {
        NotificationChannel serviceChannel = new NotificationChannel(
                CHANNEL_ID,
                "Location Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
        );

        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(serviceChannel);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("UNDC Bus")
                .setContentText("Compartiendo ubicación en segundo plano")
                .setSmallIcon(R.drawable.horizontal_traffic_light_flat)
                .build();

        startForeground(NOTIFICATION_ID, notification);

        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null; // Para un servicio no vinculado.
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        fusedLocationClient.removeLocationUpdates(locationCallback); // Detener actualizaciones de ubicación cuando el servicio se destruye.
    }
}
