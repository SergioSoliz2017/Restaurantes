package com.example.restaurantes

import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_registro_restaurante_datos.*  // Importar todo el layout de manera automática
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
        ponerVisible()
        dias()
    }

    private fun dias() {
        lunesAbre.setOnClickListener { ClickHora(lunesAbre) }
        lunesCierra.setOnClickListener { ClickHora(lunesCierra) }
        martesAbre.setOnClickListener { ClickHora(martesAbre) }
        martesCierra.setOnClickListener { ClickHora(martesCierra) }
        miercolesAbre.setOnClickListener { ClickHora(miercolesAbre) }
        miercolesCierra.setOnClickListener { ClickHora(miercolesCierra) }
        juevesAbre.setOnClickListener { ClickHora(juevesAbre) }
        juevesCierra.setOnClickListener { ClickHora(juevesCierra) }
        viernesAbre.setOnClickListener { ClickHora(viernesAbre) }
        viernesCierra.setOnClickListener { ClickHora(viernesCierra) }
        sabadoAbre.setOnClickListener { ClickHora(sabadoAbre) }
        sabadoCierra.setOnClickListener { ClickHora(sabadoCierra) }
        domingoAbre.setOnClickListener { ClickHora(domingoAbre) }
        domingoCierra.setOnClickListener { ClickHora(domingoCierra) }
    }

    private fun ponerVisible() {
        Lunes?.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                lineaLunes.visibility = View.VISIBLE
            } else {
                lineaLunes.visibility = View.INVISIBLE
            }
        }
        Martes?.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                lineaMartes.visibility = View.VISIBLE
            } else {
                lineaMartes.visibility = View.INVISIBLE
            }
        }
        Miercoles?.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                lineaMiercoles.visibility = View.VISIBLE
            } else {
                lineaMiercoles.visibility = View.INVISIBLE
            }
        }
        Jueves?.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                lineaJueves.visibility = View.VISIBLE
            } else {
                lineaJueves.visibility = View.INVISIBLE
            }
        }
        Viernes?.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                lineaViernes.visibility = View.VISIBLE
            } else {
                lineaViernes.visibility = View.INVISIBLE
            }
        }
        Sabado?.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                lineaSabado.visibility = View.VISIBLE
            } else {
                lineaSabado.visibility = View.INVISIBLE
            }
        }
        Domingo?.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                lineaDomingo.visibility = View.VISIBLE
            } else {
                lineaDomingo.visibility = View.INVISIBLE
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
}