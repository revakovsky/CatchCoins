package com.revakovskyi.catchcoins.utils

import android.app.AlertDialog
import androidx.fragment.app.Fragment
import com.revakovskyi.catchcoins.R

fun Fragment.showDialog(
    icon: Int,
    dialogTitle: Int,
    dialogMessage: Int,
    onClickPositive: () -> Unit,
    onClickNegative: () -> Unit
) {
    AlertDialog.Builder(requireContext())
        .setIcon(icon)
        .setTitle(dialogTitle)
        .setMessage(dialogMessage)
        .setPositiveButton(R.string.yes) { _, _ ->
            onClickPositive()
        }
        .setNegativeButton(R.string.no) { _, _ ->
            onClickNegative()
        }
        .create()
        .show()
}