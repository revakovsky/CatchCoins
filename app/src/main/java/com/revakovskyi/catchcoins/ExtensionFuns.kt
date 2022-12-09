package com.revakovskyi.catchcoins

import android.app.AlertDialog
import androidx.fragment.app.Fragment

fun Fragment.showExitDialog() {
    AlertDialog.Builder(requireContext())
        .setIcon(R.drawable.cancel)
        .setTitle(R.string.exit)
        .setMessage(R.string.want_to_leave)
        .setPositiveButton(R.string.yes) {_, _ ->
            requireActivity().finish()
        }
        .setNegativeButton(R.string.no, null)
        .create()
        .show()
}