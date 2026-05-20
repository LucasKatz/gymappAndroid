package com.example.clubdeportivodam

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton

class SociosActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_socios)

        // IMPORTANTE: Verifica que R.id.fragment_socios_container sea igual al del XML
        if (savedInstanceState == null) {
            try {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_socios_container, ListadoSociosFragment())
                    .commit()
            } catch (e: Exception) {
                e.printStackTrace() // Esto te dirá en el Logcat qué falló exactamente
            }
        }

        val fabNuevoSocio = findViewById<FloatingActionButton>(R.id.fabNuevoSocio)
        fabNuevoSocio.setOnClickListener {
            // Asegúrate de que esta clase exista, si no la app crashea aquí
            val intent = Intent(this, RegistroSocioActivity::class.java)
            startActivity(intent)
        }
    }
}