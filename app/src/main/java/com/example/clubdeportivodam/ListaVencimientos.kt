package com.example.clubdeportivodam

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class ListaVencimientos: Fragment() {

    private lateinit var rv: RecyclerView
    private var filtro: String = "HOY"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_lista_vencimientos, container, false)
        rv = view.findViewById(R.id.rvSociosVencimientos)
        rv.layoutManager = LinearLayoutManager(context)

        // Recuperar el filtro enviado
        filtro = arguments?.getString("FILTRO") ?: "HOY"

        cargarDatos()
        return view
    }

    private fun cargarDatos() {
        val admin = AdminSQLiteOpenHelper(requireContext())
        val db = admin.readableDatabase
        val lista = mutableListOf<Socio>() // Asegúrate de tener tu data class Socio

        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val hoyStr = sdf.format(Date())

        // Lógica de Query según el botón
        val query = when (filtro) {
            "HOY" -> "SELECT * FROM socios WHERE vencimiento = '$hoyStr'"
            "VENCIDOS" -> "SELECT * FROM socios WHERE estado = 'Moroso'"
            "PROXIMOS" -> "SELECT * FROM socios WHERE vencimiento > '$hoyStr' AND estado != 'Moroso'"
            else -> "SELECT * FROM socios"
        }

        val cursor = db.rawQuery(query, null)
        if (cursor.moveToFirst()) {
            do {
                // AQUÍ ESTABA EL ERROR: Debes pasar los 8 parámetros
                lista.add(Socio(
                    dni = cursor.getString(0),
                    nombre = cursor.getString(1),
                    Email = cursor.getString(2),    // Ojo: tu Adapter usa socio.Email con E mayúscula
                    telefono = cursor.getString(3),
                    categoria = cursor.getString(4),
                    vencimiento = cursor.getString(5),

                    estado = cursor.getString(6)
                ))
            } while (cursor.moveToNext())
        }
        cursor.close()

        // Aquí usas un Adapter similar al de Actividades pero para Socios
        rv.adapter = SocioAdapter(lista)
    }

    companion object {
        fun newInstance(filtro: String): ListaVencimientos {
            val fragment = ListaVencimientos()
            val args = Bundle()
            args.putString("FILTRO", filtro)
            fragment.arguments = args
            return fragment
        }
    }
}