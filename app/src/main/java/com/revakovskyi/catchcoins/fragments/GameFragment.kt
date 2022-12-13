package com.revakovskyi.catchcoins.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
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
import com.revakovskyi.catchcoins.utils.SharedPrefs
import com.revakovskyi.catchcoins.utils.showDialog
import kotlin.random.Random

class GameFragment : Fragment(R.layout.fragment_game), SensorEventListener {

    private var binding: FragmentGameBinding? = null
    private var sharedPrefs: SharedPrefs? = null

    private var width = 0f
    private var height = 0f

    private lateinit var handler: Handler
    private lateinit var runnable: Runnable

    private var isContinue = false
    private var time = 0
    private var score = 0
    private var running = true

    private var listOfSubjects = listOf<SubjectsItem>()
    private lateinit var basketItem: BasketItem

    lateinit var sensorManager: SensorManager
    private lateinit var accelerometer: Sensor
    private var isSensorExist: Boolean = false


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentGameBinding.bind(view)
        sharedPrefs = SharedPrefs(requireActivity())

        checkIsContinue()
        setBackButtonAction()
        getScreenDimensions()
        initSensorManager()
        createBasket()

        @Suppress("DEPRECATION")
        handler = Handler()
        runnable = object : Runnable {

            @SuppressLint("SetTextI18n")
            override fun run() {
                if (running) {
                    time++
                    if (time >= GameMainSettings.TIME_BETWEEN_CREATING_SUBJECTS) {
                        time = 0
                        createSubject()
                    }

                    val iterator: Iterator<*> = listOfSubjects.iterator()

                    while (iterator.hasNext()) {
                        val subject: SubjectsItem = (iterator.next() as SubjectsItem?)!!

                        subject.fall(speed = GameMainSettings.FALLING_SUBJECTS_SPEED)

                        if (subject.rectangle.overlaps(basketItem.rectangle) && subject.index == 0) {
                            increaseScore()
                            removeSubject(subject)
                        }
                        if (subject.rectangle.overlaps(basketItem.rectangle) && subject.index != 0) {
                            decreaseScore()
                            removeSubject(subject)
                        }
                        if (isSubjectFeltDown(subject)) {
                            removeSubject(subject)
                        }
                        if (score < 0) {
                            openGameOverScreen()
                        }
                    }
                }
                handler.postDelayed(this, GameMainSettings.FRAME_RATE)
            }
        }
    }

    private fun checkIsContinue() {
        isContinue = requireArguments().getBoolean(GameMainSettings.CONTINUE_BUNDLE_KEY)
        if (isContinue) {
            score = sharedPrefs?.getCurrentScore() ?: 0
            binding?.coinCounter?.text = score.toString()
        } else {
            score = 0
            binding?.coinCounter?.text = score.toString()
            sharedPrefs?.clearCurrentScore()
        }
    }

    private fun setBackButtonAction() {
        binding?.backButton?.setOnClickListener {
            running = false

            showDialog(
                R.drawable.question_icon,
                R.string.exit,
                R.string.want_to_exit,
                { findNavController().popBackStack() },
                { running = true }
            )
        }
    }

    private fun getScreenDimensions() {
        width = resources.displayMetrics.widthPixels.toFloat()
        height = resources.displayMetrics.heightPixels.toFloat()
    }


    private fun initSensorManager() {
        sensorManager = requireActivity().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val accelerometersList = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER)

        if (accelerometersList.isNotEmpty()) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
            isSensorExist = true
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        val valueY = event?.values?.get(1)

        if (valueY != null && running) {
            setBasketPosition(positionValue = valueY, isInsideScreen = true)

            if (isBasketLeftOfScreen()) setBasketPosition(
                positionValue = GameMainSettings.SCREEN_OFFSET_LEFT * width,
                isInsideScreen = false
            )

            if (isBasketRightOfScreen()) setBasketPosition(
                positionValue = GameMainSettings.SCREEN_OFFSET_RIGHT * width,
                isInsideScreen = false
            )
        }
    }

    private fun isBasketLeftOfScreen() =
        basketItem.rectangle.x < GameMainSettings.SCREEN_OFFSET_LEFT * width

    private fun isBasketRightOfScreen() =
        basketItem.rectangle.x > GameMainSettings.SCREEN_OFFSET_RIGHT * width

    private fun setBasketPosition(positionValue: Float, isInsideScreen: Boolean) {
        if (isInsideScreen) {
            basketItem.rectangle.x -= positionValue
            basketItem.image.x -= positionValue
        } else {
            basketItem.rectangle.x = positionValue
            basketItem.image.x = positionValue
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) { return }


    private fun openGameOverScreen() {
        binding?.apply {
            running = false
            changeGameVisibility(View.GONE)

            startNewGameButton.setOnClickListener { prepareNewGame() }
            closeGameOverButton.setOnClickListener { closeGameOverScreen() }
        }
    }

    private fun closeGameOverScreen() {
        binding?.apply {
            gameOverScreen.visibility = View.INVISIBLE
            findNavController().popBackStack()
            score = 0
        }
    }

    private fun prepareNewGame() {
        binding?.apply {
            score = 0
            coinCounter.text = score.toString()
            changeGameVisibility(View.VISIBLE)
            running = true
        }
    }

    private fun changeGameVisibility(visibilityValue: Int) {
        binding?.apply {
            basketItem.image.visibility = visibilityValue
            backButton.visibility = visibilityValue
            backText.visibility = visibilityValue

            when (visibilityValue) {
                View.GONE -> gameOverScreen.visibility = View.VISIBLE
                View.VISIBLE -> gameOverScreen.visibility = View.GONE
            }
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
            parent = binding!!.root
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
                parent = binding!!.root,
                index = index
            )
        )
        listOfSubjects = mutableList.toList()
    }


    private fun increaseScore() {
        score++
        binding?.coinCounter?.text = "$score"
    }

    private fun decreaseScore() {
        score--
        binding?.coinCounter?.text = "$score"
    }

    private fun removeSubject(
        subject: SubjectsItem
    ) {
        val mutableList = listOfSubjects.toMutableList()
        binding?.root?.removeView(subject.image)
        mutableList.remove(subject)
        listOfSubjects = mutableList.toList()
    }

    private fun isSubjectFeltDown(subject: SubjectsItem): Boolean {
        val subjectBottomLine = subject.rectangle.y + subject.rectangle.height
        val screenBottomLine = height * GameMainSettings.VALUE_OF_PIXELS_BELOW_SCREEN
        return subjectBottomLine > screenBottomLine
    }


    override fun onResume() {
        super.onResume()
        if (isSensorExist) {
            sensorManager.registerListener(
                this,
                accelerometer,
                SensorManager.SENSOR_DELAY_GAME
            )
        }
    }

    override fun onPause() {
        super.onPause()
        if (isSensorExist) {
            sensorManager.unregisterListener(this)
        }
    }

    override fun onStart() {
        super.onStart()

        running = true
        val iterator: Iterator<*> = listOfSubjects.iterator()
        while (iterator.hasNext()) {
            val element: SubjectsItem = (iterator.next() as SubjectsItem?)!!
            removeSubject(element)
        }
        handler.post(runnable)
    }

    override fun onStop() {
        super.onStop()

        running = false
        sharedPrefs?.saveMaxScore(score)
        sharedPrefs?.saveCurrentScore(score)
        handler.removeCallbacks(runnable)
    }

    override fun onDetach() {
        super.onDetach()
        binding = null
        sharedPrefs = null
    }

}