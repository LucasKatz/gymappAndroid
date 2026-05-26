package com.example.clubdeportivodam

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class SocioAdapter(private val listaSocios: List<Socio>) :
    RecyclerView.Adapter<SocioAdapter.SocioViewHolder>() {

    class SocioViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtNombre: TextView = view.findViewById(R.id.txtNombreSocio)
        val txtDni: TextView = view.findViewById(R.id.txtDniSocio)
        val txtEmail: TextView = view.findViewById(R.id.txtEmailSocio)
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

        holder.txtNombre.text = socio.nombre ?: "Sin nombre"
        holder.txtDni.text = "DNI: ${socio.dni}"
        holder.txtEmail.text = socio.Email ?: "Sin email"
        holder.txtCategoria.text = socio.categoria ?: "General"
        holder.txtTelefono.text = "Tel: ${socio.telefono ?: "-"}"
        holder.txtEstado.text = socio.estado ?: "Activo"

        // --- CORRECCIÓN DE FECHA ---
        // 1. Usamos Instant para convertir el Long (milisegundos) a LocalDate
        try {
            val fecha = Instant.ofEpochMilli(socio.vencimiento)
                .atZone(ZoneOffset.UTC)
                .toLocalDate()

            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

            // 2. Usamos la referencia correcta (txtVencimiento), tvFecha no existía en tu ViewHolder
            holder.txtVencimiento.text = "Vence: ${fecha.format(formatter)}"
        } catch (e: Exception) {
            holder.txtVencimiento.text = "Vencimiento: Error"
        }
    }

    override fun getItemCount(): Int = listaSocios.size
}