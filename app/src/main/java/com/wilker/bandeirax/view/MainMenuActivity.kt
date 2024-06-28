package com.wilker.bandeirax.view

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.wilker.bandeirax.R
import com.wilker.bandeirax.api.RetrofitInstance
import com.wilker.bandeirax.api.data.findOne.FindOneResponse
import com.wilker.bandeirax.connection.AppDatabase
import com.wilker.bandeirax.entity.User
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Base64

class MainMenuActivity : AppCompatActivity() {

    private lateinit var database: AppDatabase

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)

        database = AppDatabase.getDatabase(this)

        lifecycleScope.launch{

            val session = database.sessionDao().selectFirstSession()

            if (session == null) {
                finish()
            } else {

                this@MainMenuActivity.findUserAPI(session.userId)

            }

        }

        val btnPlay = findViewById<Button>(R.id.btnPlay)
        val btnLeaderboards = findViewById<Button>(R.id.btnLeaderboards)
        val btnProfile = findViewById<Button>(R.id.btnProfile)
        val btnAdministration = findViewById<Button>(R.id.btnAdministration)

        btnPlay.setOnClickListener {

            val intent = Intent(this, QuizActivity::class.java)
            startActivity(intent)

        }

        btnProfile.setOnClickListener{

            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)

        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun findUserAPI(id:String) {

        val idEncode = Base64.getEncoder().encodeToString(id.toByteArray())

        RetrofitInstance.api.findOneUser(idEncode).enqueue(object :
            Callback<FindOneResponse> {
            override fun onResponse(call: Call<FindOneResponse>, response: Response<FindOneResponse>) {
                if (response.isSuccessful) {
                    val data = response.body()
                    val dataUser = data?.data

                    if (dataUser != null) {

                        val user = User(dataUser.id, dataUser.name, dataUser.email, dataUser.points, dataUser.active, dataUser.admin)

                        lifecycleScope.launch {

                            database.userDao().updateUser(user)

                            runOnUiThread {
                                visibilityPage(user)
                            }
                        }
                    } else {
                        showError(data?.message ?: "Erro desconhecido")
                    }
                } else {
                    val errorMessage = response.errorBody()?.string() ?: "Unknown error"
                    showError("HTTP Error ${response.code()}: $errorMessage")
                }
            }

            override fun onFailure(call: Call<FindOneResponse>, t: Throwable) {
                showError("Falha: ${t.message}")
            }
        })

    }

    private fun visibilityPage(user:User) {

        if (user.active) {

            val btnPlay = findViewById<Button>(R.id.btnPlay)
            val btnAdministration = findViewById<Button>(R.id.btnAdministration)
            val btnLeaderboards = findViewById<Button>(R.id.btnLeaderboards)
            val btnProfile = findViewById<Button>(R.id.btnProfile)

            btnPlay.visibility = View.VISIBLE
            btnLeaderboards.visibility = View.VISIBLE
            btnProfile.visibility = View.VISIBLE

            if (user.admin)
                btnAdministration.visibility = View.VISIBLE

            return
        }

        val textAlert = findViewById<TextView>(R.id.textAlert)
        textAlert.setText("Acesse o seu email ${user.email} para finalizar seu cadastro.")
        textAlert.visibility = View.VISIBLE

    }

    private fun showError(message: String) {
        runOnUiThread {
            Toast.makeText(this@MainMenuActivity, message, Toast.LENGTH_LONG).show()
        }
    }

}
