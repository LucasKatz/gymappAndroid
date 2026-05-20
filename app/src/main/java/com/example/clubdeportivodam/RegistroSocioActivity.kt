package com.example.clubdeportivodam

import android.content.ContentValues
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


class RegistroSocioActivity : AppCompatActivity() {

    // 1. Inicializamos el ViewModel compartido.
    // Esto es lo que permite que el Fragment 1 guarde datos y el 3 los lea.
    val socioViewModel: SocioViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 2. IMPORTANTE: El layout de la actividad debe ser el que tiene el contenedor (FrameLayout)
        // No pongas aquí el layout de un fragmento.
        setContentView(R.layout.activity_registro_socio)

        // 3. Al iniciar, cargamos el primer fragmento (Sección 1)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_registros, RegistroSocioFragment1())
                .commit()
        }
    }
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