package com.example.clubdeportivodam

import android.content.ContentValues
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

// Clase encargada de administrar la caja diaria, cobrar actividades/membresías y actualizar la vigencia de los socios
class GestionPagosActivity : AppCompatActivity() {

    private lateinit var dbHelper: AdminSQLiteOpenHelper
    private var listaActividades = mutableListOf<Pair<String, Double>>() // Estructura de mapeo: Nombre y Precio
    private var dniEncontrado: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gestion_pagos)

        dbHelper = AdminSQLiteOpenHelper(this)

        val etEmail = findViewById<EditText>(R.id.etEmailBusqueda)
        val btnBuscar = findViewById<Button>(R.id.btnBuscarSocio)
        val tvNombre = findViewById<TextView>(R.id.tvNombreResultado)
        val spinner = findViewById<Spinner>(R.id.spinnerActividades)
        val tvMonto = findViewById<TextView>(R.id.tvMontoPagar)
        val btnConfirmar = findViewById<Button>(R.id.btnConfirmarPago)
        val btnBack = findViewById<ImageButton>(R.id.btnBack)

        // Búsqueda del socio mediante su correo electrónico
        btnBuscar.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val db = dbHelper.readableDatabase

            // Consultamos los campos esenciales, incluyendo categoría para aplicar las reglas de negocio
            val cursor = db.rawQuery("SELECT nombre, dni, categoria FROM socios WHERE email = ?", arrayOf(email))

            if (cursor.moveToFirst()) {
                val nombre = cursor.getString(0)
                dniEncontrado = cursor.getString(1)
                val categoria = cursor.getString(2) // Puede ser "Socio" o el nombre de una actividad fija

                tvNombre.text = nombre
                configurarOpcionesSpinner(spinner, categoria)

            } else {
                // Reset completo de la interfaz si el mail no existe en el padrón
                tvNombre.text = "Socio no encontrado"
                dniEncontrado = ""
                listaActividades.clear()
                spinner.adapter = null
                tvMonto.text = "$ 0.00"
                Toast.makeText(this, "El email no pertenece a un socio", Toast.LENGTH_SHORT).show()
            }
            cursor.close()
        }

        // Evento de escucha que actualiza la etiqueta de precio en tiempo real según la selección del operador
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (listaActividades.isNotEmpty() && position < listaActividades.size) {
                    val precio = listaActividades[position].second
                    tvMonto.text = "$ $precio"
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                tvMonto.text = "$ 0.00"
            }
        }

        // Guarda el pago en el historial de caja Y actualiza las fechas para la pantalla de Vencimientos
        // Se confirma el pago, se registra en BBDD, actualiza la actividad/vencimiento del socio y genera el PDF
        btnConfirmar.setOnClickListener {
            val nombreSocio = tvNombre.text.toString()
            val actividad = spinner.selectedItem?.toString() ?: ""
            val montoStr = tvMonto.text.toString().replace("$", "").trim()

            if (nombreSocio.isEmpty() || nombreSocio == "Socio no encontrado" || actividad.isEmpty()) {
                Toast.makeText(this, "Debe buscar un socio válido primero", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val monto = montoStr.toDoubleOrNull() ?: 0.0
            val fechaActual = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

            val db = dbHelper.writableDatabase

            db.beginTransaction()
            try {
                // 1. Insertamos el comprobante en la tabla PAGOS (Historial de caja)
                val valuesPago = ContentValues().apply {
                    put("dni_socio", dniEncontrado)
                    put("nombre_socio", nombreSocio)
                    put("actividad", actividad)
                    put("monto", monto)
                    put("fecha", fechaActual)
                }
                val resultadoPago = db.insert("pagos", null, valuesPago)

                if (resultadoPago != -1L) {

                    // 2. Calculamos la extensión de la vigencia
                    val calendar = Calendar.getInstance()
                    if (actividad == "Cuota") {
                        calendar.add(Calendar.MONTH, 1) // +1 mes si es cuota social
                    } else {
                        calendar.add(Calendar.DAY_OF_YEAR, 1) // +1 día si es actividad suelta o pase diario
                    }
                    val nuevoVencimientoMillis = calendar.timeInMillis

                    // 3. ACTUALIZADO: Preparamos la actualización del Socio incluyendo su nueva actividad
                    val valuesSocio = ContentValues().apply {
                        put("vencimiento", nuevoVencimientoMillis)
                        put("estado", "Al día")

                        // REGLA CRÍTICA: Si el usuario paga "Pase Diario", mantenemos su categoría base (para no pisar su historial).
                        // Si paga una disciplina nueva (ej. Boxeo, Pilates), actualizamos su campo categoría para que impacte en su carnet.
                        if (actividad != "Pase Diario" && actividad != "Cuota") {
                            put("categoria", actividad) // <--- Aquí pisamos la actividad anterior con la nueva
                        }
                    }

                    // Ejecutamos el cambio en la tabla de Socios
                    db.update("socios", valuesSocio, "dni = ?", arrayOf(dniEncontrado))

                    db.setTransactionSuccessful()

                    generarPDF(nombreSocio, actividad, monto)
                    Toast.makeText(this, "Pago registrado y actividad del socio actualizada", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Error al registrar el pago en la BD", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "Error en la transacción: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                db.endTransaction()
                db.close()
            }
        }
        btnBack.setOnClickListener { finish() }
    }

    // Discrimina las opciones de compra dependiendo del tipo de vinculación institucional
    private fun configurarOpcionesSpinner(spinner: Spinner, categoria: String) {
        val nombres = mutableListOf<String>()
        listaActividades.clear()

        if (categoria == "Socio") {
            // Regreso comercial A: Si ya paga una cuota general, solo puede comprar el "Pase Diario" institucional de invitado
            nombres.add("Pase Diario")
            listaActividades.add(Pair("Pase Diario", 2500.00))
        } else {
            // Regreso comercial B: Si es No Socio, listamos dinámicamente todo el portfolio cargado en la base de datos
            val db = dbHelper.readableDatabase
            val cursor = db.rawQuery("SELECT nombre, monto FROM actividades", null)

            if (cursor.moveToFirst()) {
                do {
                    val nombre = cursor.getString(0)
                    val monto = cursor.getDouble(1)
                    listaActividades.add(Pair(nombre, monto))
                    nombres.add(nombre)
                } while (cursor.moveToNext())
            }
            cursor.close()

            if (nombres.isEmpty()) {
                nombres.add("No hay actividades")
                listaActividades.add(Pair("Nada", 0.0))
            }
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, nombres)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }

    // Renderiza un lienzo de manera nativa y emite el archivo del ticket de pago en PDF
    private fun generarPDF(socio: String, actividad: String, monto: Double) {
        val pdfDocument = PdfDocument()
        val paint = Paint()
        val pageInfo = PdfDocument.PageInfo.Builder(300, 450, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas: Canvas = page.canvas

        val fecha = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())

        paint.textSize = 14f
        paint.isFakeBoldText = true
        canvas.drawText("CLUB DEPORTIVO DAM", 70f, 50f, paint)

        paint.isFakeBoldText = false
        paint.textSize = 10f
        canvas.drawText("Comprobante de Pago Oficial", 75f, 70f, paint)
        canvas.drawText("------------------------------------------------", 30f, 90f, paint)

        canvas.drawText("Fecha: $fecha", 20f, 130f, paint)
        canvas.drawText("Socio: $socio", 20f, 160f, paint)
        canvas.drawText("Actividad: $actividad", 20f, 190f, paint)

        paint.isFakeBoldText = true
        canvas.drawText("TOTAL PAGADO: $ $monto", 20f, 230f, paint)

        paint.isFakeBoldText = false
        canvas.drawText("------------------------------------------------", 30f, 270f, paint)
        canvas.drawText("Conserve este ticket para ingresar", 65f, 300f, paint)

        pdfDocument.finishPage(page)

        val directory = getExternalFilesDir(null)
        val file = File(directory, "Comprobante_${System.currentTimeMillis()}.pdf")

        try {
            pdfDocument.writeTo(FileOutputStream(file))
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error al escribir PDF", Toast.LENGTH_SHORT).show()
        }
        pdfDocument.close()
    }
}