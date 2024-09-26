package com.example.restaurantes

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import www.sanju.motiontoast.MotionToast

class PantallaPrincipal : AppCompatActivity() {
    val db = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pantalla_principal)
        window.statusBarColor = Color.parseColor("#000000")
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        title= "FOOTABLE"

        if (savedInstanceState == null) {
            cargarFragmento(Ofertas()) // Cargar el fragmento al iniciar la actividad
        }
    }

    private fun cargarFragmento(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.containerView, fragment) // Reemplaza el contenedor con el fragmento
        transaction.commit() // Ejecuta la transacción
    }
}