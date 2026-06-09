package com.example.clubdeportivodam

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

//Muestra la pantalla de bienvenida
class WelcomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)


        val btnContinuar = findViewById<View>(R.id.btnCreateAccount)

        btnContinuar.setOnClickListener {

            val intent = Intent(this, PanelGestionActivity::class.java)
            startActivity(intent)


            finish()
        }
    }
}