package com.babacode.walletexpensetracker.ui.setting.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.navigation.NavDeepLinkBuilder
import com.babacode.walletexpensetracker.R


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
        val pendingIntent = NavDeepLinkBuilder(mContext)
            .setGraph(R.navigation.nav_graph)
            .setDestination(R.id.homeFragment)
            .createPendingIntent()

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


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

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
    }

    fun launchNotification() {
        with(NotificationManagerCompat.from(mContext)) {
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