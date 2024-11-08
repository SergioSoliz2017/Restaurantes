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
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_detalle_menu.ListaIngredientesEdit
import kotlinx.android.synthetic.main.activity_ver_menu.AñadirPlato
import kotlinx.android.synthetic.main.activity_ver_menu.BotonGuardarPlato
import kotlinx.android.synthetic.main.activity_ver_menu.ImagenPlato
import kotlinx.android.synthetic.main.activity_ver_menu.ListaIngredientesText
import kotlinx.android.synthetic.main.activity_ver_menu.ListaMenu
import kotlinx.android.synthetic.main.activity_ver_menu.SubirFotoPlato
import kotlinx.android.synthetic.main.activity_ver_menu.botonAñadirIngrediente
import kotlinx.android.synthetic.main.activity_ver_menu.botonAñadirPlato
import kotlinx.android.synthetic.main.activity_ver_menu.textNombrePlatoAñadir
import kotlinx.android.synthetic.main.activity_ver_menu.textPrecioPlatoAñadir
import www.sanju.motiontoast.MotionToast

class VerMenu : AppCompatActivity() {

    lateinit var usuario: Usuario
    val db = FirebaseFirestore.getInstance()

    override fun onBackPressed() {
        if (botonAñadirPlato.visibility == View.INVISIBLE) {
            botonAñadirPlato.visibility = View.VISIBLE
            ListaMenu.visibility = View.VISIBLE
            AñadirPlato.visibility = View.GONE
            crearMenu()
        } else {
            super.onBackPressed()
        }
    }
val listaMenu = ArrayList<Menu>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ver_menu)
        window.statusBarColor = Color.parseColor("#000000")
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        usuario = intent.getParcelableExtra("usuario")!!
        title = usuario.nombreRestaurante
        crearMenu()
        botonAñadirPlato.setOnClickListener {
            ListaMenu.visibility = View.GONE
            AñadirPlato.visibility = View.VISIBLE
            botonAñadirPlato.visibility = View.INVISIBLE
        }
        AñadirPlatoNuevo()
        agregarIngredientes()
    }

    private val PICK_IMAGE_REQUEST = 1
    private var uri: Uri? = null
    lateinit var storageReference : StorageReference

    private fun AñadirPlatoNuevo() {
        SubirFotoPlato.setOnClickListener {
            abrirGaleria()
        }

        BotonGuardarPlato.setOnClickListener {
            storageReference = FirebaseStorage.getInstance().getReference("Menu/${usuario.nombreRestaurante}/${textNombrePlatoAñadir.text.toString()}")
            storageReference.putFile(uri!!).addOnSuccessListener { snapshot ->
                val uriTask: Task<Uri> = snapshot.getStorage().getDownloadUrl()
                uriTask.addOnSuccessListener { uri ->
                obtenerIngredientes()
                    val menu = Menu (uri.toString(),textNombrePlatoAñadir.text.toString(),textPrecioPlatoAñadir.text.toString(),listaIngredientes)
                    db.collection("Menu").document(usuario.nombreRestaurante).get().addOnSuccessListener { document ->
                        if (document.exists()) {
                            db.collection("Menu").document(usuario.nombreRestaurante).update(menu.nombrePlato, hashMapOf(
                                "Precio" to menu.precio,
                                "FotoPlato" to menu.fotoPlato,
                                "Ingredientes" to menu.ingredientes
                            ))
                        }else{
                            db.collection("Menu").document(usuario.nombreRestaurante).set(hashMapOf(
                                menu.nombrePlato to hashMapOf(
                                    "Precio" to menu.precio ,
                                    "FotoPlato" to menu.fotoPlato,
                                    "Ingredientes" to menu.ingredientes,
                                )
                            ) as Map<String, Any>)
                        }
                    }.addOnSuccessListener {
                        borrar()
                        botonAñadirPlato.visibility = View.VISIBLE
                        ListaMenu.visibility = View.VISIBLE
                        AñadirPlato.visibility = View.GONE
                        MotionToast.createToast(this,"Operacion Exitosa", "Plato añadido exitoso",
                            MotionToast.TOAST_SUCCESS,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.LONG_DURATION,null)
                        crearMenu()
                    }
                }
            }
        }
    }

    private fun borrar() {
        textNombrePlatoAñadir.setText("")
        textPrecioPlatoAñadir.setText("")
        ListaIngredientesText.removeAllViews()
        uri = null;
        Glide.with(this)
            .load(uri)
            .circleCrop()
            .into(ImagenPlato)
    }

    val listaIngredientes = ArrayList<String>()

    fun obtenerIngredientes() {
        for (i in 0 until ListaIngredientesText.childCount) {
            val childView = ListaIngredientesText.getChildAt(i)

            if (childView is LinearLayout) {
                val editText = childView.getChildAt(0) as EditText
                listaIngredientes.add(editText.text.toString())
            }
        }

    }

    private fun agregarIngredientes() {
        botonAñadirIngrediente.setOnClickListener {
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
            ListaIngredientesText.addView(nuevoLayout)
        }
    }

    fun Int.dpToPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            uri = data.data
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

    private fun abrirGaleria() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    private fun moveToDescription(item: Menu) {
        val detalle = Intent(this, DetalleMenu::class.java).apply {
            putExtra("menu", item)
            putExtra("usuario",usuario)
        }
        startActivity(detalle)
        finish()
    }

    private fun crearMenu() {
        val db = FirebaseFirestore.getInstance()
        db.collection("Menu").document(usuario.nombreRestaurante)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.data != null) {
                    listaMenu.clear()
                    for (menu in document.data!!.keys) {
                        val menuData = document.get(menu) as Map<String, Any>
                        val fotoPlato = menuData["FotoPlato"] as String
                        val precio = menuData["Precio"] as String
                        val ingredientes = menuData["Ingredientes"] as ArrayList<String>
                        val menuItem = Menu(fotoPlato, menu, precio, ingredientes)
                        listaMenu.add(menuItem)
                    }
                    val listAdapter : MenuRestauranteAdapter = MenuRestauranteAdapter(listaMenu,this,MenuRestauranteAdapter.OnItemClickListener { item -> moveToDescription(item) })
                    ListaMenu.setHasFixedSize(true)
                    ListaMenu.setLayoutManager(LinearLayoutManager(this))
                    ListaMenu.setAdapter(listAdapter)
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }

    }

}