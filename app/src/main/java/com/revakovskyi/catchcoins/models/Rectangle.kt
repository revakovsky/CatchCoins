package com.revakovskyi.catchcoins.models

data class Rectangle(
    var x: Float,
    var y: Float,
    var width: Float,
    var height: Float
) {
    fun overlaps(rectangle: Rectangle): Boolean =
                x < rectangle.x + rectangle.width &&
                x + width > rectangle.x + rectangle.width / 4 &&
                x < rectangle.x + rectangle.width - rectangle.width / 3 &&
                y < rectangle.y + rectangle.height &&
                y + height > rectangle.y + rectangle.height / 2 + 50
}
