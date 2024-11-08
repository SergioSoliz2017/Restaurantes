package com.example.restaurantes

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_detalle_menu.textNombrePlatoEditar
import kotlinx.android.synthetic.main.activity_detalle_menu.textPrecioPlatoEditar
import kotlinx.android.synthetic.main.activity_mi_restaurante.BotonGuardarMiRestaurante
import kotlinx.android.synthetic.main.activity_mi_restaurante.BotonVerMenu
import kotlinx.android.synthetic.main.activity_mi_restaurante.LayoutEditarMiRestaurante
import kotlinx.android.synthetic.main.activity_mi_restaurante.LayoutMiRestaurante
import kotlinx.android.synthetic.main.activity_mi_restaurante.TextoDescripcionRestaurante
import kotlinx.android.synthetic.main.activity_mi_restaurante.TextoDescripcionRestauranteEdit
import kotlinx.android.synthetic.main.activity_mi_restaurante.TextoDireccion
import kotlinx.android.synthetic.main.activity_mi_restaurante.botonEditarMiRestaurante
import kotlinx.android.synthetic.main.activity_mi_restaurante.imageViewLogo
import kotlinx.android.synthetic.main.activity_mi_restaurante.imageViewLogoEdit
import kotlinx.android.synthetic.main.activity_mi_restaurante.linearLayoutCategorias
import kotlinx.android.synthetic.main.activity_mi_restaurante.linearLayoutHorarios
import kotlinx.android.synthetic.main.activity_mi_restaurante.linearLayoutServicios
import kotlinx.android.synthetic.main.activity_mi_restaurante.subirFotoRestauranteEdit
import kotlinx.android.synthetic.main.activity_mi_restaurante.textNombreRestaurante
import kotlinx.android.synthetic.main.activity_mi_restaurante.textNombreRestauranteEdit
import kotlinx.android.synthetic.main.activity_ver_menu.ImagenPlato
import www.sanju.motiontoast.MotionToast
import java.util.Locale

class MiRestaurante : AppCompatActivity() {

    lateinit var usuario: Usuario
    lateinit var restaurante: Restaurante
    val db = FirebaseFirestore.getInstance()

    override fun onBackPressed() {
        if (botonEditarMiRestaurante.visibility == View.INVISIBLE && usuario.tieneRestaurante){
            LayoutMiRestaurante.visibility = View.VISIBLE
            LayoutEditarMiRestaurante.visibility = View.GONE
            botonEditarMiRestaurante.visibility = View.VISIBLE
        }else{
            super.onBackPressed()
        }
    }
    lateinit var descripcion : String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mi_restaurante)
        window.statusBarColor = Color.parseColor("#000000")
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        usuario = intent.getParcelableExtra("usuario")!!
        restaurante =intent.getParcelableExtra("restaurante")!!
        title = usuario.nombre
        val esRestauranteVacio = restaurante.nombreRestaurante.isNullOrEmpty()
        var documento = ""
        if (usuario.tieneRestaurante){
            documento = usuario.nombreRestaurante
            textNombreRestaurante.text = usuario.nombreRestaurante
            cargarLogo(usuario.nombreRestaurante)

        }else{
            documento = restaurante.nombreRestaurante
            botonEditarMiRestaurante.visibility = View.INVISIBLE
            textNombreRestaurante.text = restaurante.nombreRestaurante
            cargarLogo(restaurante.nombreRestaurante)
        }
            db.collection("Restaurante").document(documento).get().addOnCompleteListener { documentTask ->
                if (documentTask.isSuccessful) {
                    val document = documentTask.result
                    val horariosAtencion = document.get("horarioAtencion") as Map<*, *>
                    val ubicacion = document?.get("ubicacion") as? Map<*, *>
                    val latitud = (ubicacion?.get("latitude") as? Number)?.toDouble()
                    val longitud = (ubicacion?.get("longitude") as? Number)?.toDouble()
                    descripcion = document?.getString("descripcion").toString()
                    val ingredientesPrincipal = document.get("categoria.IngredientePrincipal") as List<String>
                    val regiones = document.get("categoria.Region") as List<String>
                    val tiposPlato = document.get("categoria.TipoPlato") as List<String>
                    if (descripcion != ""){
                        TextoDescripcionRestaurante.text = descripcion
                    }
                    crearHorarioAtencion(horariosAtencion)
                    crearCategorias(ingredientesPrincipal,regiones,tiposPlato)
                    obtenerDireccion(latitud!!, longitud!!)
                    crearServicios(document.get("servicios") as List<String>)
                }
            }

        BotonVerMenu.setOnClickListener {
            val inicio = Intent(this, VerMenu:: class.java).apply {
                putExtra("usuario", usuario)
                putExtra("restaurante", restaurante)
            }
            startActivity(inicio)
        }
        botonEditarMiRestaurante.setOnClickListener {
            LayoutMiRestaurante.visibility = View.GONE
            LayoutEditarMiRestaurante.visibility = View.VISIBLE
            botonEditarMiRestaurante.visibility = View.INVISIBLE
            textNombreRestauranteEdit.text = usuario.nombreRestaurante
            TextoDescripcionRestauranteEdit.setText(descripcion)

        }
        BotonGuardarMiRestaurante.setOnClickListener {
            if (uri != null){
                storageReference = FirebaseStorage.getInstance().getReference("Restaurante/${usuario.nombreRestaurante}")
                storageReference.putFile((uri) as Uri).addOnSuccessListener { snapshot ->
                    val uriTask: Task<Uri> = snapshot.getStorage().getDownloadUrl()
                    uriTask.addOnSuccessListener { uri ->
                        db.collection("Restaurante").document(usuario.nombreRestaurante).update(
                            mapOf(
                                "descripcion" to TextoDescripcionRestauranteEdit.text.toString(),
                                "logo" to uri.toString()
                            )).addOnCompleteListener {
                            LayoutMiRestaurante.visibility = View.VISIBLE
                            LayoutEditarMiRestaurante.visibility = View.GONE
                            botonEditarMiRestaurante.visibility = View.VISIBLE
                            TextoDescripcionRestaurante.text = TextoDescripcionRestauranteEdit.text.toString()
                            Glide.with(this)
                                .load(uri)
                                .circleCrop()
                                .into(imageViewLogo)
                            MotionToast.createToast(
                                this, "Operación Exitosa", "Se guardaron los datos correctamente", MotionToast.TOAST_SUCCESS,
                                MotionToast.GRAVITY_BOTTOM, MotionToast.LONG_DURATION, null
                            )
                        }
                    }
                }
            }else{
                db.collection("Restaurante").document(usuario.nombreRestaurante).update(
                    mapOf(
                        "descripcion" to TextoDescripcionRestauranteEdit.text.toString()
                    )).addOnCompleteListener {
                    LayoutMiRestaurante.visibility = View.VISIBLE
                    LayoutEditarMiRestaurante.visibility = View.GONE
                    botonEditarMiRestaurante.visibility = View.VISIBLE
                    TextoDescripcionRestaurante.text = TextoDescripcionRestauranteEdit.text.toString()
                    MotionToast.createToast(
                        this, "Operación Exitosa", "Se guardaron los datos correctamente", MotionToast.TOAST_SUCCESS,
                        MotionToast.GRAVITY_BOTTOM, MotionToast.LONG_DURATION, null
                    )
                }
            }

        }
        subirFotoRestauranteEdit.setOnClickListener {
            abrirGaleria()
        }
    }

    private fun convertirHorarioAHashMap(horarioAtencionList: ArrayList<Horario>): Map<String, Map<String, String>> {
        val horarioMap = mutableMapOf<String, Map<String, String>>()

        horarioAtencionList.forEach { horario ->
            val horarioInfo = mapOf(
                "abrir" to horario.Abrir,
                "cerrar" to horario.Cerrar
            )
            horarioMap[horario.Dia] = horarioInfo
        }

        return horarioMap
    }

    private val PICK_IMAGE_REQUEST = 1
    private var uri: Uri? = null
    lateinit var storageReference : StorageReference
    private fun abrirGaleria() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            uri = data.data
            if (uri != null) {
                this?.let {
                    Glide.with(it)
                        .load(uri)
                        .circleCrop()
                        .into(imageViewLogoEdit)
                }
            }
        }
    }
    private fun crearServicios(listaServicios: List<String>?) {
        val servicioLayout = LinearLayout(this)
        servicioLayout.orientation = LinearLayout.VERTICAL
        servicioLayout.setPadding(75, -20, 8, 8)
        for (servicio in listaServicios!!) {
            val textView = TextView(this).apply {
                text =  "- $servicio"
                textSize = 18f
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            }
            servicioLayout.addView(textView)
        }
        linearLayoutServicios.addView(servicioLayout)
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
            Glide.with(this)
                .load(imageUrl)
                .circleCrop()
                .into(imageViewLogoEdit)
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
