package mx.edu.ittepic.ladm_u4_practica1_ricardovilla

import android.app.AlertDialog
import android.database.sqlite.SQLiteException
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main2.*
import kotlinx.android.synthetic.main.activity_main3.*

class Main3Activity : AppCompatActivity() {
    var nombreBaseDatos = "contestadora"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main3)

        cargarListaNoAgradables()

        btn_add_no.setOnClickListener {
            if (edit_tel.text.isEmpty()) {
                mensaje("DEBE PONER UN NUMERO")
                return@setOnClickListener
            }
            else{
                insertarTelefonoNoAgradable()

            }
        }
        try {
            var baseDatos = BaseDatos(this,nombreBaseDatos,null,1)
            var select = baseDatos.readableDatabase
            var SQL = "SELECT * FROM MENSAJES"


            var cursor = select.rawQuery(SQL,null)
            if(cursor.count>0){
                cursor.moveToFirst()
                cursor.moveToNext()
                edit_msj_no.setText(cursor.getString(1))

            }


            select.close()
            baseDatos.close()



        }catch (error: SQLiteException){
            mensaje(error.message.toString())
        }

        btn_add_no.setOnClickListener {
            insertarTelefonoNoAgradable()

        }


        button3.setOnClickListener {
            try{
                var baseDatos = BaseDatos(this,nombreBaseDatos,null,1)

                var insertar = baseDatos.writableDatabase
                var SQL = "UPDATE MENSAJES SET CONTENIDO ='${edit_msj_no.text.toString()}' WHERE ID = ?"
                var parametros = arrayOf(2)
                insertar.execSQL(SQL,parametros)
                mensaje("SE ACTUALIZO CORRECTAMENTE")
                insertar.close()
                baseDatos.close()


            }catch (error:SQLiteException){
                mensaje(error.message.toString())
            }
        }






    }

    private fun insertarTelefonoNoAgradable() {
        try{
            var baseDatos = BaseDatos(this,nombreBaseDatos,null,1)

            var insertar = baseDatos.writableDatabase
            var SQL = "INSERT INTO TELEFONOS_REGISTRADOS VALUES(NULL,'${edit_tel_no.text.toString()}','NO_AGRADABLE')"

            insertar.execSQL(SQL)
            mensaje("SE INSERTO CORRECTAMENTE")
            insertar.close()
            baseDatos.close()
            edit_tel_no.setText("")
            cargarListaNoAgradables()

        }catch (error:SQLiteException){
            mensaje(error.message.toString())
        }
    }

    override fun onStart() {
        super.onStart()
        cargarListaNoAgradables()
    }


    fun cargarListaNoAgradables(){

        try {
            var baseDatos = BaseDatos(this,nombreBaseDatos,null,1)
            var select = baseDatos.readableDatabase
            var SQL = "SELECT * FROM TELEFONOS_REGISTRADOS"


            var cursor = select.rawQuery(SQL,null)
            if(cursor.count>0){
                var arreglo = ArrayList<String>()
                cursor.moveToFirst()
                var cantidad = cursor.count-1

                (0..cantidad).forEach {


                    if(cursor.getString(2).toString() == "NO_AGRADABLE"){
                        var data = "Numero: ${cursor.getString(1)}"
                        arreglo.add(data)
                    }


                    cursor.moveToNext()

                }

                lista_no_agradables.adapter = ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,arreglo)


            }

            select.close()
            baseDatos.close()



        }catch (error: SQLiteException){
            mensaje(error.message.toString())
        }



    }
    fun mensaje(mensaje:String){
        AlertDialog.Builder(this)
            .setMessage(mensaje)
            .show()
    }
}
