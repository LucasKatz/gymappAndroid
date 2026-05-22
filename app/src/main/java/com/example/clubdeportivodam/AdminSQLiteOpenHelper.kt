package com.example.clubdeportivodam

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class AdminSQLiteOpenHelper(
    context: Context?
) : SQLiteOpenHelper(context, "administracion", null, 2) { // Subimos la versión a 2

    override fun onCreate(db: SQLiteDatabase) {

        db.execSQL("""
            CREATE TABLE socios (
                dni INTEGER PRIMARY KEY, 
                nombre TEXT, 
                email TEXT,
                telefono TEXT, 
                categoria TEXT, 
                vencimiento TEXT, 
                monto REAL, 
                estado TEXT
            )
        """.trimIndent())
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Si la versión cambia, borramos la tabla vieja y creamos la nueva
        db.execSQL("DROP TABLE IF EXISTS socios")
        onCreate(db)
    }
}