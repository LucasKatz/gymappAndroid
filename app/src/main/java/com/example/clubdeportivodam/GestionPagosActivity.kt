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

class GestionPagosActivity : AppCompatActivity() {

    private lateinit var dbHelper: AdminSQLiteOpenHelper
    private var listaActividades = mutableListOf<Pair<String, Double>>() // Nombre y Precio
    private var dniEncontrado: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gestion_pagos)

        dbHelper = AdminSQLiteOpenHelper(this)

        // Referencias de la UI
        val etEmail = findViewById<EditText>(R.id.etEmailBusqueda)
        val btnBuscar = findViewById<Button>(R.id.btnBuscarSocio)
        val tvNombre = findViewById<TextView>(R.id.tvNombreResultado)
        val spinner = findViewById<Spinner>(R.id.spinnerActividades)
        val tvMonto = findViewById<TextView>(R.id.tvMontoPagar)
        val btnConfirmar = findViewById<Button>(R.id.btnConfirmarPago)
        val btnBack = findViewById<ImageButton>(R.id.btnBack)

        // 1. Cargar actividades desde DB al Spinner
        cargarActividades(spinner)

        // 2. Evento buscar socio por Email
        btnBuscar.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val db = dbHelper.readableDatabase
            // Ahora pedimos DNI y NOMBRE
            val cursor = db.rawQuery("SELECT nombre, dni FROM socios WHERE email = ?", arrayOf(email))

            if (cursor.moveToFirst()) {
                tvNombre.text = cursor.getString(0) // Nombre
                dniEncontrado = cursor.getString(1) // Guardamos el DNI en la variable
            } else {
                tvNombre.text = "Socio no encontrado"
                dniEncontrado = ""
                Toast.makeText(this, "El email no pertenece a un socio", Toast.LENGTH_SHORT).show()
            }
            cursor.close()
        }

        // 3. Evento cambio de actividad en Spinner (Actualiza el precio)
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (listaActividades.isNotEmpty()) {
                    val precio = listaActividades[position].second
                    tvMonto.text = "$ $precio"
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                tvMonto.text = "$ 0.00"
            }
        }

        // 4. Evento Confirmar Pago (Guarda en BD y genera PDF)
        btnConfirmar.setOnClickListener {
            val nombreSocio = tvNombre.text.toString()
            val actividad = spinner.selectedItem.toString()
            val montoStr = tvMonto.text.toString().replace("$", "").trim()

            if (nombreSocio.isEmpty() || nombreSocio == "Socio no encontrado" || nombreSocio == "Nombre del Socio") {
                Toast.makeText(this, "Debe buscar un socio válido primero", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val monto = montoStr.toDoubleOrNull() ?: 0.0
            val fechaActual = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

            // Guardar en la tabla de PAGOS
            val db = dbHelper.writableDatabase
            val values = ContentValues().apply {
                put("dni_socio", dniEncontrado) // <--- AHORA SÍ LO MANDAMOS
                put("nombre_socio", nombreSocio)
                put("actividad", actividad)
                put("monto", monto)
                put("fecha", fechaActual)
            }

            val resultado = db.insert("pagos", null, values)

            if (resultado != -1L) {
                generarPDF(nombreSocio, actividad, monto)
                Toast.makeText(this, "Pago registrado y PDF generado", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Error al registrar el pago en la BD", Toast.LENGTH_SHORT).show()
            }
        }

        // Botón volver
        btnBack.setOnClickListener { finish() }
    }

    private fun cargarActividades(spinner: Spinner) {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT nombre, monto FROM actividades", null)
        val nombres = mutableListOf<String>()
        listaActividades.clear()

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

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, nombres)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }

    private fun generarPDF(socio: String, actividad: String, monto: Double) {
        val pdfDocument = PdfDocument()
        val paint = Paint()
        val pageInfo = PdfDocument.PageInfo.Builder(300, 450, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas: Canvas = page.canvas

        val fecha = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())

        // Cabecera
        paint.textSize = 14f
        paint.isFakeBoldText = true
        canvas.drawText("CLUB DEPORTIVO DAM", 70f, 50f, paint)

        paint.isFakeBoldText = false
        paint.textSize = 10f
        canvas.drawText("Comprobante de Pago Oficial", 75f, 70f, paint)
        canvas.drawText("------------------------------------------------", 30f, 90f, paint)

        // Cuerpo del ticket
        canvas.drawText("Fecha: $fecha", 20f, 130f, paint)
        canvas.drawText("Socio: $socio", 20f, 160f, paint)
        canvas.drawText("Actividad: $actividad", 20f, 190f, paint)

        paint.isFakeBoldText = true
        canvas.drawText("TOTAL PAGADO: $ $monto", 20f, 230f, paint)

        paint.isFakeBoldText = false
        canvas.drawText("------------------------------------------------", 30f, 270f, paint)
        canvas.drawText("Conserve este ticket para ingresar", 65f, 300f, paint)

        pdfDocument.finishPage(page)

        // Ubicación: Android/data/com.example.clubdeportivodam/files
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