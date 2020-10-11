package by.bsuir.tabata_app.presentation.countdown

import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import by.bsuir.tabata_app.data.entity.Timer
import by.bsuir.tabata_app.domain.ActionsQueue
import by.bsuir.tabata_app.domain.CountdownService
import ru.ar2code.mutableliveevent.EventArgs
import ru.ar2code.mutableliveevent.MutableLiveEvent

class CountdownViewModel : ViewModel() {

    private val _secondsLeft = MutableLiveData<Int>()
    val secondsLeft: LiveData<Int> = _secondsLeft

    private val _currentCycle = MutableLiveData(1)
    val currentCycle: LiveData<Int> = _currentCycle

    private val _currentInterval = MutableLiveData<Int?>(1)
    val currentInterval: LiveData<Int?> = _currentInterval

    private val _currentStage = MutableLiveData<String>()
    val currentStage: LiveData<String> = _currentStage

    private val _nextExercise = MutableLiveData<String?>()
    val nextExercise: LiveData<String?> = _nextExercise

    private val _timerFinished = MutableLiveEvent<EventArgs<Boolean>>()
    val timerFinished: LiveData<EventArgs<Boolean>> = _timerFinished

    private val _countdownIsRunning = MutableLiveData<Boolean>(false)
    val countdownIsRunning: LiveData<Boolean> = _countdownIsRunning

    private val _startCountdownSeconds = MutableLiveEvent<EventArgs<Int>>()
    val startCountdownSeconds: LiveData<EventArgs<Int>> = _startCountdownSeconds

    private val _stopCountdownEvent = MutableLiveEvent<EventArgs<Boolean>>()
    val stopCountdownEvent: LiveData<EventArgs<Boolean>> = _stopCountdownEvent

    private var actionsQueue: ActionsQueue? = null
    var timer: Timer? = null
        private set

    fun setSelectedTimer(timer: Timer, actionsQueue: ActionsQueue) {
        this.timer = timer
        this.actionsQueue = actionsQueue
    }

    fun startTimer() {
        _countdownIsRunning.value = true
        nextTimerStep()
    }

    fun pauseTimer() {
        stopCountdown()
        _countdownIsRunning.value = false
    }

    fun resumeTimer() {
        startCountdown(secondsLeft.value!!)
    }

    fun nextTimerStep() {
        val action = actionsQueue?.nextAction()
        if (action == null)
            finishTimer()
        else
            launchTimerStep(action)
    }

    fun prevTimerStep() {
        var action = actionsQueue?.prevAction()
        if (action == null) action = actionsQueue?.getCurrentAction()

        launchTimerStep(action!!)
    }

    private fun launchTimerStep(action: ActionsQueue.TimerAction) {
        _secondsLeft.value = action.durationSeconds

        _currentInterval.value = action.intervalNumber
        _currentCycle.value = action.cycleNumber

        _currentStage.value = action.name
        _nextExercise.value = actionsQueue?.getNextUpAction()

        startCountdown(action.durationSeconds)
    }

    private fun startCountdown(seconds: Int) {
        _startCountdownSeconds.value = EventArgs(seconds)
        _countdownIsRunning.value = true
    }

    private fun finishTimer() {
        stopCountdown()
        _timerFinished.value = EventArgs(true)
    }

    private fun stopCountdown() {
        _stopCountdownEvent.value = EventArgs(true)
    }

    fun processCountdownIntent(intent: Intent?) {
        if (intent == null) return

        if (intent.extras!!.containsKey(CountdownService.BROADCAST_SECONDS_LEFT))
            _secondsLeft.value = intent.extras!!.getInt(CountdownService.BROADCAST_SECONDS_LEFT)

        if (intent.extras!!.containsKey(CountdownService.BROADCAST_TIMER_FINISHED))
            nextTimerStep()
    }
}