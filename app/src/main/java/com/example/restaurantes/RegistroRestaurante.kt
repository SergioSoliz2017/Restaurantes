package com.example.restaurantes

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_inicio.textoRegistrate
import kotlinx.android.synthetic.main.activity_registro.EsRestaurante
import kotlinx.android.synthetic.main.activity_registro.Registrar
import kotlinx.android.synthetic.main.activity_registro_restaurante.Adelante
import kotlinx.android.synthetic.main.activity_registro_restaurante.Atras

class RegistroRestaurante : AppCompatActivity() {

    val registro1 :RegistroRestauranteDatos = RegistroRestauranteDatos()
    val registro2 : RegistroRestauranteDatos2 = RegistroRestauranteDatos2()
    val registro3 : RegistroRestauranteDatos3 = RegistroRestauranteDatos3();
    ;
    var numero : Int = 1;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro_restaurante)
        window.statusBarColor = Color.parseColor("#000000")
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        abrirFragment(registro1)
        actualizarBotonAtras()

        Adelante.setOnClickListener {
            if (numero < 3) {
                numero++
                when (numero) {
                    2 -> {
                        abrirFragment(registro2)
                        Adelante.text = "Siguiente"
                    }
                    3 -> {
                        abrirFragment(registro3)
                        Adelante.text = "Registrar" // Cambia el texto a "Registrar" cuando estés en el fragmento 3
                    }
                }
            } else {
                // Cuando llega al fragmento 3, inicia la nueva actividad
                val inicio = Intent(this, PantallaPrincipal::class.java)
                startActivity(inicio)
                finish()
            }
            actualizarBotonAtras()
        }

        Atras.setOnClickListener {
            numero--
            when (numero) {
                1 -> {
                    abrirFragment(registro1)
                    Adelante.text = "Siguiente"
                }
                2 -> {
                    abrirFragment(registro2)
                    Adelante.text = "Siguiente"
                }
            }
            actualizarBotonAtras()
        }
    }

    private fun actualizarBotonAtras() {
        // Mostrar/ocultar el botón Atras según el valor de numero
        Atras.visibility = if (numero > 1) View.VISIBLE else View.GONE
    }
    private fun abrirFragment(fragment: Fragment) {

        val transaccion = supportFragmentManager.beginTransaction()
        transaccion.replace(R.id.containerView,fragment)
        transaccion.commit()
    }
}
