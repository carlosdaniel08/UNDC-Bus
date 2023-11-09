package pe.carlos.undcbusestudiante.Activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Context;

import android.content.DialogInterface;
import android.content.Intent;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;

import android.net.Uri;
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


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import pe.carlos.undcbusestudiante.Class.Historial;
import pe.carlos.undcbusestudiante.R;

public class MainActivity extends AppCompatActivity {

    private TextView tvSaludo;
    private TextView tvNombre;
    private TextView tvCorreoInstitucional;
    private TextView tvUsuario, tvTipoBus;
    private TextView txtEnviarNotificaciones;
    private CardView cvMapa, cvRutas, cvNotifaciones;
    private DatabaseReference mDatabase;
    private int nVersion, versionActual;
    private FirebaseUser currentUser;
    private DatabaseReference historialDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        historialDatabase = FirebaseDatabase.getInstance().getReference("historial");

        tvSaludo = findViewById(R.id.tvSaludo);
        tvNombre = findViewById(R.id.tvNombre);
        tvUsuario = findViewById(R.id.tvUsuario);
        tvTipoBus = findViewById(R.id.tvTipoBus);
        tvCorreoInstitucional = findViewById(R.id.tvCorreoInstitucional);
        txtEnviarNotificaciones = findViewById(R.id.txtEnviarNotificaciones);
        String saludo = obtenerSaludoSegunHora();
        tvSaludo.setText(saludo);
        ImageView imageView = findViewById(R.id.imageView);


       mDatabase = FirebaseDatabase.getInstance().getReference("version");
       VersionApp();



      if (isConnectedToInternet()) {

       } else {
         Toast.makeText(this, "La aplicación no tiene conexión a Internet, intenta de nuevo", Toast.LENGTH_SHORT).show();
          finish();// Finalizar la actividad si no hay conexión

       }


        cvMapa = findViewById(R.id.cvMapa);
        cvMapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarHistorial("Acceso a Mapa");
                Intent intent = new Intent(MainActivity.this, TrackBusActivity.class);
                startActivity(intent);

            }
        });

        cvRutas = findViewById(R.id.cvRutas);
        cvRutas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarHistorial("Acceso a Rutas");
               Intent intent = new Intent(MainActivity.this, RutasActivity.class);
               startActivity(intent);
            }
        });

        cvNotifaciones = findViewById(R.id.cvNotificaciones);
        cvNotifaciones.setVisibility(View.GONE);  // Ocultar el CardView inicialmente
        txtEnviarNotificaciones.setVisibility(View.GONE);


        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(currentUser.getUid());

            mDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String tipoUsuario = dataSnapshot.child("TipoUsuario").getValue(String.class);

                        // Mostrar el CardView sólo si TipoUsuario es "CONDUCTOR"
                        if ("CONDUCTOR".equals(tipoUsuario)) {
                            cvNotifaciones.setVisibility(View.VISIBLE);
                            txtEnviarNotificaciones.setVisibility(View.VISIBLE);

                            cvNotifaciones.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(MainActivity.this, SendNotificationActivity.class);
                                    startActivity(intent);
                                }
                            });
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Manejar error...
                    Toast.makeText(MainActivity.this, "Error al consultar datos", Toast.LENGTH_SHORT).show();
                }
            });
        }

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

    private void guardarHistorial(String accion) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String idUsuario = user.getUid();
            DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users").child(idUsuario);

            usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String correo = dataSnapshot.child("Correo").getValue(String.class);
                        String nombre = dataSnapshot.child("Nombre").getValue(String.class);
                        String tipoUsuario = dataSnapshot.child("TipoUsuario").getValue(String.class);
                        String fecha = obtenerFechaActual();

                        Historial historial = new Historial(correo, idUsuario, nombre, tipoUsuario, fecha, accion);
                        historialDatabase.child(idUsuario).push().setValue(historial);  // Corrección aquí
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(MainActivity.this, "Error al consultar datos", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    private void VersionApp() {

        try {
            PackageInfo packageInfo = this.getPackageManager().getPackageInfo(getPackageName(),0);
            versionActual = packageInfo.versionCode;

        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException(e);
        }

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                nVersion = Integer.parseInt(snapshot.getValue().toString());
            if(versionActual != nVersion){
                ActualizarApp();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void ActualizarApp() {
        new AlertDialog.Builder(this)
                .setTitle("Actualización Requerida")
                .setMessage("Hay una nueva versión disponible. Por favor, actualiza para continuar.")
                .setPositiveButton("Actualizar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Redirige al usuario a la página de la aplicación en la tienda de aplicaciones
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse("market://details?id=" + getPackageName()));
                        startActivity(intent);
                        finish();
                    }
                })
                .setNegativeButton("Salir", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setIcon(R.drawable.warning_color)
                .show();
    }


    private boolean isConnectedToInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        Network network = connectivityManager.getActiveNetwork();

     if (network != null) {
          NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(network);
         return networkCapabilities != null && networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
      }
       return false;
    }

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

                        // Verificar si el tipoBus no es nulo o vacío
                        if (tipoBus != null && !tipoBus.isEmpty()) {
                            tvTipoBus.setText(tipoBus);
                            tvTipoBus.setVisibility(View.VISIBLE); // Mostrar el TextView
                        } else {
                            tvTipoBus.setVisibility(View.GONE); // Ocultar el TextView si no hay información
                        }



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





    private String obtenerFechaActual() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

}
