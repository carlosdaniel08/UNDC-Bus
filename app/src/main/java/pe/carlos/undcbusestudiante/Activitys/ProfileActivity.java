package pe.carlos.undcbusestudiante.Activitys;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.annotations.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

import pe.carlos.undcbusestudiante.R;

public class ProfileActivity extends AppCompatActivity {
    private TextView tvDNI, tvCodigo, tvEmail, tvAlumno, tvCelular, tvCarrera, tvCiclo, tvTurno, tvDistrito, tvProvincia, tvDepartamento;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        tvDNI = findViewById(R.id.tvDNI);
        tvCodigo = findViewById(R.id.tvCodigo);
        tvEmail = findViewById(R.id.tvEmail);
        tvAlumno = findViewById(R.id.tvAlumno);
        tvCelular = findViewById(R.id.tvCelular);
        tvCarrera = findViewById(R.id.tvCarrera);
        tvCiclo = findViewById(R.id.tvCiclo);
        tvTurno = findViewById(R.id.tvTurno);
        tvDistrito = findViewById(R.id.tvDistrito);
        tvProvincia = findViewById(R.id.tvProvincia);
        tvDepartamento = findViewById(R.id.tvDepartamento);

        // Obtener el correo electrónico del intent
        String userEmail = getIntent().getStringExtra("USER_EMAIL");

        // Verificar si el correo electrónico no es nulo o vacío
        if (userEmail != null && !userEmail.isEmpty()) {
            // URL de tu API
            String apiUrl = "http://192.168.1.24/android_mysql/consulta.php?email=" + userEmail;

            // Crear una cola de solicitudes Volley
            RequestQueue queue = Volley.newRequestQueue(this);

            // Crear la solicitud GET
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.GET, apiUrl, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            // Procesar la respuesta JSON
                            try {
                                String dni = response.getString("DNI");
                                String codigo = response.getString("CODIGO");
                                String email = response.getString("EMAIL");
                                String alumno = response.getString("ALUMNO");
                                String celular = response.getString("CELULAR");
                                String carrera = response.getString("CARRERA");
                                String ciclo = response.getString("CICLO");
                                String turno = response.getString("TURNO");
                                String distrito = response.getString("DISTRITO");
                                String provincia = response.getString("PROVINCIA");
                                String departamento = response.getString("DEPARTAMENTO");

                                tvDNI.setText(dni);
                                tvCodigo.setText(codigo);
                                tvEmail.setText(email);
                                tvAlumno.setText(alumno);
                                tvCelular.setText(celular);
                                tvCarrera.setText(carrera);
                                tvCiclo.setText(ciclo);
                                tvTurno.setText(turno);
                                tvDistrito.setText(distrito);
                                tvProvincia.setText(provincia);
                                tvDepartamento.setText(departamento);

                            } catch (JSONException e) {
                                // Mostrar aviso si no se encuentran datos coincidentes
                                mostrarAvisoNoDisponible();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // Manejar errores de la solicitud
                            error.printStackTrace();
                            // Mostrar aviso si hay un error en la solicitud
                            mostrarAvisoNoDisponible();
                        }
                    }
            );

            // Añadir la solicitud a la cola
            queue.add(jsonObjectRequest);
        } else {
            // Mostrar aviso si el correo electrónico es nulo o vacío
            mostrarAvisoNoDisponible();
        }
    }

    private void mostrarAvisoNoDisponible() {
        Toast.makeText(ProfileActivity.this, "Solo disponible para alumnos del 2023-2", Toast.LENGTH_SHORT).show();
        finish();
    }
}