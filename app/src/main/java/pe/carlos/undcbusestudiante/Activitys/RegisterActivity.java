package pe.carlos.undcbusestudiante.Activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pe.carlos.undcbusestudiante.R;

public class RegisterActivity extends AppCompatActivity {

    Button btnRegistrar;
    EditText CorreoIntitucional, Nombre, editTextPassword;
    Spinner spTipoUsuario, spTipoBus;
    FirebaseAuth firebaseAuth;
    CheckBox cbTerminosCondiciones;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        CorreoIntitucional = findViewById(R.id.edtCorreoInstitucional);
        Nombre = findViewById(R.id.edtNombre);
        editTextPassword = findViewById(R.id.edtContrasena);
        btnRegistrar = findViewById(R.id.btnRegistrar);
        spTipoUsuario = findViewById(R.id.spTipoUsuario);
        spTipoBus = findViewById(R.id.spTipoBus);
        firebaseAuth = FirebaseAuth.getInstance();
        cbTerminosCondiciones = findViewById(R.id.cbTerminosCondiciones);


        // Crear una lista de elementos para el Spinner
        List<String> tiposUsuarios = new ArrayList<>();
        tiposUsuarios.add("ESTUDIANTE");
        tiposUsuarios.add("DOCENTE");
        tiposUsuarios.add("ADMINISTRATIVO");
       // tiposUsuarios.add("CONDUCTOR");

        // Crear una lista de elementos para el Spinner de bus
        List<String> tiposBuses = new ArrayList<>();
        tiposBuses.add("Bus A");
        tiposBuses.add("Bus B");
        tiposBuses.add("Bus C");
        tiposBuses.add("Bus Interprovincial");

        // Crear un adaptador utilizando el contexto, el layout para los elementos y la lista de valores
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, tiposUsuarios);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTipoUsuario.setAdapter(adapter);

        ArrayAdapter<String> adapterBus = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, tiposBuses);
        adapterBus.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTipoBus.setAdapter(adapterBus);

        spTipoUsuario.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedItem = parentView.getItemAtPosition(position).toString();
                if ("CONDUCTOR".equals(selectedItem)) {
                    Toast.makeText(RegisterActivity.this, "Disponible solo para conductores.", Toast.LENGTH_SHORT).show();
                    spTipoBus.setVisibility(View.VISIBLE); // Muestra el Spinner para el tipo de bus
                } else {
                    spTipoBus.setVisibility(View.GONE); // Oculta el Spinner para el tipo de bus
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // No necesitas hacer nada aquí
            }
        });

        Drawable drawableVisible = getResources().getDrawable(R.drawable.see_no_evil_monkey_color);
        Drawable drawableHidden = getResources().getDrawable(R.drawable.hear_no_evil_monkey_color);

        // lógica para cambiar la visibilidad al hacer clic en el ícono
        editTextPassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (editTextPassword.getRight() - editTextPassword.getCompoundDrawables()[2].getBounds().width())) {
                        if (editTextPassword.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                            editTextPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                            editTextPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, drawableHidden, null);
                        } else {
                            editTextPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                            editTextPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, drawableVisible, null);
                        }
                        return true;
                    }
                }
                return false;
            }
        });

        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String correoInstitucional = CorreoIntitucional.getText().toString();
                String nombre = Nombre.getText().toString();
                String contrasena = editTextPassword.getText().toString();
                String tipoUsuario = spTipoUsuario.getSelectedItem().toString();
                String tipoBus = spTipoBus.getSelectedItem().toString();

                CheckBox cbTerminosCondiciones = findViewById(R.id.cbTerminosCondiciones);
                boolean isChecked = cbTerminosCondiciones.isChecked();

                if (!validateForm()) {
                    return;
                }

                if (!isChecked) {
                    Toast.makeText(RegisterActivity.this, "Debes aceptar los términos y condiciones", Toast.LENGTH_SHORT).show();
                    return;
                }

                firebaseAuth.createUserWithEmailAndPassword(correoInstitucional, contrasena)
                        .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser firebaseUser = task.getResult().getUser();
                                    onAuthSuccess(firebaseUser, nombre, tipoUsuario, tipoBus);
                                    firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(RegisterActivity.this, "Se ha enviado un correo de verificación", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(RegisterActivity.this, "Error al enviar el correo de verificación", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                } else {
                                    Toast.makeText(RegisterActivity.this, "Registro fallido", Toast.LENGTH_SHORT).show();
                                }
                                hideProgressDialog();
                            }
                        });
            }
        });
    }

    private boolean validateForm() {
        boolean result = true;
        List<EditText> editTextList = new ArrayList<>();
        editTextList.add(CorreoIntitucional);
        editTextList.add(Nombre);
        editTextList.add(editTextPassword);

        for (EditText editText : editTextList) {
            String fieldValue = editText.getText().toString();
            if (TextUtils.isEmpty(fieldValue)) {
                editText.setError("Requerido");
                result = false;
            } else {
                editText.setError(null);
            }
        }

        String email = CorreoIntitucional.getText().toString();
        if (!TextUtils.isEmpty(email) && !email.endsWith("@undc.edu.pe")) {
            CorreoIntitucional.setError("Utiliza tu correo institucional");
            result = false;
        }

        return result;
    }

    private void onAuthSuccess(FirebaseUser firebaseUser, String nombre, String tipoUsuario, String tipoBus) {
        String userId = firebaseUser.getUid();

        Map<String, Object> userData = new HashMap<>();
        userData.put("IdUsuario", firebaseUser.getUid());
        userData.put("Nombre", nombre);
        userData.put("TipoUsuario", tipoUsuario);
        userData.put("Correo", firebaseUser.getEmail());

        if ("CONDUCTOR".equals(tipoUsuario)) {
            userData.put("TipoBus", tipoBus);
        }

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId);
        userRef.setValue(userData)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(RegisterActivity.this, "Error al guardar los datos adicionales", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void hideProgressDialog() {
        // Aquí puedes ocultar el diálogo de progreso
    }
}
