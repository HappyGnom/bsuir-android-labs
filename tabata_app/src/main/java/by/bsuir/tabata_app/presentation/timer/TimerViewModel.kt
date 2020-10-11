package by.bsuir.tabata_app.presentation.timer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import by.bsuir.tabata_app.R
import by.bsuir.tabata_app.data.database.TabataDatabase
import by.bsuir.tabata_app.data.entity.Timer
import by.bsuir.tabata_app.data.entity.TimerEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

class TimerViewModel(private val tabataDB: TabataDatabase) : ViewModel() {

    val possibleTimerColors = intArrayOf(
        0xFFC40837.toInt(), 0xFFDA4167.toInt(), 0xFFEC9A29.toInt(), 0xFF3FA53B.toInt(),
        0xFF028090.toInt(), 0xFF1982C4.toInt(), 0xFF5F00BA.toInt(), 0xFF143642.toInt()
    )

    private val _timerName = MutableLiveData("New timer")
    val timerName: LiveData<String> = _timerName

    private val _timerColor = MutableLiveData(possibleTimerColors.first())
    val timerColor: LiveData<Int> = _timerColor

    private val _workSeconds = MutableLiveData(45)
    val workSeconds: LiveData<Int> = _workSeconds

    private val _restSeconds = MutableLiveData(20)
    val restSeconds: LiveData<Int> = _restSeconds

    private val _intervalsCount = MutableLiveData(5)
    val intervalsCount: LiveData<Int> = _intervalsCount

    private val _cyclesCount = MutableLiveData(3)
    val cyclesCount: LiveData<Int> = _cyclesCount

    private val _breakSeconds = MutableLiveData(60)
    val breakSeconds: LiveData<Int> = _breakSeconds

    private val _totalSeconds = MutableLiveData<Int>(getTotalTimeSeconds())
    val totalSeconds: LiveData<Int> = _totalSeconds

    private val _exercises = mutableListOf<String>()
    val exercises: List<String> = _exercises

    private var selectedTimerId: Int? = null

    fun setTimerName(name: String) {
        _timerName.value = name
    }

    fun setTimerColor(color: Int) {
        _timerColor.value = color
    }

    fun setWorkSeconds(seconds: Int) {
        _workSeconds.value = seconds
        _totalSeconds.value = getTotalTimeSeconds()
    }

    fun setRestSeconds(seconds: Int) {
        _restSeconds.value = seconds
        _totalSeconds.value = getTotalTimeSeconds()
    }

    fun setIntervalsCount(count: Int) {
        _intervalsCount.value = count
        _totalSeconds.value = getTotalTimeSeconds()
    }

    fun setCyclesCount(count: Int) {
        _cyclesCount.value = count
        _totalSeconds.value = getTotalTimeSeconds()
    }

    fun setBreakSeconds(seconds: Int) {
        _breakSeconds.value = seconds
        _totalSeconds.value = getTotalTimeSeconds()
    }

    fun addExercise() {
        _exercises.add("")
    }

    fun removeLastExercise() {
        if (_exercises.isNotEmpty())
            _exercises.removeAt(_exercises.size - 1)
    }

    fun changeExerciseAt(index: Int, newText: String) {
        _exercises[index] = newText
    }

    fun setSelectedTimerId(id: Int) {
        selectedTimerId = id
    }

    fun setSelectedTimerExercises(exercises: List<String>) {
        _exercises.clear()
        _exercises.addAll(exercises)
    }

    fun getErrorId() = when {
        timerName.value.isNullOrBlank() -> R.string.title_empty
        !exercises.none { return@none it.isBlank() } -> R.string.exercise_empty
        else -> null
    }

    fun saveCurrentTimer() {
        CoroutineScope(IO).launch {
            val currentTimer = getCurrentTimerEntity()

            if (selectedTimerId == null)
                tabataDB.timerDao.insert(currentTimer)
            else
                tabataDB.timerDao.update(currentTimer)
        }
    }

    fun getCurrentTimerNoId() = Timer(
        timerName.value!!, timerColor.value!!,
        workSeconds.value!!, restSeconds.value!!,
        intervalsCount.value!!, cyclesCount.value!!,
        breakSeconds.value!!, exercises
    )

    private fun getCurrentTimerEntity() = TimerEntity(
        timerName.value!!, timerColor.value!!,
        workSeconds.value!!, restSeconds.value!!,
        intervalsCount.value!!, cyclesCount.value!!,
        breakSeconds.value!!, exercises, selectedTimerId
    )

    private fun getTotalTimeSeconds() =
        ((workSeconds.value!! + restSeconds.value!!) * intervalsCount.value!! - restSeconds.value!! + breakSeconds.value!!) * cyclesCount.value!! - breakSeconds.value!!
}