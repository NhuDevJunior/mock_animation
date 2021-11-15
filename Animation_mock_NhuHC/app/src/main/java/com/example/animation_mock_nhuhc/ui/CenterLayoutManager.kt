package com.example.animation_mock_nhuhc.ui

import android.content.Context
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.View
import androidx.core.view.updatePadding
import androidx.core.view.updatePaddingRelative
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView

class CenterLayoutManager : LinearLayoutManager {
    constructor(context: Context) : super(context)
    constructor(context: Context, orientation: Int, reverseLayout: Boolean) : super(
        context,
        orientation,
        reverseLayout
    )

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(
        context,
        attrs,
        defStyleAttr,
        defStyleRes
    )
    private lateinit var recyclerView: RecyclerView
    override fun smoothScrollToPosition(
        recyclerView: RecyclerView,
        state: RecyclerView.State,
        position: Int
    ) {
        val centerSmoothScroller = CenterSmoothScroller(recyclerView.context)
        centerSmoothScroller.targetPosition = position
        startSmoothScroll(centerSmoothScroller)
    }

    private class CenterSmoothScroller(context: Context) : LinearSmoothScroller(context) {
        override fun calculateDtToFit(
            viewStart: Int,
            viewEnd: Int,
            boxStart: Int,
            boxEnd: Int,
            snapPreference: Int
        ): Int =
            (boxStart + (boxEnd - boxStart) / 2) - (viewStart + (viewEnd - viewStart) / 2)

        override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics): Float {
            return MILLISECONDS_PER_INCH / displayMetrics.densityDpi
        }
    }

    override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State?) {
        if (childCount == 0 && state!!.itemCount > 0) {
            val firstChild = recycler!!.getViewForPosition(0)
            measureChildWithMargins(firstChild, 0, 0)
            recycler.recycleView(firstChild)
        }
        super.onLayoutChildren(recycler, state)
    }

    override fun measureChildWithMargins(child: View, widthUsed: Int, heightUsed: Int) {
        val lp = (child.layoutParams as RecyclerView.LayoutParams).viewAdapterPosition
        super.measureChildWithMargins(child, widthUsed, heightUsed)
        if (lp != 0 && lp != itemCount - 1) return
//         after determining first and/or last items size use it to alter host padding
        when (orientation) {
            HORIZONTAL -> {
                val hPadding = ((width - child.measuredWidth)).coerceAtLeast(0)
                if (!reverseLayout) {
                    if (lp == 0) recyclerView.updatePaddingRelative(start = 100)
                    if (lp == itemCount - 1) recyclerView.updatePadding(right = hPadding)
                } else {
                    if (lp == 0) recyclerView.updatePaddingRelative(end = hPadding)
                    if (lp == itemCount - 1) recyclerView.updatePaddingRelative(start = 100)
                }
            }
            VERTICAL -> {
                val vPadding = ((height - child.measuredHeight) / 2).coerceAtLeast(0)
                if (!reverseLayout) {
                    if (lp == 0) recyclerView.updatePaddingRelative(top = vPadding)
                    if (lp == itemCount - 1) recyclerView.updatePaddingRelative(bottom = vPadding)
                } else {
                    if (lp == 0) recyclerView.updatePaddingRelative(bottom = vPadding)
                    if (lp == itemCount - 1) recyclerView.updatePaddingRelative(top = vPadding)
                }
            }
        }
    }
    override fun onAttachedToWindow(view: RecyclerView) {
        recyclerView = view
        super.onAttachedToWindow(view)
    }
    companion object {
        // This number controls the speed of smooth scroll

        private const val MILLISECONDS_PER_INCH = 500f
    }
}