package com.revakovskyi.catchcoins.utils

object GameMainSettings {

    // Time and Speed values
    const val TIME_PRELOAD_GAME: Int = 1
    const val FALLING_SUBJECTS_SPEED: Int = 8
    const val FRAME_RATE: Long = 16                         // 1000msec / 60frame
    const val TIME_BETWEEN_CREATING_SUBJECTS: Int = 120     // 1sec = 60

    // Screen dimension values
    const val VALUE_OF_PIXELS_BELOW_SCREEN: Float = 1.01f

    // Values for the Basket
    const val BASKET_POS_X: Float = 0.45f
    const val BASKET_POS_Y: Float = 0.72f
    const val BASKET_WIDTH: Float = 0.15f
    const val BASKET_HEIGHT: Float = 0.25f

    // Values for the Subjects
    const val SCREEN_OFFSET_LEFT: Float = 0.08f
    const val SCREEN_OFFSET_RIGHT: Float = 0.84f
    const val SUBJECT_WIDTH: Float = 0.05f
    const val SUBJECT_INITIAL_POS: Float = -0.1f

    // Constants
    const val CONTINUE_BUNDLE_KEY = "continue"

}