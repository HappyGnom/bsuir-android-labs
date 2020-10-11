package by.bsuir.tabata_app

import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import by.bsuir.tabata_app.util.IOnBackPressed
import kotlinx.android.synthetic.main.nav_host_activity.*

class NavHostActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        theme.applyStyle(getStyle(preferences), true)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.nav_host_activity)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
    }

    private fun getStyle(preferences: SharedPreferences): Int {
        val fontSize = preferences.getString("font_size", "normal")!!
        val isDarkTheme = preferences.getBoolean("enable_night_theme", false)

        if (isDarkTheme) setDarkStatusBar()
        else setLightStatusBar()

        return when (fontSize) {
            "small" -> if (isDarkTheme) R.style.TabataAppThemeDark_FontSmall else R.style.TabataAppThemeLight_FontSmall
            "normal" -> if (isDarkTheme) R.style.TabataAppThemeDark_FontNormal else R.style.TabataAppThemeLight_FontNormal
            "big" -> if (isDarkTheme) R.style.TabataAppThemeDark_FontBig else R.style.TabataAppThemeLight_FontBig
            else -> R.style.TabataAppThemeDark_FontNormal
        }
    }

    private fun setLightStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    }

    private fun setDarkStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            window.decorView.systemUiVisibility =
                window.decorView.systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
    }

    override fun onBackPressed() {
        val fragment = navigation_fragment.childFragmentManager.primaryNavigationFragment

        if (fragment is IOnBackPressed)
            (fragment as IOnBackPressed).onBackPressed()
        else
            super.onBackPressed()
    }
}
