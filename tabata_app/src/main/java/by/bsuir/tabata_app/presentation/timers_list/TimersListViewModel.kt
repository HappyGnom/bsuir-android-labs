package by.bsuir.tabata_app.presentation.timers_list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import by.bsuir.tabata_app.data.database.TabataDatabase
import by.bsuir.tabata_app.data.entity.Timer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

class TimersListViewModel(private val tabataDB: TabataDatabase) : ViewModel() {

    private val _timers = MutableLiveData<List<Timer>>()
    val timers: LiveData<List<Timer>> = _timers

    fun getAllTimersAsync() {
        CoroutineScope(IO).launch {
            _timers.postValue(
                tabataDB.timerDao.getAll().map { Timer(it) }
            )
        }
    }

    fun deleteTimer(timerId: Int) {
        CoroutineScope(IO).launch {
            tabataDB.timerDao.deleteById(timerId)
            getAllTimersAsync()
        }
    }
}