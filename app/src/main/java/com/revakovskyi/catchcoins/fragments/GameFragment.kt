package com.revakovskyi.catchcoins.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.revakovskyi.catchcoins.R
import com.revakovskyi.catchcoins.databinding.FragmentGameBinding
import com.revakovskyi.catchcoins.models.BasketItem
import com.revakovskyi.catchcoins.models.Rectangle
import com.revakovskyi.catchcoins.models.SubjectsItem
import com.revakovskyi.catchcoins.utils.GameMainSettings
import kotlin.random.Random

class GameFragment : Fragment(R.layout.fragment_game) {

    private lateinit var binding: FragmentGameBinding

    private var width = 0f
    private var height = 0f

    private lateinit var handler: Handler
    private lateinit var runnable: Runnable

    private var time = 0
    private var score = 0
    private var running = true

    private var listOfSubjects = listOf<SubjectsItem>()
    private lateinit var basketItem: BasketItem


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentGameBinding.bind(view)

        setBackButtonAction()
        getScreenDimensions()

        createBasket()
        createSubject()

        @Suppress("DEPRECATION")
        handler = Handler()
        runnable = object : Runnable {

            @SuppressLint("SetTextI18n")
            override fun run() {
                if (running) {
                    time++

                    // TODO WHAT THE TIME IS THIS?
                    if (time >= GameMainSettings.MAX_TIME) {
                        time = 0
                        createSubject()
                    }

                    val iterator: Iterator<*> = listOfSubjects.iterator()

                    while (iterator.hasNext()) {
                        val subject: SubjectsItem = (iterator.next() as SubjectsItem?)!!

                        subject.startFalling(speed = GameMainSettings.FALLING_SUBJECTS_SPEED)

                        if (isKoinOverlapsBasket(subject)) {
                            increaseScore()
                            removeSubject(subject)

                        } else if (isRockOverlapsBasket(subject)) {
                            decreaseScore()
                            removeSubject(subject)

                        } else if (isSubjectFeltDown(subject)) {
                            removeSubject(subject)

                        } else if (score < 0) {
                            // todo finish part
                        }
                    }
                }
                handler.postDelayed(this, GameMainSettings.FALL_TIME_DELAY)
            }
        }
    }

    override fun onStart() {
        super.onStart()

        // todo Ask what is it for?
        val iterator: Iterator<*> = listOfSubjects.iterator()
        while (iterator.hasNext()) {
            val element: SubjectsItem = (iterator.next() as SubjectsItem?)!!
            removeSubject(element)
        }
        handler.post(runnable)
    }

    private fun getScreenDimensions() {
        width = resources.displayMetrics.widthPixels.toFloat()
        height = resources.displayMetrics.heightPixels.toFloat()
    }

    private fun removeSubject(
        subject: SubjectsItem
    ) {
        val mutableList = listOfSubjects.toMutableList()
        binding.root.removeView(subject.image)
        mutableList.remove(subject)
        listOfSubjects = mutableList.toList()
    }

    private fun isKoinOverlapsBasket(subject: SubjectsItem): Boolean =
        subject.rectangle.overlaps(basketItem.rectangle) && subject.index == 0

    private fun isRockOverlapsBasket(subject: SubjectsItem): Boolean =
        subject.rectangle.overlaps(basketItem.rectangle) && subject.index != 0

    private fun isSubjectFeltDown(subject: SubjectsItem): Boolean {
        val subjectBottomLine = subject.rectangle.y + subject.rectangle.height
        val screenBottomLine = height * GameMainSettings.VALUE_OF_PIXELS_BELOW_SCREEN
        return subjectBottomLine > screenBottomLine
    }

    private fun increaseScore() {
        score++
        binding.coinCounter.text = "$score"
    }

    private fun decreaseScore() {
        score--
        binding.coinCounter.text = "$score"
    }

    private fun setBackButtonAction() {
        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun createBasket() {
        basketItem = BasketItem(
            rectangle = Rectangle(
                x = width * GameMainSettings.BASKET_POS_X,
                y = height * GameMainSettings.BASKET_POS_Y,
                width = width * GameMainSettings.BASKET_WIDTH,
                height = height * GameMainSettings.BASKET_HEIGHT
            ),
            parent = binding.root
        )
    }

    private fun createSubject() {
        val mutableList = listOfSubjects.toMutableList()
        val index = Random.nextInt(0, SubjectsItem.listOfSubjectsIcons.size)
        val randomScreenOffset =
            GameMainSettings.SCREEN_OFFSET_LEFT + Random.nextFloat() * GameMainSettings.SCREEN_OFFSET_RIGHT

        mutableList.add(
            SubjectsItem(
                rectangle = Rectangle(
                    x = width * randomScreenOffset,
                    y = height * GameMainSettings.SUBJECT_INITIAL_POS,
                    width = width * GameMainSettings.SUBJECT_WIDTH,
                    height = width * GameMainSettings.SUBJECT_WIDTH
                ),
                parent = binding.root,
                index = index
            )
        )
        listOfSubjects = mutableList.toList()
    }

    override fun onStop() {
        super.onStop()
        handler.removeCallbacks(runnable)
    }

}