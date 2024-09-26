package com.example.restaurantes

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_registro_restaurante.Adelante
import kotlinx.android.synthetic.main.activity_registro_restaurante.Atras
import kotlinx.android.synthetic.main.fragment_registro_restaurante_datos.celularReferencia
import kotlinx.android.synthetic.main.fragment_registro_restaurante_datos.nombreRestaurante

class RegistroRestaurante : AppCompatActivity() {

    val registro1 :RegistroRestauranteDatos = RegistroRestauranteDatos()
    val registro2 : RegistroRestauranteDatos2 = RegistroRestauranteDatos2()
    val registro3 : RegistroRestauranteDatos3 = RegistroRestauranteDatos3();
    private val db = FirebaseFirestore.getInstance()
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
                        Adelante.text = "Registrar"
                    }
                }
            } else {
                subirDatos()
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

    private fun subirDatos() {
        val usuario: Usuario? = intent.getParcelableExtra("usuario")
        db.collection("Usuarios").document(usuario?.correo.toString()).set(
            hashMapOf(
                "Nombre" to usuario?.nombre,
                "Contraseña" to usuario?.contraseña,
                "FechaNacimiento" to usuario?.fechaNacimiento,
                "TieneRestaurante" to true
            )
        )

        db.collection("Restaurante").document(nombreRestaurante.text.toString()).set(
                    hashMapOf(
                        "Dueño" to usuario?.correo,
                        "Celular" to celularReferencia.text.toString(),
                        "HorarioAtencion" to "" ,  // Aquí pones los horarios seleccionados del fragmento
                        "Categoria" to true,
                        "Logo" to true,
                        "Ubicacion" to true
                    )
                )

        val inicio = Intent(this, PantallaPrincipal::class.java)
        startActivity(inicio)
        finish()
    }

    private fun actualizarBotonAtras() {
        Atras.visibility = if (numero > 1) View.VISIBLE else View.GONE
    }

    private fun abrirFragment(fragment: Fragment) {
        val transaccion = supportFragmentManager.beginTransaction()
        transaccion.replace(R.id.containerView,fragment)
        transaccion.commit()
    }

}
