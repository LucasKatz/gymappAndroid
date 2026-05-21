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

class RegistroSocioFragment2 : Fragment() {

    private lateinit var viewModel: SocioViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 1. Inflamos la vista del fragmento 2
        val view = inflater.inflate(R.layout.fragment_registro_socio_2, container, false)

        // 2. Conectamos con el ViewModel compartido de la Activity
        viewModel = ViewModelProvider(requireActivity()).get(SocioViewModel::class.java)

        // 3. Referencias a los componentes del XML
        val etCategoria = view.findViewById<EditText>(R.id.etCategoria)
        val etVencimiento = view.findViewById<EditText>(R.id.etVencimiento)
        val btnSiguiente = view.findViewById<Button>(R.id.btnSiguiente2)

        // 4. Configuración del botón Siguiente
        btnSiguiente.setOnClickListener {
            val categoria = etCategoria.text.toString()
            val vencimiento = etVencimiento.text.toString()

            if (categoria.isNotEmpty()) {
                // GUARDAMOS EN EL VIEWMODEL
                viewModel.categoria = categoria
                viewModel.vencimiento = vencimiento

                // 5. Navegamos al Fragmento 3 (Paso final)
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container_registros, RegistroSocioFragment3())
                    .addToBackStack(null) // Permite volver al paso anterior con el botón atrás
                    .commit()
            } else {
                Toast.makeText(requireContext(), "Por favor, ingresa una categoría", Toast.LENGTH_SHORT).show()
                etCategoria.error = "Campo requerido"
            }
        }
        etCategoria.setText(viewModel.categoria)
        etVencimiento.setText(viewModel.vencimiento)
        return view
    }
}