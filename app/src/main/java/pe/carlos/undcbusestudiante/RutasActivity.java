package pe.carlos.undcbusestudiante;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

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

        Button agregarRutaButton = findViewById(R.id.agregarRutaButton);
        agregarRutaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostrarDialogoAgregarRuta();
            }
        });

        cargarRutasDesdeFirebase();
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
        builder.setTitle("Agregar Nueva Ruta");

        // Infla el diseño personalizado del diálogo
        View viewInflated = LayoutInflater.from(this).inflate(R.layout.dialog_agregar_ruta, null);
        builder.setView(viewInflated);

        final EditText nombreEditText = viewInflated.findViewById(R.id.nombreEditText);
        final EditText turnoEditText = viewInflated.findViewById(R.id.turnoEditText);
        final EditText puntoRecojoEditText = viewInflated.findViewById(R.id.puntoRecojoEditText);

        builder.setPositiveButton("Agregar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String nombre = nombreEditText.getText().toString();
                String turno = turnoEditText.getText().toString();
                String puntoRecojo = puntoRecojoEditText.getText().toString();

                // Validación de entrada de datos (puedes agregar más validaciones)
                if (!nombre.isEmpty() && !turno.isEmpty() && !puntoRecojo.isEmpty()) {
                    // Crea una nueva instancia de la clase Ruta con los datos ingresados
                    Ruta nuevaRuta = new Ruta(nombre, turno, puntoRecojo);

                    // Guarda la nueva ruta en Firebase (debes implementar esta parte)
                    guardarRutaEnFirebase(nuevaRuta);
                } else {
                    Toast.makeText(RutasActivity.this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
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