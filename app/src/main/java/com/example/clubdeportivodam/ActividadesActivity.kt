package com.example.clubdeportivodam

import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView




class ActividadesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_actividades)

        // Botón de volver
        val btnBack = findViewById<ImageButton>(R.id.btnBackToPanel)
        btnBack.setOnClickListener { finish() }

        // Obtener los datos de la Base de Datos

        val admin = AdminSQLiteOpenHelper(this)
        val listaDeActividades = obtenerActividades(admin)


        val rv = findViewById<RecyclerView>(R.id.rvActividades)
        rv.layoutManager = LinearLayoutManager(this)


        rv.adapter = ActividadAdapter(listaDeActividades)
    }

    // Lectura de la tabla de actividades

    private fun obtenerActividades(admin: AdminSQLiteOpenHelper): List<Actividad> {
        val lista = mutableListOf<Actividad>()
        val db = admin.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM actividades", null)

        if (cursor.moveToFirst()) {
            do {
                val act = Actividad(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getInt(5)
                )
                lista.add(act)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return lista
    }
}