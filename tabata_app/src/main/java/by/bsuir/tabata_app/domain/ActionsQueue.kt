package by.bsuir.tabata_app.domain

import android.content.Context
import by.bsuir.tabata_app.R
import by.bsuir.tabata_app.data.entity.Timer
import by.bsuir.tabata_app.domain.ActionsQueue.TimerAction.QueueItemType.*

class ActionsQueue(context: Context, timer: Timer) {

    data class TimerAction(
        val name: String, val durationSeconds: Int, val type: QueueItemType,
        val intervalNumber: Int?, val cycleNumber: Int
    ) {
        enum class QueueItemType {
            EXERCISE, REST, BREAK, OTHER
        }
    }

    private var actions = listOf<TimerAction>()
    private var currentActionIndex = -1

    init {
        val prepareAction = TimerAction(context.getString(R.string.prepare), 10, OTHER, null, 1)
        val actionsQueue = mutableListOf(prepareAction)

        fun addExercise(exerciseName: String, interval: Int, cycle: Int) {
            val exerciseAction = TimerAction(
                exerciseName, timer.workSeconds, EXERCISE, interval, cycle
            )
            actionsQueue.add(exerciseAction)
        }

        fun addRest(interval: Int, cycle: Int) {
            val restAction = TimerAction(
                context.getString(R.string.rest), timer.restSeconds,
                REST, interval, cycle
            )
            actionsQueue.add(restAction)
        }

        fun addBreak(cycle: Int) {
            val breakAction = TimerAction(
                context.getString(R.string.cycles_break), timer.breakSeconds,
                BREAK, null, cycle
            )
            actionsQueue.add(breakAction)
        }

        for (cycle in 0.until(timer.cyclesCount)) {
            timer.exercises.forEachIndexed { interval, exercise ->
                addExercise(exercise, interval + 1, cycle + 1)
                if (interval < (timer.intervalsCount - 1)) addRest(interval + 1, cycle + 1)
            }

            if (cycle < (timer.cyclesCount - 1)) addBreak(cycle + 1)
        }

        actions = actionsQueue
    }

    fun getCurrentAction() = actions.getOrNull(currentActionIndex)

    fun nextAction(): TimerAction? {
        if (currentActionIndex + 1 >= actions.size) return null

        currentActionIndex++
        return actions[currentActionIndex]
    }

    fun prevAction(): TimerAction? {
        if (currentActionIndex - 1 < 0) return null

        currentActionIndex--
        return actions[currentActionIndex]
    }

    fun getNextUpAction(): String? {
        val actionsLeft = actions.slice((currentActionIndex + 1).until(actions.size))
        return actionsLeft.firstOrNull { it.type == EXERCISE || it.type == BREAK }?.name
    }
}