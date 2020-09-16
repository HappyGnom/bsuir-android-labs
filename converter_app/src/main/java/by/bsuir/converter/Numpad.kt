package by.bsuir.converter

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.ExtractedTextRequest
import android.view.inputmethod.InputConnection
import android.widget.Button
import android.widget.GridLayout
import kotlinx.android.synthetic.main.numpad_layout.view.*

class Numpad @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null,
    defStyleAttr: Int = 0, defStyleRes: Int = 0
) : GridLayout(context, attrs, defStyleAttr, defStyleRes),
    View.OnClickListener, View.OnLongClickListener {

    private var inputConnection: InputConnection? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.numpad_layout, this, true).apply {
            button_1.setOnClickListener(this@Numpad)
            button_2.setOnClickListener(this@Numpad)
            button_3.setOnClickListener(this@Numpad)
            button_4.setOnClickListener(this@Numpad)
            button_5.setOnClickListener(this@Numpad)
            button_6.setOnClickListener(this@Numpad)
            button_7.setOnClickListener(this@Numpad)
            button_8.setOnClickListener(this@Numpad)
            button_9.setOnClickListener(this@Numpad)
            button_0.setOnClickListener(this@Numpad)

            button_point.setOnClickListener(this@Numpad)
            button_backspace.setOnClickListener(this@Numpad)
            button_backspace.setOnLongClickListener(this@Numpad)
        }
    }

    override fun onClick(v: View) {
        if (inputConnection == null) return

        if (v.id == R.id.button_backspace)
            backspace()
        else
            inputConnection!!.commitText((v as Button).text, 1)

        if (v.id == R.id.button_point)
            pointEntered()
    }

    private fun backspace() {
        val selectedText = inputConnection!!.getSelectedText(0)

        if (TextUtils.isEmpty(selectedText)) {
            if (inputConnection!!.getTextBeforeCursor(1, 0) == ".") pointCleared()
            inputConnection!!.deleteSurroundingText(1, 0)
        } else {
            if (selectedText.contains('.')) pointCleared()
            inputConnection!!.commitText("", 1)
        }
    }

    override fun onLongClick(v: View): Boolean {
        if (inputConnection != null && v.id == R.id.button_backspace)
            clearBeforeCursor()

        return true
    }

    private fun clearBeforeCursor() {
        val currentText = inputConnection!!.getExtractedText(ExtractedTextRequest(), 0).text
        val beforeCursorText = inputConnection!!.getTextBeforeCursor(currentText.length, 0)
        inputConnection!!.deleteSurroundingText(beforeCursorText.length, 0)

        if (beforeCursorText.contains('.')) pointCleared()
    }

    private fun pointEntered() {
        button_point.isEnabled = false
        button_point.visibility = View.INVISIBLE
    }

    private fun pointCleared() {
        button_point.isEnabled = true
        button_point.visibility = View.VISIBLE
    }

    fun setInputConnection(inputConnection: InputConnection?) {
        this.inputConnection = inputConnection
    }
}