package com.example.animation_mock_nhuhc.ui

import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class OffsetItemSeekBar(private val width : Int,private val guideline:Float) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        val offLast = width*0.15
        val offStart = width*0.85
        //val offFirst = (width / 8.toFloat()).toInt()
        if (parent.getChildAdapterPosition(view) == state.itemCount - 1) {
            (view.layoutParams as ViewGroup.MarginLayoutParams).rightMargin = 0
            setupOutRect(outRect, offStart.toInt(), false)
        }
        if (parent.getChildAdapterPosition(view) == 0) {
            (view.layoutParams as ViewGroup.MarginLayoutParams).leftMargin = 0
            setupOutRect(outRect, offLast.toInt(), true)
        }
    }
    private fun setupOutRect(rect: Rect, offset: Int, start: Boolean) {
        if (start) {
            rect.left = offset
        } else {
            rect.right = offset
        }
    }
}