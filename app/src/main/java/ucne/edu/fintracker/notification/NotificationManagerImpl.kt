package ucne.edu.fintracker.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import ucne.edu.fintracker.MainActivity
import ucne.edu.fintracker.R

class NotificationManagerImpl(private val context: Context) {

    companion object {
        const val CHANNEL_ID_TRANSACTIONS = "transactions_channel"
        const val CHANNEL_ID_TIPS = "tips_channel"
        const val CHANNEL_ID_GOALS = "goals_channel"

        const val NOTIFICATION_ID_TRANSACTION = 1001
        const val NOTIFICATION_ID_TIP = 1002
        const val NOTIFICATION_ID_GOAL = 1003

        private const val INTENT_FLAGS = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        private const val PENDING_INTENT_FLAGS = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    }

    init {
        createNotificationChannels()
    }

    fun showTransactionAlert(title: String, message: String) =
        showNotification(
            channelId = CHANNEL_ID_TRANSACTIONS,
            notificationId = NOTIFICATION_ID_TRANSACTION,
            title = title,
            message = message,
            priority = NotificationCompat.PRIORITY_DEFAULT
        )

    fun showFinancialTip(title: String, message: String) =
        showNotification(
            channelId = CHANNEL_ID_TIPS,
            notificationId = NOTIFICATION_ID_TIP,
            title = title,
            message = message,
            priority = NotificationCompat.PRIORITY_LOW
        )

    fun showGoalUpdate(title: String, message: String) =
        showNotification(
            channelId = CHANNEL_ID_GOALS,
            notificationId = NOTIFICATION_ID_GOAL,
            title = title,
            message = message,
            priority = NotificationCompat.PRIORITY_DEFAULT
        )

    private fun showNotification(
        channelId: String,
        notificationId: Int,
        title: String,
        message: String,
        priority: Int
    ) {
        if (!hasNotificationPermission()) return

        val notification = buildNotification(channelId, title, message, priority)

        try {
            NotificationManagerCompat.from(context).notify(notificationId, notification)
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    private fun buildNotification(
        channelId: String,
        title: String,
        message: String,
        priority: Int
    ) = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(R.mipmap.ic_launcher)
        .setContentTitle(title)
        .setContentText(message)
        .setPriority(priority)
        .setContentIntent(createPendingIntent())
        .setAutoCancel(true)
        .build()

    private fun createPendingIntent(): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = INTENT_FLAGS
        }
        return PendingIntent.getActivity(context, 0, intent, PENDING_INTENT_FLAGS)
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channels = listOf(
                createChannel(
                    CHANNEL_ID_TRANSACTIONS,
                    "Alertas de Transacciones",
                    "Notificaciones sobre transacciones y límites de gastos",
                    NotificationManager.IMPORTANCE_DEFAULT
                ),
                createChannel(
                    CHANNEL_ID_TIPS,
                    "Consejos Financieros",
                    "Consejos y sugerencias financieras",
                    NotificationManager.IMPORTANCE_LOW
                ),
                createChannel(
                    CHANNEL_ID_GOALS,
                    "Metas de Ahorro",
                    "Actualizaciones sobre metas de ahorro",
                    NotificationManager.IMPORTANCE_DEFAULT
                )
            )

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            channels.forEach { channel ->
                notificationManager.createNotificationChannel(channel)
            }
        }
    }

    @androidx.annotation.RequiresApi(Build.VERSION_CODES.O)
    private fun createChannel(
        channelId: String,
        name: String,
        description: String,
        importance: Int
    ): NotificationChannel = NotificationChannel(channelId, name, importance).apply {
        this.description = description
    }

    private fun hasNotificationPermission(): Boolean =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            NotificationManagerCompat.from(context).areNotificationsEnabled()
        }
}