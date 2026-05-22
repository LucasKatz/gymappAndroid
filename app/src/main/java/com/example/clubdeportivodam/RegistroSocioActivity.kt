package com.example.clubdeportivodam

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import android.content.Intent
import android.view.View
import android.widget.ImageButton

class RegistroSocioActivity : AppCompatActivity() {

    lateinit var socioViewModel: SocioViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro_socio)

        socioViewModel = ViewModelProvider(this).get(SocioViewModel::class.java)

        // Busca el botón por su ID
        val btnBack = findViewById<View>(R.id.btnBackToPanel)

        btnBack.setOnClickListener {
            // finish() es la mejor opción porque "mata" la pantalla de registro
            // y te deja ver la que estaba justo detrás (el Panel).
            finish()
        }

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_registros, RegistroSocioFragment1())
                .commit()
        }
    }
}