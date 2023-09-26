package pe.carlos.undcbusestudiante.Administrador;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
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

import pe.carlos.undcbusestudiante.LoginActivity;
import pe.carlos.undcbusestudiante.R;
import pe.carlos.undcbusestudiante.RutasActivity;

public class AdministradorActivity extends AppCompatActivity {

    private TextView tvSaludo;
    private TextView tvNombre;
    private TextView tvCorreoInstitucional;
    private TextView tvUsuario, tvTipoBus;
    private CardView cvHorarios, cvRutas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_administrador);

        TextView tvCerrarSesion = findViewById(R.id.tvCerrarSesion);
        tvCerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cerrarSesion();
            }
        });

        tvSaludo = findViewById(R.id.tvSaludo);
        tvNombre = findViewById(R.id.tvNombre);
        tvUsuario = findViewById(R.id.tvUsuario);
        tvTipoBus = findViewById(R.id.tvTipoBus);
        tvCorreoInstitucional = findViewById(R.id.tvCorreoInstitucional);
        String saludo = obtenerSaludoSegunHora();
        tvSaludo.setText(saludo);
        ImageView imageView = findViewById(R.id.imageView);

        // Consultar usuario de Firebase
        consultarUsuarioFirebase();


        cvHorarios = findViewById(R.id.cvHorarios);
        cvHorarios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent intent = new Intent(AdministradorActivity.this, RutasActivity.class);
                   startActivity(intent);

            }
        });



    }

    private String obtenerSaludoSegunHora() {
        ImageView imageView = findViewById(R.id.imageView);

        Calendar calendar = Calendar.getInstance();
        int hora = calendar.get(Calendar.HOUR_OF_DAY);
        int imageResId;
        String saludo;

        if (hora >= 0 && hora < 12) {
            imageResId = R.drawable.sun_behind_small_cloud_color;
            saludo = "Buenos días";
        } else if (hora >= 12 && hora < 18) {
            imageResId = R.drawable.sun_with_face_color;
            saludo = "Buenas tardes";
        } else {
            imageResId = R.drawable.full_moon_face_color;
            saludo = "Buenas noches";
        }

        imageView.setImageResource(imageResId);
        return saludo;
    }


    private void consultarUsuarioFirebase() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());

            usersRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String userId = user.getUid();
                        String username = dataSnapshot.child("Nombre").getValue(String.class);
                        String userEmail = dataSnapshot.child("Correo").getValue(String.class);
                        String tipoUsuario = dataSnapshot.child("TipoUsuario").getValue(String.class);
                        String tipoBus = dataSnapshot.child("TipoBus").getValue(String.class);

                        tvNombre.setText(username);
                        tvCorreoInstitucional.setText(userEmail);
                        tvUsuario.setText(tipoUsuario);
                        tvTipoBus.setText(tipoBus);

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(AdministradorActivity.this, "Error al consultar datos", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void cerrarSesion() {
        FirebaseAuth.getInstance().signOut();
        // Redirigir al LoginActivity o a la actividad de inicio de sesión
        Intent intent = new Intent(AdministradorActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

}