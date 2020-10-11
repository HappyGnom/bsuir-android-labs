package by.bsuir.tabata_app.presentation.settings

import androidx.lifecycle.ViewModel
import by.bsuir.tabata_app.data.database.TabataDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SettingsViewModel(private val tabataDB: TabataDatabase) : ViewModel() {

    fun clearAllData() {
        CoroutineScope(Dispatchers.IO).launch {
            tabataDB.timerDao.deleteAll()
        }
    }
}