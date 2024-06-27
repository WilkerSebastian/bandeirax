package com.wilker.bandeirax.view

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.wilker.bandeirax.R
import com.wilker.bandeirax.api.RetrofitInstance
import com.wilker.bandeirax.api.data.create.CreateRequest
import com.wilker.bandeirax.api.data.create.CreateResponse
import com.wilker.bandeirax.connection.AppDatabase
import com.wilker.bandeirax.entity.User
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {

    private lateinit var database: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        database = AppDatabase.getDatabase(this)

        findViewById<Button>(R.id.btnBack).setOnClickListener {
            finish()
        }
    }

    public fun saveUser(view: View) {
        val edtName = findViewById<EditText>(R.id.edtName)
        val edtEmail = findViewById<EditText>(R.id.edtEmail)
        val edtPassword = findViewById<EditText>(R.id.edtPassword)

        val name = edtName.text.toString().trim()
        val email = edtEmail.text.toString().trim()
        val password = edtPassword.text.toString().trim()

        try {
            validate(name, email, password)
            createUserAPI(name, email, password)
        } catch (e: Exception) {
            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun createUserAPI(name: String, email: String, password: String) {
        val requestData = CreateRequest(name, email, password)

        RetrofitInstance.api.postCreateUser(requestData).enqueue(object : Callback<CreateResponse> {
            override fun onResponse(call: Call<CreateResponse>, response: Response<CreateResponse>) {
                if (response.isSuccessful) {
                    val data = response.body()
                    if (data?.id != null) {
                        val user = User(data.id, name, email, 0, false, false)

                        lifecycleScope.launch {
                            database.userDao().insertUser(user)
                            runOnUiThread {
                                Toast.makeText(this@RegisterActivity, "Usuário registrado com sucesso!", Toast.LENGTH_SHORT).show()
                                finish()
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

            override fun onFailure(call: Call<CreateResponse>, t: Throwable) {
                showError("Falha: ${t.message}")
            }
        })
    }

    private fun showError(message: String) {
        runOnUiThread {
            Toast.makeText(this@RegisterActivity, message, Toast.LENGTH_SHORT).show()
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

        if (password.length < 8) {
            throw Exception("A senha deve ter pelo menos 8 caracteres.")
        }
    }
}
