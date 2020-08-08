package com.nexters.mytine.ui.home

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.nexters.mytine.R

internal class ItemTouchHelperCallback(var listener: ItemTouchHelperListener) : ItemTouchHelper.Callback() {

    private val background = ColorDrawable(Color.RED)

    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        var swipeFlags = ItemTouchHelper.START or ItemTouchHelper.END

        Log.e("언제호출되나~", "$swipeFlags")
        Log.e("언제호출되나~스따뜨", "${ItemTouchHelper.START}")
        Log.e("언제호출되나~엔드", "${ItemTouchHelper.END}")
        return makeMovementFlags(0, swipeFlags)
    }

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean = true

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        listener.onItemSwipe(viewHolder.adapterPosition, direction)
        Log.e("언제호출되나~", "onSwiped")
    }

    override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

        val itemView: View = viewHolder.itemView

        when {
            dX > 0 -> { // 오른쪽으로
                background.color = Color.parseColor("#ff4775")
                background.setBounds(
                    itemView.left, itemView.top,
                    itemView.left + dX.toInt() + R.integer.background_color_offset,
                    itemView.bottom
                )

                background.draw(c)
            }
            dX < 0 -> { // 왼쪽으로
                background.color = Color.parseColor("#00ed75")
                background.setBounds(
                    itemView.right + dX.toInt() - R.integer.background_color_offset,
                    itemView.top, itemView.right, itemView.bottom
                )
                background.draw(c)
            }
            else -> { // 스와이프 X
                background.setBounds(0, 0, 0, 0)
            }
        }
    }
}
