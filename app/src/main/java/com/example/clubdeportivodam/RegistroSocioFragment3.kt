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

        // 1. Limpieza de DNI (Quitamos puntos y espacios)
        val dniLimpio = viewModel.dni.replace(".", "").replace(" ", "").trim()
        val dniFinal = dniLimpio.toIntOrNull()

        if (dniFinal == null) {
            Toast.makeText(requireContext(), "DNI inválido. Solo números por favor.", Toast.LENGTH_SHORT).show()
            return
        }

        // 2. Mapeo EXACTO con las columnas de tu AdminSQLite
        registro.put("dni", dniFinal)
        registro.put("nombre", viewModel.nombre)
        registro.put("telefono", viewModel.telefono) // Antes decía "apellido", ahora coincide con tu tabla
        registro.put("categoria", viewModel.categoria)
        registro.put("vencimiento", viewModel.vencimiento)
        registro.put("monto", viewModel.monto.toDoubleOrNull() ?: 0.0)
        registro.put("estado", "Activo")

        try {
            // 3. Intento de inserción
            val resultado = db.insert("socios", null, registro)

            if (resultado != -1L) {
                Toast.makeText(requireContext(), "Socio registrado con éxito", Toast.LENGTH_LONG).show()
                requireActivity().finish() // Cerramos y volvemos al listado
            } else {
                // Si el resultado es -1, es porque el DNI ya está en la tabla
                Toast.makeText(requireContext(), "Error: El DNI $dniFinal ya está registrado", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            // Esto te dirá si falta alguna columna o si el SQL está mal
            Toast.makeText(requireContext(), "Error crítico: ${e.message}", Toast.LENGTH_LONG).show()
        } finally {
            db.close()
        }
    }
}