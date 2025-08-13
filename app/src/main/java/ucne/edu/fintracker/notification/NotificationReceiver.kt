package ucne.edu.fintracker.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_BOOT_COMPLETED -> {
                val notificationScheduler = NotificationScheduler(context)
                notificationScheduler.schedulePeriodicNotifications()
            }
        }
    }
}