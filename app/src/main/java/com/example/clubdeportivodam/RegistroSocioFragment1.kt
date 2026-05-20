package com.example.clubdeportivodam

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider

class RegistroSocioFragment1 : Fragment() {

    // 1. Declaramos la variable del ViewModel
    private lateinit var viewModel: SocioViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 2. Inflamos el layout XML de la sección 1
        val view = inflater.inflate(R.layout.fragment_registro_socio_1, container, false)

        // 3. Inicializamos el ViewModel de forma manual para conectar con la Activity
        viewModel = ViewModelProvider(requireActivity()).get(SocioViewModel::class.java)

        // 4. Referencias a los componentes del XML
        val btnSiguiente = view.findViewById<Button>(R.id.btnSiguiente1)
        val etNombre = view.findViewById<EditText>(R.id.etNombreRegistro)
        val etDni = view.findViewById<EditText>(R.id.etDniRegistro)

        // 5. Configuración del botón Siguiente
        btnSiguiente.setOnClickListener {
            val nombre = etNombre.text.toString()
            val dni = etDni.text.toString()

            if (nombre.isNotEmpty() && dni.isNotEmpty()) {
                // Guardamos los datos en la "caja fuerte" compartida (ViewModel)
                viewModel.nombre = nombre
                viewModel.dni = dni

                // Navegamos al Fragmento 2
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container_registros, RegistroSocioFragment2())
                    .addToBackStack(null)
                    .commit()
            } else {
                // Validación simple para no pasar vacíos
                etNombre.error = "Campo obligatorio"
            }
        }

        return view
    }
}