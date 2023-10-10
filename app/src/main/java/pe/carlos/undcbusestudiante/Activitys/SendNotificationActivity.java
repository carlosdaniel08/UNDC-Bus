package pe.carlos.undcbusestudiante.Activitys;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import pe.carlos.undcbusestudiante.R;

public class SendNotificationActivity extends AppCompatActivity {

    private EditText editTextTitle;
    private EditText editTextMessage;
    private Button buttonSendNotification;
    private RequestQueue myrequest;
    private String url_foto;
    private FirebaseAuth firebaseAuth;
    private String userName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_notification);

        editTextTitle = findViewById(R.id.editTextTitle);
        editTextMessage = findViewById(R.id.editTextMessage);
        buttonSendNotification = findViewById(R.id.buttonSendNotification);

        myrequest = Volley.newRequestQueue(getApplicationContext());
        firebaseAuth = FirebaseAuth.getInstance();

        FirebaseMessaging.getInstance().subscribeToTopic("enviaratodos").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                String message = task.isSuccessful() ? "Usalo con sabiduria" : "¡Error!";
                Toast.makeText(SendNotificationActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });

        buttonSendNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendNotification();
            }
        });

        // Obtén una referencia a la base de datos de Firebase
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        // Accede a la URL de la imagen almacenada en Firebase
        databaseReference.child("configuracion").child("url_foto").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                url_foto = dataSnapshot.getValue(String.class);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Manejar error...
            }
        });

        // Recuperar el nombre del usuario
        fetchUserName();
    }

    private void fetchUserName() {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
            databaseReference.child("users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        userName = dataSnapshot.child("TipoBus").getValue(String.class);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }


    private void sendNotification() {
        JSONObject json = new JSONObject();

        try {
            json.put("to", "/topics/" + "enviaratodos");
            JSONObject notificacion = new JSONObject();
            notificacion.put("titulo", editTextTitle.getText().toString().trim());
            notificacion.put("detalle", editTextMessage.getText().toString().trim());
            notificacion.put("foto", url_foto);  // Utiliza url_foto obtenida de Firebase

            if (userName != null) {
                notificacion.put("senderName", userName);
            }


            json.put("data", notificacion);
            String URL = "https://fcm.googleapis.com/fcm/send";
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL, json,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Toast.makeText(SendNotificationActivity.this, "Notificación enviada", Toast.LENGTH_SHORT).show();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(SendNotificationActivity.this, "Error al enviar notificación", Toast.LENGTH_SHORT).show();
                        }
                    }
            ) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> header = new HashMap<>();
                    header.put("content-type", "application/json");
                    header.put("authorization", "key=AAAAopMXsck:APA91bHahufExUFdKxnDOkPiYCPcFnmynE3TOkVEyK_MxrJVGcFTRp2FJ443isQAn606Qfp7j93bv3dqDhJxVTQ7-g1QkjoYhtHpJRSAuq7qa0R4ZvseSPC_ameuQoDW15chntD0_pJI");
                    return header;
                }
            };
            myrequest.add(request);

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
