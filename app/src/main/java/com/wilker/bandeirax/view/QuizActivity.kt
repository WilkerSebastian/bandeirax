package com.wilker.bandeirax.view

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.wilker.bandeirax.R
import com.wilker.bandeirax.connection.AppDatabase
import com.wilker.bandeirax.entity.User
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

class QuizActivity : AppCompatActivity() {

    private lateinit var textCountryName: TextView
    private lateinit var flag1: ImageView
    private lateinit var flag2: ImageView
    private lateinit var flag3: ImageView
    private lateinit var flag4: ImageView
    private lateinit var textScore: TextView

    private lateinit var database: AppDatabase
    private lateinit var currentUser: User

    private var currentScore = 10000
    private var totalRounds = 3
    private var currentRound = 1
    private lateinit var correctAnswer: String

    private val countryFlagMap = mapOf(
        "Brasil" to R.drawable.brasil,
        "Argélia" to R.drawable.argelia,
        "Croácia" to R.drawable.croacia,
        "Chipre do Norte" to R.drawable.chipre_do_norte,
        "Eslováquia" to R.drawable.eslovaquia,
        "Eslovênia" to R.drawable.eslovenia,
        "Jordânia" to R.drawable.jordania,
        "Palestina" to R.drawable.palestina,
        "Sudão" to R.drawable.sudao,
        "Sudão do Sul" to R.drawable.sudao_do_sul,
        "Tunísia" to R.drawable.tunisia,
        "Turquia" to R.drawable.turquia
    )

    private val handler = Handler(Looper.getMainLooper())
    private val runnable = object : Runnable {
        override fun run() {
            currentScore -= 10
            updateScore()
            handler.postDelayed(this, 100)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)

        textCountryName = findViewById(R.id.textCountryName)
        flag1 = findViewById(R.id.flag1)
        flag2 = findViewById(R.id.flag2)
        flag3 = findViewById(R.id.flag3)
        flag4 = findViewById(R.id.flag4)
        textScore = findViewById(R.id.textScore)

        database = AppDatabase.getDatabase(this)

        lifecycleScope.launch {
            val session = database.sessionDao().selectFirstSession()

            if (session == null) {
                finish()
            } else {

                val user = database.userDao().getUserById(session.userId)

                if (user == null) {
                    finish()
                } else {
                    currentUser = user
                }

                startNewRound()
                handler.post(runnable)

            }

        }

        flag1.setOnClickListener { checkAnswer(flag1.tag as String) }
        flag2.setOnClickListener { checkAnswer(flag2.tag as String) }
        flag3.setOnClickListener { checkAnswer(flag3.tag as String) }
        flag4.setOnClickListener { checkAnswer(flag4.tag as String) }
    }

    private fun startNewRound() {
        if (currentRound > totalRounds) {
            endQuiz()
            return
        }

        val countries = countryFlagMap.keys.toList()
        val correctCountry = countries.random()
        correctAnswer = correctCountry

        textCountryName.text = correctCountry

        val flagImages = countryFlagMap.values.shuffled().take(4).toMutableList()
        if (!flagImages.contains(countryFlagMap[correctCountry])) {
            flagImages[Random().nextInt(4)] = countryFlagMap[correctCountry]!!
        }

        flag1.setImageResource(flagImages[0])
        flag1.tag = countries.find { countryFlagMap[it] == flagImages[0] }
        flag2.setImageResource(flagImages[1])
        flag2.tag = countries.find { countryFlagMap[it] == flagImages[1] }
        flag3.setImageResource(flagImages[2])
        flag3.tag = countries.find { countryFlagMap[it] == flagImages[2] }
        flag4.setImageResource(flagImages[3])
        flag4.tag = countries.find { countryFlagMap[it] == flagImages[3] }
    }

    private fun checkAnswer(selectedCountry: String) {
        if (selectedCountry == correctAnswer) {
            currentRound++
            startNewRound()
        } else {
            currentScore = 0
            endQuiz()
        }
    }

    private fun updateScore() {
        textScore.text = "Pontuação: $currentScore"
    }

    private fun endQuiz() {
        handler.removeCallbacks(runnable)
        lifecycleScope.launch {
            if (currentScore > currentUser.points) {
                currentUser.points = currentScore
                database.userDao().updateUser(currentUser)
            }
            Toast.makeText(
                this@QuizActivity,
                "Quiz Finalizado! Pontuação: $currentScore",
                Toast.LENGTH_LONG
            ).show()
            finish()
        }
    }
}
