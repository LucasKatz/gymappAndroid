package com.example.clubdeportivodam

import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class PaseActividadActivity : AppCompatActivity() {

    private lateinit var rvNoSocios: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pase_actividad)

        val btnVolver = findViewById<ImageButton>(R.id.btnBackToPanel)
        rvNoSocios = findViewById(R.id.rvNoSocios)
        rvNoSocios.layoutManager = LinearLayoutManager(this)

        btnVolver.setOnClickListener { finish() }

        cargarNoSocios()
    }

    private fun cargarNoSocios() {
        val admin = AdminSQLiteOpenHelper(this)
        val db = admin.readableDatabase
        val lista = mutableListOf<Socio>()

        // Buscamos a los que NO SOCIOS (se muestra la actividad abonada)
        val cursor = db.rawQuery("SELECT * FROM socios WHERE categoria != 'Socio'", null)

        if (cursor.moveToFirst()) {
            val iDni = cursor.getColumnIndex("dni")
            val iNom = cursor.getColumnIndex("nombre")
            val iEma = cursor.getColumnIndex("email")
            val iTel = cursor.getColumnIndex("telefono")
            val iCat = cursor.getColumnIndex("categoria") // Aquí viene la actividad o "Pase Diario"
            val iVen = cursor.getColumnIndex("vencimiento")
            val iMon = cursor.getColumnIndex("monto")
            val iEst = cursor.getColumnIndex("estado")

            do {

                val actividadRegistrada = cursor.getString(iCat)
                val queMostrar = if (actividadRegistrada == "No Socio" || actividadRegistrada.isNullOrBlank()) {
                    "Pase Diario"
                } else {
                    actividadRegistrada
                }

                lista.add(Socio(
                    dni = cursor.getString(iDni),
                    nombre = cursor.getString(iNom),
                    Email = cursor.getString(iEma),
                    telefono = cursor.getString(iTel),
                    categoria = queMostrar, // Enviamos el nombre corregido
                    vencimiento = cursor.getLong(iVen),
                    monto = cursor.getDouble(iMon),
                    estado = cursor.getString(iEst)
                ))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()

        rvNoSocios.adapter = SocioAdapter(lista)
    }
}