package com.example.clubdeportivodam

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

// Adaptador del RecyclerView: se encarga mostrar datos y  gestionar las acciones de socio
class SocioAdapter(private val listaSocios: MutableList<Socio>) :
    RecyclerView.Adapter<SocioAdapter.SocioViewHolder>() {
// Viewholder con las referencias a los distintos datos del socio
    class SocioViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtNombre: TextView = view.findViewById(R.id.txtNombreSocio)
        val txtDni: TextView = view.findViewById(R.id.txtDniSocio)
        val txtEmail: TextView = view.findViewById(R.id.txtEmailSocio)
        val txtTelefono: TextView = view.findViewById(R.id.txtTelefonoSocio)
        val txtCategoria: TextView = view.findViewById(R.id.txtCategoriaSocio)
        val txtVencimiento: TextView = view.findViewById(R.id.txtVencimientoSocio)
        val txtEstado: TextView = view.findViewById(R.id.txtEstadoSocio)

        val btnEliminar: ImageButton = view.findViewById(R.id.btnEliminarSocio)
    }
    // Crea la estructura visual del renglón
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SocioViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_socio, parent, false)
        return SocioViewHolder(adapterLayout)
    }
    // Imprime los datos correspondientes a cada renglón
    override fun onBindViewHolder(holder: SocioViewHolder, position: Int) {
        val socio = listaSocios[position]
        val context = holder.itemView.context

        holder.txtNombre.text = socio.nombre ?: "Sin nombre"
        holder.txtDni.text = "DNI: ${socio.dni}"
        holder.txtEmail.text = socio.Email ?: "Sin email"
        holder.txtCategoria.text = socio.categoria ?: "General"
        holder.txtTelefono.text = "Tel: ${socio.telefono ?: "-"}"
        holder.txtEstado.text = socio.estado ?: "Activo"

// Transforma los milisegundos (Long) a una fecha legible
        try {
            val fecha = Instant.ofEpochMilli(socio.vencimiento)
                .atZone(ZoneOffset.UTC)
                .toLocalDate()

            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            holder.txtVencimiento.text = "Vence: ${fecha.format(formatter)}"
        } catch (e: Exception) {
            holder.txtVencimiento.text = "Vencimiento: Error"
        }

//Botón para eliminar/dar de baja al socio
        holder.btnEliminar.setOnClickListener {
            AlertDialog.Builder(context)
                .setTitle("Eliminar Socio")
                .setMessage("¿Estás seguro de que deseas eliminar a ${socio.nombre}?")
                .setPositiveButton("Eliminar") { _, _ ->


                    val dbHelper = AdminSQLiteOpenHelper(context)
                    val db = dbHelper.writableDatabase


                    val filasAfectadas = db.delete("socios", "dni = ?", arrayOf(socio.dni))
                    db.close()

                    if (filasAfectadas > 0) {

                        val currentPosition = holder.adapterPosition


                        listaSocios.removeAt(currentPosition)
                        notifyItemRemoved(currentPosition)
                        notifyItemRangeChanged(currentPosition, listaSocios.size)

                        Toast.makeText(context, "Socio eliminado correctamente", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Error al eliminar de la base de datos", Toast.LENGTH_SHORT).show()
                    }
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }
    }

    override fun getItemCount(): Int = listaSocios.size
}