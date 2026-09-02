package com.babacode.walletexpensetracker.ui.setting.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.babacode.walletexpensetracker.R
import com.babacode.walletexpensetracker.ui.MainActivity


class NotificationUtils(context: Context) {


    private val mContext = context
    private lateinit var notificationBuilder: NotificationCompat.Builder
    private val notificationManager = NotificationManagerCompat.from(mContext)


    init {
        createNotification()
        initNotificationBuilder()
    }

    private fun initNotificationBuilder() {

        //Setting Sound for Notification
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)


        //Intent For Add Transaction Screen
        val intent = Intent(mContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            mContext,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        //Create Notification With Notification Builder
        notificationBuilder = NotificationCompat.Builder(mContext, MY_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(MY_NOTIFICATION_TITLE)
            .setContentText(MY_NOTIFICATION_TEXT)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
    }

    private fun createNotification() {


        val name = MY_CHANNEL_NAME
        val description = MY_CHANNEL_DESCRIPTION
        val importance = NotificationManager.IMPORTANCE_DEFAULT


        val notificationChannel = NotificationChannel(
            MY_CHANNEL_ID,
            name,
            importance
        ).apply {
            this.description = description
        }


        val notificationManagerForChannel =
            mContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManagerForChannel.createNotificationChannel(notificationChannel)


    }

    fun launchNotification() {
        with(NotificationManagerCompat.from(mContext)) {
            if (ActivityCompat.checkSelfPermission(
                    mContext,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            notificationManager.notify(MY_NOTIFICATION_ID, notificationBuilder.build())
        }

    }



    companion object {
        const val MY_CHANNEL_ID = "Wallet Expense Tracker Notification Channel Id"
        const val MY_CHANNEL_NAME = "Wallet Expense Tracker Notification"
        const val MY_NOTIFICATION_ID = 0
        const val MY_CHANNEL_DESCRIPTION = "Notification for Wallet Expense Tracker Notification"
        const val MY_NOTIFICATION_TITLE = "Forget to add transaction?"
        const val MY_NOTIFICATION_TEXT = "Click to add transaction for today"

    }

}