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
        val txtEstado: TextView = view.findViewById(R.id.txtEstadoSocio)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SocioViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_socio, parent, false)
        return SocioViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: SocioViewHolder, position: Int) {
        val socio = listaSocios[position]

        // Usamos el operador de seguridad para evitar que un nulo cierre la app
        holder.txtNombre.text = socio.nombre ?: "Sin nombre"
        holder.txtDni.text = "DNI: ${socio.dni}"
        holder.txtCategoria.text = socio.categoria ?: "General"
        holder.txtVencimiento.text = "Vencimiento: ${socio.vencimiento}"
        holder.txtTelefono.text = socio.telefono ?: "-"

        // IMPORTANTE: Asegúrate de que txtEstado esté declarado en el ViewHolder
        holder.txtEstado.text = socio.estado ?: "Activo"
    }

    override fun getItemCount(): Int = listaSocios.size
}