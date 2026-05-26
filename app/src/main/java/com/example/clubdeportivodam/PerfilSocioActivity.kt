package com.example.clubdeportivodam

import android.os.Bundle
import android.widget.TextView
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class PerfilSocioActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil_socio)

        val tvNombre = findViewById<TextView>(R.id.tvNombreSocio)
        val tvDni = findViewById<TextView>(R.id.tvDniSocio)
        val tvVencimiento = findViewById<TextView>(R.id.tvVencimientoSocio)
        val btnVolver = findViewById<ImageButton>(R.id.btnBack)

        val dniRecibido = intent.getStringExtra("DNI_SOCIO")

        if (dniRecibido != null) {
            val admin = AdminSQLiteOpenHelper(this)
            val db = admin.readableDatabase
            val cursor = db.rawQuery("SELECT * FROM socios WHERE dni = ?", arrayOf(dniRecibido))

            if (cursor.moveToFirst()) {
                val nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"))
                val vencimientoLong = cursor.getLong(cursor.getColumnIndexOrThrow("vencimiento"))

                // Formatear la fecha
                val fecha = Instant.ofEpochMilli(vencimientoLong)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()
                val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

                tvNombre.text = nombre
                tvDni.text = "DNI: $dniRecibido"
                tvVencimiento.text = "Vence el: ${fecha.format(formatter)}"
            }
            cursor.close()
            db.close()
        }

        btnVolver.setOnClickListener { finish() }
    }
}