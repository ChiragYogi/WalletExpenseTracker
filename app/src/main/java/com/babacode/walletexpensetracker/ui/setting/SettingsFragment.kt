package com.babacode.walletexpensetracker.ui.setting


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit
import androidx.preference.*
import com.babacode.walletexpensetracker.R
import com.babacode.walletexpensetracker.utiles.Extra.privacy_policy_url
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SettingsFragment : PreferenceFragmentCompat() {


    private val themePreference by lazy {
        findPreference<ListPreference>(getString(R.string.themeKey))
    }

    private val themeProvider by lazy { ThemeProvider(requireContext()) }

    private val notificationPreference by lazy {
        findPreference<SwitchPreferenceCompat>(getString(R.string.notificationKey))
    }

    private val currencyPreference by lazy {
        findPreference<ListPreference>(getString(R.string.currencyKey))
    }

    private val requestAFeature by lazy {
        findPreference<Preference>(getString(R.string.contactSupportKey))
    }

    private val reportBugPreference by lazy {
        findPreference<Preference>(getString(R.string.raiseBugKey))
    }

    private val showPrivacyPolicyPreference by lazy {
        findPreference<Preference>(getString(R.string.privacyPolicy))
    }


    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        setThemePreferences()
        setCurrencyPreferences()
        setNotificationPreferences()
        reportPreference()
        requestNewFeature()
        showPrivacyPolicyDialog()


    }

    private fun reportPreference() {
        reportBugPreference?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            val subject = context?.getString(R.string.foundABugInApp)
            val email = context?.getString(R.string.emailForQuery)

            val selectIntent = Intent().apply {
                action = Intent.ACTION_SENDTO
                data = Uri.parse("mailto:")
            }
            val emailIntent = Intent(Intent.ACTION_SEND).apply {
                putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
                putExtra(Intent.EXTRA_SUBJECT, subject)
                this.selector = selectIntent
            }

            try {
                startActivity(Intent.createChooser(emailIntent, "send mail using..."))
            } catch (e: Exception) {
                Toast.makeText(
                    context,
                    context?.getString(R.string.emailError),
                    Toast.LENGTH_LONG
                ).show()
                e.printStackTrace()
            }
            true
        }


    }


    private fun requestNewFeature() {
        requestAFeature?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            val subject = context?.getString(R.string.requestFromUser)
            val email = context?.getString(R.string.emailForQuery)

            val selectedIntent = Intent().apply {
                action = Intent.ACTION_SENDTO
                data = Uri.parse("mailto:")
            }
            val emailIntent = Intent(Intent.ACTION_SEND).apply {
                putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
                putExtra(Intent.EXTRA_SUBJECT, subject)
                this.selector = selectedIntent
            }

            try {
              startActivity(Intent.createChooser(emailIntent, "send mail using..."))
            } catch (e: Exception) {
                Toast.makeText(
                    context,
                    context?.getString(R.string.emailError),
                    Toast.LENGTH_LONG
                ).show()
                e.printStackTrace()
            }
            true
        }


    }


    private fun setCurrencyPreferences() {

        val sharedPreference = PreferenceManager.getDefaultSharedPreferences(requireContext())

        currencyPreference?.onPreferenceChangeListener =
            Preference.OnPreferenceChangeListener { preference, newValue ->
                if (newValue is String) {
                    try {
                        sharedPreference.edit {
                            this.putString(preference.key, newValue)
                        }
                    } catch (e: Exception) {
                        Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show()
                       }


                }

                true
            }


    }


    private fun setNotificationPreferences() {

        val sharedPreference = PreferenceManager.getDefaultSharedPreferences(requireContext())
        notificationPreference?.onPreferenceChangeListener =
            Preference.OnPreferenceChangeListener { preference, newValue ->


                if (newValue is Boolean) {
                    try {
                        sharedPreference.edit {
                            this.putBoolean(preference.key, newValue)
                        }

                    } catch (e: Exception) {
                        Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show()
                     }


                }

                true
            }

    }

    private fun setThemePreferences() {

        themePreference?.onPreferenceChangeListener =
            Preference.OnPreferenceChangeListener { _, newValue ->
                if (newValue is String) {
                    try {
                        val theme = themeProvider.getTheme(newValue)
                        AppCompatDelegate.setDefaultNightMode(theme)
                    } catch (e: Exception) {
                        Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show()
                     }

                }

                true
            }

        themePreference?.summaryProvider =
            Preference.SummaryProvider<ListPreference> { preference ->
                themeProvider.getThemeDescriptionFromPreference(preference.value)
            }


    }

    private fun showPrivacyPolicyDialog() {

        showPrivacyPolicyPreference?.onPreferenceClickListener =
            Preference.OnPreferenceClickListener {

                try {

                    val intent = Intent.makeMainSelectorActivity(Intent.ACTION_MAIN, Intent.CATEGORY_APP_BROWSER)
                    intent.data = Uri.parse(privacy_policy_url)
                    startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show()
                }
                true
            }


    }




}