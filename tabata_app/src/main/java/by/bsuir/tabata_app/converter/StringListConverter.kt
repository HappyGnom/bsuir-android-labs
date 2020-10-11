package by.bsuir.tabata_app.converter

import androidx.room.TypeConverter

class StringListConverter {

    @TypeConverter
    fun fromStringList(list: List<String>): String {
        list.forEach { it.replace(",", " ").trim() }
        return list.joinToString(",")
    }

    @TypeConverter
    fun toStringList(dataString: String) = dataString.split(",")
}