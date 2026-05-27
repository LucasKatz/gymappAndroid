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

        val etEmail1 = findViewById<EditText>(R.id.etEmail1)
        val etEmail2 = findViewById<EditText>(R.id.etEmail2)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val checkPoliticas = findViewById<CheckBox>(R.id.checkPoliticas)
        val btnRegistrar = findViewById<android.view.View>(R.id.btnLogin)

        btnRegistrar.setOnClickListener {
            val email1 = etEmail1.text.toString().trim()
            val email2 = etEmail2.text.toString().trim()
            val pass = etPassword.text.toString().trim()

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
                // LLAMADA A LA NUEVA FUNCIÓN DE VALIDACIÓN
                validarYRegistrar(email1, pass)
            }
        }
    }

    private fun validarYRegistrar(email: String, pass: String) {
        val admin = AdminSQLiteOpenHelper(this)
        val db = admin.readableDatabase // Usamos readable para consultar primero

        // 1. PASO CRÍTICO: ¿Es socio activo del club?
        // Buscamos si el email existe en la tabla 'socios'
        val cursorSocio = db.rawQuery("SELECT dni FROM socios WHERE email = ?", arrayOf(email))

        if (!cursorSocio.moveToFirst()) {
            // Si no está en la tabla socios, no lo dejamos crear cuenta
            Toast.makeText(this, "Acceso denegado: Este correo no figura como socio del club.", Toast.LENGTH_LONG).show()
            cursorSocio.close()
            db.close()
            return
        }

        val dniSocio = cursorSocio.getString(0) // Obtenemos su DNI para el log o mensaje
        cursorSocio.close()

        // 2. ¿Ya tiene una cuenta de usuario creada?
        val cursorUser = db.rawQuery("SELECT email FROM usuarios WHERE email = ?", arrayOf(email))

        if (cursorUser.moveToFirst()) {
            Toast.makeText(this, "Usted ya tiene una cuenta creada. Intente iniciar sesión.", Toast.LENGTH_SHORT).show()
            cursorUser.close()
            db.close()
        } else {
            cursorUser.close()

            // 3. REGISTRO FINAL (Si pasó las dos pruebas anteriores)
            val dbWrite = admin.writableDatabase
            val registro = ContentValues()
            registro.put("email", email)
            registro.put("password", pass)

            val resultado = dbWrite.insert("usuarios", null, registro)
            dbWrite.close()

            if (resultado != -1L) {
                Toast.makeText(this, "¡Cuenta creada con éxito! Bienvenido.", Toast.LENGTH_SHORT).show()

                // Como es un socio "normal", no debería ir al Panel de Gestión (Admin)
                // Lo mandamos directamente a su Carnet (PerfilSocioActivity)
                val intent = Intent(this, PerfilSocioActivity::class.java)
                intent.putExtra("DNI_SOCIO", dniSocio)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Error al guardar en el sistema", Toast.LENGTH_SHORT).show()
            }
        }
    }
}