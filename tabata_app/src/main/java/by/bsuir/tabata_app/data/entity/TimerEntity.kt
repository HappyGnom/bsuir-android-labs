package by.bsuir.tabata_app.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import by.bsuir.tabata_app.converter.StringListConverter

@Entity
@TypeConverters(StringListConverter::class)
class TimerEntity(
    val name: String, val color: Int,
    val workSeconds: Int, val restSeconds: Int,
    val intervalsCount: Int, val cyclesCount: Int,
    val breakSeconds: Int, val exercises: List<String> = mutableListOf()
) {
    @PrimaryKey(autoGenerate = true)
    private var id: Int = 0

    fun getId() = id

    fun setId(id: Int) {
        this.id = id
    }

    constructor(
        name: String, color: Int,
        workSeconds: Int, restSeconds: Int,
        intervalsCount: Int, cyclesCount: Int,
        breakSeconds: Int, exercises: List<String> = mutableListOf(),
        id: Int? = null
    ) : this(
        name, color,
        workSeconds, restSeconds,
        intervalsCount, cyclesCount,
        breakSeconds, exercises
    ) {
        if (id != null)
            this.id = id
    }

    constructor(timer: Timer) : this(
        timer.name, timer.color,
        timer.workSeconds, timer.restSeconds,
        timer.intervalsCount, timer.cyclesCount,
        timer.breakSeconds, timer.exercises
    ) {
        if (timer.id != null)
            this.id = timer.id
    }
}