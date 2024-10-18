package com.example.restaurantes

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_pantalla_principal.Buscar
import kotlinx.android.synthetic.main.activity_pantalla_principal.bottomNavigationView

class PantallaPrincipal : AppCompatActivity() {
    val db = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pantalla_principal)
        window.statusBarColor = Color.parseColor("#000000")
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        title= "Menu's Here"

        abrirFragment (Ofertas())
        bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.Ofertas -> {
                    abrirFragment (Ofertas())
                    true
                }
                R.id.MisOfertas -> {
                        abrirFragment (MisOfertas())
                    true
                }
                R.id.MiRestaurante -> {
                        abrirFragment (MiRestaurante())
                    true
                }
                R.id.Perfil -> {
                        abrirFragment (Perfil())
                    true
                }

                else -> false
            }
        }
        Buscar.setOnClickListener {
            abrirFragment (Busqueda())
        }
    }

    private fun abrirFragment(fragment: Fragment) {
        //val bundle = Bundle()
        //bundle.putString("nombreUsuario", user)
        //fragment.arguments = bundle
        val transaccion = supportFragmentManager.beginTransaction()
        transaccion.replace(R.id.containerView,fragment)
        transaccion.commit()
    }

    private fun cargarFragmento(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.containerView, fragment) // Reemplaza el contenedor con el fragmento
        transaction.commit() // Ejecuta la transacción
    }
}