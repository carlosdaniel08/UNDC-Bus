package pe.carlos.undcbusestudiante;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;

public class TrackBusActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Switch switchOnlineOffline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_bus);

        // Obtener el Fragmento del mapa
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapFragmentContainer);
        mapFragment.getMapAsync(this);

        // Obtener referencia al Switch
        switchOnlineOffline = findViewById(R.id.switchOnlineOffline);

        // Configurar el listener del Switch
        switchOnlineOffline.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Aquí puedes agregar la lógica para manejar el cambio de estado del Switch
                if (isChecked) {
                    // El Switch está activado
                    Toast.makeText(TrackBusActivity.this, "activo", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(TrackBusActivity.this, "No activo", Toast.LENGTH_SHORT).show();
                    // El Switch está desactivado
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Configurar el mapa y añadir marcadores, polilíneas, etc.
        // Aquí puedes agregar tu lógica para interactuar con el mapa de Google
    }
}
