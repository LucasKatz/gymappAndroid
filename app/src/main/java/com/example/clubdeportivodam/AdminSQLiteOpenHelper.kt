package com.example.clubdeportivodam

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class AdminSQLiteOpenHelper(
    context: Context?
) : SQLiteOpenHelper(context, "administracion", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        // Esta sentencia crea la tabla 'socios' la primera vez que se usa la app
        db.execSQL("create table socios(dni int primary key, nombre text, apellido text, estado text)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Se usa para cambios estructurales en el futuro
    }
}