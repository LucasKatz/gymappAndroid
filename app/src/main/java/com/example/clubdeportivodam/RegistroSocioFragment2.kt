package com.example.clubdeportivodam

import android.content.ContentValues
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class RegistroSocioFragment2 : Fragment() {

    private lateinit var viewModel: SocioViewModel
    private lateinit var spEstado: AutoCompleteTextView
    private lateinit var spActividades: AutoCompleteTextView
    private lateinit var spPago: AutoCompleteTextView
    private lateinit var etVencimiento: EditText
    private lateinit var etMonto: EditText
    private lateinit var btnFinalizar: Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_registro_socio_2, container, false)
        viewModel = ViewModelProvider(requireActivity()).get(SocioViewModel::class.java)

        spEstado = view.findViewById(R.id.spinnerEstado)
        spActividades = view.findViewById(R.id.spinnerActividades)
        spPago = view.findViewById(R.id.spinnerPago)
        etVencimiento = view.findViewById(R.id.etVencimiento)
        etMonto = view.findViewById(R.id.etMonto)
        btnFinalizar = view.findViewById(R.id.btnConfirmarInscripcion)

        configurarLogica()
        btnFinalizar.setOnClickListener { guardarRegistroFinal() }

        return view
    }

    private fun configurarLogica() {
        val estados = arrayOf("Socio", "No Socio")
        spEstado.setAdapter(ArrayAdapter(requireContext(), R.layout.list_item, estados))
        spEstado.setOnClickListener { spEstado.showDropDown() }

        val metodosPago = arrayOf("Tarjeta", "Transferencia", "Efectivo")
        spPago.setAdapter(ArrayAdapter(requireContext(), R.layout.list_item, metodosPago))
        spPago.setOnClickListener { spPago.showDropDown() }

        spEstado.setOnItemClickListener { _, _, position, _ ->
            val seleccion = estados[position]
            spActividades.setText("")
            etVencimiento.setText("")

            val listaAct = if (seleccion == "Socio") {
                mutableListOf("Cuota")
            } else {
                val desdeDB = obtenerActividadesDeDB()
                if (!desdeDB.contains("Pase Diario")) {
                    desdeDB.add("Pase Diario")
                }
                desdeDB
            }
            spActividades.setAdapter(ArrayAdapter(requireContext(), R.layout.list_item, listaAct))
            spActividades.setOnClickListener { spActividades.showDropDown() }
        }

        spActividades.setOnItemClickListener { _, _, _, _ ->
            val hoy = LocalDate.now()
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            val actividadSeleccionada = spActividades.text.toString()

            // Si es Cuota vence en 1 mes, si es actividad o pase diario vence mañana
            val vencimiento = if (actividadSeleccionada == "Cuota") {
                hoy.plusMonths(1)
            } else {
                hoy.plusDays(1)
            }
            etVencimiento.setText(vencimiento.format(formatter))
        }
    }

    private fun obtenerActividadesDeDB(): MutableList<String> {
        val nombres = mutableListOf<String>()
        val admin = AdminSQLiteOpenHelper(requireContext())
        val db = admin.readableDatabase
        val cursor = db.rawQuery("SELECT nombre FROM actividades", null)
        if (cursor.moveToFirst()) {
            do { nombres.add(cursor.getString(0)) } while (cursor.moveToNext())
        }
        cursor.close()
        return nombres
    }

    private fun guardarRegistroFinal() {
        val estadoTxt = spEstado.text.toString()
        val actividadTxt = spActividades.text.toString()
        val montoTxt = etMonto.text.toString()

        if (estadoTxt.isEmpty() || actividadTxt.isEmpty() || montoTxt.isEmpty()) {
            Toast.makeText(context, "Complete todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        // 1. DETERMINAR CATEGORÍA:
        // Si el estado es "Socio", guardamos "Socio".
        // Si es "No Socio", guardamos la actividad (ej: "Yoga") o "Pase Diario".
        val categoriaFinal = if (estadoTxt == "Socio") "Socio" else actividadTxt

        // 2. LÓGICA DE FECHA (Milisegundos)
        val hoy = LocalDate.now()
        val fechaVenc = if (actividadTxt == "Cuota") hoy.plusMonths(1) else hoy.plusDays(1)
        val vencimientoMillis = fechaVenc.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()

        val admin = AdminSQLiteOpenHelper(requireContext())
        val db = admin.writableDatabase

        val registro = ContentValues().apply {
            put("dni", viewModel.dni)
            put("nombre", viewModel.nombre)
            put("email", viewModel.Email)
            put("telefono", viewModel.telefono)

            // --- AQUÍ SE GUARDA LA ACTIVIDAD SELECCIONADA ---
            put("categoria", categoriaFinal)

            put("vencimiento", vencimientoMillis)
            put("monto", montoTxt.toDouble())
            put("estado", "Al día")
        }

        val res = db.insert("socios", null, registro)
        if (res != -1L) {
            Toast.makeText(context, "Registro Exitoso", Toast.LENGTH_LONG).show()
            activity?.finish() // Cierra el flujo de registro y vuelve al panel
        } else {
            Toast.makeText(context, "Error: DNI ya registrado", Toast.LENGTH_SHORT).show()
        }
        db.close()
    }
}