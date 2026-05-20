package com.example.clubdeportivodam

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels

class RegistroSocioFragment1 : Fragment() {

    // Accedemos al ViewModel compartido de la Activity
    private val viewModel: SocioViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflamos el layout XML de la sección 1
        val view = inflater.inflate(R.layout.fragment_registro_socio_1, container, false)

        val btnSiguiente = view.findViewById<Button>(R.id.btnSiguiente1)

        // Referencias a los EditText (Asegúrate de que estos IDs estén en tu XML)
        val etNombre = view.findViewById<EditText>(R.id.etNombreRegistro)
        val etDni = view.findViewById<EditText>(R.id.etDniRegistro)

        btnSiguiente.setOnClickListener {
            // Guardamos los datos en el ViewModel antes de pasar al siguiente
            viewModel.nombre = etNombre.text.toString()
            viewModel.dni = etDni.text.toString()

            // Navegamos al Fragmento 2
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_registros, RegistroSocioFragment2())
                .addToBackStack(null)
                .commit()
        }

        return view
    }
}