package com.example.animation_mock_nhuhc.ui

import android.content.Context
import android.os.Build
import android.os.Handler
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.AbsListView.OnScrollListener.SCROLL_STATE_FLING
import android.widget.AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL
import androidx.recyclerview.widget.RecyclerView
import java.lang.Math.*


class SnappingRecyclerView : RecyclerView {
    private var snapEnabled = false
    private var userScrolling = false
    private var scrolling = false
    private var scrollStateTemp: Int = 0
    private var lastScrollTime: Long = 0
    private val handlerUpdate = Handler()
    private var scaleUnfocusedViews = false
    private var onItemSelectedListener: OnItemSelectedListener? = null

    private val centerView: View?
        get() = getChildClosestToPosition(0)

    val horizontalScrollOffset: Int
        get() = computeHorizontalScrollOffset()

    val verticalScrollOffset: Int
        get() = computeVerticalScrollOffset()

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    /**
     * Enable snapping behaviour for this recyclerView
     * @param enabled enable or disable the snapping behaviour
     */
    fun setSnapEnabled(enabled: Boolean) {
        snapEnabled = enabled

        if (enabled) {
            addOnLayoutChangeListener(object : View.OnLayoutChangeListener {
                override fun onLayoutChange(
                    v: View, left: Int, top: Int, right: Int, bottom: Int, oldLeft: Int,
                    oldTop: Int, oldRight: Int, oldBottom: Int
                ) {
                    if (left == oldLeft && right == oldRight && top == oldTop && bottom == oldBottom) {
                        removeOnLayoutChangeListener(this)
                        updateViews()
                        handlerUpdate.postDelayed({
                            scrollToView(getChildClosestToPosition(adapter?.itemCount?.minus(1) ?: 0 / 2)) }
                            , 20)
                    }
                }
            })

            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    updateViews()
                    super.onScrolled(recyclerView, dx, dy)
                }

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)

                    /** if scroll is caused by a touch (scroll touch, not any touch)  */
                    if (newState == SCROLL_STATE_TOUCH_SCROLL) {
                        /** if scroll was initiated already, this is not a user scrolling, but probably a tap, else set userScrolling  */
                        if (!scrolling) {
                            userScrolling = true
                        }
                    } else if (newState == SCROLL_STATE_IDLE) {
                        scrollToView(centerView)

                        userScrolling = false
                        scrolling = false
                    } else if (newState == SCROLL_STATE_FLING) {
                        scrolling = true
                    }

                    scrollStateTemp = newState
                }
            })
        }
    }

    /**
     * Enable snapping behaviour for this recyclerView
     * @param enabled enable or disable the snapping behaviour
     * @param scaleUnfocusedViews downScale the views which are not focused based on how far away they are from the center
     */
    fun setSnapEnabled(enabled: Boolean, scaleUnfocusedViews: Boolean) {
        this.scaleUnfocusedViews = scaleUnfocusedViews
        setSnapEnabled(enabled)
    }

    private fun updateViews() {
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            setMarginsForChild(child)
            if (scaleUnfocusedViews) {
                val percentage = getPercentageFromCenter(child)
                val scale = 1f - 0.2f * percentage
                    child.scaleX = scale
                    child.scaleY = scale

//                val layoutParams = child.layoutParams
//                layoutParams.width = (child.width/1.5f).toInt()
//                child.layoutParams = layoutParams
            }
        }
    }

    /**
     * Adds the margins to a childView so a view will still center even if it's only a single child
     * @param child childView to set margins for
     */
    private fun setMarginsForChild(child: View) {
        val lastItemIndex = layoutManager!!.itemCount - 1
        val childIndex = getChildAdapterPosition(child)
        val startMargin = if (childIndex == 0) measuredWidth / 2 else 0
        val endMargin = if (childIndex == lastItemIndex) measuredWidth / 2 else 0
        (child.layoutParams as MarginLayoutParams).apply {
            marginStart = startMargin
            marginEnd = endMargin
            setMargins(startMargin, 0, endMargin, 0)
        }
        child.requestLayout()
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (!snapEnabled)
            return super.dispatchTouchEvent(event)

        val currentTime = System.currentTimeMillis()

        /** if touch events are being spammed, this is due to user scrolling right after a tap,
         * so set userScrolling to true  */
        if (scrolling && scrollStateTemp == SCROLL_STATE_TOUCH_SCROLL) {
            if (currentTime - lastScrollTime < MINIMUM_SCROLL_EVENT_OFFSET_MS) {
                userScrolling = true
            }
        }

        lastScrollTime = currentTime

        val targetView = getChildClosestToPosition(event.x.toInt())

        if (!userScrolling && event.action == MotionEvent.ACTION_UP && targetView !== centerView) {
            scrollToView(targetView)
            return true
        }
        return super.dispatchTouchEvent(event)
    }

    override fun onInterceptTouchEvent(e: MotionEvent): Boolean {
        if (!snapEnabled) return super.onInterceptTouchEvent(e)
        val targetView = getChildClosestToPosition(e.x.toInt())
        return if (targetView !== centerView) {
            true
        } else super.onInterceptTouchEvent(e)

    }

    private fun getChildClosestToPosition(x: Int): View? {
        if (childCount <= 0) return null
        val itemWidth = getChildAt(0).measuredWidth

        var closestX = 9999
        var closestChild: View? = null

        for (i in 0 until childCount) {
            val child = getChildAt(i)

            val childCenterX = child.x.toInt()
            val xDistance = childCenterX - x

            /** if child center is closer than previous closest, set it as closest   */
            if (abs(xDistance) < abs(closestX)) {
                closestX = xDistance
                closestChild = child
            }
        }

        return closestChild
    }

    public fun scrollToView(child: View?) {
        if (child == null)
            return
        stopScroll()
        val scrollDistance = getScrollDistance(child)
        if (scrollDistance != 0)
            smoothScrollBy(scrollDistance, 0)
        onItemSelectedListener?.onItemSelected(getChildAdapterPosition(child))
    }

    private fun getScrollDistance(child: View): Int {
        val itemWidth = getChildAt(0).measuredWidth
        val centerX = 0

        val childCenterX = child.x.toInt()

        return childCenterX - centerX
    }

    private fun getPercentageFromCenter(child: View): Float {
        val centerX = 0f
        val childCenterX = child.x
        val offSet = max(centerX, childCenterX) - min(centerX, childCenterX)
        val maxOffset = 0 + child.width
        return offSet / maxOffset
    }

    fun isChildCenterView(child: View): Boolean {
        return child === centerView
    }

    fun smoothUserScrollBy(x: Int, y: Int) {
        userScrolling = true
        smoothScrollBy(x, y)
    }

    fun addOnItemSelectedListener(onItemSelectedListener: OnItemSelectedListener) {
        this.onItemSelectedListener = onItemSelectedListener
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        handlerUpdate.removeCallbacksAndMessages(null)
    }

    companion object {
        private const val MINIMUM_SCROLL_EVENT_OFFSET_MS = 20
    }

    interface OnItemSelectedListener {
        fun onItemSelected(position: Int)
    }
}