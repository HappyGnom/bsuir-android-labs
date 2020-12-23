package by.bsuir.battleships_app.model

import android.graphics.drawable.Drawable
import by.bsuir.battleships_app.R
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.cell_image_item.*

class CellImageItem(private val drawable: Drawable) : Item() {

    private var view: GroupieViewHolder? = null
    private var cellDrawable: Drawable? = null

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        view = viewHolder
        if (cellDrawable == null) cellDrawable = drawable

        viewHolder.cell_imageView.setImageDrawable(cellDrawable)
    }

    fun updateImage(cellDrawable: Drawable) {
        view?.cell_imageView?.setImageDrawable(cellDrawable)
        this.cellDrawable = cellDrawable
    }

    override fun getLayout() = R.layout.cell_image_item
}