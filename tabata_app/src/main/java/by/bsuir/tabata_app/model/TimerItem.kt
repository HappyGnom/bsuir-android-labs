package by.bsuir.tabata_app.model

import android.content.res.ColorStateList
import by.bsuir.tabata_app.R
import by.bsuir.tabata_app.data.entity.Timer
import by.bsuir.tabata_app.domain.TimeUtils
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.timer_item.*

class TimerItem(val timer: Timer) : Item() {

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.color_circle_imageView.imageTintList = ColorStateList.valueOf(timer.color)
        viewHolder.first_letter_textView.text = timer.name.first().toUpperCase().toString()
        viewHolder.title_textView.text = timer.name
        viewHolder.total_time_textView.text = TimeUtils.secondsToTime(timer.getTotalTimeSeconds())
        viewHolder.exercises_textView.text = timer.exercises.joinToString(" - ")
    }

    override fun getLayout() = R.layout.timer_item
}