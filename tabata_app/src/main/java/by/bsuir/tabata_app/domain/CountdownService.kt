package by.bsuir.tabata_app.domain

import android.app.Service
import android.content.Intent
import android.os.CountDownTimer
import android.os.IBinder
import android.text.format.DateUtils

class CountdownService : Service() {

    companion object {
        const val COUNTDOWN_SERVICE = "bsuir.countdown_service"
        const val ACTION_START_TIMER = "ACTION_START_TIMER"
        const val ACTION_STOP_TIMER = "ACTION_STOP_TIMER"
        const val ACTION_CHECK_TIMER_FINISHED = "ACTION_CHECK_TIMER_FINISHED"

        const val EXTRA_TIMER_SECONDS = "EXTRA_TIMER_SECONDS"

        const val BROADCAST_SECONDS_LEFT = "SECONDS_LEFT"
        const val BROADCAST_TIMER_FINISHED = "TIMER_FINISHED"
    }

    private var currentCountdown: CountDownTimer? = null
    private var isFinished: Boolean = false

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null && intent.action != null) when (intent.action) {
            ACTION_START_TIMER -> startTimer(intent.getIntExtra(EXTRA_TIMER_SECONDS, 0))
            ACTION_STOP_TIMER -> stopTimer()
            ACTION_CHECK_TIMER_FINISHED -> checkFinished()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun startTimer(seconds: Int) {
        currentCountdown?.cancel()
        isFinished = false

        currentCountdown = object : CountDownTimer(
            seconds * DateUtils.SECOND_IN_MILLIS, DateUtils.SECOND_IN_MILLIS
        ) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsLeft =
                    ((millisUntilFinished + 1000L) / DateUtils.SECOND_IN_MILLIS).toInt()
                sendSecondsLeftBroadcast(secondsLeft)
            }

            override fun onFinish() {
                sendFinishedBroadcast()
                isFinished = true
            }
        }.start()
    }

    private fun stopTimer() {
        currentCountdown?.cancel()
    }

    private fun checkFinished() {
        if (isFinished) sendFinishedBroadcast()
    }

    private fun sendSecondsLeftBroadcast(seconds: Int) {
        val countdownIntent = Intent(COUNTDOWN_SERVICE)
        countdownIntent.putExtra(BROADCAST_SECONDS_LEFT, seconds)
        sendBroadcast(countdownIntent)
    }

    private fun sendFinishedBroadcast() {
        val finishedIntent = Intent(COUNTDOWN_SERVICE)
        finishedIntent.putExtra(BROADCAST_TIMER_FINISHED, true)
        sendBroadcast(finishedIntent)
    }

    override fun onDestroy() {
        currentCountdown?.cancel()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}