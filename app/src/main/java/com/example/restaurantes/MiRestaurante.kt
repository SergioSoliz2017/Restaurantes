package com.example.restaurantes

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.location.Geocoder
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_inicio.usuario
import kotlinx.android.synthetic.main.activity_mi_restaurante.BotonVerMenu
import kotlinx.android.synthetic.main.activity_mi_restaurante.TextoDescripcionRestaurante
import kotlinx.android.synthetic.main.activity_mi_restaurante.TextoDireccion
import kotlinx.android.synthetic.main.activity_mi_restaurante.imageViewLogo
import kotlinx.android.synthetic.main.activity_mi_restaurante.linearLayoutCategorias
import kotlinx.android.synthetic.main.activity_mi_restaurante.linearLayoutHorarios
import kotlinx.android.synthetic.main.activity_mi_restaurante.textNombreRestaurante
import www.sanju.motiontoast.MotionToast
import java.util.Locale

class MiRestaurante : AppCompatActivity() {

    lateinit var usuario: Usuario
    val db = FirebaseFirestore.getInstance()

    override fun onBackPressed() {
        super.onBackPressed()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mi_restaurante)
        window.statusBarColor = Color.parseColor("#000000")
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        usuario = intent.getParcelableExtra("usuario")!!
        title = usuario.nombre
        textNombreRestaurante.text = usuario.nombreRestaurante

        db.collection("Restaurante").document(usuario.nombreRestaurante).get().addOnCompleteListener { documentTask ->
            if (documentTask.isSuccessful) {
                val document = documentTask.result
                val horariosAtencion = document.get("horarioAtencion") as Map<*, *>
                val ubicacion = document?.get("ubicacion") as? Map<*, *>
                val latitud = (ubicacion?.get("latitude") as? Number)?.toDouble()
                val longitud = (ubicacion?.get("longitude") as? Number)?.toDouble()
                val descripcion = document?.getString("descripcion")
                val ingredientesPrincipal = document.get("categoria.IngredientePrincipal") as List<String>
                val regiones = document.get("categoria.Region") as List<String>
                val tiposPlato = document.get("categoria.TipoPlato") as List<String>
                if (descripcion != ""){
                    TextoDescripcionRestaurante.text = descripcion
                }
                crearHorarioAtencion(horariosAtencion)
                crearCategorias(ingredientesPrincipal,regiones,tiposPlato)
                obtenerDireccion(latitud!!, longitud!!)
                cargarLogo(usuario.nombreRestaurante)
            }
        }
        BotonVerMenu.setOnClickListener {
            val inicio = Intent(this, VerMenu:: class.java).apply {
                putExtra("usuario", usuario)
            }
            startActivity(inicio)
        }


    }

    private fun cargarLogo(nombreRestaurante: String) {
        val storageRef = FirebaseStorage.getInstance().reference
        val imageRef = storageRef.child("Restaurante/$nombreRestaurante")
        imageRef.downloadUrl.addOnSuccessListener { uri ->
            val imageUrl = uri.toString()
            Glide.with(this)
                .load(imageUrl)
                .circleCrop()
                .into(imageViewLogo)
        }
    }

    private fun obtenerDireccion(latitud: Double, longitud: Double) {
        val geocoder = Geocoder(this, Locale.getDefault())
        Thread {
            try {
                val direcciones = geocoder.getFromLocation(latitud, longitud, 1)
                if (direcciones!!.isNotEmpty()) {
                    val direccion = direcciones[0]
                    val direccionCompleta = StringBuilder()

                    for (i in 0..direccion.maxAddressLineIndex) {
                        direccionCompleta.append(direccion.getAddressLine(i)).append("\n")
                    }
                    runOnUiThread {
                        TextoDireccion.text = direccionCompleta.toString()
                    }
                } else {
                    runOnUiThread {
                        MotionToast.createToast(
                            this, "Operación Fallida", "No se encontró dirección", MotionToast.TOAST_ERROR,
                            MotionToast.GRAVITY_BOTTOM, MotionToast.LONG_DURATION, null
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    MotionToast.createToast(
                        this, "Operación Fallida", "Error al obtener la dirección", MotionToast.TOAST_ERROR,
                        MotionToast.GRAVITY_BOTTOM, MotionToast.LONG_DURATION, null
                    )
                }
            }
        }.start()
    }

    private fun crearCategorias(
        ingredientesPrincipal: List<String>,
        regiones: List<String>,
        tiposPlato: List<String>
    ) {
        val categoriaLayout = LinearLayout(this)
        categoriaLayout.orientation = LinearLayout.VERTICAL
        categoriaLayout.setPadding(75, -20, 8, 8)

        val ingredienteTextView = TextView(this).apply {
            text = "Ingrediente Principal: "
            textSize = 20f
            setTypeface(null, Typeface.BOLD)
        }
        categoriaLayout.addView(ingredienteTextView)
        for (ingrediente in ingredientesPrincipal) {

            val textView = TextView(this).apply {
                text =  "- $ingrediente"
                textSize = 18f
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            }
            categoriaLayout.addView(textView)
        }
        val regionTextView = TextView(this).apply {
            text = "Region: "
            textSize = 20f
            setTypeface(null, Typeface.BOLD)
        }
        categoriaLayout.addView(regionTextView)
        for (region in regiones) {

            val textView = TextView(this).apply {
                text = "- $region"
                textSize = 18f
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            }
            categoriaLayout.addView(textView)
        }
        val tipoTextView = TextView(this).apply {
            text = "Tipo de Plato: "
            textSize = 20f
            setTypeface(null, Typeface.BOLD)
        }
        categoriaLayout.addView(tipoTextView)
        for (tipo in tiposPlato) {

            val textView = TextView(this).apply {
                text =  "- $tipo"
                textSize = 18f
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            }

            categoriaLayout.addView(textView)
        }
        linearLayoutCategorias.addView(categoriaLayout)
    }

    private fun crearHorarioAtencion(horariosAtencion: Map<*, *>) {
        linearLayoutHorarios.removeAllViews()
        val diasDeLaSemana = listOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo")
        for (dia in diasDeLaSemana) {
            val horarios = horariosAtencion[dia] as? Map<*, *>
            if (horarios != null) {
                val abrir = horarios["abrir"]
                val cerrar = horarios["cerrar"]
                if (abrir != null && cerrar != null) {
                    val diaLayout = LinearLayout(this)
                    diaLayout.orientation = LinearLayout.VERTICAL
                    diaLayout.setPadding(75, -20, 8, 8)
                    val diaTextView = TextView(this).apply {
                        text = dia
                        textSize = 20f
                        setTypeface(null, Typeface.BOLD)
                    }
                    val abrirTextView = TextView(this).apply {
                        text = "Desde: $abrir"
                        textSize = 18f
                    }

                    val cerrarTextView = TextView(this).apply {
                        text = "Hasta: $cerrar"
                        textSize = 18f
                    }
                    diaLayout.addView(diaTextView)
                    diaLayout.addView(abrirTextView)
                    diaLayout.addView(cerrarTextView)
                    linearLayoutHorarios.addView(diaLayout)
                }
            }
        }
    }


}
