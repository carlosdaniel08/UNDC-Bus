package pe.carlos.undcbusestudiante.Service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.Random;

import pe.carlos.undcbusestudiante.Activitys.MainActivity;
import pe.carlos.undcbusestudiante.R;

public class Fcm extends FirebaseMessagingService {

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        Log.e("token", "mi token es:" + s);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        String from = remoteMessage.getFrom();

        if (remoteMessage.getData().size() > 0) {
            String titulo = remoteMessage.getData().get("titulo");
            String detalle = remoteMessage.getData().get("detalle");
            String foto = remoteMessage.getData().get("foto");

            // Extraer el nombre del emisor de la notificación
            String senderName = remoteMessage.getData().get("senderName");
            if (senderName != null && !senderName.isEmpty()) {
                titulo = senderName + ": " + titulo;
            }

            mayorqueoreo(titulo, detalle, foto);
        }
    }

    private void mayorqueoreo(String titulo, String detalle, String foto) {
        String id = "mensaje";
        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, id);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel nc = new NotificationChannel(id, "nuevo", NotificationManager.IMPORTANCE_HIGH);
            nc.setShowBadge(true);
            assert nm != null;
            nm.createNotificationChannel(nc);
        }

        try {
            Bitmap imf_foto = Picasso.get().load(foto).get();
            PendingIntent pendingIntent = clicknoti();
            if (pendingIntent == null) {
                // Manejar el caso en el que el PendingIntent es null antes de usarlo
                Log.e("Fcm", "PendingIntent is null, not showing notification");
                return;
            }

            builder.setAutoCancel(true)
                    .setWhen(System.currentTimeMillis())
                    .setContentTitle(titulo)
                    .setSmallIcon(R.drawable.vibration_mode_color)
                    .setContentText(detalle)
                    .setStyle(new NotificationCompat.BigPictureStyle()
                            .bigPicture(imf_foto).bigLargeIcon(null))
                    .setContentIntent(pendingIntent)
                    .setContentInfo("nuevo");

            Random random = new Random();
            int idNotity = random.nextInt(8000);

            assert nm != null;
            nm.notify(idNotity, builder.build());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public PendingIntent clicknoti() {
        Intent nf = new Intent(getApplicationContext(), MainActivity.class);
        nf.putExtra("color", "rojo");
        nf.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            flags |= PendingIntent.FLAG_IMMUTABLE;
        }

        return PendingIntent.getActivity(this, 0, nf, flags);
    }
}
