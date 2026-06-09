package com.example.clubdeportivodam

import android.content.Intent // IMPORTANTE: Agrega esto
import android.os.Bundle
import android.view.View // IMPORTANTE: Agrega esto
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

//carga del main layout
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_layout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

// botón de ingreso a la app
        val btnIngresar = findViewById<View>(R.id.btnCreateAccount)

        btnIngresar.setOnClickListener {

            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
// botón de registro de nuevo usuario (crear cuenta dentro de la app)

        val btnRegistrarse = findViewById<View>(R.id.btnRegistro)
        btnRegistrarse.setOnClickListener {
            val intent = Intent(this, CreateAccountActivity::class.java)
            startActivity(intent)
        }
    }
}