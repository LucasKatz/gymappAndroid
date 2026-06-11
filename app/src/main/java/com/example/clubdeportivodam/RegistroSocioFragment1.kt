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

        // Conexión al ViewModel compartido de la Activity
        viewModel = ViewModelProvider(requireActivity()).get(SocioViewModel::class.java)


        val etNombre = view.findViewById<EditText>(R.id.etNombreRegistro)
        val etDni = view.findViewById<EditText>(R.id.etDniRegistro)
        val etEmail = view.findViewById<EditText>(R.id.etEmailSocio)
        val etTelefono = view.findViewById<EditText>(R.id.etTelefonoRegistro)
        val btnSiguiente = view.findViewById<Button>(R.id.btnSiguiente1)

        // 2. Configuración del botón Siguiente
        btnSiguiente.setOnClickListener {
            // Se "leen" los inputs y se "limpian" espacios en blanco
            val nombre = etNombre.text.toString().trim()
            val dni = etDni.text.toString().replace(".", "").trim()
            val email = etEmail.text.toString().trim()
            val tel = etTelefono.text.toString().trim()

            var esValido = true

            // Validaciones de formato

            if (nombre.isEmpty()) {
                etNombre.error = "El nombre es obligatorio"
                esValido = false
            }

            if (dni.isEmpty()) {
                etDni.error = "El DNI es obligatorio"
                esValido = false
            } else if (dni.length < 7) {
                etDni.error = "DNI demasiado corto"
                esValido = false
            }

            if (email.isEmpty()) {
                etEmail.error = "El email es obligatorio"
                esValido = false
            } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                etEmail.error = "Formato de email no válido"
                esValido = false
            }

            if (tel.isEmpty()) {
                etTelefono.error = "El teléfono es obligatorio"
                esValido = false
            }

            // Condicional de validación antes de avanzar (si hay error en algún dato, no se avanza)
            if (esValido) {

                viewModel.nombre = nombre
                viewModel.dni = dni
                viewModel.Email = email
                viewModel.telefono = tel

                // Avanzamos hacia la 2da parte del registro (Fragment2)
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container_registros, RegistroSocioFragment2())
                    .addToBackStack(null)
                    .commit()
            } else {
                Toast.makeText(requireContext(), "Por favor, completa los campos marcados", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }
}