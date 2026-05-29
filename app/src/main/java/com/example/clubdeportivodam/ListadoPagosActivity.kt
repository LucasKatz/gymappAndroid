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

        // Cargar los datos
        mostrarPagos(lvPagos)
    }

    private fun mostrarPagos(listView: ListView) {
        val dbHelper = AdminSQLiteOpenHelper(this)
        val db = dbHelper.readableDatabase
        val lista = mutableListOf<String>()

        // Consultamos la tabla pagos (el ID más alto primero para ver lo más reciente)
        val cursor = db.rawQuery("SELECT dni_socio, nombre_socio, actividad, monto, fecha FROM pagos ORDER BY id DESC", null)

        if (cursor.moveToFirst()) {
            do {
                val dni = cursor.getString(0)
                val nombre = cursor.getString(1)
                val actividad = cursor.getString(2)
                val monto = cursor.getDouble(3)
                val fecha = cursor.getString(4)

                // Formateamos como queremos que se vea cada renglón
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

        // Usamos un adaptador simple para mostrar la lista
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, lista)
        listView.adapter = adapter
    }
}