package com.example.mobileshop.worker

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.mobileshop.DismissNotificationReceiver
import com.example.mobileshop.R
import com.example.mobileshop.utils.Constants.CHANNEL_ID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

class CountDownWorker(private val context: Context, params: WorkerParameters): CoroutineWorker(context,params) {

    @SuppressLint("MissingPermission")
    override suspend fun doWork(): Result {
        //perform task here


        val notificationBuilder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.notification_bar_icon)
            .setContentTitle("title")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setOngoing(true)

        val dismissIntent = Intent("com.example.ACTION_DISMISS_NOTIFICATION")
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            dismissIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )
        val dismissNotificationReceiver = DismissNotificationReceiver()
        applicationContext.registerReceiver(dismissNotificationReceiver, IntentFilter("com.example.ACTION_DISMISS_NOTIFICATION"))

  

        val notificationManager = NotificationManagerCompat.from(applicationContext)


        withContext(Dispatchers.Default) {
            val countdownFlow = getCountdownFlow()

            countdownFlow.collect { count ->
                notificationBuilder.setContentText("Countdown: $count")
                notificationManager.notify(1, notificationBuilder.build())

            }
        }
        return Result.success()
    }

    private fun getCountdownFlow(): Flow<Int> = flow {
        val dismissIntent = Intent("com.example.ACTION_DISMISS_NOTIFICATION")
            .putExtra("notifId",1)
        val countDownSeconds = 10

        for (count in countDownSeconds downTo 0) {
            emit(count)
            delay(1000)
            if (count<1)
                applicationContext.sendBroadcast(dismissIntent)

        }
    }


}