package pe.carlos.undcbusestudiante;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class TrackBusActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int REQUEST_LOCATION_PERMISSION = 1;

    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private GoogleMap googleMap;
    private DatabaseReference usersLocationRef;
    private Switch switchOnlineOffline;
    private Marker currentUserMarker;
    private CardView cvCompartirUbicacion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_bus);

        cvCompartirUbicacion = findViewById(R.id.cvCompartirUbicacion);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        switchOnlineOffline = findViewById(R.id.switchOnlineOffline);

        // Obtener referencia a la base de datos
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        if (currentUser != null) {
            String uid = currentUser.getUid();
            usersLocationRef = FirebaseDatabase.getInstance()
                    .getReference()
                    .child("users");
        } else {

            Toast.makeText(this, "Inicia sesión", Toast.LENGTH_SHORT).show();

            // El usuario no ha iniciado sesión, maneja esta situación según tus necesidades
        }

        // Obtener el fragmento del mapa
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragmentContainer);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        switchOnlineOffline.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                cvCompartirUbicacion.setCardBackgroundColor(ContextCompat.getColor(this, R.color.Verde));
                // Cambiar el color de texto del Switch a blanco
                switchOnlineOffline.setTextColor(ContextCompat.getColor(this, R.color.White));
                startLocationUpdates();
            } else {
                // Cambiar el color de fondo del CardView a blanco
                cvCompartirUbicacion.setCardBackgroundColor(ContextCompat.getColor(this, R.color.White));
                // Cambiar el color de texto del Switch a verde
                switchOnlineOffline.setTextColor(ContextCompat.getColor(this, R.color.Verde));
                stopLocationUpdates();
                removeUserLocationFromMap();
            }
        });

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult != null) {
                    Location location = locationResult.getLastLocation();
                    if (location != null) {
                        saveLocationToFirebase(location);
                        showLocationOnMap(location);
                    }
                }
            }
        };
    }

    private void removeUserLocationFromMap() {
        if (currentUserMarker != null) {
            currentUserMarker.remove();
            currentUserMarker = null;

            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = firebaseAuth.getCurrentUser();

            if (currentUser != null) {
                String uid = currentUser.getUid();
                DatabaseReference userLocationRef = usersLocationRef
                        .child(uid)
                        .child("location");
                userLocationRef.removeValue();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (switchOnlineOffline.isChecked()) {
            startLocationUpdates();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
            return;
        }

        LocationRequest locationRequest = LocationRequest.create()
                .setInterval(5000) // Intervalo de actualización en milisegundos
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    private void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    private void saveLocationToFirebase(Location location) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        if (currentUser != null) {
            String uid = currentUser.getUid();
            DatabaseReference userLocationRef = usersLocationRef
                    .child(uid)
                    .child("location");
            UserLocation userLocation = new UserLocation(location.getLatitude(), location.getLongitude());
            userLocationRef.setValue(userLocation);
        }
    }

    private void showLocationOnMap(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        if (currentUserMarker != null) {
            currentUserMarker.setPosition(latLng);
        } else {
            currentUserMarker = googleMap.addMarker(new MarkerOptions().position(latLng));
        }
       // googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f));
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        // Configurar opciones del mapa

        LatLng initialLocation = new LatLng(-13.0673491, -76.3287982); // Latitud y longitud de San Francisco, CA
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(initialLocation, 12f));

        googleMap.getUiSettings().setMyLocationButtonEnabled(true);

        // Obtener las ubicaciones de los usuarios desde Firebase Database
        usersLocationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                googleMap.clear();

                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    DataSnapshot locationSnapshot = userSnapshot.child("location");
                    if (locationSnapshot.exists()) {
                        UserLocation userLocation = locationSnapshot.getValue(UserLocation.class);
                        if (userLocation != null) {
                            LatLng latLng = new LatLng(userLocation.getLatitude(), userLocation.getLongitude());
                            googleMap.addMarker(new MarkerOptions().position(latLng));
                        }
                    }
                }
                // Si el switch está desactivado, eliminar el marcador al actualizar la ubicación
                if (!switchOnlineOffline.isChecked()) {
                    removeUserLocationFromMap();
                }


            }



            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Manejar el error según tus necesidades
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates();
            } else {
                Toast.makeText(this, "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show();
                switchOnlineOffline.setChecked(false);
            }
        }
    }

    @Override
    public void onBackPressed() {
        // Realiza las acciones que deseas cuando se presiona el botón de retroceso

        // Por ejemplo, puedes mostrar un diálogo de confirmación antes de salir de la actividad
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("¿Deseas salir y dejar de compartir la ubicación?")
                .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Realiza las acciones para salir de la aplicación
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // No se hace nada, simplemente se cierra el diálogo
                        dialog.dismiss();
                    }
                })
                .show();
    }
}
