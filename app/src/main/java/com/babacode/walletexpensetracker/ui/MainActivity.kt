package com.babacode.walletexpensetracker.ui

import android.app.Activity
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.babacode.walletexpensetracker.R
import com.babacode.walletexpensetracker.databinding.ActivityMainBinding
import com.babacode.walletexpensetracker.ui.setting.notification.AlarmUtils
import com.babacode.walletexpensetracker.utiles.SettingUtils
import dagger.hilt.android.AndroidEntryPoint
import java.util.*


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var navController: NavController

    private val settingUtilsForNotification by lazy {
        SettingUtils(this)
    }
    private val alarmUtils by lazy {
        AlarmUtils(applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container) as NavHostFragment

        navController = navHostFragment.navController


        setupActionBarWithNavController(navController)


        val toSetAlarm = settingUtilsForNotification.notificationForAlarm()

        if (toSetAlarm) {
            val calendar = Calendar.getInstance()
            alarmUtils.initAlarmForNotification(calendar)
        } else {
            alarmUtils.cancelNotificationAlarm()
        }


    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }


}


const val ADD_TRANSACTION_RESULT_OK = Activity.RESULT_FIRST_USER
const val EDIT_TRANSACTION_RESULT_OK = Activity.RESULT_FIRST_USER + 1