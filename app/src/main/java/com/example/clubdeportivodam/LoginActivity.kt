package com.example.clubdeportivodam

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

class LoginActivity : AppCompatActivity() {

    private lateinit var googleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 9001

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
        // Como usas <include>, el ID btnGoogle es el contenedor del botón
        findViewById<View>(R.id.btnGoogle).setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }

        // 3. Botón Ingresar (Manual)
        findViewById<View>(R.id.btnLogin).setOnClickListener {
            irAPanel()
        }

        // 4. Botón Volver
        findViewById<View>(R.id.btnBack).setOnClickListener {
            finish()
        }
    }

    // Manejar el resultado de la ventana de Google
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        Log.d("GoogleTrace", "onActivityResult activado. RequestCode: $requestCode, ResultCode: $resultCode")

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                val nombre = account?.displayName ?: "Usuario"

                Toast.makeText(this, "Bienvenido $nombre", Toast.LENGTH_SHORT).show()
                irAPanel()

            } catch (e: ApiException) {
                // Si aquí sale error 10 o 12500, es que el SHA-1 no coincide en Google Cloud
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
}