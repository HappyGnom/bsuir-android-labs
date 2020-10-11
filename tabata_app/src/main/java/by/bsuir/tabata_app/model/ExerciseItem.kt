package by.bsuir.tabata_app.model

import android.widget.EditText
import androidx.core.widget.doOnTextChanged
import by.bsuir.tabata_app.R
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.exercise_item.*

class ExerciseItem(
    private val defaultName: String = "",
    private val onExerciseChangeListener: (position: Int, newText: String) -> Unit
) : Item() {

    private var exerciseNameEditText: EditText? = null

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        exerciseNameEditText = viewHolder.exercise_name_editText
        viewHolder.exercise_name_editText.setText(defaultName)

        exerciseNameEditText?.doOnTextChanged { text, _, _, _ ->
            onExerciseChangeListener(position, text.toString())
        }
    }

    override fun getLayout() = R.layout.exercise_item
}