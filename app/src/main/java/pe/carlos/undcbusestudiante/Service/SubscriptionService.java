package pe.carlos.undcbusestudiante.Service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

public class SubscriptionService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;  // Este es un servicio no vinculado, por lo que devolvemos null.
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Suscribir a los usuarios al tema "enviaratodos" cuando el servicio se inicia
        FirebaseMessaging.getInstance().subscribeToTopic("enviaratodos")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = task.isSuccessful() ? "Suscripción exitosa" : "Error en la suscripción";
                        // Opcional: puedes mostrar un mensaje o hacer un log del resultado
                        // Por ejemplo:
                        // Log.d("SubscriptionService", msg);
                    }
                });
        return START_NOT_STICKY;  // No reiniciar el servicio si el sistema lo mata.
    }
}
