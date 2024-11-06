package com.example.restaurantes

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_ver_menu.AñadirPlato
import kotlinx.android.synthetic.main.activity_ver_menu.ListaMenus
import kotlinx.android.synthetic.main.activity_ver_menu.botonAñadirPlato
import kotlinx.android.synthetic.main.activity_ver_menu.view.botonAñadirPlato

class VerMenu : AppCompatActivity() {

    lateinit var usuario: Usuario
    val db = FirebaseFirestore.getInstance()

    override fun onBackPressed() {
        if (botonAñadirPlato.visibility == View.INVISIBLE) {
            botonAñadirPlato.visibility = View.VISIBLE
            ListaMenus.visibility = View.VISIBLE
            AñadirPlato.visibility = View.GONE
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ver_menu)
        window.statusBarColor = Color.parseColor("#000000")
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        usuario = intent.getParcelableExtra("usuario")!!
        title = usuario.nombreRestaurante

        botonAñadirPlato.setOnClickListener {
            ListaMenus.visibility = View.GONE
            AñadirPlato.visibility = View.VISIBLE
            botonAñadirPlato.visibility = View.INVISIBLE
        }
    }
}
