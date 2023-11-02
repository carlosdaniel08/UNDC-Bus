package pe.carlos.undcbusestudiante.Activitys;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import pe.carlos.undcbusestudiante.Class.UserLocation;
import pe.carlos.undcbusestudiante.R;
import pe.carlos.undcbusestudiante.Service.LocationForegroundService;
import pe.carlos.undcbusestudiante.Service.LocationService;

public class TrackBusActivity extends AppCompatActivity implements OnMapReadyCallback {

    private boolean popupShown = false;
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private GoogleMap googleMap;
    private DatabaseReference usersLocationRef;
    private Switch switchOnlineOffline;
    private Marker currentUserMarker;
    private CardView cvCompartirUbicacion;
    private LatLng previousLocation = null; // Almacena la ubicación anterior
    private Map<String, Marker> markersMap = new HashMap<>();
    private Polyline busTrail;
    private List<LatLng> busTrailPoints = new ArrayList<>();
    private List<Long> busTrailTimestamps = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_bus);

        initializeViews();
        initializeFirebase();
        initializeMap();
        handleSwitchChange();

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

    private void initializeViews() {
        cvCompartirUbicacion = findViewById(R.id.cvCompartirUbicacion);
        switchOnlineOffline = findViewById(R.id.switchOnlineOffline);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
    }

    private void initializeFirebase() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Inicia sesión", Toast.LENGTH_SHORT).show();
            return;
        }

        String uid = currentUser.getUid();
        usersLocationRef = FirebaseDatabase.getInstance().getReference().child("users");
        DatabaseReference userRef = usersLocationRef.child(uid);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String tipoUsuario = dataSnapshot.child("TipoUsuario").getValue(String.class);
                cvCompartirUbicacion.setVisibility("CONDUCTOR".equals(tipoUsuario) ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });
    }



    private void initializeMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragmentContainer);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    private void handleSwitchChange() {
        switchOnlineOffline.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // El switch está activado, el usuario está compartiendo ubicación
                updateCompartiendoUbicacion(true);
                activateSharing();
                startLocationUpdates();
            } else {
                // El switch está desactivado, el usuario ya no está compartiendo ubicación
                updateCompartiendoUbicacion(false);
                deactivateSharing();
                stopLocationUpdates();
                removeUserLocationFromMap();
            }
        });
    }

    private void updateCompartiendoUbicacion(boolean compartiendo) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        if (currentUser != null) {
            String uid = currentUser.getUid();
            DatabaseReference userRef = usersLocationRef.child(uid);
            userRef.child("compartiendoUbicacion").setValue(compartiendo);
        }
    }

    private void activateSharing() {
        cvCompartirUbicacion.setCardBackgroundColor(ContextCompat.getColor(this, R.color.Verde));
        switchOnlineOffline.setTextColor(ContextCompat.getColor(this, R.color.White));
        Intent serviceIntent = new Intent(this, LocationForegroundService.class);
        startForegroundService(serviceIntent);
    }

    private void deactivateSharing() {
        cvCompartirUbicacion.setCardBackgroundColor(ContextCompat.getColor(this, R.color.White));
        switchOnlineOffline.setTextColor(ContextCompat.getColor(this, R.color.Verde));
        Intent serviceIntent = new Intent(this, LocationForegroundService.class);
        stopService(serviceIntent);
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

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
            return;
        }

        LocationRequest locationRequest = LocationRequest.create()
                .setInterval(500) // Intervalo de actualización en milisegundos
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
            UserLocation userLocation = new UserLocation(
                    location.getLatitude(),
                    location.getLongitude(),
                    location.getBearing()
            );
            userLocationRef.setValue(userLocation);
        }
    }


    private void showLocationOnMap(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        float rotation = location.getBearing();

        if (currentUserMarker != null) {
            currentUserMarker.setPosition(latLng);
            currentUserMarker.setRotation(rotation);
        } else {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            String uid = currentUser != null ? currentUser.getUid() : null;
            if (uid != null && !markersMap.containsKey(uid)) {

                MarkerOptions markerOptions = new MarkerOptions()
                        .position(latLng)
                        .rotation(rotation);
                currentUserMarker = googleMap.addMarker(markerOptions);
            }
        }

        if (busTrail != null) {
            long currentTime = System.currentTimeMillis();
            busTrailPoints.add(latLng);
            busTrailTimestamps.add(currentTime);
            updateBusTrail();
        }
    }

    private void updateBusTrail() {
        long thirtyMinutesInMillis = 30 * 60 * 1000;
        long currentTime = System.currentTimeMillis();
        List<LatLng> recentPoints = new ArrayList<>();

        for (int i = busTrailPoints.size() - 1; i >= 0; i--) {
            if (currentTime - busTrailTimestamps.get(i) <= thirtyMinutesInMillis) {
                recentPoints.add(0, busTrailPoints.get(i));
            } else {
                busTrailPoints.remove(i);
                busTrailTimestamps.remove(i);
            }
        }
        busTrail.setPoints(recentPoints);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        LatLng initialLocation = new LatLng(-13.0673491, -76.3287982);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(initialLocation, 12f));
        busTrail = googleMap.addPolyline(new PolylineOptions().width(10).color(Color.GREEN));

        if (checkLocationPermission()) {
            googleMap.setMyLocationEnabled(true);
        }

        usersLocationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Set<String> keysToRemove = new HashSet<>(markersMap.keySet());
                List<String> busNames = new ArrayList<>();

                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    DataSnapshot locationSnapshot = userSnapshot.child("location");
                    Boolean compartiendoUbicacion = userSnapshot.child("compartiendoUbicacion").getValue(Boolean.class);

                    if (compartiendoUbicacion != null && compartiendoUbicacion && locationSnapshot.exists()) {
                        UserLocation userLocation = locationSnapshot.getValue(UserLocation.class);
                        String nombre = userSnapshot.child("Nombre").getValue(String.class);
                        String tipoBus = userSnapshot.child("TipoBus").getValue(String.class);
                        String userId = userSnapshot.getKey();

                        if (userLocation != null) {
                            LatLng latLng = new LatLng(userLocation.getLatitude(), userLocation.getLongitude());
                            float userRotation = userLocation.getRotation();

                            Marker existingMarker = markersMap.get(userId);
                            if (existingMarker != null) {
                                // Actualizar el marcador existente
                                existingMarker.setPosition(latLng);
                                existingMarker.setRotation(userRotation);
                                existingMarker.setTitle(tipoBus);
                                existingMarker.setSnippet(nombre);
                                keysToRemove.remove(userId);
                            } else {
                                // Agregar un nuevo marcador
                                MarkerOptions markerOptions = new MarkerOptions()
                                        .position(latLng)
                                        .title(tipoBus)
                                        .snippet(nombre)
                                        .rotation(userRotation)
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.busmarkerdos))
                                        .anchor(0.5f, 0.5f);
                                Marker newMarker = googleMap.addMarker(markerOptions);
                                markersMap.put(userId, newMarker);
                            }
                            // Agregar el nombre del bus a la lista si está compartiendo ubicación
                            busNames.add(tipoBus);
                        }
                    } else {
                        // El bus no está compartiendo ubicación o no hay ubicación disponible
                        // Elimina el marcador si existe
                        String userId = userSnapshot.getKey();
                        keysToRemove.remove(userId);
                    }
                }

                // Mostrar la ventana emergente solo si no se ha mostrado previamente
                if (!popupShown && !busNames.isEmpty()) {
                    showBusLocationsPopup(busNames);
                    popupShown = true; // Marcar la ventana emergente como mostrada
                }

                // Eliminar marcadores que ya no están en Firebase
                for (String keyToRemove : keysToRemove) {
                    Marker markerToRemove = markersMap.remove(keyToRemove);
                    if (markerToRemove != null) {
                        markerToRemove.remove();
                    }
                }

                if (!switchOnlineOffline.isChecked()) {
                    removeUserLocationFromMap();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });
    }

    private boolean checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            // Solicitar permiso de ubicación si no se ha otorgado
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
            return false;
        }
    }


    private void showBusLocationsPopup(List<String> busNames) {
        View popupView = getLayoutInflater().inflate(R.layout.popup_bus_locations, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(popupView);

        TextView textViewBusList = popupView.findViewById(R.id.textViewBusList);

        if (busNames.isEmpty()) {
            textViewBusList.setText("No hay buses compartiendo ubicación.");
        } else {
            StringBuilder busList = new StringBuilder();
            for (String busName : busNames) {
                busList.append(busName).append("\n");
            }
            textViewBusList.setText(busList.toString().trim());
        }

        builder.setPositiveButton("Cerrar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates();
                Intent serviceIntent = new Intent(this, LocationService.class);
                startService(serviceIntent);
            } else {
                Toast.makeText(this, "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show();
                switchOnlineOffline.setChecked(false);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (switchOnlineOffline.isChecked()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("¿Deseas salir y dejar de compartir la ubicación?")
                    .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            stopLocationUpdates();
                            removeUserLocationFromMap();
                            finish();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .show();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopLocationUpdates();
    }
}