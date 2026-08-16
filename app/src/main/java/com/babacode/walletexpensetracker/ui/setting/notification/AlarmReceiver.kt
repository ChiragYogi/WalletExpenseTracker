package com.babacode.walletexpensetracker.ui.setting.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.babacode.walletexpensetracker.repository.SettingsRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class AlarmReceiver : BroadcastReceiver() {

    @Inject lateinit var settingsRepository: SettingsRepository

    override fun onReceive(context: Context?, intent: Intent?) {
        val ctx = context ?: return
        val alarmUtils = AlarmUtils(ctx)
        val pendingResult = goAsync()

        CoroutineScope(SupervisorJob() + Dispatchers.Default).launch {
            try {
                if (!settingsRepository.notificationsEnabled.first()) {
                    // Notifications were disabled after this alarm was scheduled; stop rearming.
                    alarmUtils.cancelNotificationAlarm()
                    return@launch
                }

                //create notification when broadcast is received
                NotificationUtils(ctx).launchNotification()

                // scheduling new notification for next day
                val calendar = Calendar.getInstance()
                calendar.add(Calendar.DAY_OF_YEAR, 1)
                alarmUtils.initAlarmForNotification(calendar)
            } finally {
                pendingResult.finish()
            }
        }
    }
}
