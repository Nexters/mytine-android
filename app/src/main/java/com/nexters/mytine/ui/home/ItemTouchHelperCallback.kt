package com.nexters.mytine.ui.home

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.RectF
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.nexters.mytine.R

internal class ItemTouchHelperCallback(var listener: ItemTouchHelperListener) : ItemTouchHelper.Callback() {

    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        var swipeFlags = 0

        val position = viewHolder.adapterPosition
        val isLeftSwipeable: Boolean
        val isRightSwipeable: Boolean

        if (position != RecyclerView.NO_POSITION) {
            isLeftSwipeable = listener.isLeftSwipeable(position)
            isRightSwipeable = listener.isRightSwipeable(position)
        } else {
            isLeftSwipeable = false
            isRightSwipeable = false
        }

        if (isLeftSwipeable) {
            swipeFlags = swipeFlags or ItemTouchHelper.START
        }

        if (isRightSwipeable) {
            swipeFlags = swipeFlags or ItemTouchHelper.END
        }

        return makeMovementFlags(0, swipeFlags)
    }

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean = false

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        listener.onItemSwipe(viewHolder.adapterPosition, direction)
    }

    override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
        val itemView: View = viewHolder.itemView
        val resources = itemView.resources
        val context = itemView.context
        lateinit var icon: Bitmap
        var heightOffset = 0

        when {
            dX > 0 -> { // 오른쪽으로
                val drawable = ContextCompat.getDrawable(context, R.drawable.ic_card_check_back)
                icon = Bitmap.createBitmap(
                    drawable?.intrinsicWidth ?: 0,
                    drawable?.intrinsicHeight ?: 0, Bitmap.Config.ARGB_8888
                )
                val canvas = Canvas(icon)
                drawable?.setBounds(0, 0, canvas.width, canvas.height)
                drawable?.draw(canvas)
                heightOffset = (itemView.height - icon.height) / 2
                val dest = RectF(
                    itemView.left.toFloat() + icon.width,
                    itemView.top.toFloat() + heightOffset,
                    itemView.left.toFloat() + 2 * icon.width,
                    itemView.bottom.toFloat() - heightOffset
                )
                c.drawBitmap(icon, null, dest, null)
            }
            dX < 0 -> { // 왼쪽으로
                val drawable = ContextCompat.getDrawable(context, R.drawable.ic_card_check)
                icon = Bitmap.createBitmap(
                    drawable?.intrinsicWidth ?: 0,
                    drawable?.intrinsicHeight ?: 0, Bitmap.Config.ARGB_8888
                )
                val canvas = Canvas(icon)
                drawable?.setBounds(0, 0, canvas.width, canvas.height)
                drawable?.draw(canvas)
                heightOffset = (itemView.height - icon.height) / 2
                val dest = RectF(
                    itemView.right.toFloat() - 2 * icon.width,
                    itemView.top.toFloat() + heightOffset,
                    itemView.right.toFloat() - icon.width,
                    itemView.bottom.toFloat() - heightOffset
                )

                c.drawBitmap(icon, null, dest, null)
            }
        }
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    companion object {
        const val verticalOffset = 60f
        const val startOffset = 80f
        const val endOffset = 150f
    }
}
