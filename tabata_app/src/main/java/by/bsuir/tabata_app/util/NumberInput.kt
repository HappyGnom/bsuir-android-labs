package by.bsuir.tabata_app.util

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import by.bsuir.tabata_app.R
import kotlinx.android.synthetic.main.number_input_layout.view.*


class NumberInput @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null,
    defStyleAttr: Int = 0, defStyleRes: Int = 0
) : LinearLayout(context, attrs, defStyleAttr, defStyleRes), View.OnClickListener {

    private val number = MutableLiveData(1)
    private val minValue: Int
    private val maxValue: Int

    init {
        LayoutInflater.from(context).inflate(R.layout.number_input_layout, this, true).apply {
            minus_button.setOnClickListener(this@NumberInput)
            plus_button.setOnClickListener(this@NumberInput)
        }

        number.observeForever(this::updateNumber)

        val attributes: TypedArray = context.obtainStyledAttributes(
            attrs, R.styleable.NumberInput, defStyleAttr, defStyleRes
        )

        minValue = attributes.getInt(R.styleable.NumberInput_minValue, 1)
        maxValue = attributes.getInt(R.styleable.NumberInput_maxValue, 60)
        number.value = attributes.getInt(R.styleable.NumberInput_defaultInputValue, 1)

        attributes.recycle()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.minus_button -> minusOne()
            R.id.plus_button -> plusOne()
        }
    }

    private fun minusOne() {
        if (number.value!! > 1)
            number.value = (number.value!! - 1)
    }

    private fun plusOne() {
        if (number.value!! < 20)
            number.value = (number.value!! + 1)
    }

    private fun updateNumber(number: Int) {
        number_value_textView.text = number.toString()
    }

    fun observeValue(viewLifecycleOwner: LifecycleOwner, callback: (number: Int) -> Unit) {
        number.observe(viewLifecycleOwner, {
            callback(getNumber())
        })
    }

    fun setNumber(value: Int) {
        number.value = value
    }

    fun getNumber() = number.value!!
}