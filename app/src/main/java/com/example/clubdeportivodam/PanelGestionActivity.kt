package com.example.clubdeportivodam

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.card.MaterialCardView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class PanelGestionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_panel_gestion)

        // Botones de panel de gestión, lleva a cada sección de la app
        val btnSocios = findViewById<MaterialCardView>(R.id.btnTotalSocios)
        btnSocios.setOnClickListener {
            val intent = Intent(this, SociosActivity::class.java)
            startActivity(intent)
        }

        val btnVencimientos = findViewById<MaterialCardView>(R.id.btnCuotasVencidas)
        btnVencimientos.setOnClickListener {
            val intent = Intent(this, VencimientosActivity::class.java)
            startActivity(intent)
        }

        val btnActividades = findViewById<MaterialCardView>(R.id.btnActividades)
        btnActividades.setOnClickListener {
            val intent = Intent(this, ActividadesActivity::class.java)
            startActivity(intent)
        }

        val btnPaseActividad = findViewById<MaterialCardView>(R.id.btnPaseActividad)
        btnPaseActividad.setOnClickListener {
            val intent = Intent(this, PaseActividadActivity::class.java)
            startActivity(intent)
        }

        val btnPagos = findViewById<MaterialCardView>(R.id.btnGestionPagos)
        btnPagos.setOnClickListener {
            val intent = Intent(this, GestionPagosActivity::class.java)
            startActivity(intent)
        }

        findViewById<MaterialCardView>(R.id.btnVerPagos).setOnClickListener {
            val intent = Intent(this, ListadoPagosActivity::class.java)
            startActivity(intent)
        }


        val fabNuevoSocio = findViewById<FloatingActionButton>(R.id.fabNuevoSocio)
        fabNuevoSocio.setOnClickListener {
            val intent = Intent(this, RegistroSocioActivity::class.java)
            startActivity(intent)
        }
    }
}