package com.example.clubdeportivodam

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.time.LocalDate
import java.time.ZoneOffset

class ListaVencimientos : Fragment() {

    private lateinit var rv: RecyclerView
    private lateinit var tvVacio: TextView
    private var filtro: String = "HOY"

    companion object {
        fun newInstance(filtro: String): ListaVencimientos {
            val fragment = ListaVencimientos()
            val args = Bundle()
            args.putString("FILTRO", filtro)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_lista_vencimientos, container, false)
        rv = view.findViewById(R.id.rvSociosVencimientos)
        tvVacio = view.findViewById(R.id.tvMensajeVacio)
        rv.layoutManager = LinearLayoutManager(context)

        filtro = arguments?.getString("FILTRO") ?: "HOY"

        cargarDatos()
        return view
    }

    private fun cargarDatos() {
        val admin = AdminSQLiteOpenHelper(requireContext())
        val db = admin.readableDatabase
        val lista = mutableListOf<Socio>()

        val zona = java.time.ZoneId.systemDefault()
        val hoyInicio = LocalDate.now().atStartOfDay(zona).toInstant().toEpochMilli()
        val hoyFin = LocalDate.now().plusDays(1).atStartOfDay(zona).toInstant().toEpochMilli()

        val query = when (filtro) {
            // HOY: Mismo rango de tiempo
            "HOY" -> "SELECT * FROM socios WHERE vencimiento >= $hoyInicio AND vencimiento < $hoyFin"

            // VENCIDOS (CORREGIDO): Quitamos el "AND estado != 'Al día'" para que coincida con la Activity
            "VENCIDOS" -> "SELECT * FROM socios WHERE vencimiento < $hoyInicio OR estado = 'Moroso'"

            // PROXIMOS: Mismo rango de tiempo
            "PROXIMOS" -> "SELECT * FROM socios WHERE vencimiento >= $hoyFin AND estado != 'Moroso'"

            else -> "SELECT * FROM socios"
        }

        val cursor = db.rawQuery(query, null)
        // Reemplaza el bloque do-while por este:
        if (cursor.moveToFirst()) {
            val iDni = cursor.getColumnIndex("dni")
            val iNom = cursor.getColumnIndex("nombre")
            val iEma = cursor.getColumnIndex("email")
            val iTel = cursor.getColumnIndex("telefono")
            val iCat = cursor.getColumnIndex("categoria")
            val iVen = cursor.getColumnIndex("vencimiento")
            val iMon = cursor.getColumnIndex("monto")
            val iEst = cursor.getColumnIndex("estado")

            do {
                lista.add(Socio(
                    dni = cursor.getString(iDni),
                    nombre = cursor.getString(iNom),
                    Email = cursor.getString(iEma),
                    telefono = cursor.getString(iTel),
                    categoria = cursor.getString(iCat),
                    vencimiento = cursor.getLong(iVen),
                    monto = cursor.getDouble(iMon), // <--- Agregado
                    estado = cursor.getString(iEst)
                ))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()

        if (lista.isEmpty()) {
            rv.visibility = View.GONE
            tvVacio.visibility = View.VISIBLE
        } else {
            rv.visibility = View.VISIBLE
            tvVacio.visibility = View.GONE
            rv.adapter = SocioAdapter(lista)
        }
    }
}