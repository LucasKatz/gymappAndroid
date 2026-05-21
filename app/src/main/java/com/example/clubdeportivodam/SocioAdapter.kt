package com.example.clubdeportivodam

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SocioAdapter(private val listaSocios: List<Socio>) :
    RecyclerView.Adapter<SocioAdapter.SocioViewHolder>() {

    // 1. Ampliamos el ViewHolder para que reconozca TODOS los campos de tu diseño
    class SocioViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtNombre: TextView = view.findViewById(R.id.txtNombreSocio)
        val txtDni: TextView = view.findViewById(R.id.txtDniSocio)
        val txtTelefono: TextView = view.findViewById(R.id.txtTelefonoSocio)
        val txtCategoria: TextView = view.findViewById(R.id.txtCategoriaSocio)
        val txtVencimiento: TextView = view.findViewById(R.id.txtVencimientoSocio)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SocioViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_socio, parent, false)
        return SocioViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: SocioViewHolder, position: Int) {
        val socio = listaSocios[position]

        // 2. Aquí "escribimos" los datos reales sobre tu diseño estético
        holder.txtNombre.text = socio.nombre
        holder.txtDni.text = "DNI: ${socio.dni}"
        holder.txtCategoria.text = socio.categoria
        holder.txtVencimiento.text = "Vencimiento\n${socio.vencimiento}"

        holder.txtTelefono.text = socio.telefono
    }

    override fun getItemCount(): Int = listaSocios.size
}