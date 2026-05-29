package com.example.clubdeportivodam

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class AdminSQLiteOpenHelper(
    context: Context?
) : SQLiteOpenHelper(context, "administracion", null, 3) { // Subimos a versión 3

    override fun onCreate(db: SQLiteDatabase) {
        // 1. Crear tabla SOCIOS
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

        // 2. Crear tabla ACTIVIDADES
        // Tabla ACTIVIDADES modificada con MONTO
        db.execSQL("""
            CREATE TABLE actividades (
                id INTEGER PRIMARY KEY AUTOINCREMENT, 
                nombre TEXT, 
                profesor TEXT, 
                horario1 TEXT, 
                horario2 TEXT, 
                cupos INTEGER,
                monto REAL
            )
        """.trimIndent())

        // 3. Crear tabla USUARIOS (La que necesitás para el registro)
        db.execSQL("""
            CREATE TABLE usuarios (
                email TEXT PRIMARY KEY, 
                password TEXT
            )
        """.trimIndent())

        db.execSQL("""
    CREATE TABLE pagos (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        dni_socio TEXT,
        nombre_socio TEXT,
        actividad TEXT,
        monto REAL,
        fecha TEXT
    )
""".trimIndent())
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Borramos todo en orden inverso si detectamos cambio de versión
        db.execSQL("DROP TABLE IF EXISTS usuarios")
        db.execSQL("DROP TABLE IF EXISTS actividades")
        db.execSQL("DROP TABLE IF EXISTS socios")
        onCreate(db)
    }
}