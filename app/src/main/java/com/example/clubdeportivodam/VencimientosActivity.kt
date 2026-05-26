package com.example.clubdeportivodam

import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import java.text.SimpleDateFormat
import java.util.*

class VencimientosActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vencimientos)

        // 1. Referencias de la UI
        val btnHoy = findViewById<MaterialButton>(R.id.btnHoy)
        val btnVencidos = findViewById<MaterialButton>(R.id.btnVencidos)
        val btnProximos = findViewById<MaterialButton>(R.id.btnProximos)
        val btnVolver = findViewById<ImageButton>(R.id.btnBackToPanel)

        // 2. Configuración de Base de Datos y Fecha
        val admin = AdminSQLiteOpenHelper(this)
        val db = admin.readableDatabase
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val hoyStr = sdf.format(Date())

        // 3. Función interna para contar filas en la DB
        fun contar(sql: String): Int {
            val c = db.rawQuery(sql, null)
            val res = if (c.moveToFirst()) c.getInt(0) else 0
            c.close()
            return res
        }

        // 4. Actualizar los textos de los botones con los números reales
        btnHoy.text = "${contar("SELECT COUNT(*) FROM socios WHERE vencimiento = '$hoyStr'")}\nHoy"
        btnVencidos.text = "${contar("SELECT COUNT(*) FROM socios WHERE estado = 'Moroso' OR vencimiento < '$hoyStr'")}\nVencidos"
        btnProximos.text = "${contar("SELECT COUNT(*) FROM socios WHERE vencimiento > '$hoyStr' AND estado != 'Moroso'")}\nPróximos"

        // 5. Lógica de los botones (Filtros)
        btnVolver.setOnClickListener { finish() }

        btnHoy.setOnClickListener { reemplazarFragmento("HOY") }
        btnVencidos.setOnClickListener { reemplazarFragmento("VENCIDOS") }
        btnProximos.setOnClickListener { reemplazarFragmento("PROXIMOS") }

        // 6. Cargar el fragmento inicial por defecto
        if (savedInstanceState == null) {
            reemplazarFragmento("HOY")
        }
    }

    private fun reemplazarFragmento(filtro: String) {
        val fragment = ListaVencimientos.newInstance(filtro)
        supportFragmentManager.beginTransaction()
            .replace(R.id.vencimientos_container, fragment)
            .commit()
    }
}