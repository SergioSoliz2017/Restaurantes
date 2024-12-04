package com.example.restaurantes

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_busqueda.TextBuscador
import kotlinx.android.synthetic.main.activity_busqueda.recyclerRestaurantes
import android.view.View





class Busqueda : AppCompatActivity() {

    private val listaRestaurantes = ArrayList<Restaurante>()

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
    //Tipo de plato
    private lateinit var checkBoxDesayuno: CheckBox
    private lateinit var checkBoxEntrante: CheckBox
    private lateinit var checkBoxPlatoPrincipal: CheckBox
    private lateinit var checkBoxPostres: CheckBox
    private lateinit var checkBoxBebidas: CheckBox
    private lateinit var checkBoxSnack: CheckBox
    private lateinit var checkBoxEnsaladas: CheckBox
    //Ingrediente Pirncipal
    private lateinit var checkBoxPollo: CheckBox
    private lateinit var checkBoxPescado: CheckBox
    private lateinit var checkBoxCarneRes: CheckBox
    private lateinit var checkBoxCarneCerdo: CheckBox
    private lateinit var checkBoxVerduras: CheckBox
    private lateinit var checkBoxMariscos: CheckBox
    private lateinit var checkBoxConejo: CheckBox

    private var checkBoxes: List<CheckBox> = emptyList()
    private var serviciosSeleccionados = ArrayList<String>()
    private var regionesSeleccionadas = ArrayList<String>()
    private var tipoPlatoSeleccionadas = ArrayList<String>()
    private var ingredientePrincipalSeleccionadas = ArrayList<String>()


    private lateinit var usuario: Usuario

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_busqueda)
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

        checkBoxDesayuno = findViewById(R.id.checkBoxDesayuno)
        checkBoxEntrante = findViewById(R.id.checkBoxEntrante)
        checkBoxPlatoPrincipal = findViewById(R.id.checkBoxPlatoPrincipal)
        checkBoxPostres = findViewById(R.id.checkBoxPostres)
        checkBoxBebidas = findViewById(R.id.checkBoxBebidas)
        checkBoxSnack = findViewById(R.id.checkBoxSnack)
        checkBoxEnsaladas = findViewById(R.id.checkBoxEnsaladas)

        checkBoxPollo = findViewById(R.id.checkBoxPollo)
        checkBoxPescado = findViewById(R.id.checkBoxPescado)
        checkBoxCarneRes = findViewById(R.id.checkBoxCarneRes)
        checkBoxCarneCerdo = findViewById(R.id.checkBoxCarneCerdo)
        checkBoxVerduras = findViewById(R.id.checkBoxVerduras)
        checkBoxMariscos = findViewById(R.id.checkBoxMariscos)
        checkBoxConejo = findViewById(R.id.checkBoxConejo)

        checkBoxes = listOf(
            checkBoxItaliana, checkBoxMexicana, checkBoxAsiatica, checkBoxColombiana, checkBoxBoliviana, checkBoxOtros,
            checkBoxDomicilio ,checkBoxParaLlevar ,checkBoxBuffetLibre ,checkBoxComedorInterno,
            checkBoxDesayuno, checkBoxEntrante, checkBoxPlatoPrincipal, checkBoxPostres, checkBoxBebidas, checkBoxSnack, checkBoxEnsaladas,
            checkBoxPollo, checkBoxPescado, checkBoxCarneRes, checkBoxCarneCerdo, checkBoxVerduras, checkBoxMariscos, checkBoxConejo
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
        val buttonTextMap = mapOf(
            R.id.toggleButton1 to "Tipo de Comida ↓",
            R.id.toggleButton2 to "Tipo de Servicios ↓",
            R.id.toggleButton3 to "Tipo de Plato ↓",
            R.id.toggleButton4 to "Ingrediente Principal ↓"
        )


        val checkboxContainers: List<LinearLayout> = listOf(
            findViewById(R.id.checkboxContainer1),
            findViewById(R.id.checkboxContainer2),
            findViewById(R.id.checkboxContainer3),
            findViewById(R.id.checkboxContainer4)
        )


        buttonTextMap.forEach { (buttonId, initialText) ->
            val toggleButton: TextView = findViewById(buttonId)
            toggleButton.text = initialText

            toggleButton.setOnClickListener {

                val checkboxContainer = checkboxContainers[buttonTextMap.keys.indexOf(buttonId)]


                if (checkboxContainer.visibility == View.GONE) {
                    checkboxContainer.visibility = View.VISIBLE
                    toggleButton.text = initialText.replace("↓", "↑")
                } else {
                    checkboxContainer.visibility = View.GONE
                    toggleButton.text = initialText
                }
            }
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
    }

    private fun aplicarFiltros(textoBusqueda: String) {
        serviciosSeleccionados.clear()
        regionesSeleccionadas.clear()
        tipoPlatoSeleccionadas.clear()
        ingredientePrincipalSeleccionadas.clear()

        if (checkBoxItaliana.isChecked) regionesSeleccionadas.add("Italiana")
        if (checkBoxMexicana.isChecked) regionesSeleccionadas.add("Mexicana")
        if (checkBoxAsiatica.isChecked) regionesSeleccionadas.add("Asiatica")
        if (checkBoxColombiana.isChecked) regionesSeleccionadas.add("Colombiana")
        if (checkBoxBoliviana.isChecked) regionesSeleccionadas.add("Boliviana")
        if (checkBoxOtros.isChecked) regionesSeleccionadas.add("Otros")

        if (checkBoxDomicilio.isChecked) serviciosSeleccionados.add("Domicilio")
        if (checkBoxParaLlevar.isChecked) serviciosSeleccionados.add("Para llevar")
        if (checkBoxBuffetLibre.isChecked) serviciosSeleccionados.add("Buffet libre")
        if (checkBoxComedorInterno.isChecked) serviciosSeleccionados.add("Comeedor interno")

        if (checkBoxDesayuno.isChecked) tipoPlatoSeleccionadas.add("Desayuno")
        if (checkBoxEntrante.isChecked) tipoPlatoSeleccionadas.add("Entrante")
        if (checkBoxPlatoPrincipal.isChecked) tipoPlatoSeleccionadas.add("Plato principal")
        if (checkBoxPostres.isChecked) tipoPlatoSeleccionadas.add("Postres")
        if (checkBoxBebidas.isChecked) tipoPlatoSeleccionadas.add("Bebidas")
        if (checkBoxSnack.isChecked) tipoPlatoSeleccionadas.add("Comida rapida")
        if (checkBoxEnsaladas.isChecked) tipoPlatoSeleccionadas.add("Ensaladas")

        if (checkBoxPollo.isChecked) ingredientePrincipalSeleccionadas.add("Pollo")
        if (checkBoxPescado.isChecked) ingredientePrincipalSeleccionadas.add("Pescado")
        if (checkBoxCarneRes.isChecked) ingredientePrincipalSeleccionadas.add("Carne de res")
        if (checkBoxCarneCerdo.isChecked) ingredientePrincipalSeleccionadas.add("Carne de cerdo")
        if (checkBoxVerduras.isChecked) ingredientePrincipalSeleccionadas.add("Verduras")
        if (checkBoxMariscos.isChecked) ingredientePrincipalSeleccionadas.add("Mariscos")
        if (checkBoxConejo.isChecked) ingredientePrincipalSeleccionadas.add("Conejo")

        listAdapter.filtrar(textoBusqueda, serviciosSeleccionados, regionesSeleccionadas, tipoPlatoSeleccionadas, ingredientePrincipalSeleccionadas)
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
                        var plato = restaurante.get("categoria.TipoPlato")  as java.util.ArrayList<String>?
                        val listaCategorias = ArrayList<Categoria>()

                        var catRegion = Categoria("Region", region)
                        var catIngrediente = Categoria("IngredientePrincipal", ingrediente)
                        var catTipoPlato = Categoria("TipoPlato", plato)

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
