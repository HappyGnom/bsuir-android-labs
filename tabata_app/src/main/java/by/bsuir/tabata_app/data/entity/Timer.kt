package by.bsuir.tabata_app.data.entity

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Timer(
    val name: String, val color: Int,
    val workSeconds: Int, val restSeconds: Int,
    val intervalsCount: Int, val cyclesCount: Int,
    val breakSeconds: Int, val exercises: List<String> = mutableListOf(),
    val id: Int? = null
) : Parcelable {

    constructor(timerEntity: TimerEntity) : this(
        timerEntity.name, timerEntity.color,
        timerEntity.workSeconds, timerEntity.restSeconds,
        timerEntity.intervalsCount, timerEntity.cyclesCount,
        timerEntity.breakSeconds, timerEntity.exercises,
        timerEntity.getId()
    )

    fun getTotalTimeSeconds() =
        ((workSeconds + restSeconds) * intervalsCount - restSeconds + breakSeconds) * cyclesCount - breakSeconds
}