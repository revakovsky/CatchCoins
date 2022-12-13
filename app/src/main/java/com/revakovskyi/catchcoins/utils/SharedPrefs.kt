package com.revakovskyi.catchcoins.utils

import android.content.Context
import android.content.Context.MODE_PRIVATE
import androidx.core.content.edit

class SharedPrefs(context: Context) {
    private val shPrefs = context.getSharedPreferences(SHARED_PREFS_KEY, MODE_PRIVATE)

    fun saveCurrentScore(currentScore: Int) { shPrefs.edit { putInt(SP_CURRENT_SCORE, currentScore) }}

    fun getCurrentScore(): Int = shPrefs.getInt(SP_CURRENT_SCORE, 0)

    fun clearCurrentScore() { shPrefs.edit { remove(SP_CURRENT_SCORE) }}

    fun saveMaxScore(score: Int) {
        val maxScore = getMaxScore()
        if (score > maxScore)
            shPrefs.edit { putInt(SP_MAX_SCORE, score) }
    }

    fun getMaxScore(): Int = shPrefs.getInt(SP_MAX_SCORE, 0)

    fun clearMaxScore() { shPrefs.edit { remove(SP_MAX_SCORE) }}


    companion object {
        private const val SHARED_PREFS_KEY = "KEY"
        private const val SP_CURRENT_SCORE = "CURRENT"
        private const val SP_MAX_SCORE = "MAX"
    }
}