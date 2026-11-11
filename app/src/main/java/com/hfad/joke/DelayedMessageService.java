package com.hfad.joke;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

/**
 * Started Service implementado con IntentService.
 *
 * Recibe un intent con un mensaje, espera 10 segundos en un hilo
 * secundario (el que IntentService crea automaticamente) y:
 *   1. Registra el mensaje en Logcat.
 *   2. Emite una notificacion en la barra de estado.
 *
 * Contexto academico: se usa para notificar al estudiante la
 * calificacion de un taller/parcial, o al docente que un estudiante
 * entrego una tarea pendiente de calificar.
 */
public class DelayedMessageService extends IntentService {

    public static final String EXTRA_MESSAGE = "message";
    public static final String EXTRA_TITLE   = "title";
    public static final int NOTIFICATION_ID  = 5453;
    public static final String CHANNEL_ID    = "joke_channel";

    public DelayedMessageService() {
        super("DelayedMessageService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        synchronized (this) {
            try {
                wait(10000); // 10 segundos simulan el procesamiento en background
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        String title = intent.getStringExtra(EXTRA_TITLE);
        String text  = intent.getStringExtra(EXTRA_MESSAGE);
        showText(title, text);
    }

    private void showText(final String title, final String text) {
        // 1. Log en Logcat (primera etapa del laboratorio)
        Log.v("DelayedMessageService", "The message is: " + text);

        // 2. Construir la notificacion
        Intent actionIntent = new Intent(this, MainActivity.class);
        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            flags |= PendingIntent.FLAG_IMMUTABLE;
        }
        PendingIntent actionPendingIntent = PendingIntent.getActivity(
                this, 0, actionIntent, flags);

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(android.R.drawable.sym_def_app_icon)
                        .setContentTitle(title != null ? title : getString(R.string.question))
                        .setContentText(text)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(text))
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setVibrate(new long[]{0, 1000})
                        .setAutoCancel(true)
                        .setContentIntent(actionPendingIntent);

        // 3. Emitir la notificacion
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(NOTIFICATION_ID, builder.build());
        }
    }

    /**
     * El canal es obligatorio desde API 26 (Android 8.0 Oreo).
     * En versiones anteriores la llamada se ignora.
     */
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Notificaciones Joke",
                    NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Canal para avisos de calificaciones y entregas");
            channel.enableVibration(true);

            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }
}
