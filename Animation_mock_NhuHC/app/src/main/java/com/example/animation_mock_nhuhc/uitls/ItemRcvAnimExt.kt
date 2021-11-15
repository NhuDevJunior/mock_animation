package com.example.animation_mock_nhuhc.uitls

import android.content.ClipData
import android.content.res.ColorStateList
import android.os.Build
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.AnimationUtils
import android.view.animation.DecelerateInterpolator
import androidx.annotation.RequiresApi
import androidx.core.view.doOnLayout
import com.example.animation_mock_nhuhc.R
import com.example.animation_mock_nhuhc.databinding.ItemBinding
import com.example.animation_mock_nhuhc.model.Item


@RequiresApi(Build.VERSION_CODES.M)
fun ItemBinding.enableItem(translateContentAnimId: Int,item: Item) {
    root.doOnLayout {
        avatar.setImageResource(item.path)
        iconView.backgroundTintList =
            ColorStateList.valueOf(it.context.getColor(R.color.white))
        layoutDetailExpense.backgroundTintList =
            ColorStateList.valueOf(it.context.getColor(item.color))

        layoutDetailExpense.apply {
            alpha = 1f
            scaleX = 1f
            scaleY = 1f
            visibility = View.VISIBLE
            startAnimation(
                AnimationUtils.loadAnimation(
                    it.context,
                    R.anim.scale_item_rcv
                ).apply {
                    interpolator = AccelerateInterpolator()
                }
            )
        }

        tvName.startAnimation(
            AnimationUtils.loadAnimation(
                it.context,
                translateContentAnimId
            ).apply {
                interpolator = DecelerateInterpolator()
            }
        )
        tvCash.startAnimation(
            AnimationUtils.loadAnimation(
                it.context,
                translateContentAnimId
            ).apply {
                interpolator = DecelerateInterpolator()
            }
        )
    }
}

@RequiresApi(Build.VERSION_CODES.M)
fun ItemBinding.disable(item: Item) {
    avatar.setImageResource(item.path)
    iconView.backgroundTintList = ColorStateList.valueOf(root.context.getColor(R.color.white))
    layoutDetailExpense.startAnimation(AnimationUtils.loadAnimation(root.context, R.anim.zoom_out).apply {
        duration = 1000
    })
    layoutDetailExpense.apply {
        visibility = View.GONE
        alpha = 0f
//        scaleX = 0.8f
//        pivotX = 0f
//        animate()
//            .setInterpolator(DecelerateInterpolator())
//            .scaleX(0.5f)
//            .scaleY(0.8f)
//            .alpha(0f)
//            .duration = 0
    }
}

