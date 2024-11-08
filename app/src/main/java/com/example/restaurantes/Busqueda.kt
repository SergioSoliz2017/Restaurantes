package com.example.restaurantes

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.CheckBox
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_busqueda.TextBuscador
import kotlinx.android.synthetic.main.activity_busqueda.recyclerRestaurantes

class Busqueda : AppCompatActivity() {

    private val listaRestaurantes = ArrayList<Restaurante>()
    //mis check box
    private lateinit var checkBoxItaliana: CheckBox
    private lateinit var checkBoxMexicana: CheckBox
    private lateinit var checkBoxDomicilio: CheckBox
    private lateinit var usuario: Usuario

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_busqueda)
        //inicializar os check
        usuario = intent.getParcelableExtra("usuario")!!
        checkBoxItaliana = findViewById(R.id.checkBoxItaliana)
        checkBoxMexicana = findViewById(R.id.checkBoxMexicana)
        checkBoxDomicilio = findViewById(R.id.checkBoxDomicilio)


        window.statusBarColor = Color.parseColor("#000000")
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        listAdapter = RestauranteAdapterFiltro(
            listaRestaurantes,
            this,
            RestauranteAdapterFiltro.OnItemClickListener { item ->
                moveToDescription(item)
            })

        recyclerRestaurantes.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@Busqueda)
            adapter = listAdapter
        }

        obtenerRestaurantesDesdeDB()
        buscar()
    }

    private fun buscar() {

        // si check obtener text del check y listAdapter.filtrar(textoBusqueda)
        /*if (pollo.isCkeck){
            listAdapter.filtrar(pollo.text.toString)
        }*/
        TextBuscador.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val textoBusqueda = s.toString()
                listAdapter.filtrar(textoBusqueda)
            }
        })
        checkBoxItaliana.setOnCheckedChangeListener { _, _ -> aplicarFiltros(TextBuscador.text.toString()) }
        checkBoxMexicana.setOnCheckedChangeListener { _, _ -> aplicarFiltros(TextBuscador.text.toString()) }
        checkBoxDomicilio.setOnCheckedChangeListener { _, _ -> aplicarFiltros(TextBuscador.text.toString()) }
        // Repite esto para cada CheckBox
    }

    private fun aplicarFiltros(textoBusqueda: String) {
        val serviciosSeleccionados = ArrayList<String>()
        if (checkBoxItaliana.isChecked) serviciosSeleccionados.add("Italiana")
        if (checkBoxMexicana.isChecked) serviciosSeleccionados.add("Mexicana")
        if (checkBoxDomicilio.isChecked) serviciosSeleccionados.add("Domicilio")
        // Repite esto para cada CheckBox

        listAdapter.filtrar(textoBusqueda, serviciosSeleccionados)
    }

    lateinit var listAdapter : RestauranteAdapterFiltro

    private fun obtenerRestaurantesDesdeDB() {
        val db = FirebaseFirestore.getInstance()
        db.collection("Restaurante")
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    listaRestaurantes.clear()
                    for (restaurante in document) {
                        val nombreRestaurante = restaurante.data["nombreRestaurante"].toString()
                        var servicios = restaurante.data["servicios"]
                        val storageRef = FirebaseStorage.getInstance().reference
                        val imageRef = storageRef.child("Restaurante/$nombreRestaurante")
                        imageRef.downloadUrl.addOnSuccessListener { uri ->
                            val imageUrl = uri.toString()
                            val restaurante = Restaurante().apply {
                                this.nombreRestaurante = nombreRestaurante
                                this.logo = uri
                                this.dirLogo = imageUrl
                                this.servicios = servicios as java.util.ArrayList<String>?
                            }
                            listaRestaurantes.add(restaurante)
                            listAdapter.notifyDataSetChanged()
                        }
                    }
                }
            }
    }

    private fun moveToDescription(item: Restaurante?) {
        val detalle = Intent(this, MiRestaurante::class.java).apply {
            putExtra("restaurante", item)
            putExtra("usuario",usuario)
        }
        startActivity(detalle)
    }
}
