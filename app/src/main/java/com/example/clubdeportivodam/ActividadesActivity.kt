package com.example.clubdeportivodam

import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.example.clubdeportivodam.AdminSQLiteOpenHelper
import com.example.clubdeportivodam.Actividad
import com.example.clubdeportivodam.ActividadAdapter


class ActividadesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_actividades)

        // 1. Configurar el botón de volver (opcional pero recomendado)
        val btnBack = findViewById<ImageButton>(R.id.btnBackToPanel)
        btnBack.setOnClickListener { finish() }

        // 2. Obtener los datos de la Base de Datos

        val admin = AdminSQLiteOpenHelper(this)
        val listaDeActividades = obtenerActividades(admin)

        // 3. Configurar el RecyclerView
        val rv = findViewById<RecyclerView>(R.id.rvActividades)
        rv.layoutManager = LinearLayoutManager(this)

        // 4. Conectar el Adapter con la lista de la DB
        rv.adapter = ActividadAdapter(listaDeActividades)
    }

    // Función para leer la tabla de actividades
    private fun obtenerActividades(admin: AdminSQLiteOpenHelper): List<Actividad> {
        val lista = mutableListOf<Actividad>()
        val db = admin.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM actividades", null)

        if (cursor.moveToFirst()) {
            do {
                val act = Actividad(
                    cursor.getInt(0),    // id
                    cursor.getString(1), // nombre
                    cursor.getString(2), // profesor
                    cursor.getString(3), // horario1
                    cursor.getString(4), // horario2
                    cursor.getInt(5)     // cupos
                )
                lista.add(act)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return lista
    }
}