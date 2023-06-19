package pe.carlos.undcbusestudiante;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
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

public class RegisterActivity extends AppCompatActivity {

    Button btnRegistrar;
    EditText CorreoIntitucional, Nombre, editTextPassword;
    Spinner spTipoUsuario;
    FirebaseAuth firebaseAuth;
    CheckBox cbTerminosCondiciones;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        CorreoIntitucional = findViewById(R.id.edtCorreoInstitucional);
        Nombre = findViewById(R.id.edtNombre);
        editTextPassword = findViewById(R.id.edtContrasena);
        btnRegistrar = findViewById(R.id.btnRegistrar);
        spTipoUsuario = findViewById(R.id.spTipoUsuario);
        firebaseAuth = FirebaseAuth.getInstance();
        cbTerminosCondiciones = findViewById(R.id.cbTerminosCondiciones);

        // Crear una lista de elementos para el Spinner
        List<String> tiposUsuarios = new ArrayList<>();
        tiposUsuarios.add("ESTUDIANTE");
        tiposUsuarios.add("DOCENTE");
        tiposUsuarios.add("ADMINISTRATIVO");

        // Crear un adaptador utilizando el contexto, el layout para los elementos y la lista de valores
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, tiposUsuarios);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTipoUsuario.setAdapter(adapter);

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

                CheckBox cbTerminosCondiciones = findViewById(R.id.cbTerminosCondiciones);
                boolean isChecked = cbTerminosCondiciones.isChecked();

                if (!validateForm()) {
                    return;
                }

                if (!isChecked) {
                    // El CheckBox no está seleccionado, mostrar un mensaje de error o realizar acciones adicionales según tus necesidades
                    Toast.makeText(RegisterActivity.this, "Debes aceptar los términos y condiciones", Toast.LENGTH_SHORT).show();
                    return;
                }

                firebaseAuth.createUserWithEmailAndPassword(correoInstitucional, contrasena)
                        .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser firebaseUser = task.getResult().getUser();
                                    onAuthSuccess(firebaseUser, nombre, tipoUsuario);
                                    firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                // Correo de verificación enviado exitosamente
                                                Toast.makeText(RegisterActivity.this, "Se ha enviado un correo de verificación", Toast.LENGTH_SHORT).show();
                                            } else {
                                                // Error al enviar el correo de verificación
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

        // Crea una lista para almacenar los campos a validar
        List<EditText> editTextList = new ArrayList<>();

        // Agrega los campos a la lista
        editTextList.add(CorreoIntitucional);
        editTextList.add(Nombre);
        editTextList.add(editTextPassword);

        // Itera sobre la lista y valida cada campo
        for (EditText editText : editTextList) {
            String fieldValue = editText.getText().toString();

            if (TextUtils.isEmpty(fieldValue)) {
                editText.setError("Requerido");
                result = false;
            } else {
                editText.setError(null);
            }
        }

        // Verifica la validez del correo institucional
        String email = CorreoIntitucional.getText().toString();
        if (!TextUtils.isEmpty(email) && !email.endsWith("@undc.edu.pe")) {
            CorreoIntitucional.setError("Utiliza tu correo institucional");
            result = false;
        }

        return result;
    }

    private void onAuthSuccess(FirebaseUser firebaseUser, String nombre, String tipoUsuario ) {
        String userId = firebaseUser.getUid();

        Map<String, Object> userData = new HashMap<>();
        userData.put("IdUsuario",firebaseUser.getUid());
        userData.put("Nombre", nombre);
        userData.put("TipoUsuario", tipoUsuario);
        userData.put("Correo", firebaseUser.getEmail());


        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId);
        userRef.setValue(userData)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                            //Toast.makeText(RegisterActivity.this, "Registro exitoso", Toast.LENGTH_SHORT).show();
                            // Aquí puedes iniciar una nueva actividad, por ejemplo:
                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                            startActivity(intent);

                            // También puedes finalizar la actividad actual si ya no es necesaria
                            finish();
                        } else {
                            Toast.makeText(RegisterActivity.this, "Error al guardar los datos adicionales", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void hideProgressDialog() {
        // Aquí puedes ocultar el diálogo de progreso
        // Por ejemplo, si estás utilizando un ProgressDialog, puedes llamar a progressDialog.dismiss()
    }
}