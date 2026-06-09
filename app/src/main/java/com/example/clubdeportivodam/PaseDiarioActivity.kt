package com.example.clubdeportivodam

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity


//Carga la activity "Pase Diario"
class PaseDiarioActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_pase_diario)

        val btnVolver = findViewById<android.widget.ImageButton>(R.id.btnBackToPanel)
        btnVolver.setOnClickListener {
            finish()
        }


        }
    }
