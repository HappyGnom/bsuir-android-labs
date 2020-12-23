package by.bsuir.battleships_app.model

import by.bsuir.battleships_app.R
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.cell_text_item.*

class CellTextItem(private val cellText: String) : Item() {

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.cell_textView.text = cellText
    }

    override fun getLayout() = R.layout.cell_text_item
}