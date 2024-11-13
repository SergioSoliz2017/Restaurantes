package com.example.restaurantes

import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore
import www.sanju.motiontoast.MotionToast

class RegistroRestaurante : AppCompatActivity() {
    var restaurante = Restaurante()
    private var numeroFragmento = 1
    var cerrar = false
    private val db = FirebaseFirestore.getInstance()
    lateinit var usuario: Usuario

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro_restaurante)
        window.statusBarColor = Color.parseColor("#000000")
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        usuario = intent.getParcelableExtra("usuario")!!

        mostrarFragmento(1)
    }

    private fun mostrarFragmento(numero: Int) {

            val fragment = when (numero) {
                1 -> RegistroRestauranteDatos()
                2 -> RegistroRestauranteDatos2()
                3 -> {
                    val fragmento3 = RegistroRestauranteDatos3()
                    val bundle = Bundle()
                    bundle.putParcelable("usuario", usuario)
                    fragmento3.arguments = bundle
                    fragmento3
                }
                else -> RegistroRestauranteDatos()
            }

            supportFragmentManager.beginTransaction()
                .replace(R.id.containerView, fragment as Fragment)
                .commit()

    }

    fun agregarRestaurante(nombre: String, celular: String) {
        restaurante.nombreRestaurante = nombre
        restaurante.celularreferencia = celular
    }

    fun agregarLogo(logo: Uri) {
        restaurante.logo = logo
    }
    fun agregarHorario(horario: ArrayList<Horario>) {
        restaurante.horarioAtencion = horario
    }
    fun agregarCategoria(categoria: ArrayList<Categoria>) {
        restaurante.categoria = categoria
    }
    fun agregarServicios(servicios: ArrayList<String>) {
        restaurante.servicios = servicios
    }

    fun agregarUbicacion(ubicacion: com.google.android.gms.maps.model.LatLng?) {
        ubicacion?.let {
            restaurante.ubicacion = com.google.android.gms.maps.model.LatLng(it.latitude, it.longitude)
        }
    }

    fun cambiarFragmento(nuevoNumero: Int) {
        numeroFragmento = nuevoNumero
        mostrarFragmento(numeroFragmento)
    }
    fun mostrar() {
        MotionToast.createToast(this,"Operacion Exitosa", "Registro exitoso",MotionToast.TOAST_SUCCESS,
            MotionToast.GRAVITY_BOTTOM,MotionToast.LONG_DURATION,null)
    }
}
