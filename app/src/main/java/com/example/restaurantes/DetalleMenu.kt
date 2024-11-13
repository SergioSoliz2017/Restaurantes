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
import com.google.firebase.firestore.FieldValue
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
import www.sanju.motiontoast.MotionToast

class DetalleMenu : AppCompatActivity() {

    lateinit var menu: Menu
    val db = FirebaseFirestore.getInstance()
    lateinit var usuario: Usuario

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle_menu)
        window.statusBarColor = Color.parseColor("#000000")
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        menu = intent.getParcelableExtra("menu")!!
        usuario = intent.getParcelableExtra("usuario")!!
        cargarDatos()
        agregarIngredientes()
        SubirFotoPlatoEditar.setOnClickListener {
            abrirGaleria()
        }
        BotonGuardarPlatoEditar.setOnClickListener {
            if (uri!= null){
                storageReference = FirebaseStorage.getInstance().getReference("Menu/${usuario.nombreRestaurante}/${textNombrePlatoEditar.text.toString()}")
                storageReference.putFile((uri) as Uri).addOnSuccessListener { snapshot ->
                    val uriTask: Task<Uri> = snapshot.getStorage().getDownloadUrl()
                    uriTask.addOnSuccessListener { uri ->
                        obtenerIngredientes()
                        val nombreOriginalPlato =menu.nombrePlato
                        val menu = Menu (uri.toString(),textNombrePlatoEditar.text.toString(),textPrecioPlatoEditar.text.toString(),listaIngredientes)
                        db.collection("Menu").document(usuario.nombreRestaurante).update(mapOf(nombreOriginalPlato to FieldValue.delete()))
                            .addOnSuccessListener {
                                db.collection("Menu").document(usuario.nombreRestaurante).update( hashMapOf(
                                    menu.nombrePlato to hashMapOf(
                                        "Precio" to menu.precio ,
                                        "FotoPlato" to menu.fotoPlato,
                                        "Ingredientes" to menu.ingredientes,
                                    )
                                ) as Map<String, Any>)
                                    .addOnSuccessListener {
                                        MotionToast.createToast(
                                            this, "Operación Exitosa", "Plato actualizado exitosamente",
                                            MotionToast.TOAST_SUCCESS,
                                            MotionToast.GRAVITY_BOTTOM,
                                            MotionToast.LONG_DURATION, null
                                        )
                                        val verMenu = Intent(this, VerMenu::class.java).apply {
                                            putExtra("usuario",usuario)
                                        }
                                        startActivity(verMenu)
                                        finish()
                                    }
                            }
                    }
                }
            }else{
                obtenerIngredientes()
                val nombreOriginalPlato =menu.nombrePlato
                val menu = Menu (menu.fotoPlato,textNombrePlatoEditar.text.toString(),textPrecioPlatoEditar.text.toString(),listaIngredientes)
                db.collection("Menu").document(usuario.nombreRestaurante).update(mapOf(nombreOriginalPlato to FieldValue.delete()))
                    .addOnSuccessListener {
                        db.collection("Menu").document(usuario.nombreRestaurante).update( hashMapOf(
                            menu.nombrePlato to hashMapOf(
                            "Precio" to menu.precio ,
                            "FotoPlato" to menu.fotoPlato,
                            "Ingredientes" to menu.ingredientes,
                            )
                        ) as Map<String, Any>)
                        .addOnSuccessListener {
                            MotionToast.createToast(
                                this, "Operación Exitosa", "Plato actualizado exitosamente",
                                MotionToast.TOAST_SUCCESS,
                                MotionToast.GRAVITY_BOTTOM,
                                MotionToast.LONG_DURATION, null
                            )
                            val verMenu = Intent(this, VerMenu::class.java).apply {
                                putExtra("usuario",usuario)
                            }
                            startActivity(verMenu)
                            finish()
                        }
                }
            }

        }
    }
    private var PICK_IMAGE_REQUEST = 1
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
                        .into(ImagenPlatoEditar)
                }
            }
        }
    }
    val listaIngredientes = ArrayList<String>()

    fun obtenerIngredientes() {

        for (i in 0 until ListaIngredientesEdit.childCount) {
            val childView = ListaIngredientesEdit.getChildAt(i)

            if (childView is LinearLayout) {
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
                textSize = 20f
                inputType = InputType.TYPE_CLASS_TEXT
            }
            nuevoLayout.addView(nuevoEditText)
            ListaIngredientesEdit.addView(nuevoLayout)
        }
    }

    fun llenarIngredientes(ingredientes: ArrayList<String>) {
        ListaIngredientesEdit.removeAllViews()
        for (ingrediente in ingredientes) {
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
                textSize = 20f
                inputType = InputType.TYPE_CLASS_TEXT
                setText(ingrediente)
            }

            nuevoLayout.addView(nuevoEditText)
            ListaIngredientesEdit.addView(nuevoLayout)
        }
    }
    fun Int.dpToPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()

}