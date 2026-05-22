package com.example.clubdeportivodam

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class AdminSQLiteOpenHelper(
    context: Context?
) : SQLiteOpenHelper(context, "administracion", null, 2) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE socios (
                dni TEXT PRIMARY KEY, 
                nombre TEXT, 
                email TEXT,
                telefono TEXT, 
                categoria TEXT, 
                vencimiento TEXT, 
                monto REAL, 
                estado TEXT
            )
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE actividades (
                id INTEGER PRIMARY KEY AUTOINCREMENT, 
                nombre TEXT, 
                profesor TEXT, 
                horario1 TEXT, 
                horario2 TEXT, 
                cupos INTEGER
            )
        """.trimIndent())
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Borramos AMBAS tablas si actualizamos la versión
        db.execSQL("DROP TABLE IF EXISTS socios")
        db.execSQL("DROP TABLE IF EXISTS actividades")
        onCreate(db)
    }
}