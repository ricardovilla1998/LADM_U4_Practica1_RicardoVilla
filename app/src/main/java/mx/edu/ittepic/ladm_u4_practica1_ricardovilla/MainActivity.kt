package mx.edu.ittepic.ladm_u4_practica1_ricardovilla

import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.database.sqlite.SQLiteException
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.telephony.SmsManager
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main2.*
import kotlinx.android.synthetic.main.activity_main3.*

class MainActivity : AppCompatActivity() {

    val siPermisoEstadoLlamada = 10
    val siPermisoLecturaLlamada = 11
    val siPermisoMsj = 12
    var tel = ""
    var nombreBaseDatos = "contestadora"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.READ_PHONE_STATE), siPermisoEstadoLlamada)
        }








        cargarLista()
        //envioSMS()

        btn_agradables.setOnClickListener {
            var activity_agradable = Intent(this,Main2Activity::class.java)
            startActivity(activity_agradable)
        }

        btn_no_agradables.setOnClickListener {
            var activity_no_agradable = Intent(this,Main3Activity::class.java)
            startActivity(activity_no_agradable )
        }

        try {
            var baseDatos = BaseDatos(this,nombreBaseDatos,null,1)
            var select = baseDatos.readableDatabase
            var SQL = "SELECT * FROM MENSAJES"


            var cursor = select.rawQuery(SQL,null)
            if(cursor.count>0){
                cursor.moveToFirst()

            }
            else{
                insertarMensaje()
            }

            select.close()
            baseDatos.close()



        }catch (error: SQLiteException){
            mensaje(error.message.toString())
        }


        var hilo = Hilo(this)
        hilo.start()









    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == siPermisoEstadoLlamada){
          cargarLista()


        }

    }


    fun mensaje(mensaje:String){
        AlertDialog.Builder(this)
            .setMessage(mensaje)
            .show()
    }
    fun elegirMensaje(id:Int):String{
        var msj = ""
        try {
            var baseDatos = BaseDatos(this,nombreBaseDatos,null,1)
            var select = baseDatos.readableDatabase
            var SQL = "SELECT * FROM MENSAJES WHERE ID = ${id}"


            var cursor = select.rawQuery(SQL,null)

            cursor.moveToFirst()


            msj = cursor.getString(1)



            select.close()
            baseDatos.close()





        }catch (error: SQLiteException){
            mensaje(error.message.toString())
        }
        return msj
    }

    private fun insertarMensaje() {
        try{
            var baseDatos = BaseDatos(this,nombreBaseDatos,null,1)

            var insertar = baseDatos.writableDatabase
            var SQL = "INSERT INTO MENSAJES VALUES(NULL,'¡EN UN MOMENTO TE RESPONDO!')"
            var SQL2 = "INSERT INTO MENSAJES VALUES(NULL,'¡DEJA DE LLAMARME!')"

            insertar.execSQL(SQL)
            insertar.execSQL(SQL2)
            //mensaje("SE INSERTO MENSAJE CORRECTAMENTE")
            insertar.close()
            baseDatos.close()



        }catch (error:SQLiteException){
            mensaje(error.message.toString())
        }
    }

    fun cargarLista(){

        if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.READ_CALL_LOG), siPermisoLecturaLlamada)
        }

        try {
            var baseDatos = BaseDatos(this,nombreBaseDatos,null,1)
            var select = baseDatos.readableDatabase
            var SQL = "SELECT * FROM LLAMADAS_ENTRANTES"


            var cursor = select.rawQuery(SQL,null)
            if(cursor.count>0){
                var arreglo = ArrayList<String>()
                cursor.moveToFirst()
                var cantidad = cursor.count-1

                (0..cantidad).forEach {

                    if(it%2==0) {
                        var data = "Numero: ${cursor.getString(1)}" + "\nMSJ enviado : ${cursor.getString(2)} "
                        arreglo.add(data)
                    }
                    cursor.moveToNext()

                }

                lista_perdidas.adapter = ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,arreglo)


            }

            select.close()
            baseDatos.close()



        }catch (error: SQLiteException){
           mensaje(error.message.toString())
        }


    }

    fun actualizaEstado (tel:String){

        // 0 -> No respondido
        // 1 -> Respondido
        try{
            var baseDatos = BaseDatos(this,nombreBaseDatos,null,1)

            var insertar = baseDatos.writableDatabase
            var SQL = "UPDATE LLAMADAS_ENTRANTES SET RESPONDIDO ='1' WHERE CELULAR = ?"
            var parametros = arrayOf(tel)
            insertar.execSQL(SQL,parametros)
           //mensaje("SE ACTUALIZO CORRECTAMENTE")
            insertar.close()
            baseDatos.close()


        }catch (error:SQLiteException){
            mensaje(error.message.toString())
        }
    }

     fun envioSMS() {
        var cont = 0
        var contar = 0
        var contar_no = 0

         if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED){
             ActivityCompat.requestPermissions(this,
                 arrayOf(android.Manifest.permission.SEND_SMS), siPermisoMsj)
         }

        try {
            var baseDatos = BaseDatos(this,nombreBaseDatos,null,1)
            var select = baseDatos.readableDatabase
            var SQL = "SELECT * FROM LLAMADAS_ENTRANTES"
            var SQL2 = "SELECT * FROM TELEFONOS_REGISTRADOS"
            var SQL3 = "SELECT * FROM MENSAJES"


            var cursor = select.rawQuery(SQL,null)
            var cursor2 = select.rawQuery(SQL2,null)
            var cursor3 = select.rawQuery(SQL3,null)

            if(cursor2.count>0){
                cursor.moveToFirst()
                cursor2.moveToFirst()
                var cantidad = cursor.count-1
                var cantidad_tel = cursor2.count-1

               do{
                   cursor.moveToFirst()
                        (0..cantidad).forEach{it
                            cont++
                            if(cont%2==0) {

                                if (cursor.getString(1) == cursor2.getString(1)) {

                                    if (cursor2.getString(2) == "AGRADABLE") {

                                        //mensaje(cursor.getString(1) + ":" + cursor2.getString(2))
                                        if(cursor.getString(2)=="0"){
                                            SmsManager.getDefault().sendTextMessage(cursor.getString(1),null,elegirMensaje(1),null,null)
                                            contar++
                                            //mensaje("MSJ enviado a : "+"${cursor.getString(1)}")
                                            actualizaEstado(cursor.getString(1))
                                        }



                                    }

                                    if (cursor2.getString(2) == "NO_AGRADABLE") {
                                        contar_no++
                                        //mensaje(cursor.getString(1) + ":" + cursor2.getString(2))
                                        if(cursor.getString(2)=="0"){
                                            SmsManager.getDefault().sendTextMessage(cursor.getString(1),null,elegirMensaje(2),null,null)
                                           //mensaje("MSJ enviado a : "+"${cursor.getString(1)}")
                                            actualizaEstado(cursor.getString(1))
                                        }

                                    }


                                }
                                //cursor.moveToNext()
                            }

                            cursor.moveToNext()
                        }


                }while(cursor2.moveToNext())
               // mensaje("AGRADABLES : "+contar.toString())
                //mensaje("NO AGRADABLES : "+contar_no.toString())



            }

            select.close()
            baseDatos.close()



        }catch (error: SQLiteException){
            mensaje(error.message.toString())
        }









       // SmsManager.getDefault().sendTextMessage("",null,"Prueba desde codigo KOTLIN",null,null)
        //Toast.makeText(this,"",Toast.LENGTH_LONG).show()

    }


}

//CLASE HILO
class Hilo (p:MainActivity) : Thread(){
    private var iniciado = false
    private var puntero = p
    private var pausa = false

    override fun run() {
        super.run()
        iniciado = true
        while(iniciado){
            sleep(1000)
            if(!pausa){
                puntero.runOnUiThread {


                    puntero.cargarLista()
                    puntero.envioSMS()



                }
            }
        }

    }

    fun estaIniciado(): Boolean {
        return iniciado
    }

    fun pausar() {
        pausa = true
    }

    fun despausar() {
        pausa = false
    }

    fun detener() {
        iniciado = false
    }
}

