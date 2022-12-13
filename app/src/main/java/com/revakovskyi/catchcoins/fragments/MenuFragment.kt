package com.revakovskyi.catchcoins.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.revakovskyi.catchcoins.R
import com.revakovskyi.catchcoins.databinding.FragmentMenuBinding
import com.revakovskyi.catchcoins.utils.SharedPrefs
import com.revakovskyi.catchcoins.utils.showExitDialog

class MenuFragment : Fragment(R.layout.fragment_menu) {

    private var binding: FragmentMenuBinding? = null
    private var sharedPrefs: SharedPrefs? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMenuBinding.bind(view)
        sharedPrefs = SharedPrefs(requireActivity())

        val currentScore = sharedPrefs?.getCurrentScore()
        if (currentScore == 0) binding?.continueGameButton?.visibility = View.GONE

        binding?.startGameButton?.setOnClickListener {

            if (currentScore != 0) {
                AlertDialog.Builder(requireContext())
                    .setIcon(R.drawable.question_icon)
                    .setTitle(R.string.continue_game)
                    .setMessage(R.string.progress_status)
                    .setPositiveButton(R.string.yes) { _, _ ->
                        findNavController().navigate(
                            R.id.action_menuFragment2_to_gameFragment2,
                            bundleOf("continue" to true)
                        )
                    }
                    .setNegativeButton(R.string.no) { _, _ ->
                        findNavController().navigate(
                            R.id.action_menuFragment2_to_gameFragment2,
                            bundleOf("continue" to false)
                        )
                    }
                    .create()
                    .show()
            } else {
                findNavController().navigate(
                    R.id.action_menuFragment2_to_gameFragment2,
                    bundleOf("continue" to false)
                )
            }
        }

        binding?.continueGameButton?.setOnClickListener {
            findNavController().navigate(
                R.id.action_menuFragment2_to_gameFragment2,
                bundleOf("continue" to true)
            )
        }

        binding?.bestResultButton?.setOnClickListener {
            openDashboard()
        }

        binding?.exitButton?.setOnClickListener {
            showExitDialog()
        }

    }

    private fun openDashboard() {
        changeMenuVisibility(View.GONE)

        binding?.apply {
            coinCounter.text = sharedPrefs?.getMaxScore().toString()
            clearButton.setOnClickListener {
                showIntentDialog()
            }
            closeDashboardButton.setOnClickListener {
                changeMenuVisibility(View.VISIBLE)
            }
        }
    }

    private fun changeMenuVisibility(visibilityValue: Int) {
        binding?.apply {
            dowerChestImage.visibility = visibilityValue
            coinRainImage.visibility = visibilityValue
            imageView.visibility = visibilityValue
            gameTitle.visibility = visibilityValue
            continueGameButton.visibility = visibilityValue
            startGameButton.visibility = visibilityValue
            bestResultButton.visibility = visibilityValue
            exitButton.visibility = visibilityValue

            when (visibilityValue) {
                View.GONE -> dashboard.visibility = View.VISIBLE
                View.VISIBLE -> dashboard.visibility = View.GONE
            }
        }
    }

    private fun showIntentDialog() {
        AlertDialog.Builder(requireContext())
            .setIcon(R.drawable.question_icon)
            .setTitle(R.string.clear)
            .setMessage(R.string.are_you_sure)
            .setPositiveButton(R.string.yes) { _, _ ->
                sharedPrefs?.clearMaxScore()
                binding?.coinCounter?.text = "0"
            }
            .setNegativeButton(R.string.no, null)
            .create()
            .show()
    }

    override fun onDetach() {
        super.onDetach()
        binding = null
        sharedPrefs = null
    }

}