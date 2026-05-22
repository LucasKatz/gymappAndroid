package com.example.clubdeportivodam

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity


class ActividadesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_actividades)

        val btnVolver = findViewById<android.widget.ImageButton>(R.id.btnBackToPanel)
        btnVolver.setOnClickListener {
            finish()
        }


    }
}