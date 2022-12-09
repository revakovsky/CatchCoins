package com.revakovskyi.catchcoins.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.revakovskyi.catchcoins.R
import com.revakovskyi.catchcoins.databinding.FragmentMenuBinding

class MenuFragment : Fragment(R.layout.fragment_menu) {

    private lateinit var binding: FragmentMenuBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMenuBinding.bind(view)

        binding.startGameButton.setOnClickListener {
            findNavController().navigate(R.id.action_menuFragment2_to_gameFragment2)
        }

        binding.bestResultButton.setOnClickListener {
            //todo
        }

        binding.exitButton.setOnClickListener {
            //todo
        }

    }

}