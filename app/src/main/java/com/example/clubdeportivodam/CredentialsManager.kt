package com.example.clubdeportivodam

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys

class SecurityManager(context: Context) {

    private val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

    private val sharedPreferences = EncryptedSharedPreferences.create(
        "secret_shared_prefs",
        masterKeyAlias,
        context,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    // Graba la clave inicial solo si las SharedPreferences están vacías
    fun inicializarClavePorDefecto(passwordTxt: String) {
        if (!sharedPreferences.contains("admin_pass")) {
            sharedPreferences.edit().putString("admin_pass", passwordTxt).apply()
        }
    }

    fun verificarPassword(passwordIngresada: String): Boolean {
        val passGuardada = sharedPreferences.getString("admin_pass", null)
        return passGuardada == passwordIngresada
    }
}