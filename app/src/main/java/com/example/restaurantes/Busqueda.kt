package com.example.restaurantes

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_busqueda.TextBuscador
import kotlinx.android.synthetic.main.activity_busqueda.recyclerRestaurantes

class Busqueda : AppCompatActivity() {

    private val listaRestaurantes = ArrayList<Restaurante>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_busqueda)
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
                        //aca obtienes valores
                        imageRef.downloadUrl.addOnSuccessListener { uri ->
                            val imageUrl = uri.toString()
                            val restaurante = Restaurante().apply {
                                this.nombreRestaurante = nombreRestaurante
                                this.dirLogo = imageUrl
                                this.servicios = servicios as java.util.ArrayList<String>?
                                //luego aca agregas el valor que sacaste
                            }
                            if (restaurante.servicios != null ){
                                Toast.makeText(this, "hay", Toast.LENGTH_SHORT).show()

                            }else{
                                Toast.makeText(this, "no hay", Toast.LENGTH_SHORT).show()

                            }
                            listaRestaurantes.add(restaurante)
                            listAdapter.notifyDataSetChanged()
                        }
                    }
                }
            }
    }

    private fun moveToDescription(item: Restaurante?) {
        //aca es para ir detalle restaurante a su actividad o aca tambien puedes acceder a toda la informacion del restaurante
    }
}
