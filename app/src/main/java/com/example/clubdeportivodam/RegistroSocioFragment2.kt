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

// Fragmento del Paso 2: Define la categoría del usuario, calcula vencimientos automáticos y procesa el alta final junto al pago
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
            etMonto.setText("") // Limpiamos el monto al cambiar de estado

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

        // MODIFICADO: Ahora calcula automáticamente la fecha de vencimiento Y el monto
        spActividades.setOnItemClickListener { _, _, _, _ ->
            val hoy = LocalDate.now()
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            val actividadSeleccionada = spActividades.text.toString()

            // 1. Lógica de Vencimiento
            val vencimiento = if (actividadSeleccionada == "Cuota") {
                hoy.plusMonths(1)
            } else {
                hoy.plusDays(1)
            }
            etVencimiento.setText(vencimiento.format(formatter))

            // 2. NUEVA LÓGICA: Asignación automática de montos
            if (actividadSeleccionada == "Cuota") {
                // Si es Socio, se le asigna el valor fijo requerido
                etMonto.setText("25000.0")
            } else if (actividadSeleccionada == "Pase Diario") {
                // Valor estimado para el pase diario (puedes ajustarlo si lo deseas)
                etMonto.setText("2500.0")
            } else {
                // Si es una disciplina (Yoga, Boxeo, etc.), la buscamos en la BBDD
                val montoDb = obtenerMontoActividadDeDB(actividadSeleccionada)
                etMonto.setText(montoDb.toString())
            }
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

    // AGREGADO: Nueva función auxiliar para consultar el precio específico de una actividad
    private fun obtenerMontoActividadDeDB(nombreActividad: String): Double {
        var monto = 0.0
        val admin = AdminSQLiteOpenHelper(requireContext())
        val db = admin.readableDatabase

        // Buscamos el monto filtrando por el nombre exacto de la actividad
        val cursor = db.rawQuery("SELECT monto FROM actividades WHERE nombre = ?", arrayOf(nombreActividad))
        if (cursor.moveToFirst()) {
            monto = cursor.getDouble(0)
        }
        cursor.close()
        db.close()
        return monto
    }

    private fun guardarRegistroFinal() {
        val estadoTxt = spEstado.text.toString()
        val actividadTxt = spActividades.text.toString()
        val montoTxt = etMonto.text.toString()

        if (estadoTxt.isEmpty() || actividadTxt.isEmpty() || montoTxt.isEmpty()) {
            Toast.makeText(context, "Complete todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        val categoriaFinal = if (estadoTxt == "Socio") "Socio" else actividadTxt

        val hoy = LocalDate.now()
        val formatterDB = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val fechaActualStr = hoy.atStartOfDay().format(formatterDB)

        val fechaVenc = if (actividadTxt == "Cuota") hoy.plusMonths(1) else hoy.plusDays(1)
        val vencimientoMillis = fechaVenc.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()

        val admin = AdminSQLiteOpenHelper(requireContext())
        val db = admin.writableDatabase

        db.beginTransaction()
        try {
            val registroSocio = ContentValues().apply {
                put("dni", viewModel.dni)
                put("nombre", viewModel.nombre)
                put("email", viewModel.Email)
                put("telefono", viewModel.telefono)
                put("categoria", categoriaFinal)
                put("vencimiento", vencimientoMillis)
                put("monto", montoTxt.toDouble())
                put("estado", "Al día")
            }

            val resSocio = db.insert("socios", null, registroSocio)

            if (resSocio != -1L) {
                val registroPago = ContentValues().apply {
                    put("dni_socio", viewModel.dni)
                    put("nombre_socio", viewModel.nombre)
                    put("actividad", actividadTxt)
                    put("monto", montoTxt.toDouble())
                    put("fecha", fechaActualStr)
                }

                db.insert("pagos", null, registroPago)
                db.setTransactionSuccessful()

                Toast.makeText(context, "Cliente registrado y Pago asentado", Toast.LENGTH_LONG).show()
                activity?.finish()
            } else {
                Toast.makeText(context, "Error: DNI ya registrado", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Error en el registro: ${e.message}", Toast.LENGTH_SHORT).show()
        } finally {
            db.endTransaction()
            db.close()
        }
    }
}