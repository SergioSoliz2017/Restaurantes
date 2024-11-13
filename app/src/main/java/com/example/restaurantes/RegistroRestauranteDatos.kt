package com.example.restaurantes

import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
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
        Adelante1.setOnClickListener{
            val nombre = nombreRestaurante.text.toString()
            val celular = celularReferencia.text.toString()
            (activity as RegistroRestaurante).agregarRestaurante(nombre, celular)
            (activity as RegistroRestaurante).cambiarFragmento(2)
            (activity as RegistroRestaurante).agregarHorario(obtenerHorarios())
        }
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

    fun obtenerHorarios(): ArrayList<Horario> {
        val listaHorarios = ArrayList<Horario>()

        if (Lunes.isChecked) {
            val abrir = lunesAbre.text.toString()
            val cerrar = lunesCierra.text.toString()
            val horarioLunes = Horario("Lunes", abrir, cerrar)
            listaHorarios.add(horarioLunes)
        }
        if (Martes.isChecked) {
            val abrir = martesAbre.text.toString()
            val cerrar = martesCierra.text.toString()
            val horarioLunes = Horario("Martes", abrir, cerrar)
            listaHorarios.add(horarioLunes)
        }
        if (Miercoles.isChecked) {
            val abrir = miercolesAbre.text.toString()
            val cerrar = miercolesCierra.text.toString()
            val horarioLunes = Horario("Miercoles", abrir, cerrar)
            listaHorarios.add(horarioLunes)
        }
        if (Jueves.isChecked) {
            val abrir = juevesAbre.text.toString()
            val cerrar = juevesCierra.text.toString()
            val horarioLunes = Horario("Jueves", abrir, cerrar)
            listaHorarios.add(horarioLunes)
        }
        if (Viernes.isChecked) {
            val abrir = viernesAbre.text.toString()
            val cerrar = viernesCierra.text.toString()
            val horarioLunes = Horario("Viernes", abrir, cerrar)
            listaHorarios.add(horarioLunes)
        }
        if (Sabado.isChecked) {
            val abrir = sabadoAbre.text.toString()
            val cerrar = sabadoCierra.text.toString()
            val horarioLunes = Horario("Sabado", abrir, cerrar)
            listaHorarios.add(horarioLunes)
        }
        if (Domingo.isChecked) {
            val abrir = domingoAbre.text.toString()
            val cerrar = domingoCierra.text.toString()
            val horarioLunes = Horario("Domingo", abrir, cerrar)
            listaHorarios.add(horarioLunes)
        }
        return listaHorarios
    }
}
