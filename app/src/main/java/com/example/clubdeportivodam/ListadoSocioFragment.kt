package com.example.clubdeportivodam

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

// Esta es la CLASE que SociosActivity está buscando
class ListadoSociosFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Asegúrate de que R.layout.fragment_listado_socios existe y NO TIENE ERRORES
        return inflater.inflate(R.layout.fragment_listado_socios, container, false)
    }
}