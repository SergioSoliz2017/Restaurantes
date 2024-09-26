package com.example.restaurantes

import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_registro_restaurante_datos.*
import java.util.Calendar

class RegistroRestauranteDatos : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_registro_restaurante_datos, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        gestionarHorarios()
        //CrearLista()
    }

    private fun gestionarHorarios() {
        val dias = listOf(
            Pair("Lunes", Pair(lunesAbre, lunesCierra)),
            Pair("Martes", Pair(martesAbre, martesCierra)),
            Pair("Miercoles", Pair(miercolesAbre, miercolesCierra)),
            Pair("Jueves", Pair(juevesAbre, juevesCierra)),
            Pair("Viernes", Pair(viernesAbre, viernesCierra)),
            Pair("Sabado", Pair(sabadoAbre, sabadoCierra)),
            Pair("Domingo", Pair(domingoAbre, domingoCierra))
        )

        dias.forEach { (dia, pair) ->
            val (abre, cierra) = pair
            abre.setOnClickListener { ClickHora(abre) }
            cierra.setOnClickListener { ClickHora(cierra) }

            val checkBox = when (dia) {
                "Lunes" -> Lunes
                "Martes" -> Martes
                "Miercoles" -> Miercoles
                "Jueves" -> Jueves
                "Viernes" -> Viernes
                "Sabado" -> Sabado
                "Domingo" -> Domingo
                else -> null
            }

            checkBox?.setOnCheckedChangeListener { _, isChecked ->
                val linea = when (dia) {
                    "Lunes" -> lineaLunes
                    "Martes" -> lineaMartes
                    "Miercoles" -> lineaMiercoles
                    "Jueves" -> lineaJueves
                    "Viernes" -> lineaViernes
                    "Sabado" -> lineaSabado
                    "Domingo" -> lineaDomingo
                    else -> null
                }
                linea?.visibility = if (isChecked) View.VISIBLE else View.INVISIBLE
            }
        }
    }

    private fun ClickHora(edit: EditText) {
        val calendario = Calendar.getInstance()
        val hora = calendario.get(Calendar.HOUR_OF_DAY)
        val minuto = calendario.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(context,
            { _, selectedHour, selectedMinute ->
                edit.setText(String.format("%02d:%02d", selectedHour, selectedMinute))
            }, hora, minuto, true)

        timePickerDialog.show()
    }

    private fun CrearLista () {
        if (Lunes.isChecked){
            Toast.makeText(this.context, "funciona", Toast.LENGTH_SHORT).show()
        }
    }
}
