package com.babacode.walletexpensetracker.ui.setting


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.babacode.walletexpensetracker.R
import com.babacode.walletexpensetracker.ui.setting.compose.SettingsRoute
import com.babacode.walletexpensetracker.ui.theme.WalletExpenseTheme
import com.babacode.walletexpensetracker.utiles.Extra.privacy_policy_url
import com.babacode.walletexpensetracker.utiles.applyEdgeToEdgeInsetsPadding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SettingsFragment : Fragment() {

    private val viewModel: SettingsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            applyEdgeToEdgeInsetsPadding()
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                WalletExpenseTheme {
                    SettingsRoute(
                        viewModel = viewModel,
                        onPrivacyPolicyClick = ::openPrivacyPolicy,
                        onContactSupportClick = ::requestNewFeature,
                        onReportBugClick = ::reportBug
                    )
                }
            }
        }
    }

    private fun reportBug() {
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
    }

    private fun requestNewFeature() {
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
    }

    private fun openPrivacyPolicy() {
        try {
            val intent = Intent.makeMainSelectorActivity(Intent.ACTION_MAIN, Intent.CATEGORY_APP_BROWSER)
            intent.data = Uri.parse(privacy_policy_url)
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show()
        }
    }
}
