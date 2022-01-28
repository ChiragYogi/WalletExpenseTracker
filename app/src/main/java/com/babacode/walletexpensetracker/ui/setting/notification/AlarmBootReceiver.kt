package com.babacode.walletexpensetracker.ui.setting.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import java.util.*

class AlarmBootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {

            // sending new alarm for today when device reboot
            val calendar = Calendar.getInstance()
            val alarmUtils = AlarmUtils(context)
            alarmUtils.initAlarmForNotification(calendar)
        }
    }
}