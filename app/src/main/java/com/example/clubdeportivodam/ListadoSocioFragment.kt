package com.example.clubdeportivodam

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.Toast

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

    // Agregamos onResume para que la lista se refresque al volver de registrar un socio
    override fun onResume() {
        super.onResume()
        cargarYMostrarSocios()
    }

    private fun cargarYMostrarSocios() {
        try {
            val admin = AdminSQLiteOpenHelper(requireContext())
            val db = admin.readableDatabase
            val lista = mutableListOf<Socio>()

            // Traemos todo para no errar nombres
            val cursor = db.rawQuery("SELECT * FROM socios", null)

            if (cursor.moveToFirst()) {
                // Obtenemos los índices de forma segura
                val iDni = cursor.getColumnIndex("dni")
                val iNom = cursor.getColumnIndex("nombre")
                val iEmail = cursor.getColumnIndex("email")
                val iCat = cursor.getColumnIndex("categoria")
                val iVen = cursor.getColumnIndex("vencimiento")
                val iTel = cursor.getColumnIndex("telefono")
                val iEst = cursor.getColumnIndex("estado")

                do {
                    // Verificamos que la columna exista (-1 significa que no existe)
                    val socio = Socio(
                        dni = if (iDni != -1) cursor.getInt(iDni).toString() else "0",
                        nombre = if (iNom != -1) cursor.getString(iNom) ?: "N/A" else "N/A",
                        Email = if (iEmail != -1) cursor.getString(iEmail) ?: "N/A" else "N/A",
                        categoria = if (iCat != -1) cursor.getString(iCat) ?: "S/C" else "S/C",
                        vencimiento = if (iVen != -1) cursor.getString(iVen) ?: "S/V" else "S/V",
                        telefono = if (iTel != -1) cursor.getString(iTel) ?: "-" else "-",
                        estado = if (iEst != -1) cursor.getString(iEst) ?: "Activo" else "Activo"
                    )
                    lista.add(socio)
                } while (cursor.moveToNext())
            }
            cursor.close()
            db.close()

            rvSocios.adapter = SocioAdapter(lista)

        } catch (e: Exception) {
            // Esto te mostrará el error exacto en un mensaje flotante
            android.util.Log.e("SQL_ERROR", "Error: ${e.message}")
            Toast.makeText(requireContext(), "Error al listar: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}