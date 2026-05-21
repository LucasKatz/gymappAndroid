package com.example.clubdeportivodam

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider

class RegistroSocioActivity : AppCompatActivity() {

    // 1. Declaramos el ViewModel (Caja fuerte)
    lateinit var socioViewModel: SocioViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro_socio)

        // 2. Inicializamos el ViewModel de forma manual (sin usar 'by viewModels')
        socioViewModel = ViewModelProvider(this).get(SocioViewModel::class.java)

        // 3. Cargamos el primer fragmento al iniciar
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_registros, RegistroSocioFragment1())
                .commit()
        }
    }


}