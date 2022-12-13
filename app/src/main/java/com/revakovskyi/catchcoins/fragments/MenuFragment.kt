package com.revakovskyi.catchcoins.fragments

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.revakovskyi.catchcoins.R
import com.revakovskyi.catchcoins.databinding.FragmentMenuBinding
import com.revakovskyi.catchcoins.utils.GameMainSettings
import com.revakovskyi.catchcoins.utils.SharedPrefs
import com.revakovskyi.catchcoins.utils.showDialog

class MenuFragment : Fragment(R.layout.fragment_menu) {

    private var binding: FragmentMenuBinding? = null
    private var sharedPrefs: SharedPrefs? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentMenuBinding.bind(view)
        sharedPrefs = SharedPrefs(requireActivity())

        val currentScore = sharedPrefs?.getCurrentScore()
        if (currentScore == 0) setContinueButtonVisibility()

        binding?.apply {

            startGameButton.setOnClickListener {
                if (currentScore != 0) {
                    showDialog(
                        icon = R.drawable.question_icon,
                        dialogTitle = R.string.continue_game,
                        dialogMessage = R.string.progress_status,
                        onClickPositive = { continueGame() },
                        onClickNegative = { startNewGame() }
                    )
                } else startNewGame()
            }

            continueGameButton.setOnClickListener { continueGame() }

            bestResultButton.setOnClickListener { openDashboard() }

            exitButton.setOnClickListener {
                showDialog(
                    icon = R.drawable.question_icon,
                    dialogTitle = R.string.exit,
                    dialogMessage = R.string.want_to_leave,
                    onClickPositive = { requireActivity().finish() },
                    onClickNegative = { }
                )
            }
        }
    }

    private fun startNewGame() {
        findNavController().navigate(
            resId = R.id.action_menuFragment2_to_gameFragment2,
            args = bundleOf(GameMainSettings.CONTINUE_BUNDLE_KEY to false)
        )
    }

    private fun continueGame() {
        findNavController().navigate(
            resId = R.id.action_menuFragment2_to_gameFragment2,
            args = bundleOf(GameMainSettings.CONTINUE_BUNDLE_KEY to true)
        )
    }

    private fun setContinueButtonVisibility() {
        binding?.continueGameButton?.visibility = View.GONE
    }

    private fun openDashboard() {
        changeMenuVisibility(View.GONE)

        binding?.apply {
            coinCounter.text = sharedPrefs?.getMaxScore().toString()
            clearButton.setOnClickListener { showIntentDialog() }
            closeDashboardButton.setOnClickListener { changeMenuVisibility(View.VISIBLE) }
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
        showDialog(
            icon = R.drawable.question_icon,
            dialogTitle = R.string.clear,
            dialogMessage = R.string.are_you_sure,
            onClickPositive = {
                sharedPrefs?.clearMaxScore()
                binding?.coinCounter?.text = "0"
            },
            onClickNegative = {}
        )
    }

    override fun onDetach() {
        super.onDetach()
        binding = null
        sharedPrefs = null
    }

}