package com.example.clubdeportivodam

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton


//Activity que carga la pantalla de socio
class SociosActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_socios)


        val btnVolver = findViewById<android.widget.ImageButton>(R.id.btnBackToPanel)
        btnVolver.setOnClickListener {
            finish()
        }

        if (savedInstanceState == null) {
            try {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_socios_container, ListadoSociosFragment())
                    .commit()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        val fabNuevoSocio = findViewById<FloatingActionButton>(R.id.fabNuevoSocio)
        fabNuevoSocio.setOnClickListener {

            val intent = Intent(this, RegistroSocioActivity::class.java)
            startActivity(intent)
        }
    }
}