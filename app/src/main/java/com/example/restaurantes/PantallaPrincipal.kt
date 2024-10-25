package com.example.restaurantes

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_pantalla_principal.BuscarCliente
import kotlinx.android.synthetic.main.activity_pantalla_principal.MiRestaurante
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
        title= "Menu's Here"
        val email = intent.getStringExtra("email") ?: ""
        db.collection("Usuarios").document(email).get().addOnCompleteListener { documentTask ->
            if (documentTask.isSuccessful) {
                val document = documentTask.result
                if (document != null && document.exists()) {
                    val nombre = document.getString("Nombre")
                    val correo = email
                    val contraseña = document.getString("Contraseña")
                    val fechaNacimiento = document.getString("FechaNacimiento")
                    val tieneRestaurante = document.getBoolean("TieneRestaurante") ?: false

                    usuario = Usuario(
                        nombre ?: "",
                        correo ?: "",
                        contraseña ?: "",
                        fechaNacimiento ?: "",
                        tieneRestaurante
                    )
                    MotionToast.createToast(this, "Operación Exitosa", "Inicio exitoso", MotionToast.TOAST_SUCCESS,
                        MotionToast.GRAVITY_BOTTOM, MotionToast.LONG_DURATION, null)
                    if (usuario.tieneRestaurante) {
                        bottomNavigationView.menu.clear()
                        bottomNavigationView.inflateMenu(R.menu.botton_menu)
                        MiRestaurante.visibility = View.VISIBLE
                        abrirFragment (MiRestaurante())
                    } else {
                        bottomNavigationView.menu.clear()
                        bottomNavigationView.inflateMenu(R.menu.botton_menu_cliente)
                        BuscarCliente.visibility = View.VISIBLE
                        abrirFragment (Ofertas())
                    }
                }
            }
        }

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
                R.id.Perfil -> {
                    abrirFragment (Perfil())
                    true
                }

                else -> false
            }
        }
        BuscarCliente.setOnClickListener {
            abrirFragment (Busqueda())
        }
        MiRestaurante.setOnClickListener {
            abrirFragment (MiRestaurante())
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
}