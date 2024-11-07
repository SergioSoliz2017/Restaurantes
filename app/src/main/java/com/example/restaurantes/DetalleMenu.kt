package com.example.restaurantes

import android.app.Activity
import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatDelegate
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_detalle_menu.BotonGuardarPlatoEditar
import kotlinx.android.synthetic.main.activity_detalle_menu.ImagenPlatoEditar
import kotlinx.android.synthetic.main.activity_detalle_menu.ListaIngredientesEdit
import kotlinx.android.synthetic.main.activity_detalle_menu.SubirFotoPlatoEditar
import kotlinx.android.synthetic.main.activity_detalle_menu.botonAñadirIngredienteEditar
import kotlinx.android.synthetic.main.activity_detalle_menu.textNombrePlatoEditar
import kotlinx.android.synthetic.main.activity_detalle_menu.textPrecioPlatoEditar
import kotlinx.android.synthetic.main.activity_ver_menu.AñadirPlato
import kotlinx.android.synthetic.main.activity_ver_menu.ImagenPlato
import kotlinx.android.synthetic.main.activity_ver_menu.ListaIngredientesText
import kotlinx.android.synthetic.main.activity_ver_menu.ListaMenu
import kotlinx.android.synthetic.main.activity_ver_menu.SubirFotoPlato
import kotlinx.android.synthetic.main.activity_ver_menu.botonAñadirIngrediente
import kotlinx.android.synthetic.main.activity_ver_menu.botonAñadirPlato
import kotlinx.android.synthetic.main.activity_ver_menu.textNombrePlatoAñadir
import kotlinx.android.synthetic.main.activity_ver_menu.textPrecioPlatoAñadir
import www.sanju.motiontoast.MotionToast

class DetalleMenu : AppCompatActivity() {

    lateinit var menu: Menu
    val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle_menu)
        window.statusBarColor = Color.parseColor("#000000")
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        menu = intent.getParcelableExtra("menu")!!
        val nombreRestaurante = intent.getStringExtra("restaurante")!!

        cargarDatos()
        agregarIngredientes()
        SubirFotoPlatoEditar.setOnClickListener {
            abrirGaleria()
        }
        BotonGuardarPlatoEditar.setOnClickListener {
            if (uri!= null){
                storageReference = FirebaseStorage.getInstance().getReference("Menu/${nombreRestaurante}/${textNombrePlatoEditar.text.toString()}")
                storageReference.putFile((uri) as Uri).addOnSuccessListener { snapshot ->
                    val uriTask: Task<Uri> = snapshot.getStorage().getDownloadUrl()
                    uriTask.addOnSuccessListener { uri ->
                        obtenerIngredientes()
                        val menu = Menu (uri.toString(),textNombrePlatoEditar.text.toString(),textPrecioPlatoEditar.text.toString(),listaIngredientes)
                        db.collection("Menu").document(nombreRestaurante).update(
                            hashMapOf(
                                menu.nombrePlato to hashMapOf(
                                    "Precio" to menu.precio ,
                                    "FotoPlato" to menu.fotoPlato,
                                    "Ingredientes" to menu.ingredientes,
                                )
                            ) as Map<String, Any>
                        ).addOnSuccessListener {

                            botonAñadirPlato.visibility = View.VISIBLE
                            ListaMenu.visibility = View.VISIBLE
                            AñadirPlato.visibility = View.GONE
                            MotionToast.createToast(this,"Operacion Exitosa", "Plato añadido exitoso",
                                MotionToast.TOAST_SUCCESS,
                                MotionToast.GRAVITY_BOTTOM,
                                MotionToast.LONG_DURATION,null)

                        }
                    }
                }
            }else{
                obtenerIngredientes()
                val menu = Menu (menu.fotoPlato,textNombrePlatoEditar.text.toString(),textPrecioPlatoEditar.text.toString(),listaIngredientes)
                db.collection("Menu").document(nombreRestaurante).update(
                    hashMapOf(
                        menu.nombrePlato to hashMapOf(
                            "Precio" to menu.precio ,
                            "FotoPlato" to menu.fotoPlato,
                            "Ingredientes" to menu.ingredientes,
                        )
                    ) as Map<String, Any>
                ).addOnSuccessListener {

                    MotionToast.createToast(this,"Operacion Exitosa", "Plato añadido exitoso",
                        MotionToast.TOAST_SUCCESS,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.LONG_DURATION,null)

                }
            }

        }
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
            uri = data.data // Obtiene la URI de la imagen seleccionada
            if (uri != null) {
                this?.let {
                    Glide.with(it)
                        .load(uri)
                        .circleCrop()
                        .into(ImagenPlato)
                }
            }
        }
    }
    val listaIngredientes = ArrayList<String>()

    fun obtenerIngredientes() {

        for (i in 0 until ListaIngredientesEdit.childCount) {
            val childView = ListaIngredientesEdit.getChildAt(i)

            // Verificar si el hijo es un LinearLayout (es donde están los EditText dinámicos)
            if (childView is LinearLayout) {
                // Obtener el primer hijo dentro del LinearLayout, que debería ser un EditText
                val editText = childView.getChildAt(0) as EditText
                listaIngredientes.add(editText.text.toString())
            }
        }

    }
    private fun cargarDatos() {
        Glide.with(this)
            .load(menu.fotoPlato)
            .circleCrop()
            .into(ImagenPlatoEditar)
        textNombrePlatoEditar.setText(menu.nombrePlato)
        textPrecioPlatoEditar.setText(menu.precio)
        llenarIngredientes(menu.ingredientes)
    }

    private fun agregarIngredientes() {
        botonAñadirIngredienteEditar.setOnClickListener {
            val nuevoLayout = LinearLayout(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(0, 10.dpToPx(), 0, 0)
                }
                orientation = LinearLayout.HORIZONTAL
            }
            val nuevoEditText = EditText(this).apply {
                id = View.generateViewId()
                layoutParams = LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    1f
                ).apply {
                    marginStart = 40.dpToPx()
                    marginEnd = 40.dpToPx()
                }
                setBackgroundResource(R.drawable.recuadro)
                setPadding(17.dpToPx(), 0, 0, 0)
                textSize = 20f  // Tamaño de texto explícito
                inputType = InputType.TYPE_CLASS_TEXT
            }
            nuevoLayout.addView(nuevoEditText)
            ListaIngredientesEdit.addView(nuevoLayout)
        }
    }

    fun llenarIngredientes(ingredientes: ArrayList<String>) {
        // Limpiamos el contenedor para evitar duplicados
        ListaIngredientesEdit.removeAllViews()

        // Iteramos sobre la lista de ingredientes
        for (ingrediente in ingredientes) {
            // Crear un nuevo LinearLayout para el conjunto EditText + botón
            val nuevoLayout = LinearLayout(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(0, 10.dpToPx(), 0, 0)  // Margen superior de 10dp
                }
                orientation = LinearLayout.HORIZONTAL
            }

            // Crear el EditText y establecer el texto del ingrediente
            val nuevoEditText = EditText(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    0,  // Width en 0 para que tome el peso
                    LinearLayout.LayoutParams.MATCH_PARENT,  // Altura que coincida con el padre
                    1f  // Peso para ocupar el espacio disponible
                ).apply {
                    marginStart = 40.dpToPx()
                    marginEnd = 40.dpToPx()
                }
                setBackgroundResource(R.drawable.recuadro)
                setPadding(17.dpToPx(), 0, 0, 0)
                textSize = 20f  // Tamaño de texto explícito
                inputType = InputType.TYPE_CLASS_TEXT
                setText(ingrediente)  // Establece el texto del ingrediente
            }

            // Crear el FloatingActionButton (puedes añadir lógica de eliminar si quieres)


            nuevoLayout.addView(nuevoEditText)

            // Añadir el nuevo LinearLayout al contenedor principal
            ListaIngredientesEdit.addView(nuevoLayout)
        }
    }
    fun Int.dpToPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()

}