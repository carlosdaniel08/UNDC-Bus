package pe.carlos.undcbusestudiante;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import pe.carlos.undcbusestudiante.Adapter.RutasAdapter;

import pe.carlos.undcbusestudiante.Administrador.AdministradorActivity;
import pe.carlos.undcbusestudiante.Class.Retorno;
import pe.carlos.undcbusestudiante.Class.Ruta;
import pe.carlos.undcbusestudiante.Class.Salida;

public class RutasActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RutasAdapter rutasAdapter;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rutas);

        recyclerView = findViewById(R.id.recyclerViewRutas);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        rutasAdapter = new RutasAdapter();
        recyclerView.setAdapter(rutasAdapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("rutas");

        mostrarBoton();

        cargarRutasDesdeFirebase();
    }

    private void mostrarBoton() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");

        Button agregarRutaButton = findViewById(R.id.agregarRutaButton);
        agregarRutaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostrarDialogoAgregarRuta();
            }
        });

        usersRef.child(currentUser.getUid()).child("TipoUsuario").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String userType = dataSnapshot.getValue(String.class);
                if ("ADMINISTRADOR".equals(userType) || "CONDUCTOR".equals(userType)) {
                    agregarRutaButton.setVisibility(View.VISIBLE);
                } else {
                    agregarRutaButton.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(RutasActivity.this, "Error al obtener el tipo de usuario", Toast.LENGTH_SHORT).show();
                finish();
            }
        });


    }

    // Método para cargar las rutas desde Firebase y actualizar el RecyclerView
    private void cargarRutasDesdeFirebase() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Ruta> rutas = new ArrayList<>();
                for (DataSnapshot rutaSnapshot : dataSnapshot.getChildren()) {
                    Ruta ruta = rutaSnapshot.getValue(Ruta.class);
                    rutas.add(ruta);
                }
                rutasAdapter.setRutas(rutas);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Manejar errores de Firebase si es necesario
            }
        });
    }

    // Método para mostrar el diálogo para agregar una nueva ruta
    private void mostrarDialogoAgregarRuta() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Agregar nuevo horario");

        // Infla el diseño personalizado del diálogo
        View viewInflated = LayoutInflater.from(this).inflate(R.layout.dialog_agregar_ruta, null);
        builder.setView(viewInflated);

        final EditText nombreEditText = viewInflated.findViewById(R.id.nombreEditText);
        final EditText turnoEditText = viewInflated.findViewById(R.id.turnoEditText);
        final EditText puntoRecojoEditText = viewInflated.findViewById(R.id.puntoRecojoEditText);

        final LinearLayout paraderosLayout = viewInflated.findViewById(R.id.paraderosLayout);
        Button agregarParaderoButton = viewInflated.findViewById(R.id.agregarParaderoButton);

        // Elementos para los retornos
        final LinearLayout retornosLayout = viewInflated.findViewById(R.id.paraderosRetornoLayout);
        Button agregarRetornoButton = viewInflated.findViewById(R.id.agregarParaderoRetornoButton);

        // Manejar la adición dinámica de salidas
        agregarRetornoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                agregarNuevaSalida(retornosLayout);
            }
        });

        agregarParaderoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                agregarNuevoParadero(paraderosLayout);
            }
        });

        builder.setPositiveButton("Agregar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String nombre = nombreEditText.getText().toString().trim();
                String turno = turnoEditText.getText().toString().trim();
                String puntoRecojo = puntoRecojoEditText.getText().toString().trim();

                if (isValidInput(nombre, turno, puntoRecojo)) {
                    Ruta nuevaRuta = new Ruta(nombre, turno, puntoRecojo);
                    if (agregarInfoARuta(paraderosLayout, nuevaRuta, true) && agregarInfoARuta(retornosLayout, nuevaRuta, false)) {
                        guardarRutaEnFirebase(nuevaRuta);
                    } else {
                        showToast(getString(R.string.error_message));
                    }
                } else {
                    showToast(getString(R.string.error_message));
                }
            }
        });

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void agregarNuevaSalida(LinearLayout layout) {
        View nuevaSalidaView = LayoutInflater.from(RutasActivity.this).inflate(R.layout.layout_nuevo_paradero_salida, null);
        layout.addView(nuevaSalidaView);
    }

    private void agregarNuevoParadero(LinearLayout layout) {
        View nuevoParaderoView = LayoutInflater.from(RutasActivity.this).inflate(R.layout.layout_nuevo_paradero, null);
        layout.addView(nuevoParaderoView);
    }

    private boolean isValidInput(String nombre, String turno, String puntoRecojo) {
        return !nombre.isEmpty() && !turno.isEmpty() && !puntoRecojo.isEmpty() && nombre.length() > 2;
    }

    private boolean agregarInfoARuta(LinearLayout layout, Ruta ruta, boolean isSalida) {
        for (int i = 0; i < layout.getChildCount(); i++) {
            View view = layout.getChildAt(i);
            EditText paraderoEditText = view.findViewById(R.id.paraderoEditText);
            EditText horarioEditText = view.findViewById(R.id.horarioEditText);
            String paradero = paraderoEditText.getText().toString().trim();
            String horario = horarioEditText.getText().toString().trim();

            if (!paradero.isEmpty() && !horario.isEmpty()) {
                if (isSalida) {
                    ruta.agregarSalida(new Salida(paradero, horario));
                } else {
                    ruta.agregarRetorno(new Retorno(paradero, horario));
                }
            } else {
                return false;
            }
        }
        return true;
    }

    private void showToast(String message) {
        Toast.makeText(RutasActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    private void guardarRutaEnFirebase(Ruta nuevaRuta) {
        // Obtiene una referencia a la base de datos de Firebase
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        // Genera un nuevo ID único para la ruta
        String rutaId = databaseReference.child("rutas").push().getKey();

        // Crea un nodo para la nueva ruta con el ID generado
        DatabaseReference nuevaRutaReference = databaseReference.child("rutas").child(rutaId);

        // Guarda los datos de la nueva ruta en Firebase
        nuevaRutaReference.setValue(nuevaRuta)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Ruta guardada exitosamente en Firebase
                        Toast.makeText(RutasActivity.this, "Ruta agregada exitosamente", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Error al guardar la ruta en Firebase
                        Toast.makeText(RutasActivity.this, "Error al agregar la ruta", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
