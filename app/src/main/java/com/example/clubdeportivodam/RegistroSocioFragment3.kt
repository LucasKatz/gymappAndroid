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

        // 1. Conexión con la "caja fuerte" compartida
        viewModel = ViewModelProvider(requireActivity()).get(SocioViewModel::class.java)

        // 2. Referencias a los componentes del XML 3
        val etMonto = view.findViewById<EditText>(R.id.etMontoInscripcion)
        // Nota: Si quieres capturar el método de pago, ponle ID en el XML y referéncialo aquí
        val btnRegistrar = view.findViewById<Button>(R.id.btnEmitir)

        btnRegistrar.setOnClickListener {
            val montoStr = etMonto.text.toString()

            if (montoStr.isNotEmpty()) {
                // 3. Guardamos el último dato antes de procesar
                viewModel.monto = montoStr

                // 4. Disparamos la grabación final
                guardarEnBaseDeDatos()
            } else {
                etMonto.error = "Ingresa el monto de inscripción"
                Toast.makeText(requireContext(), "Faltan datos de pago", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

    private fun guardarEnBaseDeDatos() {
        val admin = AdminSQLiteOpenHelper(requireContext())
        val db = admin.writableDatabase

        val registro = ContentValues()

        // RECOLECTAMOS TODO DEL VIEWMODEL
        // Importante: Si tu tabla espera un INT en el DNI, usamos toIntOrNull()
        registro.put("dni", viewModel.dni.toIntOrNull() ?: 0)
        registro.put("nombre", viewModel.nombre)
        registro.put("apellido", viewModel.telefono) // Opcional: podrías guardar el tel aquí si no tienes esa columna
        registro.put("categoria", viewModel.categoria)
        registro.put("vencimiento", viewModel.vencimiento)
        registro.put("monto", viewModel.monto.toDoubleOrNull() ?: 0.0)
        registro.put("estado", "Activo")

        // Inserción en la tabla 'socios'
        val resultado = db.insert("socios", null, registro)
        db.close()

        if (resultado != -1L) {
            Toast.makeText(requireContext(), "Socio ${viewModel.nombre} registrado con éxito", Toast.LENGTH_LONG).show()

            // VOLVEMOS AL LISTADO
            // finish() cierra la RegistroSocioActivity y te devuelve a SociosActivity
            requireActivity().finish()
        } else {
            Toast.makeText(requireContext(), "Error: El DNI ya existe o hay un problema con la tabla", Toast.LENGTH_SHORT).show()
        }
    }
}