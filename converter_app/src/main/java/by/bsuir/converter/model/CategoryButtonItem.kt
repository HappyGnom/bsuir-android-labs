package by.bsuir.converter.model

import android.content.Context
import androidx.core.content.ContextCompat
import by.bsuir.converter.R
import by.bsuir.converter.data.Category
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.category_button_item.*

class CategoryButtonItem(
    private val context: Context, private val category: Category,
    private val onClick: (category: Category) -> Unit
) : Item() {

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.category_name_textView.text = context.getString(category.nameId)
        viewHolder.category_icon_imageView.setImageDrawable(
            ContextCompat.getDrawable(context, category.iconId)
        )

        viewHolder.category_button.setOnClickListener { onClick(category) }
    }

    override fun getLayout() = R.layout.category_button_item
}