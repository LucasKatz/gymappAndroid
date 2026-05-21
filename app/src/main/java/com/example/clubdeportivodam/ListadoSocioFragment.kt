package com.example.clubdeportivodam

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

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
        val admin = AdminSQLiteOpenHelper(requireContext())
        val db = admin.readableDatabase
        val lista = mutableListOf<Socio>()

        // 1. Ampliamos la consulta para traer TODAS las columnas necesarias
        // El orden aquí es importante para el paso 2
        val cursor = db.rawQuery("SELECT dni, nombre, categoria, vencimiento, telefono FROM socios", null)

        if (cursor.moveToFirst()) {
            do {
                // 2. Creamos el objeto Socio pasando los 5 parámetros
                val socio = Socio(
                    dni = cursor.getString(0),
                    nombre = cursor.getString(1),
                    categoria = cursor.getString(2),
                    vencimiento = cursor.getString(3),
                    telefono = cursor.getString(4)
                )
                lista.add(socio)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()

        // 3. Enviamos la lista completa al Adapter
        rvSocios.adapter = SocioAdapter(lista)
    }
}