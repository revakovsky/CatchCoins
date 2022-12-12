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
import kotlin.random.Random

@Suppress("SpellCheckingInspection")
class GameFragment : Fragment(R.layout.fragment_game), SensorEventListener {

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

    lateinit var sensorManager: SensorManager
    lateinit var accelerometer: Sensor
    private var isSencorExist: Boolean = false


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentGameBinding.bind(view)

        setBackButtonAction()
        getScreenDimensions()

        createBasket()
        createSubject()

        initSensorManager()

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

    private fun initSensorManager() {
        sensorManager = requireActivity().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val accelerometersList = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER)

        if (accelerometersList.isNotEmpty()) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
            isSencorExist = true
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

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        return
    }

    override fun onStart() {
        super.onStart()

        // todo Ask what is it for?
        val iterator: Iterator<*> = listOfSubjects.iterator()
        while (iterator.hasNext()) {
            val element: SubjectsItem = (iterator.next() as SubjectsItem?)!!
            removeSubject(element)
        }
        running = true
        handler.post(runnable)
    }

    override fun onResume() {
        super.onResume()

        if (isSencorExist) {
            sensorManager.registerListener(
                this,
                accelerometer,
                SensorManager.SENSOR_DELAY_FASTEST,
                SensorManager.SENSOR_DELAY_FASTEST
            )
        }
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

    override fun onPause() {
        super.onPause()

        if (isSencorExist) {
            sensorManager.unregisterListener(this)
        }
    }

    override fun onStop() {
        super.onStop()
        running = false
        handler.removeCallbacks(runnable)
    }

}