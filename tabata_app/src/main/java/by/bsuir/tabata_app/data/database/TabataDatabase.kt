package by.bsuir.tabata_app.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import by.bsuir.tabata_app.data.dao.TimerDao
import by.bsuir.tabata_app.data.entity.TimerEntity

@Database(entities = [TimerEntity::class], version = 3)
abstract class TabataDatabase : RoomDatabase() {

    abstract val timerDao: TimerDao

    companion object {

        @Volatile
        private var instance: TabataDatabase? = null

        fun getInstance(context: Context): TabataDatabase =
            instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }

        private fun buildDatabase(context: Context) = Room.databaseBuilder(
            context.applicationContext, TabataDatabase::class.java, "Tabata.db"
        ).build()
    }
}