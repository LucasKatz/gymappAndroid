package com.example.clubdeportivodam

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider

class RegistroSocioFragment1 : Fragment() {

    private lateinit var viewModel: SocioViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_registro_socio_1, container, false)

        // Conectamos con el ViewModel compartido de la Activity
        viewModel = ViewModelProvider(requireActivity()).get(SocioViewModel::class.java)

        // 1. Referencias a TODOS los componentes de tu XML sección 1
        val etNombre = view.findViewById<EditText>(R.id.etNombreRegistro)
        val etDni = view.findViewById<EditText>(R.id.etDniRegistro)
        val etFechaNac = view.findViewById<EditText>(R.id.etFechaNacRegistro)
        val etTelefono = view.findViewById<EditText>(R.id.etTelefonoRegistro)
        val btnSiguiente = view.findViewById<Button>(R.id.btnSiguiente1)

        // 2. Configuración del botón Siguiente
        btnSiguiente.setOnClickListener {
            val nombre = etNombre.text.toString()
            val dni = etDni.text.toString().replace(".", "").trim()
            val fecha = etFechaNac.text.toString()
            val tel = etTelefono.text.toString()

            // Validación: Al menos Nombre y DNI deben estar presentes
            if (nombre.isNotEmpty() && dni.isNotEmpty()) {

                // 3. GUARDAMOS TODO EN EL VIEWMODEL (La caja fuerte)
                viewModel.nombre = nombre
                viewModel.dni = dni
                viewModel.fechaNac = fecha
                viewModel.telefono = tel

                // 4. Navegamos al Fragmento 2
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container_registros, RegistroSocioFragment2())
                    .addToBackStack(null)
                    .commit()

            } else {
                Toast.makeText(requireContext(), "Por favor, completa Nombre y DNI", Toast.LENGTH_SHORT).show()
                if (nombre.isEmpty()) etNombre.error = "Requerido"
                if (dni.isEmpty()) etDni.error = "Requerido"
            }
        }

        return view
    }
}