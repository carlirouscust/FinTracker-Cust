package ucne.edu.fintracker.notification

import android.content.Context
import androidx.work.*
import java.util.concurrent.TimeUnit

class NotificationScheduler(private val context: Context) {

    companion object {
        const val PERIODIC_NOTIFICATION_WORK = "periodic_notification_work"

        private const val REPEAT_INTERVAL = 24L
        private const val FLEX_TIME_INTERVAL = 4L
        private const val DEFAULT_TITLE = "Nueva Transacción"
        private const val DEFAULT_MESSAGE = "Se ha registrado una nueva transacción"
        private const val TITLE_KEY = "title"
        private const val MESSAGE_KEY = "message"
    }

    private val workManager = WorkManager.getInstance(context)

    fun schedulePeriodicNotifications() {
        val periodicWorkRequest = createPeriodicWorkRequest()
        workManager.enqueueUniquePeriodicWork(
            PERIODIC_NOTIFICATION_WORK,
            ExistingPeriodicWorkPolicy.KEEP,
            periodicWorkRequest
        )
    }

    fun cancelPeriodicNotifications() {
        workManager.cancelAllWorkByTag(PERIODIC_NOTIFICATION_WORK)
    }

    fun scheduleTransactionNotification(title: String, message: String) {
        val workRequest = createTransactionWorkRequest(title, message)
        workManager.enqueue(workRequest)
    }

    private fun createPeriodicWorkRequest(): PeriodicWorkRequest =
        PeriodicWorkRequestBuilder<NotificationWorker>(
            repeatInterval = REPEAT_INTERVAL,
            repeatIntervalTimeUnit = TimeUnit.HOURS,
            flexTimeInterval = FLEX_TIME_INTERVAL,
            flexTimeIntervalUnit = TimeUnit.HOURS
        )
            .setConstraints(createConstraints())
            .addTag(PERIODIC_NOTIFICATION_WORK)
            .build()

    private fun createTransactionWorkRequest(title: String, message: String): OneTimeWorkRequest =
        OneTimeWorkRequestBuilder<TransactionNotificationWorker>()
            .setInputData(createWorkData(title, message))
            .build()

    private fun createConstraints(): Constraints =
        Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .setRequiresBatteryNotLow(false)
            .build()

    private fun createWorkData(title: String, message: String): Data =
        workDataOf(
            TITLE_KEY to title,
            MESSAGE_KEY to message
        )
}

class TransactionNotificationWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    companion object {
        private const val DEFAULT_TITLE = "Nueva Transacción"
        private const val DEFAULT_MESSAGE = "Se ha registrado una nueva transacción"
        private const val TITLE_KEY = "title"
        private const val MESSAGE_KEY = "message"
    }

    override suspend fun doWork(): Result =
        try {
            processTransactionNotification()
            Result.success()
        } catch (exception: Exception) {
            Result.failure()
        }

    private fun processTransactionNotification() {
        val (title, message) = extractNotificationData()
        val notificationManager = NotificationManagerImpl(applicationContext)
        notificationManager.showTransactionAlert(title, message)
    }

    private fun extractNotificationData(): Pair<String, String> =
        Pair(
            inputData.getString(TITLE_KEY) ?: DEFAULT_TITLE,
            inputData.getString(MESSAGE_KEY) ?: DEFAULT_MESSAGE
        )
}