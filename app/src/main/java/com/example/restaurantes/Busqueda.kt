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
    //CHeck box del filtrador
    //Region
    private lateinit var checkBoxItaliana: CheckBox
    private lateinit var checkBoxMexicana: CheckBox
    private lateinit var checkBoxAsiatica: CheckBox
    private lateinit var checkBoxColombiana: CheckBox
    private lateinit var checkBoxBoliviana: CheckBox
    private lateinit var checkBoxOtros: CheckBox
    //Tipo de servicio
    private lateinit var checkBoxDomicilio: CheckBox
    private lateinit var checkBoxParaLlevar: CheckBox
    private lateinit var checkBoxBuffetLibre: CheckBox
    private lateinit var checkBoxComedorInterno: CheckBox
    //Tipo de servicio


    private var checkBoxes: List<CheckBox> = emptyList()
    private var serviciosSeleccionados = ArrayList<String>()
    private var regionesSeleccionadas = ArrayList<String>()


    private lateinit var usuario: Usuario

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_busqueda)
        //inicializar los check
        usuario = intent.getParcelableExtra("usuario")!!
        checkBoxItaliana = findViewById(R.id.checkBoxItaliana)
        checkBoxMexicana = findViewById(R.id.checkBoxMexicana)
        checkBoxAsiatica = findViewById(R.id.checkBoxAsiatica)
        checkBoxColombiana = findViewById(R.id.checkBoxColombiana)
        checkBoxBoliviana = findViewById(R.id.checkBoxBoliviana)
        checkBoxOtros = findViewById(R.id.checkBoxOtros)

        checkBoxDomicilio = findViewById(R.id.checkBoxDomicilio)
        checkBoxParaLlevar = findViewById(R.id.checkBoxParaLlevar)
        checkBoxBuffetLibre = findViewById(R.id.checkBoxBuffetLibre)
        checkBoxComedorInterno = findViewById(R.id.checkBoxComedorInterno)


        checkBoxes = listOf(
            checkBoxItaliana, checkBoxMexicana, checkBoxAsiatica, checkBoxColombiana, checkBoxBoliviana, checkBoxOtros,
            checkBoxDomicilio ,checkBoxParaLlevar ,checkBoxBuffetLibre ,checkBoxComedorInterno
        )

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
        TextBuscador.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val textoBusqueda = s.toString()
                aplicarFiltros(textoBusqueda)
            }
        })
        checkBoxes.forEach { checkBox ->
            checkBox.setOnCheckedChangeListener { _, _ ->
                aplicarFiltros(TextBuscador.text.toString())
            }
        }
        //checkBoxItaliana.setOnCheckedChangeListener { _, _ -> aplicarFiltros(TextBuscador.text.toString()) }
    }

    private fun aplicarFiltros(textoBusqueda: String) {
        serviciosSeleccionados.clear()
        regionesSeleccionadas.clear()
        if (checkBoxItaliana.isChecked) regionesSeleccionadas.add("Italiana")
        if (checkBoxMexicana.isChecked) regionesSeleccionadas.add("Mexicana")
        if (checkBoxAsiatica.isChecked) regionesSeleccionadas.add("Asiatica")
        if (checkBoxColombiana.isChecked) regionesSeleccionadas.add("Colombiana")
        if (checkBoxBoliviana.isChecked) regionesSeleccionadas.add("Boliviana")
        if (checkBoxOtros.isChecked) regionesSeleccionadas.add("Otros")

        if (checkBoxDomicilio.isChecked) serviciosSeleccionados.add("Domicilio")
        if (checkBoxParaLlevar.isChecked) serviciosSeleccionados.add("Para llevar")
        if (checkBoxBuffetLibre.isChecked) serviciosSeleccionados.add("Buffet Libre")
        if (checkBoxComedorInterno.isChecked) serviciosSeleccionados.add("Comeedor Interno")
        listAdapter.filtrar(textoBusqueda, serviciosSeleccionados, regionesSeleccionadas)
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
                        var region = restaurante.get("categoria.Region") as java.util.ArrayList<String>?
                        var ingrediente = restaurante.get("categoria.IngredientePrincipal")  as java.util.ArrayList<String>?
                        var tipoPlato = restaurante.get("categoria.TipoPlato")  as java.util.ArrayList<String>?
                        val listaCategorias = ArrayList<Categoria>()

                        var catRegion = Categoria("Region", region)
                        var catIngrediente = Categoria("IngredientePrincipal", ingrediente)
                        var catTipoPlato = Categoria("TipoPlato", region)

                        listaCategorias.add(catRegion)
                        listaCategorias.add(catIngrediente)
                        listaCategorias.add(catTipoPlato)

                        val storageRef = FirebaseStorage.getInstance().reference
                        val imageRef = storageRef.child("Restaurante/$nombreRestaurante")
                        imageRef.downloadUrl.addOnSuccessListener { uri ->
                            val imageUrl = uri.toString()
                            val restaurante = Restaurante().apply {
                                this.nombreRestaurante = nombreRestaurante
                                this.logo = uri
                                this.dirLogo = imageUrl
                                this.categoria = listaCategorias
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
