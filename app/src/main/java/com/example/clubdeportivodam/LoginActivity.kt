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

    // 1. MODIFICADO: Instanciamos el manejador de seguridad encriptada
    private lateinit var securityManager: SecurityManager

    // Mantenemos el mail fijo (no es secreto), pero la contraseña vuela de acá
    private val ADMIN_USER = "admin@gym.com"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // 2. MODIFICADO: Inicializar seguridad y precargar la clave de fábrica de forma encriptada
        securityManager = SecurityManager(this)
        securityManager.inicializarClavePorDefecto("admin123")

        // Configurar Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("153442224117-m9cs0f4su0vihkikr2el6f6v2fplekej.apps.googleusercontent.com")
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Configurar el botón de Google
        findViewById<View>(R.id.btnGoogle).setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }

        // Botón Ingresar (Manual con validación)
        val btnIngresar = findViewById<View>(R.id.btnLogin)
        btnIngresar.setOnClickListener {
            Log.d("LoginDebug", "Botón presionado")
            validarAcceso()
        }

        // Botón Volver
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

        // --- CASO 1: ADMINISTRADOR (MODIFICADO) ---
        // Comparamos el mail de siempre, pero la contraseña se delega al validador encriptado
        if (emailIngresado == ADMIN_USER && securityManager.verificarPassword(passwordIngresada)) {
            Toast.makeText(this, "¡Login Admin Exitoso!", Toast.LENGTH_SHORT).show()
            irAPanel()
            return
        }

        // --- CASO 2: USUARIO/SOCIO (Búsqueda en base de datos) ---
        val adminDB = AdminSQLiteOpenHelper(this)
        val db = adminDB.readableDatabase

        val cursor = db.rawQuery(
            "SELECT email FROM usuarios WHERE email = ? AND password = ?",
            arrayOf(emailIngresado, passwordIngresada)
        )

        if (cursor.moveToFirst()) {
            cursor.close()

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

            Toast.makeText(this, "¡Login exitoso! Bienvenido $emailIngresado", Toast.LENGTH_LONG).show()
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

                // Verificamos si es el Administrador (Por Google)
                if (emailGoogle == ADMIN_USER) {
                    irAPanel()
                    return
                }

                val adminDB = AdminSQLiteOpenHelper(this)
                val db = adminDB.readableDatabase
                val c = db.rawQuery("SELECT dni FROM socios WHERE email = ?", arrayOf(emailGoogle))

                if (c.moveToFirst()) {
                    val dniSocio = c.getString(0)
                    c.close()
                    db.close()

                    Toast.makeText(this, "Bienvenido socio: $emailGoogle", Toast.LENGTH_SHORT).show()
                    irAPerfilSocio(dniSocio)
                } else {
                    c.close()
                    db.close()

                    googleSignInClient.signOut().addOnCompleteListener {
                        Toast.makeText(this, "Acceso denegado: El correo $emailGoogle no es socio del club", Toast.LENGTH_LONG).show()
                    }
                }

            } catch (e: ApiException) {
                Log.e("GoogleError", "Error al conectar con Google: ${e.statusCode}")
                Toast.makeText(this, "Error de autenticación", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun irAPanel() {
        val intent = Intent(this, PanelGestionActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun irAPerfilSocio(dni: String) {
        val intent = Intent(this, PerfilSocioActivity::class.java)
        intent.putExtra("DNI_SOCIO", dni)
        startActivity(intent)
        finish()
    }
}