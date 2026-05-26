package com.example.clubdeportivodam

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class CreateAccountActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)

        // Referencias a los componentes
        // Buscamos los EditText que están dentro de los <include>
        val etEmail1 = findViewById<EditText>(R.id.etEmail1)
        val etEmail2 = findViewById<EditText>(R.id.etEmail2)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val checkPoliticas = findViewById<CheckBox>(R.id.checkPoliticas)
        val btnRegistrar = findViewById<android.view.View>(R.id.btnLogin)

        btnRegistrar.setOnClickListener {
            val email1 = etEmail1.text.toString().trim()
            val email2 = etEmail2.text.toString().trim()
            val pass = etPassword.text.toString().trim()

            // Validaciones de negocio
            if (email1.isEmpty() || email2.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Debe completar todos los campos", Toast.LENGTH_SHORT).show()
            }
            else if (email1 != email2) {
                Toast.makeText(this, "Los emails no coinciden", Toast.LENGTH_SHORT).show()
            }
            else if (!checkPoliticas.isChecked) {
                Toast.makeText(this, "Debe aceptar los términos y condiciones", Toast.LENGTH_SHORT).show()
            }
            else {
                // Si todo está bien, registramos en la BD
                registrarUsuario(email1, pass)
            }
        }
    }

    private fun registrarUsuario(email: String, pass: String) {
        val admin = AdminSQLiteOpenHelper(this)
        val db = admin.writableDatabase

        // Consultamos si el email ya existe
        val consulta = db.rawQuery("SELECT email FROM usuarios WHERE email = '$email'", null)

        if (consulta.moveToFirst()) {
            Toast.makeText(this, "Este correo ya se encuentra registrado", Toast.LENGTH_SHORT).show()
            db.close()
        } else {
            // Preparamos los datos para insertar
            val registro = ContentValues()
            registro.put("email", email)
            registro.put("password", pass)

            // Insertamos en la tabla usuarios
            val resultado = db.insert("usuarios", null, registro)
            db.close()

            if (resultado != -1L) {
                Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show()

                // Redirigir al Panel de Gestión
                val intent = Intent(this, PanelGestionActivity::class.java)
                startActivity(intent)
                finish() // Cerramos esta actividad
            } else {
                Toast.makeText(this, "Error al crear la cuenta", Toast.LENGTH_SHORT).show()
            }
        }
    }
}