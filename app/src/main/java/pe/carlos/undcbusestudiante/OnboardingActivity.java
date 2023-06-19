package pe.carlos.undcbusestudiante;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class OnboardingActivity extends AppCompatActivity {
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    // Variables de instancia y otros métodos

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Verificar si el permiso de ubicación ya está concedido
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // El permiso de ubicación ya está concedido, iniciar directamente la actividad MainActivity
            Toast.makeText(this, "Ya tienes permiso", Toast.LENGTH_SHORT).show();
            startMainActivity();
            return; // Salir del método onCreate() para evitar mostrar el OnboardingActivity
        }

        setContentView(R.layout.activity_onboarding);

        // Aquí puedes realizar la configuración y lógica específica de la actividad Onboarding
        // Obtén las referencias a los elementos de diseño XML utilizando los ID correspondientes

        TextView titleTextView = findViewById(R.id.titleTextView);
        TextView descriptionTextView = findViewById(R.id.descriptionTextView);
        Button btnIngresar = findViewById(R.id.btnIngresar);

        // Configura el evento de clic para el botón "Comenzar"
        btnIngresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Verificar si el permiso de ubicación ya está concedido
                if (ContextCompat.checkSelfPermission(OnboardingActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    // El permiso de ubicación ya está concedido, realizar la acción deseada
                    // Por ejemplo, iniciar la siguiente actividad o realizar otra tarea
                    Toast.makeText(OnboardingActivity.this, "Permiso de ubicación ya concedido", Toast.LENGTH_SHORT).show();
                } else {
                    // El permiso de ubicación no está concedido, solicitarlo al usuario
                    ActivityCompat.requestPermissions(OnboardingActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
                }
            }
        });
    }

    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish(); // Finalizar el OnboardingActivity para que no se pueda volver atrás desde MainActivity
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // El usuario concedió el permiso de ubicación
                // Realizar la acción deseada, por ejemplo, iniciar la siguiente actividad
                Toast.makeText(OnboardingActivity.this, "Permiso de ubicación concedido", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(OnboardingActivity.this, MainActivity.class);
                startActivity(intent);
            } else {
                // El usuario denegó el permiso de ubicación
                // Puedes mostrar un mensaje o realizar alguna otra acción en consecuencia
                Toast.makeText(OnboardingActivity.this, "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
