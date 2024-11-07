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
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_inicio.usuario
import kotlinx.android.synthetic.main.activity_main.IniciarSesion
import kotlinx.android.synthetic.main.activity_main.Registrarse
import www.sanju.motiontoast.MotionToast

class MainActivity : AppCompatActivity() {
    val db = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        window.statusBarColor = Color.parseColor("#000000")
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        Sesion()
        IniciarSesion.setOnClickListener {
            val inicio = Intent(this, InicioSesion:: class.java)
            startActivity(inicio)
            finish()
        }
        Registrarse.setOnClickListener {
            val inicio = Intent(this, Registro:: class.java)
            startActivity(inicio)
            finish()
        }
    }

    private fun Sesion() {
        val preferencias : SharedPreferences = getSharedPreferences("Credenciales",Context.MODE_PRIVATE)
        val user = preferencias.getString("email","")
        if (user != ""){
            val inicio = Intent(this, PantallaPrincipal:: class.java).apply {
                putExtra("email", user)
            }
            startActivity(inicio)
            finish()
        }

    }

}