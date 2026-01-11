package de.alex.lightweights.services

import android.app.NotificationChannel
import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.core.app.NotificationCompat
import android.app.NotificationManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import de.alex.lightweights.R

class TimerWorker(private val context: Context, workerParams: WorkerParameters) : CoroutineWorker(context, workerParams) {
    companion object {
        const val DURATION_KEY = "timer_duration"
        const val NOTIFICATION_CHANNEL_ID = "timer_channel"
        const val NOTIFICATION_ID = 1
    }

    override suspend fun doWork(): Result {
        val durationInSeconds = inputData.getInt(DURATION_KEY, 0)
        if (durationInSeconds <= 0) return Result.failure()

        // Countdown simulieren, da der Worker im Hintergrund läuft
        kotlinx.coroutines.delay(durationInSeconds * 1000L)

        // Timer ist abgelaufen, zeige Benachrichtigung und vibriere
        showNotification()

        return Result.success()
    }

    private fun showNotification() {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        createNotificationChannel(notificationManager)

        val notification = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Ersetze dies durch ein passendes Icon
            .setContentTitle("Pause vorbei!")
            .setContentText("Deine Pause ist zu Ende. Zeit für den nächsten Satz!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun createNotificationChannel(notificationManager: NotificationManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            // 1. Definiere dein Vibrationsmuster
            // Format: [Pause, Vibration, Pause, Vibration, ...]
            val localVibrationPattern = longArrayOf(0, 400, 100, 400) // 0ms warten, 400ms vibrieren, 100ms warten, 400ms vibrieren

            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "Pausentimer",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Benachrichtigungen für den abgelaufenen Pausentimer"

                // 2. Aktiviere die Vibration und weise das Muster zu
                enableVibration(true)
                vibrationPattern = localVibrationPattern
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun vibrate() {
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (vibrator.hasVibrator()) {
            // Vibrieren für 2x 200ms mit 100ms Pause
            val timings = longArrayOf(0, 200, 100, 200)
            val effect = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                VibrationEffect.createWaveform(timings, -1)
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(timings, -1)
                return
            }
            vibrator.vibrate(effect)
        }
    }
}