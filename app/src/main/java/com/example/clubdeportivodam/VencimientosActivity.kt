package com.example.clubdeportivodam

import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import java.time.LocalDate
import java.time.ZoneId

// Pantalla de control de vencimientos

class VencimientosActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vencimientos)


        val btnHoy = findViewById<MaterialButton>(R.id.btnHoy)
        val btnVencidos = findViewById<MaterialButton>(R.id.btnVencidos)
        val btnProximos = findViewById<MaterialButton>(R.id.btnProximos)
        val btnVolver = findViewById<ImageButton>(R.id.btnBackToPanel)


        actualizarContadores(btnHoy, btnVencidos, btnProximos)


        btnVolver.setOnClickListener { finish() }

        //botones de navegación entre pantallas de vencimientos

        btnHoy.setOnClickListener { reemplazarFragmento("HOY") }
        btnVencidos.setOnClickListener { reemplazarFragmento("VENCIDOS") }
        btnProximos.setOnClickListener { reemplazarFragmento("PROXIMOS") }


        if (savedInstanceState == null) {
            reemplazarFragmento("HOY")
        }
    }

    //Función de consulta a la base de datos
    private fun actualizarContadores(bHoy: MaterialButton, bVenc: MaterialButton, bProx: MaterialButton) {
        val admin = AdminSQLiteOpenHelper(this)
        val db = admin.readableDatabase

        // Lógica de tiempo igual a la del Fragment (java.time)
        val zona = ZoneId.systemDefault()
        val hoyInicio = LocalDate.now().atStartOfDay(zona).toInstant().toEpochMilli()
        val hoyFin = LocalDate.now().plusDays(1).atStartOfDay(zona).toInstant().toEpochMilli()

        fun contar(sql: String): Int {
            val c = db.rawQuery(sql, null)
            val res = if (c.moveToFirst()) c.getInt(0) else 0
            c.close()
            return res
        }



        // HOY: Entre las 00:00:00 de hoy y las 00:00:00 de mañana
        val numHoy = contar("SELECT COUNT(*) FROM socios WHERE vencimiento >= $hoyInicio AND vencimiento < $hoyFin")

        // VENCIDOS: Menor al inicio de hoy O estado Moroso
        val numVenc = contar("SELECT COUNT(*) FROM socios WHERE vencimiento < $hoyInicio OR estado = 'Moroso'")

        // PRÓXIMOS: Mayor o igual al inicio de mañana
        val numProx = contar("SELECT COUNT(*) FROM socios WHERE vencimiento >= $hoyFin AND estado != 'Moroso'")


        bHoy.text = "$numHoy\nHoy"
        bVenc.text = "$numVenc\nVencidos"
        bProx.text = "$numProx\nPróximos"

        db.close()
    }

    // Recibe el filtro ("HOY", "VENCIDOS", "PROXIMOS") e inyecta dinámicamente el fragmento con la lista filtrada
    private fun reemplazarFragmento(filtro: String) {
        val fragment = ListaVencimientos.newInstance(filtro)
        supportFragmentManager.beginTransaction()
            .replace(R.id.vencimientos_container, fragment)
            .commit()
    }
}