package com.example.clubdeportivodam

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        val btnIngresar = findViewById<View>(R.id.btnLogin)

        btnIngresar.setOnClickListener {

            val intent = Intent(this, PanelGestionActivity::class.java)
            startActivity(intent)


            finish()
        }


        findViewById<View>(R.id.btnBack).setOnClickListener {
            finish()
        }
    }
}