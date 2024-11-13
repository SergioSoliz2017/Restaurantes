package com.example.restaurantes

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_pantalla_principal.BuscarCliente
import kotlinx.android.synthetic.main.activity_pantalla_principal.MiRestauranteBoton
import kotlinx.android.synthetic.main.activity_pantalla_principal.bottomNavigationView
import www.sanju.motiontoast.MotionToast

class PantallaPrincipal : AppCompatActivity() {
    val db = Firebase.firestore
    var usuario = Usuario()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pantalla_principal)
        window.statusBarColor = Color.parseColor("#000000")
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        title = "FOOTABLE"
        val email = intent.getStringExtra("email") ?: ""
        guardarDatos(email)
        db.collection("Usuarios").document(email).get().addOnCompleteListener { documentTask ->
            if (documentTask.isSuccessful) {
                val document = documentTask.result
                if (document != null && document.exists()) {
                    val nombre = document.getString("Nombre")
                    val correo = email
                    val contraseña = document.getString("Contraseña")
                    val fechaNacimiento = document.getString("FechaNacimiento")
                    val tieneRestaurante = document.getBoolean("TieneRestaurante") ?: false
                    val nombreRestaurante = document.getString("Restaurante")
                    val fotoPerfil = document.getString("FotoPerfil")
                    usuario = Usuario(
                        nombre ?: "",
                        correo ?: "",
                        contraseña ?: "",
                        fechaNacimiento ?: "",
                        tieneRestaurante,
                        nombreRestaurante,
                        fotoPerfil ?: "",
                        null
                    )
                    MotionToast.createToast(
                        this, "Operación Exitosa", "Inicio exitoso", MotionToast.TOAST_SUCCESS,
                        MotionToast.GRAVITY_BOTTOM, MotionToast.LONG_DURATION, null
                    )

                    if (usuario.tieneRestaurante) {
                        bottomNavigationView.menu.clear()
                        bottomNavigationView.inflateMenu(R.menu.botton_menu)
                        MiRestauranteBoton.visibility = View.VISIBLE
                        abrirFragment(MisOfertas())
                        bottomNavigationView.selectedItemId = R.id.MisOfertas
                    } else {
                        bottomNavigationView.menu.clear()
                        bottomNavigationView.inflateMenu(R.menu.botton_menu_cliente)
                        BuscarCliente.visibility = View.VISIBLE
                        abrirFragment(OfertasFragment())
                        bottomNavigationView.selectedItemId = R.id.Ofertas
                    }
                }
            }
        }

        bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.Ofertas -> {
                    abrirFragment(OfertasFragment())
                    true
                }
                R.id.MisOfertas -> {
                    abrirFragment(MisOfertas())
                    true
                }
                R.id.Perfil -> {
                    abrirFragment(Perfil())
                    true
                }
                else -> false
            }
        }

        BuscarCliente.setOnClickListener {
            val inicio = Intent(this, Busqueda:: class.java).apply {
                putExtra("usuario", usuario)
            }
            startActivity(inicio)
        }

        MiRestauranteBoton.setOnClickListener {
            val inicio = Intent(this, MiRestaurante:: class.java).apply {
                putExtra("usuario", usuario)
                putExtra("restaurante",Restaurante())
            }
            startActivity(inicio)
        }
    }
    private fun guardarDatos(email:String) {
        val preferencias : SharedPreferences = getSharedPreferences("Credenciales",Context.MODE_PRIVATE)
        val user = email
        val editor : SharedPreferences.Editor = preferencias.edit()
        editor.putString("email",user)
        editor.apply()
    }
    fun BorrarDatos() {
        val preferencias : SharedPreferences = getSharedPreferences("Credenciales",Context.MODE_PRIVATE)
        val editor : SharedPreferences.Editor = preferencias.edit()
        editor.putString("email",null)
        editor.apply()
        MotionToast.createToast(
            this, "Operación Exitosa", "Se cerro sesion", MotionToast.TOAST_SUCCESS,
            MotionToast.GRAVITY_BOTTOM, MotionToast.LONG_DURATION, null
        )
    }

    private fun abrirFragment(fragment: Fragment) {
        val transaccion = supportFragmentManager.beginTransaction()
        transaccion.replace(R.id.containerView, fragment)
        transaccion.commit()
    }

}