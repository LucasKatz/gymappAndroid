package com.example.clubdeportivodam

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

class LoginActivity : AppCompatActivity() {

    private lateinit var googleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 9001

    // Credenciales del Administrador Único
    private val ADMIN_USER = "admin@gym.com"
    private val ADMIN_PASS = "admin123"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // 1. Configurar Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("153442224117-m9cs0f4su0vihkikr2el6f6v2fplekej.apps.googleusercontent.com")
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // 2. Configurar el botón de Google
        findViewById<View>(R.id.btnGoogle).setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }

        // 3. Botón Ingresar (Manual con validación)
        val btnIngresar = findViewById<View>(R.id.btnLogin)
        btnIngresar.setOnClickListener {
            Log.d("LoginDebug", "Botón presionado") // Si esto no sale en Logcat, el problema es el XML
            validarAcceso()
        }

        // 4. Botón Volver
        findViewById<View>(R.id.btnBack).setOnClickListener {
            finish()
        }
    }

    private fun validarAcceso() {
        val etEmail = findViewById<EditText>(R.id.containerEmail)
        val etPass = findViewById<EditText>(R.id.containerPassword)

        if (etEmail == null || etPass == null) {
            Toast.makeText(this, "Error de interfaz", Toast.LENGTH_SHORT).show()
            return
        }

        val emailIngresado = etEmail.text.toString().trim()
        val passwordIngresada = etPass.text.toString().trim()

        if (emailIngresado.isEmpty() || passwordIngresada.isEmpty()) {
            Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        // --- CASO 1: ADMINISTRADOR ---
        if (emailIngresado == ADMIN_USER && passwordIngresada == ADMIN_PASS) {
            Toast.makeText(this, "¡Login Admin Exitoso!", Toast.LENGTH_SHORT).show()
            irAPanel()
            return
        }

        // --- CASO 2: USUARIO/SOCIO (Búsqueda en base de datos) ---
        val adminDB = AdminSQLiteOpenHelper(this)
        val db = adminDB.readableDatabase

        // 1. Validamos credenciales en la tabla 'usuarios'
        val cursor = db.rawQuery(
            "SELECT email FROM usuarios WHERE email = ? AND password = ?",
            arrayOf(emailIngresado, passwordIngresada)
        )

        if (cursor.moveToFirst()) {
            cursor.close()

            // 2. Si las credenciales son correctas, buscamos el DNI en la tabla 'socios'
            val cursorSocio = db.rawQuery(
                "SELECT dni FROM socios WHERE email = ?",
                arrayOf(emailIngresado)
            )

            var dniSocio = ""
            if (cursorSocio.moveToFirst()) {
                dniSocio = cursorSocio.getString(0)
            }
            cursorSocio.close()
            db.close()

            // --- EL TOAST QUE SOLICITASTE ---
            Toast.makeText(this, "¡Login exitoso! Bienvenido $emailIngresado", Toast.LENGTH_LONG).show()

            // Redirigimos al perfil del socio con su DNI
            irAPerfilSocio(dniSocio)

        } else {
            cursor.close()
            db.close()
            Toast.makeText(this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                val emailGoogle = account?.email ?: ""

                // Validación para Google Sign-In
                if (emailGoogle == ADMIN_USER) {
                    irAPanel()
                } else {
                    val adminDB = AdminSQLiteOpenHelper(this)
                    val db = adminDB.readableDatabase
                    val c = db.rawQuery("SELECT dni FROM socios WHERE email = ?", arrayOf(emailGoogle))

                    if (c.moveToFirst()) {
                        val dniSocio = c.getString(0)
                        c.close()
                        db.close()
                        irAPerfilSocio(dniSocio)
                    } else {
                        c.close()
                        db.close()
                        googleSignInClient.signOut()
                        Toast.makeText(this, "El correo $emailGoogle no está registrado como socio", Toast.LENGTH_LONG).show()
                    }
                }

            } catch (e: ApiException) {
                Log.e("GoogleError", "Error: ${e.statusCode}")
                Toast.makeText(this, "Error de conexión con Google", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun irAPanel() {
        val intent = Intent(this, PanelGestionActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun irAPerfilSocio(dni: String) {
        // Pasamos el DNI a la nueva Activity para mostrar el carnet del socio específico
        val intent = Intent(this, PerfilSocioActivity::class.java)
        intent.putExtra("DNI_SOCIO", dni)
        startActivity(intent)
        finish()
    }
}