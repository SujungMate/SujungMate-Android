package com.example.sujungmate

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button


class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // i can't push korean keyboard;;;;;
        // kotlin binding does not work;;;;
        // if kotlin binding work, 'val signup_button_login = findViewById<Button>(R.id.signup_button_login)' will be jusuck
        val signup_button_login = findViewById<Button>(R.id.signup_button_login)
        signup_button_login.setOnClickListener{
            // i connected LoginActivity with SignUpActivity2
            val intent = Intent(this, SignUpActivity2::class.java)
            startActivity(intent)
        }
    }

}