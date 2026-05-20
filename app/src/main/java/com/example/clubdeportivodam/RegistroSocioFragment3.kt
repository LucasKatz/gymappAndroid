package com.example.clubdeportivodam

import android.content.ContentValues
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider

class RegistroSocioFragment3 : Fragment() {

    private lateinit var viewModel: SocioViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_registro_socio_3, container, false)

        // Conectamos con el ViewModel que trae los datos del Fragment 1 y 2
        viewModel = ViewModelProvider(requireActivity()).get(SocioViewModel::class.java)

        // Referencias a los campos del Fragment 3 (Sección: Saldo y Pago)
        val etMonto = view.findViewById<EditText>(R.id.etMontoInscripcion) // Asegúrate de tener este ID en el XML 3
        val btnRegistrar = view.findViewById<Button>(R.id.btnEmitir)

        btnRegistrar.setOnClickListener {
            // 1. Guardamos el último dato en el ViewModel
            viewModel.monto = etMonto.text.toString()

            // 2. LLAMAMOS A LA BASE DE DATOS PARA GUARDAR TODO JUNTO
            guardarEnBaseDeDatos()
        }

        return view
    }

    private fun guardarEnBaseDeDatos() {
        val admin = AdminSQLiteOpenHelper(requireContext())
        val db = admin.writableDatabase

        val registro = ContentValues()

        // Aquí es donde el ViewModel brilla: recolectamos todo de la "caja fuerte"
        registro.put("dni", viewModel.dni)
        registro.put("nombre", viewModel.nombre)
        registro.put("apellido", "") // Puedes agregar apellido al ViewModel si quieres
        registro.put("categoria", viewModel.categoria)
        registro.put("vencimiento", viewModel.vencimiento)
        registro.put("monto", viewModel.monto)
        registro.put("estado", "Activo")

        val resultado = db.insert("socios", null, registro)
        db.close()

        if (resultado != -1L) {
            Toast.makeText(requireContext(), "Socio registrado con éxito", Toast.LENGTH_SHORT).show()
            // 3. Cerramos la actividad de registro para volver automáticamente a la lista de socios
            requireActivity().finish()
        } else {
            Toast.makeText(requireContext(), "Error al guardar en BBDD", Toast.LENGTH_SHORT).show()
        }
    }
}