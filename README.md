# Laboratorio — Started Services (IntentService)

**Asignatura:** Sistemas Convergentes
**Universidad Piloto de Colombia — Ingenieria de Sistemas**
**Autor:** Diego Alejandro Betancur Herrera

## Objetivo

1. Implementar un servicio iniciado (Started Service) en Android usando la clase `IntentService`, que se ejecuta en background independientemente del ciclo de vida de la actividad que lo lanza.
2. Extender la funcionalidad del laboratorio incorporando el **servicio de notificaciones** de Android para informar al usuario cuando el trabajo en background termina.

## Contexto academico de la extension

El enunciado pedia agregar una notificacion relacionada con el proyecto. Se adapto al dominio academico:

- **Calificacion publicada:** se simula que un docente publica la nota de un taller/parcial. Tras 10 s (procesamiento en background) el estudiante recibe una notificacion con la calificacion.
- **Entrega recibida:** se simula que un estudiante entrega un taller. Tras 10 s el docente recibe la notificacion indicando que hay trabajo pendiente de calificar.

Ambos flujos usan el mismo `DelayedMessageService` parametrizado por los extras `EXTRA_TITLE` y `EXTRA_MESSAGE`.

## Estructura del proyecto

```
Joke/
 |-- app/
 |    |-- build.gradle
 |    |-- src/main/
 |         |-- AndroidManifest.xml
 |         |-- java/com/hfad/joke/
 |         |    |-- MainActivity.java
 |         |    |-- DelayedMessageService.java
 |         |-- res/
 |              |-- layout/activity_main.xml
 |              |-- values/strings.xml
 |-- build.gradle
 |-- settings.gradle
```

## Flujo del servicio

1. El usuario pulsa uno de los 3 botones en `MainActivity`.
2. `MainActivity` crea un `Intent` con el titulo y mensaje y llama `startService(intent)`.
3. `DelayedMessageService.onHandleIntent()` se ejecuta en un hilo secundario y espera 10 s.
4. Se registra el mensaje en Logcat (`Log.v`).
5. Se construye la notificacion con `NotificationCompat.Builder` y se emite con `NotificationManager.notify()`.
6. Al pulsar la notificacion se abre `MainActivity` via `PendingIntent`.
7. El servicio termina y es destruido (`onDestroy()`).

## Consideraciones tecnicas

- **minSdk 19 / targetSdk 34** segun lo indicado en la guia, pero actualizado para ejecutarse en dispositivos actuales.
- **NotificationChannel:** se crea en `onCreate()` del servicio, obligatorio desde API 26 (Android 8.0 Oreo).
- **POST_NOTIFICATIONS:** permiso declarado y solicitado en runtime a partir de API 33 (Android 13).
- **PendingIntent.FLAG_IMMUTABLE:** agregado a partir de API 23 para cumplir con los requisitos de seguridad modernos.
- Se mantiene `IntentService` tal como pide la guia; en produccion moderna se recomendaria `WorkManager` o `JobIntentService`.

## Como probar

1. Abrir el proyecto en Android Studio.
2. Dejar que Gradle sincronice las dependencias.
3. Ejecutar sobre un emulador o dispositivo fisico (API >= 19).
4. Pulsar cualquier boton. A los 10 s:
   - Aparece el mensaje en Logcat (filtro `DelayedMessageService`).
   - Aparece la notificacion en la barra de estado con vibracion.
5. Pulsar la notificacion: debe reabrir `MainActivity`.
