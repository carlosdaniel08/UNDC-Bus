package pe.carlos.undcbusestudiante.Activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pe.carlos.undcbusestudiante.Administrador.AdministradorActivity;
import pe.carlos.undcbusestudiante.R;
import pe.carlos.undcbusestudiante.Service.SubscriptionService;

public class LoginActivity extends AppCompatActivity {

    Button btnLogin;
    EditText correoInstitucional, editTextPassword;
    FirebaseAuth firebaseAuth;
    TextView tvRegistrate;
    private int cursorPosition = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        correoInstitucional = findViewById(R.id.edtCorreoInstitucional);
        editTextPassword = findViewById(R.id.edtContrasena);


        btnLogin = findViewById(R.id.btnIngresar);
        firebaseAuth = FirebaseAuth.getInstance();
        tvRegistrate = findViewById(R.id.tvRegistrate);

        Drawable drawableVisible = getResources().getDrawable(R.drawable.see_no_evil_monkey_color);
        Drawable drawableHidden = getResources().getDrawable(R.drawable.hear_no_evil_monkey_color);

        TextView tvRecuperarContrasena = findViewById(R.id.tvRecuperarContraseña);
        tvRecuperarContrasena.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String correo = correoInstitucional.getText().toString().trim();

                if (TextUtils.isEmpty(correo)) {
                    // Validar si se ha ingresado un correo electrónico
                    Toast.makeText(LoginActivity.this, "Por favor, ingresa tu correo electrónico", Toast.LENGTH_SHORT).show();
                    return;
                }

                firebaseAuth.sendPasswordResetEmail(correo)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(LoginActivity.this, "Se ha enviado un correo para restablecer tu contraseña", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(LoginActivity.this, "Error al enviar el correo de recuperación de contraseña", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        CheckBox cbRecordarCorreo = findViewById(R.id.cbRecordarCorreo);
        cbRecordarCorreo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Guardar la preferencia de recordar el correo utilizando SharedPreferences
                SharedPreferences sharedPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("recordarCorreo", isChecked);
                editor.apply();
            }
        });

        // Recuperar el correo almacenado en la preferencia compartida y mostrarlo en el campo correspondiente
        SharedPreferences sharedPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        boolean recordarCorreo = sharedPreferences.getBoolean("recordarCorreo", false);
        cbRecordarCorreo.setChecked(recordarCorreo);
        if (recordarCorreo) {
            String correoGuardado = sharedPreferences.getString("correoGuardado", "");
            correoInstitucional.setText(correoGuardado);
        }

        // lógica para cambiar la visibilidad al hacer clic en el ícono
        editTextPassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (isTouchOnPasswordToggle(event)) {
                        saveCursorPosition();
                        togglePasswordVisibility();
                        restoreCursorPosition();
                        return true;
                    }
                }
                return false;
            }

            private boolean isTouchOnPasswordToggle(MotionEvent event) {
                int drawableRight = editTextPassword.getRight();
                int drawableWidth = editTextPassword.getCompoundDrawables()[2].getBounds().width();
                float touchX = event.getRawX();
                return touchX >= (drawableRight - drawableWidth);
            }

            private void saveCursorPosition() {
                cursorPosition = editTextPassword.getSelectionStart();
            }

            private void restoreCursorPosition() {
                editTextPassword.setSelection(cursorPosition);
            }

            private void togglePasswordVisibility() {
                int inputType = editTextPassword.getInputType();
                int newInputType = inputType == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)
                        ? (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD)
                        : (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                editTextPassword.setInputType(newInputType);
                updatePasswordToggleIcon(inputType);
            }

            private void updatePasswordToggleIcon(int inputType) {
                Drawable toggleIcon = inputType == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)
                        ? drawableHidden
                        : drawableVisible;
                editTextPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, toggleIcon, null);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String correo = correoInstitucional.getText().toString().trim();
                String contrasena = editTextPassword.getText().toString().trim();

                if (!validateForm()) {
                    return;
                }

                // Guardar el correo ingresado si la opción de recordar correo está habilitada
                if (cbRecordarCorreo.isChecked()) {
                    SharedPreferences sharedPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("correoGuardado", correo);
                    editor.apply();
                }

                firebaseAuth.signInWithEmailAndPassword(correo, contrasena)
                        .addOnCompleteListener(LoginActivity.this, task -> {
                            if (task.isSuccessful()) {
                                FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                                if (currentUser != null && !currentUser.isEmailVerified()) {
                                    // El correo electrónico no está verificado
                                    //Borrar despues esto

                                    // Inicio de sesión exitoso y correo electrónico verificado
                                    // Consulta la base de datos para obtener el tipo de usuario
                                    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(currentUser.getUid()).child("TipoUsuario");
                                    userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            String userType = dataSnapshot.getValue(String.class);
                                            Intent intent = new Intent(LoginActivity.this, SubscriptionService.class);
                                            startService(intent);
                                            Log.d("LoginActivity", "Tipo de usuario: " + userType); // Agregar este log
                                            if ("ADMINISTRADOR".equals(userType)) {
                                                // Inicia la actividad de administrador
                                                startActivity(new Intent(LoginActivity.this, AdministradorActivity.class));
                                            } else {
                                                // Inicia la actividad para usuarios regulares
                                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                            // Manejar error
                                            Toast.makeText(LoginActivity.this, "Error al obtener el tipo de usuario", Toast.LENGTH_SHORT).show();
                                        }
                                    });


                                    Toast.makeText(LoginActivity.this, "Por favor, verifica tu correo electrónico", Toast.LENGTH_SHORT).show();
                                    // firebaseAuth.signOut(); // Cerrar sesión del usuario



                                } else {
                                    // Inicio de sesión exitoso y correo electrónico verificado
                                    // Consulta la base de datos para obtener el tipo de usuario
                                    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(currentUser.getUid()).child("TipoUsuario");
                                    userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            String userType = dataSnapshot.getValue(String.class);
                                            Log.d("LoginActivity", "Tipo de usuario: " + userType); // Agregar este log
                                            if ("ADMINISTRADOR".equals(userType)) {
                                                // Inicia la actividad de administrador
                                                startActivity(new Intent(LoginActivity.this, AdministradorActivity.class));
                                            } else {
                                                // Inicia la actividad para usuarios regulares
                                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                            // Manejar error
                                            Toast.makeText(LoginActivity.this, "Error al obtener el tipo de usuario", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            } else {
                                // Error en el inicio de sesión
                                Exception exception = task.getException();
                                if (exception != null) {
                                    String errorMessage = exception.getMessage();

                                    // Mapear los mensajes de error en inglés a las traducciones en español
                                    Map<String, String> errorMap = new HashMap<>();
                                    errorMap.put("The email address is badly formatted.", getString(R.string.firebase_error_invalid_email));
                                    errorMap.put("The password is invalid or the user does not have a password.", getString(R.string.firebase_error_wrong_password));
                                    errorMap.put("There is no user record corresponding to this identifier. The user may have been deleted.", getString(R.string.firebase_error_user_not_found));
                                    errorMap.put("The email address is already in use by another account.", getString(R.string.firebase_error_email_already_in_use));
                                    errorMap.put("The given password is invalid. [ Password should be at least 6 characters ]", getString(R.string.firebase_error_weak_password));
                                    errorMap.put("A network error (such as timeout, interrupted connection, or unreachable host) has occurred.", getString(R.string.firebase_error_network_error));

                                    // Obtener la traducción del mensaje de error o mostrar el mensaje original si no está mapeado
                                    String translatedErrorMessage = errorMap.getOrDefault(errorMessage, "Error: " + errorMessage);
                                    Toast.makeText(LoginActivity.this, translatedErrorMessage, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });


            }
        });

        tvRegistrate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    private boolean validateForm() {
        boolean result = true;

        // Crea una lista para almacenar los campos a validar
        List<EditText> editTextList = new ArrayList<>();

        // Agrega los campos a la lista
        editTextList.add(correoInstitucional);
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
        String email = correoInstitucional.getText().toString();
        if (!TextUtils.isEmpty(email) && !email.endsWith("@undc.edu.pe")) {
            correoInstitucional.setError("Utiliza tu correo institucional");
            result = false;
        }

        return result;
    }
}
