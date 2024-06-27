package com.wilker.bandeirax.view

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.wilker.bandeirax.R

class MainMenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)

        val btnPlay = findViewById<Button>(R.id.btnPlay)
        val btnLeaderboards = findViewById<Button>(R.id.btnLeaderboards)
        val btnProfile = findViewById<Button>(R.id.btnProfile)
        val btnAdministration = findViewById<Button>(R.id.btnAdministration)

        btnPlay.setOnClickListener {

            val intent = Intent(this, QuizActivity::class.java)
            startActivity(intent)

        }

    }

}
