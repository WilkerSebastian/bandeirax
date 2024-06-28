package com.wilker.bandeirax.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.wilker.bandeirax.MainActivity
import com.wilker.bandeirax.R
import com.wilker.bandeirax.api.RetrofitInstance
import com.wilker.bandeirax.api.data.update.UpdateRequest
import com.wilker.bandeirax.api.data.update.UpdateResponse
import com.wilker.bandeirax.connection.AppDatabase
import com.wilker.bandeirax.entity.User
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileActivity : AppCompatActivity() {

    private lateinit var database: AppDatabase
    private lateinit var lastUser: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        database = AppDatabase.getDatabase(this)

        migrateForms()

    }

    private fun migrateForms() {

        lifecycleScope.launch {

            val session = database.sessionDao().selectFirstSession()

            if (session == null) {

                finish()

            } else {

               val user = database.userDao().getUserById(session.userId)

                if (user == null) {
                    finish()
                } else {

                    val edtName = findViewById<EditText>(R.id.edtName_up)
                    val edtEmail = findViewById<EditText>(R.id.edtEmail_up)
                    val textPoints = findViewById<TextView>(R.id.textPoints)

                    edtName.setText(user.name)
                    edtEmail.setText(user.email)
                    textPoints.text = "Pontuação: ${user.points}"

                    lastUser = user

                }

            }

        }

    }

    public fun updateUser(view: View) {

        val edtName = findViewById<EditText>(R.id.edtName_up)
        val edtEmail = findViewById<EditText>(R.id.edtEmail_up)
        val edtPassword = findViewById<EditText>(R.id.edtPassword_up)

        val name = edtName.text.trim().toString()
        val email = edtEmail.text.trim().toString()
        val password = edtPassword.text.trim().toString()

        try {

            validate(name, email, password)

            updateAPI(name, email, password)

        } catch (e: Exception) {

            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()

        }


    }

    private fun updateAPI(name:String, email:String, password:String) {

        val requestData = UpdateRequest(lastUser.id, name, email, password)

        RetrofitInstance.api.putUser(requestData).enqueue(object : Callback<UpdateResponse> {
            override fun onResponse(call: Call<UpdateResponse>, response: Response<UpdateResponse>) {
                if (response.isSuccessful) {

                    lifecycleScope.launch {

                        val user = User(lastUser.id, name, email, lastUser.points, lastUser.active, lastUser.admin)

                        database.userDao().updateUser(user)
                        runOnUiThread {

                            if (response.body()!!.message == "Hard reset user") {
                                restart()
                            }

                            Toast.makeText(this@ProfileActivity, "Usuário registrado com sucesso!", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                    }

                } else {
                    val errorMessage = response.errorBody()?.string() ?: "Unknown error"
                    showError("HTTP Error ${response.code()}: $errorMessage")
                }
            }
            override fun onFailure(call: Call<UpdateResponse>, t: Throwable) {
                showError("Falha: ${t.message}")
            }
        })

    }

    private fun showError(message: String) {
        runOnUiThread {
            Toast.makeText(this@ProfileActivity, message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun validate(name: String, email: String, password: String) {
        if (name.length < 3) {
            throw Exception("O nome deve ter pelo menos 3 caracteres.")
        }

        val emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$"
        if (!email.matches(Regex(emailRegex))) {
            throw Exception("O email não é válido.")
        }

        if (password.length >= 1 &&  password.length < 8) {
            throw Exception("A senha deve ter pelo menos 8 caracteres.")
        }
    }

    private fun restart() {

        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
        finish()

    }

    public fun logout(view: View) {

        lifecycleScope.launch{

            database.sessionDao().deleteAll()

            restart()

        }

    }

    public fun back(view: View) {
        finish()
    }

}