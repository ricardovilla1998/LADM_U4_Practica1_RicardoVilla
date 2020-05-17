package mx.edu.ittepic.ladm_u4_practica1_ricardovilla

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteException
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import android.util.Log
import android.widget.Toast

class IncomingCall : BroadcastReceiver(){

    var responde = true
    val nombreBD = "contestadora"
    var cursor : Context ?= null
    var cont = 0
    var telefono = ""
    var telefono_aux =""
    var puntero:MainActivity?=null

    override fun onReceive(context : Context, intent: Intent?) {
        try {
            cursor = context
            val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

            val PhoneListener = MyPhoneStateListener()

            tm.listen(PhoneListener, PhoneStateListener.LISTEN_CALL_STATE)



                //Log.d("AAAAAAAAAAAAAAAAAAAH", " $telefono_aux")






        } catch (e: Exception) {
            Log.e("Phone Receive Error", " $e")
        }


    }

    private inner class MyPhoneStateListener : PhoneStateListener() {

        var cel = ""
        var cuelga = false





        override fun onCallStateChanged(state: Int, incomingNumber: String) {



            Log.d("MyPhoneListener ${cont}", "$state   incoming no:$incomingNumber")

            //LA LLAMADA ESTÁ PERDIDA (EL TELEFONO LO CUELGAN)
            if (responde==true && state == 0) {
                telefono = "$incomingNumber"
                cuelga = true

                Log.d("Perdida \"$cont\"", telefono)
                telefono_aux = "121313233asqwqw"

                cont++

                if (cont == 1) {
                    if (telefono != "") {
                        try {
                            var baseDatos = BaseDatos(cursor, nombreBD, null, 1)
                            var insertar = baseDatos.writableDatabase
                            var SQL =
                                "INSERT INTO LLAMADAS_ENTRANTES VALUES(NULL,'${telefono}','0')"
                            insertar.execSQL(SQL)
                            baseDatos.close()

                        } catch (error: SQLiteException) {

                        }
                    }
                }
            }

            if(state == 0){

            }

            if (state == 2) {
                responde = false

            }


        }




    }




}