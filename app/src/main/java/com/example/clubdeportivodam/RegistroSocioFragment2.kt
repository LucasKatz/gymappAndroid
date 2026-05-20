package com.example.clubdeportivodam

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class RegistroSocioFragment2 : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflamos el layout de la sección 2
        return inflater.inflate(R.layout.fragment_registro_socio_2, container, false)

        // Aquí luego agregarás la lógica para guardar categoría, etc., en el ViewModel
    }
}