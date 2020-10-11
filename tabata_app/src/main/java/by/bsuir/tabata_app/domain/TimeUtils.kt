package by.bsuir.tabata_app.domain

object TimeUtils {

    fun secondsToTime(seconds: Int): String {
        val hours = seconds / 3600 % 60
        val minutes = seconds / 60 % 60
        val leftSeconds = seconds % 60

        return if (hours == 0)
            String.format("%02d:%02d", minutes, leftSeconds)
        else
            String.format("%02d:%02d:%02d", hours, minutes, leftSeconds)
    }
}