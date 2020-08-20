package com.nexters.mytine.ui.home

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.RectF
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.nexters.mytine.R

class TestCallback(private val testCallbackListener: TestCallbackListener) :
    ItemTouchHelper.Callback() {

    enum class ButtonsState {
        GONE, LEFT_VISIBLE, RIGHT_VISIBLE
    }

    private var c = Canvas()
    var isCurrentlyActive = false
    private var swipeBack = false
    private var buttonShowedState = ButtonsState.GONE

    private var buttonInstance: RectF? = null
    private var currentItemViewHolder: RecyclerView.ViewHolder? = null

    override fun convertToAbsoluteDirection(flags: Int, layoutDirection: Int): Int {

        Log.d("TestCallback", "convertToAbsoluteDirection() $flags ${ItemTouchHelper.START} $swipeBack $buttonShowedState")

        if (swipeBack) {
            swipeBack = buttonShowedState !== ButtonsState.GONE
            return 0
        }
        return super.convertToAbsoluteDirection(flags, layoutDirection)
    }

    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        var swipeFlags = 0

        val position = viewHolder.adapterPosition
        val isLeftSwipeable: Boolean
        val isRightSwipeable: Boolean

        if (position != RecyclerView.NO_POSITION) {
            isLeftSwipeable = testCallbackListener.isLeftSwipeable(position)
            isRightSwipeable = testCallbackListener.isRightSwipeable(position)
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

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        return true
    }

    override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
        var dx = dX

        this.isCurrentlyActive = isCurrentlyActive
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            if (buttonShowedState !== ButtonsState.GONE) {
                if (buttonShowedState === ButtonsState.LEFT_VISIBLE) dx = Math.max(dX, buttonWidth)
                if (buttonShowedState === ButtonsState.RIGHT_VISIBLE) dx = Math.min(dX, -buttonWidth)
                super.onChildDraw(c, recyclerView, viewHolder, dx, dY, actionState, false)
                setTouchListener(recyclerView, viewHolder, dx, dY, actionState)
                getDefaultUIUtil().onDraw(c, recyclerView, viewHolder.itemView, dx, dY, actionState, false)
            } else {
                // swipe 되는 순간은 무조건 buttonShowedState = GONE 상태
                // setTouchListener 로 초기 설정을 해준다.
                setTouchListener(recyclerView, viewHolder, dx, dY, actionState)
                getDefaultUIUtil().onDraw(c, recyclerView, viewHolder.itemView, dX, dY, actionState, isCurrentlyActive)
            }
        } else if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
            // super.onChildDraw() 호출 시 itemView 전체가 움직임.
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        }
        currentItemViewHolder = viewHolder
        onDraw(c)
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        return
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setTouchListener(
        recyclerView: RecyclerView,
        foregroundView: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int
    ) {
        recyclerView.setOnTouchListener { _, event ->
            swipeBack = event.action == MotionEvent.ACTION_CANCEL || event.action == MotionEvent.ACTION_UP
            // itemView 를 잡고 끌게 되는 경우 event.action = ACTION_MOVE 상태
            // 손가락을 띄우는 순간 event.action = ACTION_UP 상태로 swipeBack 이 true 로 바뀐다.
            if (swipeBack) {
                if (dX < -buttonWidth) buttonShowedState = ButtonsState.RIGHT_VISIBLE
                else if (dX > buttonWidth) buttonShowedState = ButtonsState.LEFT_VISIBLE

                if (buttonShowedState !== ButtonsState.GONE) {
                    // 손가락을 띄우게 되었을 때, buttonShowedState 를 보여준다.
                    // 우측 및 좌측 버튼이 공개되어 있는 경우, setTouchDownListener 와 setItemsClickable 초기 설정.
                    setTouchDownListener(recyclerView, foregroundView, dX, dY, actionState)
                    setItemsClickable(recyclerView, false)
                }
            }
            false
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setTouchDownListener(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int
    ) {
        recyclerView.setOnTouchListener { _, event ->
            Log.d("TestCallback", " touch down  ${event.action}")
            // 현재 우측 및 좌측의 버튼이 공개되어 있는 상태
            // 만약 사용자가 itemView 를 누르게 된다면 setTouchUpListener 초기 설정.
            if (event.action == MotionEvent.ACTION_DOWN) {
                setTouchUpListener(recyclerView, viewHolder, dX, dY, actionState)
            }
            false
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setTouchUpListener(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int) {
        recyclerView.setOnTouchListener { _, event ->
            Log.d("TestCallback", " touch up ${event.action}, $buttonShowedState")
            // 사용자가 손가락을 띄우게 되었을 경우(ACTION_UP 상태 일 때) 작동하는 코드.
            if (event.action == MotionEvent.ACTION_UP) {
                super.onChildDraw(c, recyclerView, viewHolder, 0f, dY, actionState, isCurrentlyActive)
                getDefaultUIUtil().onDraw(c, recyclerView, viewHolder.itemView, 0f, dY, actionState, isCurrentlyActive)

                // recyclerView 의 clickable 을 활성화하는 부분.
                setItemsClickable(recyclerView, true)

                swipeBack = false
                if (buttonInstance != null && buttonInstance!!.contains(event.x, event.y)) {
                    if (buttonShowedState === ButtonsState.LEFT_VISIBLE) {
                        testCallbackListener.onLeftClicked(viewHolder.adapterPosition)
                    } else if (buttonShowedState === ButtonsState.RIGHT_VISIBLE) {
                        testCallbackListener.onRightClicked(viewHolder.adapterPosition)
                    }
                }

                buttonShowedState = ButtonsState.GONE
                currentItemViewHolder = null
            } else if (event.action == MotionEvent.ACTION_MOVE) {
                getDefaultUIUtil().onDraw(c, recyclerView, viewHolder.itemView, dX, dY, actionState, isCurrentlyActive)
            }
            false
        }
    }

    private fun setItemsClickable(recyclerView: RecyclerView, isClickable: Boolean) {
        for (i in 0 until recyclerView.childCount) {
            recyclerView.getChildAt(i).isClickable = isClickable
        }
    }

    private fun drawButtons(c: Canvas, viewHolder: RecyclerView.ViewHolder) {
        val itemView: View = viewHolder.itemView
        val resources = itemView.resources

        val iconL = BitmapFactory.decodeResource(resources, R.drawable.ic_card_check_back)
        val leftButton = RectF(
            itemView.left.toFloat() + ItemTouchHelperCallback.startOffset,
            itemView.top.toFloat() + ItemTouchHelperCallback.verticalOffset,
            itemView.left.toFloat() + ItemTouchHelperCallback.endOffset,
            itemView.bottom.toFloat() - ItemTouchHelperCallback.verticalOffset
        )
        c.drawBitmap(iconL, null, leftButton, null)

        val iconR = BitmapFactory.decodeResource(resources, R.drawable.ic_card_check_back)
        val rightButton = RectF(
            itemView.right.toFloat() - ItemTouchHelperCallback.endOffset,
            itemView.top.toFloat() + ItemTouchHelperCallback.verticalOffset,
            itemView.right.toFloat() - ItemTouchHelperCallback.startOffset,
            itemView.bottom.toFloat() - ItemTouchHelperCallback.verticalOffset
        )
        c.drawBitmap(iconR, null, rightButton, null)

        buttonInstance = null
        if (buttonShowedState === ButtonsState.LEFT_VISIBLE) {
            buttonInstance = leftButton
        } else if (buttonShowedState === ButtonsState.RIGHT_VISIBLE) {
            buttonInstance = rightButton
        }
    }

    private fun onDraw(c: Canvas) {
        if (currentItemViewHolder != null) {
            drawButtons(c, currentItemViewHolder!!)
        }
    }

    companion object {
        private const val buttonWidth = 300f
    }
}
