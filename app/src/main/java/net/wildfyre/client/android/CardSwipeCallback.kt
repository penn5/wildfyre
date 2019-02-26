package net.wildfyre.client.android

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView


class CardSwipeCallback(ctx: Context, private val recyclerView: RecyclerView) :
    ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    private var context: Context = ctx

    private val p = Paint()


    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {

        val icon: Drawable?
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {

            val itemView = viewHolder.itemView
            val height = itemView.bottom.toFloat() - itemView.top.toFloat()
            val width = height / 3

            if (dX > 0) {
                p.color = ContextCompat.getColor(context, R.color.red)
                val background = RectF(itemView.left.toFloat(), itemView.top.toFloat(), dX, itemView.bottom.toFloat())
                c.drawRect(background, p)
                icon = ContextCompat.getDrawable(context, R.drawable.ic_ignite)

                icon?.setBounds(
                    (itemView.right.toFloat() - 2 * width).toInt(),
                    (itemView.top.toFloat() + width).toInt(),
                    (itemView.right.toFloat() - width).toInt(),
                    (itemView.bottom.toFloat() - width).toInt()
                )
                icon?.draw(c)
            } else {
                p.color = ContextCompat.getColor(context, R.color.green)
                val background = RectF(
                    itemView.right.toFloat() + dX,
                    itemView.top.toFloat(),
                    itemView.right.toFloat(),
                    itemView.bottom.toFloat()
                )
                c.drawRect(background, p)
                icon = ContextCompat.getDrawable(context, R.drawable.ic_extinguish)

                icon?.setBounds(
                    (itemView.left.toFloat() + width).toInt(),
                    (itemView.top.toFloat() + width).toInt(),
                    (itemView.left.toFloat() + 2 * width).toInt(),
                    (itemView.bottom.toFloat() - width).toInt()
                )

                icon?.draw(c)
            }
        }
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        // Now there's no card, lets add a new one and apply a fade in animation.
        //TODO Actually make a new card rather than update the old one
        recyclerView.adapter?.notifyItemChanged(0) //Bring it back
    }
}