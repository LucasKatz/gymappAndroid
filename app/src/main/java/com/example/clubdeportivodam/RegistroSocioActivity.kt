package com.example.clubdeportivodam

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import android.view.View

// Prepara el almacén de datos (ViewModel) y arranca el primer paso del registro de socio
class RegistroSocioActivity : AppCompatActivity() {

    lateinit var socioViewModel: SocioViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro_socio)

        socioViewModel = ViewModelProvider(this).get(SocioViewModel::class.java)


        val btnBack = findViewById<View>(R.id.btnBackToPanel)

        btnBack.setOnClickListener {

            finish()
        }

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_registros, RegistroSocioFragment1())
                .commit()
        }
    }
}