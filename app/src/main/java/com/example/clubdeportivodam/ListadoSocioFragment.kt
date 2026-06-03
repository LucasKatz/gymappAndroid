package com.example.clubdeportivodam

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.Toast
import android.util.Log

class ListadoSociosFragment : Fragment() {

    private lateinit var rvSocios: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_listado_socios, container, false)

        rvSocios = view.findViewById(R.id.rvSocios)
        rvSocios.layoutManager = LinearLayoutManager(requireContext())

        cargarYMostrarSocios()

        return view
    }

    override fun onResume() {
        super.onResume()
        cargarYMostrarSocios()
    }

    private fun cargarYMostrarSocios() {
        try {
            val admin = AdminSQLiteOpenHelper(requireContext())
            val db = admin.readableDatabase
            val lista = mutableListOf<Socio>()


            val cursor = db.rawQuery("SELECT * FROM socios WHERE categoria LIKE ?", arrayOf("%Socio%"))

            if (cursor.moveToFirst()) {
                val iDni = cursor.getColumnIndex("dni")
                val iNom = cursor.getColumnIndex("nombre")
                val iEmail = cursor.getColumnIndex("email")
                val iCat = cursor.getColumnIndex("categoria")
                val iVen = cursor.getColumnIndex("vencimiento")
                val iTel = cursor.getColumnIndex("telefono")
                val iMon = cursor.getColumnIndex("monto")
                val iEst = cursor.getColumnIndex("estado")

                do {

                    val vencimientoLong = if (iVen != -1) cursor.getLong(iVen) else 0L
                    val montoDouble = if (iMon != -1) cursor.getDouble(iMon) else 0.0

                    val socio = Socio(
                        dni = if (iDni != -1) cursor.getString(iDni) ?: "0" else "0",
                        nombre = if (iNom != -1) cursor.getString(iNom) ?: "N/A" else "N/A",
                        Email = if (iEmail != -1) cursor.getString(iEmail) ?: "N/A" else "N/A",
                        telefono = if (iTel != -1) cursor.getString(iTel) ?: "-" else "-",
                        categoria = if (iCat != -1) cursor.getString(iCat) ?: "S/C" else "S/C",
                        vencimiento = vencimientoLong,
                        monto = montoDouble,
                        estado = if (iEst != -1) cursor.getString(iEst) ?: "Activo" else "Activo"
                    )
                    lista.add(socio)
                } while (cursor.moveToNext())
            }
            cursor.close()
            db.close()


            rvSocios.adapter = SocioAdapter(lista.toMutableList())

        } catch (e: Exception) {
            Log.e("SQL_ERROR", "Error: ${e.message}")
            Toast.makeText(requireContext(), "Error al filtrar socios: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}