package com.example.clubdeportivodam

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider

class RegistroSocioFragment2 : Fragment() {

    private lateinit var viewModel: SocioViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 1. Inflamos la vista
        val view = inflater.inflate(R.layout.fragment_registro_socio_2, container, false)

        // 2. Conectamos con el ViewModel compartido
        viewModel = ViewModelProvider(requireActivity()).get(SocioViewModel::class.java)

        // 3. Referencias a los componentes (Asegúrate de que estos IDs existan en tu fragment_registro_socio_2.xml)
        val etCategoria = view.findViewById<EditText>(R.id.etCategoria)
        val etVencimiento = view.findViewById<EditText>(R.id.etVencimiento)
        val btnSiguiente = view.findViewById<Button>(R.id.btnSiguiente2)

        btnSiguiente.setOnClickListener {
            // 4. Guardamos los datos de la Sección 2 en la "caja fuerte"
            viewModel.categoria = etCategoria.text.toString()
            viewModel.vencimiento = etVencimiento.text.toString()

            // 5. Navegamos al Fragmento 3
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_registros, RegistroSocioFragment3())
                .addToBackStack(null)
                .commit()
        }

        return view
    }
}