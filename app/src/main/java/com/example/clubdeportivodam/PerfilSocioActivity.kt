package com.example.clubdeportivodam

import android.os.Bundle
import android.widget.TextView
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.Locale

class PerfilSocioActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil_socio)

        val tvNombre = findViewById<TextView>(R.id.tvNombreSocio)
        val tvDni = findViewById<TextView>(R.id.tvDniSocio)
        val tvCategoria = findViewById<TextView>(R.id.tvCategoriaSocio)
        val tvActividad = findViewById<TextView>(R.id.tvActividadSocio)
        val tvVencimiento = findViewById<TextView>(R.id.tvVencimientoSocio)
        val btnVolver = findViewById<ImageButton>(R.id.btnBack)

        val dniRecibido = intent.getStringExtra("DNI_SOCIO")

        if (!dniRecibido.isNullOrEmpty()) {
            val admin = AdminSQLiteOpenHelper(this)
            val db = admin.readableDatabase

            val cursor = db.rawQuery("SELECT * FROM socios WHERE dni = ?", arrayOf(dniRecibido))

            if (cursor.moveToFirst()) {
                val nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"))
                val categoria = cursor.getString(cursor.getColumnIndexOrThrow("categoria"))
                val vencimientoRaw = cursor.getString(cursor.getColumnIndexOrThrow("vencimiento"))
                val estado = cursor.getString(cursor.getColumnIndexOrThrow("estado"))

                // --- LÓGICA DE FORMATEO DE FECHA ---
                val fechaFormateada = formatearFecha(vencimientoRaw)

                // Asignar datos
                tvNombre.text = nombre
                tvDni.text = "DNI: $dniRecibido"
                tvCategoria.text = categoria.uppercase()
                tvVencimiento.text = fechaFormateada // Aquí se muestra la fecha linda
                tvActividad.text = "Estado: $estado"

                // Estética de la categoría
                if (categoria.contains("Socio", ignoreCase = true)) {
                    tvCategoria.setBackgroundColor(android.graphics.Color.parseColor("#E3F2FD"))
                    tvCategoria.setTextColor(android.graphics.Color.parseColor("#1B4F72"))
                } else {
                    tvCategoria.setBackgroundColor(android.graphics.Color.parseColor("#FBE9E7"))
                    tvCategoria.setTextColor(android.graphics.Color.parseColor("#D84315"))
                }

            } else {
                Toast.makeText(this, "Socio no encontrado", Toast.LENGTH_SHORT).show()
            }
            cursor.close()
            db.close()
        }

        btnVolver.setOnClickListener { finish() }
    }

//Convierte el formato de fecha a un formato legible por el admin
    private fun formatearFecha(fechaRaw: String): String {
        return try {
            // Formateo en caso de fecha en formato texto
            if (fechaRaw.contains("-")) {
                val parser = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val formatter = SimpleDateFormat("dd 'de' MMMM, yyyy", Locale("es", "ES"))
                val date = parser.parse(fechaRaw)
                date?.let { formatter.format(it) } ?: fechaRaw
            }
            // Fromateo en caso de fecha guardada como milisegundos
            else {
                val milis = fechaRaw.toLong()
                val formatter = SimpleDateFormat("dd 'de' MMMM, yyyy", Locale("es", "ES"))
                formatter.format(java.util.Date(milis))
            }
        } catch (e: Exception) {
            // En caso de fallas, se devuelve la fecha en su formato original
            fechaRaw
        }
    }
}