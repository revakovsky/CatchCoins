package com.revakovskyi.catchcoins.models

import android.widget.FrameLayout
import android.widget.ImageView
import com.revakovskyi.catchcoins.R

class BasketItem(
    var rectangle: Rectangle,
    parent: FrameLayout,
) {

    val image = ImageView(parent.context)

    init {
        val frameLayout =
            FrameLayout.LayoutParams(rectangle.width.toInt(), rectangle.height.toInt())

        image.apply {
            layoutParams = frameLayout
            x = rectangle.x
            y = rectangle.y
            setImageResource(R.drawable.basket)
            scaleType = ImageView.ScaleType.FIT_XY
        }
        parent.addView(image)
    }

}