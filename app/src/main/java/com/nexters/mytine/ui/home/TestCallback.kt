package com.nexters.mytine.ui.home

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.ACTION_STATE_SWIPE
import androidx.recyclerview.widget.RecyclerView
import com.nexters.mytine.R



internal class TestCallback(var listener: ItemTouchHelperListener) : ItemTouchHelper.Callback() {

    private var buttonInstance: RectF? = null

    private val background = Paint()

    private var swipeBack = false
    private var buttonShowedState = ButtonsState.GONE
    private val buttonWidth = 300f

    internal enum class ButtonsState {
        GONE, LEFT_VISIBLE, RIGHT_VISIBLE
    }


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
        var mdX = dX
        if (actionState == ACTION_STATE_SWIPE) {
            if (buttonShowedState != ButtonsState.GONE) {
                if (buttonShowedState == ButtonsState.LEFT_VISIBLE) mdX = Math.max(mdX, buttonWidth)
                if (buttonShowedState == ButtonsState.RIGHT_VISIBLE) mdX = Math.min(mdX, -buttonWidth)
                super.onChildDraw(c, recyclerView, viewHolder, mdX, dY, actionState, isCurrentlyActive)
            } else {
                setTouchListener(c, recyclerView, viewHolder, mdX, dY, actionState, isCurrentlyActive)
            }
        }

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        val itemView: View = viewHolder.itemView
        val resources = itemView.resources
        lateinit var icon: Bitmap

        when {
            dX > 0 -> { // 오른쪽으로
                icon = BitmapFactory.decodeResource(resources, R.drawable.card_check_back)
                val dest = RectF(
                    itemView.left.toFloat() + startOffset,
                    itemView.top.toFloat() + verticalOffset,
                    itemView.left.toFloat() + endOffset,
                    itemView.bottom.toFloat() - verticalOffset
                )
                c.drawBitmap(icon, null, dest, background)
            }
            dX < 0 -> { // 왼쪽으로
                icon = BitmapFactory.decodeResource(resources, R.drawable.card_check)
                val dest = RectF(
                    itemView.right.toFloat() - endOffset,
                    itemView.top.toFloat() + verticalOffset,
                    itemView.right.toFloat() - startOffset,
                    itemView.bottom.toFloat() - verticalOffset
                )

                c.drawBitmap(icon, null, dest, background)
            }
        }

    }
    override fun convertToAbsoluteDirection(flags: Int, layoutDirection: Int): Int {
        if (swipeBack) {
            swipeBack = buttonShowedState != ButtonsState.GONE
            return 0
        }
        return super.convertToAbsoluteDirection(flags, layoutDirection)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setTouchListener(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
        recyclerView.setOnTouchListener { _, event ->
            swipeBack =
                event.action == MotionEvent.ACTION_CANCEL || event.action == MotionEvent.ACTION_UP
            Log.e("setTouchListener - swipeback","${swipeBack}")

            //스와이프 한 정도 확인
            Log.e("setTouchListener - dX","${dX}")
            if (swipeBack) {
                when {
                    dX < -buttonWidth -> {buttonShowedState = ButtonsState.RIGHT_VISIBLE
                        Log.e("setTouchListener - RIGHT_VISIBLE","${buttonShowedState}")
                    }
                    dX > buttonWidth -> {buttonShowedState = ButtonsState.LEFT_VISIBLE
                        Log.e("setTouchListener - LEFT_VISIBLE","${buttonShowedState}")
                    }
                }


                //상태 변경 버튼 표시
                Log.e("setTouchListener - buttonShowedState","${buttonShowedState}")
                if (buttonShowedState != ButtonsState.GONE) {
                    //클릭리스너 재정의
                    Log.e("setTouchListener - buttonShowedState","${buttonShowedState}")
                    Log.e("setTouchListener - 재정의","${"들어와따"}")
                    setTouchDownListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    setItemsClickable(recyclerView, false)
                }
            }
            false
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setTouchDownListener(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
        recyclerView.setOnTouchListener { _, event ->

            if (event.action == MotionEvent.ACTION_DOWN) {
                Log.e("setTouchDownListener -액션 다운!!!!! ","${event.action}")
                setTouchUpListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
            false
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setTouchUpListener(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
        recyclerView.setOnTouchListener { v, event ->

            if (event.action == MotionEvent.ACTION_UP) {
                Log.e("setTouchUpListener -액션업!!!!! ","${event.action}")

                super@TestCallback.onChildDraw(c, recyclerView, viewHolder, 0F, dY, actionState, false)
                recyclerView.setOnTouchListener { _, _ -> false }
                setItemsClickable(recyclerView, true)

                if (buttonInstance != null && buttonInstance?.contains(event.x, event.y) == true) {
                    when (buttonShowedState) {
                        ButtonsState.LEFT_VISIBLE -> listener.onLeftClicked(viewHolder.adapterPosition)
                        ButtonsState.RIGHT_VISIBLE -> listener.onRightClicked(viewHolder.adapterPosition)
                        else -> Unit
                    }
                }

                swipeBack = false
                buttonShowedState = ButtonsState.GONE
            }
            false
        }
    }

    private fun setItemsClickable(recyclerView: RecyclerView, isClickable: Boolean) {
        for (i in 0 until recyclerView.childCount) {
            recyclerView.getChildAt(i).isClickable = isClickable
        }
    }

    companion object {
        const val verticalOffset = 50f
        const val startOffset = 80f
        const val endOffset = 150f
    }
}
