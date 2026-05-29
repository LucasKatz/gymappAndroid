package com.example.clubdeportivodam // Asegúrate de que este sea tu paquete real

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ActividadAdapter(private val listaActividades: List<Actividad>) :
    RecyclerView.Adapter<ActividadAdapter.ActividadViewHolder>() {


    class ActividadViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitulo: TextView = view.findViewById(R.id.tvTituloActividad)
        val tvProfesor: TextView = view.findViewById(R.id.tvProfesorActividad)
        val tvCupos: TextView = view.findViewById(R.id.tvCuposActividad)
        val tvHorario1: TextView = view.findViewById(R.id.tvHorario1Actividad)
        val tvHorario2: TextView = view.findViewById(R.id.tvHorario2Actividad)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActividadViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ActividadViewHolder(
            layoutInflater.inflate(R.layout.view_card_activity, parent, false)
        )
    }


    override fun onBindViewHolder(holder: ActividadViewHolder, position: Int) {
        val item = listaActividades[position]

        holder.tvTitulo.text = item.nombre
        holder.tvProfesor.text = "Prof. ${item.profesor}"
        holder.tvCupos.text = item.cupos.toString()
        holder.tvHorario1.text = item.horario1
        holder.tvHorario2.text = item.horario2
    }


    override fun getItemCount(): Int = listaActividades.size
}