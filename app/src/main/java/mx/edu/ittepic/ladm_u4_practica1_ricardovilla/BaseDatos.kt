package mx.edu.ittepic.ladm_u4_practica1_ricardovilla

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class BaseDatos(
    context: Context?,
    name: String?,
    factory: SQLiteDatabase.CursorFactory?,
    version: Int
) : SQLiteOpenHelper(context, name, factory, version) {
    override fun onCreate(db: SQLiteDatabase) {

        db.execSQL("CREATE TABLE LLAMADAS_ENTRANTES(ID INTEGER PRIMARY KEY AUTOINCREMENT,CELULAR VARCHAR(50),RESPONDIDO VARCHAR(2))")

        db.execSQL("CREATE TABLE MENSAJES(ID INTEGER PRIMARY KEY AUTOINCREMENT,CONTENIDO VARCHAR(250))")

        db.execSQL("CREATE TABLE TELEFONOS_REGISTRADOS(ID INTEGER PRIMARY KEY AUTOINCREMENT,CELULAR VARCHAR(40),TIPO VARCHAR(30))")

    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }

}