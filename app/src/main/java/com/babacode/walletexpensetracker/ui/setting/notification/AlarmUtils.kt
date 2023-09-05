package com.babacode.walletexpensetracker.ui.setting.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.util.*

class AlarmUtils constructor(context: Context) {

    private var mContext = context
    private var alarmMgr: AlarmManager? =
        mContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager


    private val alarmIntent = Intent(mContext, AlarmReceiver::class.java).let { mIntent ->
        PendingIntent.getBroadcast(mContext, 100, mIntent,  PendingIntent.FLAG_IMMUTABLE)
    }


    fun initAlarmForNotification(calendar: Calendar) {

        calendar.apply {
            set(Calendar.HOUR_OF_DAY, 20)
            set(Calendar.MINUTE, 10)
            set(Calendar.SECOND, 0)
        }

        val time = calendar.timeInMillis
        val systemTime = System.currentTimeMillis()

        if (systemTime > time) {
            alarmMgr?.cancel(alarmIntent)
            val timeForNextDay = setAlarmForNextDay(calendar)
            setAlarmOnTime(timeForNextDay)

        } else {
            setAlarmOnTime(time)
           }


    }

    private fun setAlarmForNextDay(calendar: Calendar): Long {
        calendar.add(Calendar.DAY_OF_YEAR, 1)
        calendar.apply {
            set(Calendar.HOUR_OF_DAY, 20)
            set(Calendar.MINUTE, 10)
            set(Calendar.SECOND, 0)
        }
        return calendar.timeInMillis


    }


    private fun setAlarmOnTime(time: Long) {
        alarmMgr?.setAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            time,
            alarmIntent
        )

    }


    fun cancelNotificationAlarm() {
        alarmMgr?.cancel(alarmIntent)
    }

}