package com.babacode.walletexpensetracker.ui.setting.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import java.text.SimpleDateFormat
import java.util.*

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {

        //create notification when broadcast is received
        val notificationUtils = NotificationUtils(context!!)
        notificationUtils.launchNotification()


        // scheduling new notification for next day
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, 1)
        val alarmUtils = AlarmUtils(context)
        alarmUtils.initAlarmForNotification(calendar)

    }
}