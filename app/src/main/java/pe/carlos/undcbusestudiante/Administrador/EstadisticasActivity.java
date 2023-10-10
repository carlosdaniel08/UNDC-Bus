package pe.carlos.undcbusestudiante.Administrador;

import android.os.Bundle;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import pe.carlos.undcbusestudiante.Class.Historial;
import pe.carlos.undcbusestudiante.R;

public class EstadisticasActivity extends AppCompatActivity {

    private TextView tvAccesoMapaCount;
    private TextView tvAccesoRutasCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estadisticas);

        tvAccesoMapaCount = findViewById(R.id.tvAccesoMapaCount);
        tvAccesoRutasCount = findViewById(R.id.tvAccesoRutasCount);

        // Obtener y procesar datos de Firebase
        obtenerEstadisticas();
    }

    private void obtenerEstadisticas() {
        DatabaseReference historialRef = FirebaseDatabase.getInstance().getReference("historial");
        historialRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int accesoMapaCount = 0;
                int accesoRutasCount = 0;
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {  // iterar a través de cada usuario
                    for (DataSnapshot historialSnapshot : userSnapshot.getChildren()) {  // iterar a través de cada registro de historial
                        Historial historial = historialSnapshot.getValue(Historial.class);
                        if (historial != null) {
                            if ("Acceso a Mapa".equals(historial.getAccion())) {
                                accesoMapaCount++;
                            } else if ("Acceso a Rutas".equals(historial.getAccion())) {
                                accesoRutasCount++;
                            }
                        }
                    }
                }
                // Actualizar UI
                actualizarUI(accesoMapaCount, accesoRutasCount);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Manejar error
            }
        });
    }


    public void actualizarUI(int accesoMapaCount, int accesoRutasCount) {
        tvAccesoMapaCount.setText("Accesos a Mapa: " + accesoMapaCount);
        tvAccesoRutasCount.setText("Accesos a Rutas: " + accesoRutasCount);
    }
}
