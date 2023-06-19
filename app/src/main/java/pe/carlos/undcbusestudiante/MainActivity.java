package pe.carlos.undcbusestudiante;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private TextView tvSaludo;
    private TextView tvNombre;
    private TextView tvCorreoInstitucional;
    private TextView tvUsuario;
    private CardView cvMapa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        tvSaludo = findViewById(R.id.tvSaludo);
        tvNombre = findViewById(R.id.tvNombre);
        tvUsuario = findViewById(R.id.tvUsuario);
        tvCorreoInstitucional = findViewById(R.id.tvCorreoInstitucional);
        String saludo = obtenerSaludoSegunHora();
        tvSaludo.setText(saludo);
        ImageView imageView = findViewById(R.id.imageView);

//        // Verificar la conectividad a Internet al iniciar la actividad
//        if (isConnectedToInternet()) {
//            // La aplicación tiene conexión a Internet
//            Toast.makeText(this, "La aplicación tiene conexión a Internet", Toast.LENGTH_SHORT).show();
//        } else {
//            // La aplicación no tiene conexión a Internet
//            Toast.makeText(this, "La aplicación no tiene conexión a Internet", Toast.LENGTH_SHORT).show();
//            finish();// Finalizar la actividad si no hay conexión
//        }

        cvMapa = findViewById(R.id.cvMapa);
        cvMapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TrackBusActivity.class);
                startActivity(intent);
                
            }
        });


        TextView tvCerrarSesion = findViewById(R.id.tvCerrarSesion);
        tvCerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cerrarSesion();
            }
        });

        // Consultar usuario de Firebase
        consultarUsuarioFirebase();
    }

//    private boolean isConnectedToInternet() {
//        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//        Network network = connectivityManager.getActiveNetwork();
//        if (network != null) {
//            NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(network);
//            return networkCapabilities != null && networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
//        }
//        return false;
//    }

    @Override
    protected void onStart() {
        super.onStart();
        // Verificar si el usuario está autenticado al iniciar la actividad
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            // No hay usuario autenticado, redirigir al LoginActivity
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }

    private String obtenerSaludoSegunHora() {
        ImageView imageView = findViewById(R.id.imageView);

        Calendar calendar = Calendar.getInstance();
        int hora = calendar.get(Calendar.HOUR_OF_DAY);
        int imageResId;

        if (hora >= 0 && hora < 12) {
            imageResId = R.drawable.sun_behind_small_cloud_color;
            imageView.setImageResource(imageResId);
            return "Buenos días";
        } else if (hora >= 12 && hora < 18) {
            imageResId = R.drawable.sun_with_face_color;
            imageView.setImageResource(imageResId);
            return "Buenas tardes";
        } else {
            imageResId = R.drawable.full_moon_face_color;
            imageView.setImageResource(imageResId);
            return "Buenas noches";
        }
    }


    private void consultarUsuarioFirebase() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());

            usersRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String username = dataSnapshot.child("Nombre").getValue(String.class);
                        String userEmail = dataSnapshot.child("Correo").getValue(String.class);
                        String tipoUsuario = dataSnapshot.child("TipoUsuario").getValue(String.class);

                        tvNombre.setText(username);
                        tvCorreoInstitucional.setText(userEmail);
                        tvUsuario.setText(tipoUsuario);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(MainActivity.this, "Error al consultar datos", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void cerrarSesion() {
        FirebaseAuth.getInstance().signOut();
        // Redirigir al LoginActivity o a la actividad de inicio de sesión
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
