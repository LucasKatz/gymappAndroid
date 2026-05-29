package com.example.clubdeportivodam

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity

class ListadoPagosActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listado_pagos)

        val lvPagos = findViewById<ListView>(R.id.lvHistorialPagos)
        val btnBack = findViewById<ImageButton>(R.id.btnBackPagos)

        btnBack.setOnClickListener { finish() }

        // Se cargan los datos de los pagos realizados
        mostrarPagos(lvPagos)
    }

    private fun mostrarPagos(listView: ListView) {
        val dbHelper = AdminSQLiteOpenHelper(this)
        val db = dbHelper.readableDatabase
        val lista = mutableListOf<String>()

        // Consultamos la tabla pagos
        val cursor = db.rawQuery("SELECT dni_socio, nombre_socio, actividad, monto, fecha FROM pagos ORDER BY id DESC", null)

        if (cursor.moveToFirst()) {
            do {
                val dni = cursor.getString(0)
                val nombre = cursor.getString(1)
                val actividad = cursor.getString(2)
                val monto = cursor.getDouble(3)
                val fecha = cursor.getString(4)


                val item = """
                    Socio: $nombre (DNI: $dni)
                    Actividad: $actividad
                    Monto: $ $monto
                    Fecha: $fecha
                    --------------------------------------------
                """.trimIndent()

                lista.add(item)
            } while (cursor.moveToNext())
        }
        cursor.close()

        // Se muestra la lista con un adapter
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, lista)
        listView.adapter = adapter
    }
}