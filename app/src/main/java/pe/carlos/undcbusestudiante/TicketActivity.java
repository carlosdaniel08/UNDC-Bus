package pe.carlos.undcbusestudiante;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class TicketActivity extends AppCompatActivity implements View.OnTouchListener {

    private ImageView[] imageViews;
    private boolean[] isSeatSelected;
    private boolean[] isSeatOccupied;
    private Drawable[] originalDrawables;
    private int selectedSeatIndex = -1; // Índice del asiento seleccionado
    private DatabaseReference seatsRef; // Referencia a la base de datos de Firebase
    private DatabaseReference usersRef; // Referencia al nodo "users" en Firebase

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket);

        // Obtén las referencias a los ImageView
        imageViews = new ImageView[39];
        int[] imageViewIds = {R.id.A1A, R.id.A1B, R.id.A1C, R.id.A1D, R.id.A2A, R.id.A2B, R.id.A3C, R.id.A3D, R.id.A3A,
                R.id.A3B, R.id.A3C, R.id.A3D, R.id.A4A, R.id.A4B, R.id.A4C, R.id.A4D, R.id.A5A, R.id.A5B, R.id.A5C,
                R.id.A5D, R.id.A6A, R.id.A6B, R.id.A6C, R.id.A6D, R.id.A7A, R.id.A7B, R.id.A7C, R.id.A7D, R.id.A8A,
                R.id.A8B, R.id.A8C, R.id.A8D, R.id.A9A, R.id.A9B, R.id.A9C, R.id.A9D};

        for (int i = 0; i < 35; i++) {
            imageViews[i] = findViewById(imageViewIds[i]);
            imageViews[i].setOnTouchListener(this);
        }

        // Inicializa los arreglos de estado y drawables originales
        isSeatSelected = new boolean[35];
        isSeatOccupied = new boolean[35];
        originalDrawables = new Drawable[35];

        // Obtén una referencia a la base de datos de Firebase
        seatsRef = FirebaseDatabase.getInstance().getReference().child("Asientos");
        usersRef = FirebaseDatabase.getInstance().getReference().child("users");

        // Agrega un ValueEventListener para escuchar los cambios en los datos de Firebase
        seatsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Actualiza el estado de los asientos según los datos de Firebase
                for (DataSnapshot seatSnapshot : dataSnapshot.getChildren()) {
                    int seatIndex = Integer.parseInt(seatSnapshot.getKey());
                    Boolean isSelected = seatSnapshot.child("isSelected").getValue(Boolean.class);
                    Boolean isOccupied = seatSnapshot.child("isOccupied").getValue(Boolean.class);

                    if (isSelected != null && isSelected.booleanValue()) {
                        isSeatSelected[seatIndex] = true;
                        imageViews[seatIndex].setSelected(true);
                        imageViews[seatIndex].setImageResource(R.drawable.your_seat_img);
                    } else {
                        isSeatSelected[seatIndex] = false;
                        imageViews[seatIndex].setSelected(false);
                        if (isOccupied != null && isOccupied.booleanValue()) {
                            isSeatOccupied[seatIndex] = true;
                            imageViews[seatIndex].setImageResource(R.drawable.booked_img);
                        } else {
                            isSeatOccupied[seatIndex] = false;
                            imageViews[seatIndex].setImageResource(R.drawable.available_img);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Manejo de errores
            }
        });
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        final int index = findIndex(v);

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (isSeatOccupied[index]) {
                // El asiento está ocupado, muestra un mensaje de error
                showErrorMessage("Este asiento está ocupado");
                return true;
            } else if (selectedSeatIndex == -1) {
                // No se ha seleccionado un asiento previamente
                showChooseSeatDialog(index);
                return true;
            } else if (selectedSeatIndex == index) {
                // El mismo asiento ya está seleccionado, deseleccionarlo
                deselectSeat();
                return true;
            } else {
                // Ya se ha seleccionado un asiento, muestra un mensaje de error
                showErrorMessage("Solo puedes seleccionar un asiento");
                return true;
            }
        }
        return false;
    }

    private int findIndex(View view) {
        for (int i = 0; i < 35; i++) {
            if (view == imageViews[i]) {
                return i;
            }
        }
        return -1;
    }

    private void showChooseSeatDialog(final int index) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Elegir asiento")
                .setMessage("¿Desea seleccionar este asiento?")
                .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Obtén el nombre del usuario actual
                        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                        if (currentUser != null) {
                            String uid = currentUser.getUid();
                            usersRef.child(uid).child("Nombre").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        String userName = dataSnapshot.getValue(String.class);
                                        // Guarda el estado del asiento en Firebase junto con el nombre del usuario
                                        HashMap<String, Object> seatData = new HashMap<>();
                                        seatData.put("isSelected", true);
                                        seatData.put("isOccupied", false);
                                        seatData.put("userName", userName);
                                        seatsRef.child(String.valueOf(index)).setValue(seatData);
                                        selectedSeatIndex = index;
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    // Manejar el error de la base de datos
                                    Toast.makeText(TicketActivity.this, "Error al acceder a la base de datos", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Guarda el estado del asiento en Firebase
                        seatsRef.child(String.valueOf(index)).child("isSelected").setValue(false);
                    }
                })
                .show();
    }

    private void deselectSeat() {
        // Deselecciona el asiento en Firebase
        seatsRef.child(String.valueOf(selectedSeatIndex)).child("isSelected").setValue(false);
        selectedSeatIndex = -1;
    }

    private void showErrorMessage(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Error")
                .setMessage(message)
                .setPositiveButton("Aceptar", null)
                .show();
    }
}
