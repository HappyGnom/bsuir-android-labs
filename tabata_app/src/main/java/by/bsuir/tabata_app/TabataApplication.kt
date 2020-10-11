package by.bsuir.tabata_app

import android.app.Application
import androidx.preference.PreferenceManager
import com.yariksoffice.lingver.Lingver

class TabataApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        val preferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val language = preferences.getString("language", "en")!!

        Lingver.init(this, language)
    }
}