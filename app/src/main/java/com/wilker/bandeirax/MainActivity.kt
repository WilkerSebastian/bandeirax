package com.wilker.bandeirax

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.wilker.bandeirax.api.RetrofitInstance
import com.wilker.bandeirax.api.data.login.LoginResponse
import com.wilker.bandeirax.connection.AppDatabase
import com.wilker.bandeirax.entity.Session
import com.wilker.bandeirax.entity.User
import com.wilker.bandeirax.view.MainMenuActivity
import com.wilker.bandeirax.view.RegisterActivity
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var database: AppDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        database = AppDatabase.getDatabase(this)

        lifecycleScope.launch {

            val session = database.sessionDao().selectFirstSession()

            if (session != null) {

                runOnUiThread{

                    goToMainMenu()

                }

            }

        }

        val edtEmail = findViewById<EditText>(R.id.edtEmail)
        val edtPassword = findViewById<EditText>(R.id.edtPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val textRegister = findViewById<TextView>(R.id.textRegister)

        btnLogin.setOnClickListener {
            val email = edtEmail.text.toString().trim()
            val password = edtPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {

                Toast.makeText(this, "Por favor, preencha todos os campos", Toast.LENGTH_SHORT).show()

            } else {

                try {

                    validate(email, password)

                    loginAPI(email, password)

                } catch (e: Exception) {

                    Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()

                }

            }
        }

        textRegister.setOnClickListener {

            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)

        }

    }

    private fun loginAPI(email: String, password: String) {

        RetrofitInstance.api.getVerfiedLogin(email, password).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val data = response.body()
                    val dataUser = data?.data

                    if (dataUser != null) {

                        val user = User(dataUser.id, dataUser.name, dataUser.email, dataUser.points, dataUser.active, dataUser.admin)

                        lifecycleScope.launch {

                            val localUser = database.userDao().getUserById(user.id)

                            if (localUser == null) {

                                database.userDao().insertUser(user)

                            } else {

                                database.userDao().updateUser(user)

                            }

                            var session = database.sessionDao().selectFirstSession()

                            if (session == null) {

                                session = Session(0, user.id)

                                database.sessionDao().insertSession(session)

                            } else {

                                session = Session(session.id, user.id)

                                database.sessionDao().updateSession(session)

                            }

                            runOnUiThread {
                                Toast.makeText(this@MainActivity, "Login realizado com sucesso!", Toast.LENGTH_SHORT).show()
                                goToMainMenu()
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

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                showError("Falha: ${t.message}")
            }
        })

    }

    private fun showError(message: String) {
        runOnUiThread {
            Toast.makeText(this@MainActivity, message, Toast.LENGTH_LONG).show()
        }
    }

    private fun goToMainMenu() {

        val intent = Intent(this, MainMenuActivity::class.java)
        startActivity(intent)

    }

    private fun validate(email: String, password: String) {

        val emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$"
        if (!email.matches(Regex(emailRegex))) {
            throw Exception("O email não é válido.")
        }

        if (password.length < 8) {
            throw Exception("A senha deve ter pelo menos 8 caracteres.")
        }
    }

}