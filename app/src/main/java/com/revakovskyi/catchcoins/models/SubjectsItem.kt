package com.revakovskyi.catchcoins.models

import android.annotation.SuppressLint
import android.widget.FrameLayout
import android.widget.ImageView
import com.revakovskyi.catchcoins.R
import com.revakovskyi.catchcoins.utils.GameMainSettings

@SuppressLint("ClickableViewAccessibility")
class SubjectsItem(
    var rectangle: Rectangle,
    parent: FrameLayout,
    var index: Int,
    private var time: Int = GameMainSettings.TIME_PRELOAD_GAME,
    private var fall: Boolean = false
) {

    val image = ImageView(parent.context)

    init {
        val frameLayout =
            FrameLayout.LayoutParams(rectangle.width.toInt(), rectangle.height.toInt())

        image.apply {
            layoutParams = frameLayout
            x = rectangle.x
            y = rectangle.y
            setImageResource(listOfSubjectsIcons[index])
            scaleType = ImageView.ScaleType.FIT_XY
        }
        parent.addView(image)
    }

    fun startFalling(speed: Int) {
        if (fall) {
            rectangle.y += speed
            image.y = rectangle.y
        } else {
            time--
            if (time < 0) {
                fall = true
            }
        }
    }


    companion object {
        val listOfSubjectsIcons = listOf(
            R.drawable.coin,
            R.drawable.rocks1
        )
    }

}