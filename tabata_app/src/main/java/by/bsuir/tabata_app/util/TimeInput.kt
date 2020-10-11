package by.bsuir.tabata_app.util

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import by.bsuir.tabata_app.R
import by.bsuir.tabata_app.domain.TimeUtils
import kotlinx.android.synthetic.main.time_input_layout.view.*
import kotlin.math.abs

class TimeInput @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null,
    defStyleAttr: Int = 0, defStyleRes: Int = 0
) : LinearLayout(context, attrs, defStyleAttr, defStyleRes), View.OnClickListener,
    View.OnTouchListener {

    private val seconds = MutableLiveData(1)
    private val minValue: Int
    private val maxValue: Int

    init {
        LayoutInflater.from(context).inflate(R.layout.time_input_layout, this, true).apply {
            minus_button.setOnTouchListener(this@TimeInput)
            plus_button.setOnTouchListener(this@TimeInput)
        }

        seconds.observeForever { updateTimeText() }

        val attributes: TypedArray = context.obtainStyledAttributes(
            attrs, R.styleable.TimeInput, defStyleAttr, defStyleRes
        )

        minValue = attributes.getInt(R.styleable.TimeInput_minValue, 1)
        maxValue = attributes.getInt(R.styleable.TimeInput_maxValue, 3599)
        seconds.value = attributes.getInt(R.styleable.TimeInput_defaultInputValue, 1)

        attributes.recycle()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.minus_button -> minusSecond()
            R.id.plus_button -> plusSecond()
        }
    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        if ((event.action == MotionEvent.ACTION_MOVE && abs(event.downTime - event.eventTime) > 500L)
            || (event.action == MotionEvent.ACTION_DOWN)
        ) when (v.id) {
            R.id.minus_button -> minusSecond()
            R.id.plus_button -> plusSecond()
        }

        return true
    }

    private fun minusSecond() {
        if (seconds.value!! > minValue)
            seconds.value = (seconds.value!! - 1)
    }

    private fun plusSecond() {
        if (seconds.value!! < maxValue)
            seconds.value = (seconds.value!! + 1)
    }

    private fun updateTimeText() {
        time_value_textView.text = getTime()
    }

    fun observeSeconds(viewLifecycleOwner: LifecycleOwner, callback: (seconds: Int) -> Unit) =
        seconds.observe(viewLifecycleOwner, { callback(it) })

    fun getTime() = TimeUtils.secondsToTime(seconds.value!!)

    fun setSeconds(seconds: Int) {
        this.seconds.value = seconds
    }

    fun getSeconds() = seconds.value!!
}