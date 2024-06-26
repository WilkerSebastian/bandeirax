package com.wilker.bandeirax.view

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.wilker.bandeirax.R
import com.wilker.bandeirax.connection.AppDatabase
import com.wilker.bandeirax.entity.User
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    private lateinit var database: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val edtName = findViewById<EditText>(R.id.edtName)
        val edtEmail = findViewById<EditText>(R.id.edtEmail)
        val edtPassword = findViewById<EditText>(R.id.edtPassword)
        val btnRegister = findViewById<Button>(R.id.btnRegister)
        val btnBack = findViewById<Button>(R.id.btnBack)

        database = AppDatabase.getDatabase(this)

        btnRegister.setOnClickListener {
            val name = edtName.text.toString().trim()
            val email = edtEmail.text.toString().trim()
            val password = edtPassword.text.toString().trim()

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Por favor, preencha todos os campos", Toast.LENGTH_SHORT).show()
            } else {

                val user = User(id = email, name = name, email = email, points = 0, activity = false, admin = false)
                lifecycleScope.launch {
                    database.userDao().insertUser(user)
                    runOnUiThread {
                        Toast.makeText(this@RegisterActivity, "Usuário registrado com sucesso!", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }

            }
        }

        btnBack.setOnClickListener {
            finish()
        }
    }
}