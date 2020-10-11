package by.bsuir.tabata_app.data.dao

import androidx.room.*
import by.bsuir.tabata_app.data.entity.TimerEntity

@Dao
interface TimerDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(timer: TimerEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg timers: TimerEntity)

    @Update
    fun update(timer: TimerEntity)

    @Delete
    fun delete(timer: TimerEntity)

    @Query("DELETE FROM timerEntity WHERE id = :id")
    fun deleteById(id: Int)

    @Query("SELECT * FROM timerEntity")
    fun getAll(): List<TimerEntity>

    @Query("SELECT * FROM timerEntity WHERE name=:name")
    fun getByName(name: String): List<TimerEntity>

    @Query("SELECT * FROM timerEntity WHERE id=:id")
    fun getById(id: Int): TimerEntity?

    @Query("DELETE FROM timerEntity")
    fun deleteAll()
}