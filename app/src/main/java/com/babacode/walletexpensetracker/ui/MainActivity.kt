package com.babacode.walletexpensetracker.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment

import androidx.navigation.ui.setupActionBarWithNavController
import com.babacode.walletexpensetracker.R
import com.babacode.walletexpensetracker.databinding.ActivityMainBinding
import com.babacode.walletexpensetracker.ui.setting.notification.AlarmUtils
import com.babacode.walletexpensetracker.utiles.SettingUtils
import com.google.android.material.snackbar.Snackbar
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

        checkForNotificationPermission()

    }

    private fun checkForNotificationPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_DENIED
        ) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (!isGranted) {
                showSettingSnackBar()
            }
        }

    private fun showSettingSnackBar() {
        Snackbar.make(
            binding.root,
            R.string.notification_permission_text,
            Snackbar.LENGTH_LONG
        ).setAction(R.string.open) {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            intent.data = Uri.parse("package:$packageName")
            startActivity(intent)
        }.show()
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }


}


const val ADD_TRANSACTION_RESULT_OK = Activity.RESULT_FIRST_USER
const val EDIT_TRANSACTION_RESULT_OK = Activity.RESULT_FIRST_USER + 1