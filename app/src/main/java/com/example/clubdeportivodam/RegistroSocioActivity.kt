package com.example.clubdeportivodam

import android.content.ContentValues
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class RegistroSocioActivity : AppCompatActivity() {

    // 1. Declaramos las variables para los componentes del XML
    private lateinit var etDni: EditText
    private lateinit var etNombre: EditText
    private lateinit var etApellido: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_registro_socio_1)

        // 2. Inicializamos los componentes buscando sus IDs (Asegúrate que coincidan con tu XML)
        etDni = findViewById(R.id.etDniRegistro) // Cambia el ID si en tu XML es distinto
        etNombre = findViewById(R.id.etNombreRegistro)
        etApellido = findViewById(R.id.etApellidoRegistro)

        val btnGuardar = findViewById<Button>(R.id.btnGuardarSocio)
        val btnModificar = findViewById<Button>(R.id.btnModificarSocio)
        val btnEliminar = findViewById<Button>(R.id.btnEliminarSocio)

        // 3. Configuramos los clics
        btnGuardar.setOnClickListener { agregarSocio() }
        btnModificar.setOnClickListener { modificarSocio() }
        btnEliminar.setOnClickListener { eliminarSocio() }
    }

    // --- MÉTODO PARA AGREGAR ---
    private fun agregarSocio() {
        val admin = AdminSQLiteOpenHelper(this)
        val db = admin.writableDatabase

        val dni = etDni.text.toString()
        val nombre = etNombre.text.toString()
        val apellido = etApellido.text.toString()

        if (dni.isNotEmpty() && nombre.isNotEmpty() && apellido.isNotEmpty()) {
            val registro = ContentValues()
            registro.put("dni", dni)
            registro.put("nombre", nombre)
            registro.put("apellido", apellido)
            registro.put("estado", "Activo")

            db.insert("socios", null, registro)
            db.close()

            etDni.setText(""); etNombre.setText(""); etApellido.setText("")
            Toast.makeText(this, "Socio guardado correctamente", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Debes completar todos los campos", Toast.LENGTH_SHORT).show()
        }
    }

    // --- MÉTODO PARA MODIFICAR ---
    private fun modificarSocio() {
        val admin = AdminSQLiteOpenHelper(this)
        val db = admin.writableDatabase

        val dni = etDni.text.toString()
        val registro = ContentValues()
        registro.put("nombre", etNombre.text.toString())
        registro.put("apellido", etApellido.text.toString())

        // Actualiza al socio que coincida con el DNI ingresado
        val cant = db.update("socios", registro, "dni=$dni", null)
        db.close()

        if (cant == 1) {
            Toast.makeText(this, "Socio modificado exitosamente", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "No existe un socio con ese DNI", Toast.LENGTH_SHORT).show()
        }
    }

    // --- MÉTODO PARA ELIMINAR ---
    private fun eliminarSocio() {
        val admin = AdminSQLiteOpenHelper(this)
        val db = admin.writableDatabase

        val dni = etDni.text.toString()

        val cant = db.delete("socios", "dni=$dni", null)
        db.close()

        etDni.setText(""); etNombre.setText(""); etApellido.setText("")

        if (cant == 1) {
            Toast.makeText(this, "Socio eliminado", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "No existe un socio con ese DNI", Toast.LENGTH_SHORT).show()
        }
    }
}