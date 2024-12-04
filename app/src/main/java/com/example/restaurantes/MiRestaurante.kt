package com.example.restaurantes

import android.app.Activity
import android.app.TimePickerDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_mi_restaurante.AsiaticaEdit
import kotlinx.android.synthetic.main.activity_mi_restaurante.BebidasEdit
import kotlinx.android.synthetic.main.activity_mi_restaurante.BolivianaEdit
import kotlinx.android.synthetic.main.activity_mi_restaurante.BotonGuardarMiRestaurante
import kotlinx.android.synthetic.main.activity_mi_restaurante.BotonVerMenu
import kotlinx.android.synthetic.main.activity_mi_restaurante.BuffetLibreEdit
import kotlinx.android.synthetic.main.activity_mi_restaurante.CarneCerdoEdit
import kotlinx.android.synthetic.main.activity_mi_restaurante.CarneResEdit
import kotlinx.android.synthetic.main.activity_mi_restaurante.ColombianaEdit
import kotlinx.android.synthetic.main.activity_mi_restaurante.ComedorInternoEdit
import kotlinx.android.synthetic.main.activity_mi_restaurante.ComidaRapidaEdit
import kotlinx.android.synthetic.main.activity_mi_restaurante.DesayunoEdit
import kotlinx.android.synthetic.main.activity_mi_restaurante.DomicilioEdit
import kotlinx.android.synthetic.main.activity_mi_restaurante.EnsaladasEdit
import kotlinx.android.synthetic.main.activity_mi_restaurante.EntranteEdit
import kotlinx.android.synthetic.main.activity_mi_restaurante.ItalianaEdit
import kotlinx.android.synthetic.main.activity_mi_restaurante.LayoutEditarMiRestaurante
import kotlinx.android.synthetic.main.activity_mi_restaurante.LayoutMiRestaurante
import kotlinx.android.synthetic.main.activity_mi_restaurante.LayoutMiRestauranteEscribirReseñas
import kotlinx.android.synthetic.main.activity_mi_restaurante.MariscosEdit
import kotlinx.android.synthetic.main.activity_mi_restaurante.MexicanaEdit
import kotlinx.android.synthetic.main.activity_mi_restaurante.OpcionesDiateticasEdit
import kotlinx.android.synthetic.main.activity_mi_restaurante.OtroEdit
import kotlinx.android.synthetic.main.activity_mi_restaurante.ParaLLevarEdit
import kotlinx.android.synthetic.main.activity_mi_restaurante.PescadoEdit
import kotlinx.android.synthetic.main.activity_mi_restaurante.PlatoPrincipalEdit
import kotlinx.android.synthetic.main.activity_mi_restaurante.PolloEdit
import kotlinx.android.synthetic.main.activity_mi_restaurante.PostresEdit
import kotlinx.android.synthetic.main.activity_mi_restaurante.TextoCelularRestaurante
import kotlinx.android.synthetic.main.activity_mi_restaurante.TextoCelularRestauranteEdit
import kotlinx.android.synthetic.main.activity_mi_restaurante.TextoDescripcionRestaurante
import kotlinx.android.synthetic.main.activity_mi_restaurante.TextoDescripcionRestauranteEdit
import kotlinx.android.synthetic.main.activity_mi_restaurante.TextoDireccion
import kotlinx.android.synthetic.main.activity_mi_restaurante.VerdurasEdit
import kotlinx.android.synthetic.main.activity_mi_restaurante.botonEditarMiRestaurante
import kotlinx.android.synthetic.main.activity_mi_restaurante.botonEnviarComentario
import kotlinx.android.synthetic.main.activity_mi_restaurante.imageViewLogo
import kotlinx.android.synthetic.main.activity_mi_restaurante.imageViewLogoEdit
import kotlinx.android.synthetic.main.activity_mi_restaurante.linearLayoutCategorias
import kotlinx.android.synthetic.main.activity_mi_restaurante.linearLayoutHorarios
import kotlinx.android.synthetic.main.activity_mi_restaurante.linearLayoutHorariosEdit
import kotlinx.android.synthetic.main.activity_mi_restaurante.linearLayoutServicios
import kotlinx.android.synthetic.main.activity_mi_restaurante.listaReseñas
import kotlinx.android.synthetic.main.activity_mi_restaurante.subirFotoRestauranteEdit
import kotlinx.android.synthetic.main.activity_mi_restaurante.textComentarioReseña
import kotlinx.android.synthetic.main.activity_mi_restaurante.textNombreRestaurante
import kotlinx.android.synthetic.main.activity_mi_restaurante.textNombreRestauranteEdit
import www.sanju.motiontoast.MotionToast
import java.util.Calendar
import java.util.Locale

class MiRestaurante : AppCompatActivity(),OnMapReadyCallback {

    private lateinit var servicios: List<String>
    lateinit var usuario: Usuario
    lateinit var restaurante: Restaurante
    val db = FirebaseFirestore.getInstance()
    var documento = ""
    lateinit var map:GoogleMap
    override fun onBackPressed() {
        if (botonEditarMiRestaurante.visibility == View.INVISIBLE && usuario.tieneRestaurante){
            LayoutMiRestaurante.visibility = View.VISIBLE
            LayoutEditarMiRestaurante.visibility = View.GONE
            botonEditarMiRestaurante.visibility = View.VISIBLE
        }else{
            if (usuario.tieneRestaurante){
                val inicio = Intent(this, PantallaPrincipal:: class.java).apply {
                    putExtra("email", usuario.correo)
                }
                startActivity(inicio)
                finish()
            }else{
                super.onBackPressed()
            }
        }
    }
    lateinit var descripcion : String
    lateinit var horariosAtencion : Map<*, *>
    lateinit var ingredientesPrincipal :List<String>
    lateinit var regiones:List<String>
    lateinit var tiposPlato:List<String>
    lateinit var nombreOriginal : String
    lateinit var celular : String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mi_restaurante)
        window.statusBarColor = Color.parseColor("#000000")
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        usuario = intent.getParcelableExtra("usuario")!!
        restaurante =intent.getParcelableExtra("restaurante")!!
        title = usuario.nombre
        if (restaurante != null){
            if (usuario.tieneRestaurante){
                LayoutMiRestauranteEscribirReseñas.visibility = View.GONE
                documento = usuario.nombreRestaurante
                textNombreRestaurante.text = usuario.nombreRestaurante
                cargarLogo(usuario.nombreRestaurante)

            }else{
                documento = restaurante.nombreRestaurante
                botonEditarMiRestaurante.visibility = View.INVISIBLE
                textNombreRestaurante.text = restaurante.nombreRestaurante
                cargarLogo(restaurante.nombreRestaurante)
            }
            cargarDatos()
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
                textNombreRestauranteEdit.setText(usuario.nombreRestaurante)
                nombreOriginal = usuario.nombreRestaurante
                TextoDescripcionRestauranteEdit.setText(descripcion)
                TextoCelularRestauranteEdit.setText(celular)
                crearHorarioAtencionEdit(horariosAtencion)
                crearMapa()
                marcarCategorias()
                marcarServicios()
            }
            BotonGuardarMiRestaurante.setOnClickListener {
                if (nombreOriginal == textNombreRestauranteEdit.text.toString()){
                    modificarBD()
                }else{
                    documento = textNombreRestauranteEdit.text.toString()
                    textNombreRestaurante.text = textNombreRestauranteEdit.text.toString()
                    cambiarNombreRestaurante()
                }
            }
            subirFotoRestauranteEdit.setOnClickListener {
                abrirGaleria()
            }
            botonEnviarComentario.setOnClickListener {
                subirComentario()
            }
        }

    }

    private fun cambiarNombreRestaurante() {
        if (uri != null) {
            db.collection("Restaurante").document(nombreOriginal).delete().addOnCompleteListener {
                val horariosActualizados = obtenerHorarios()
                val categoriasMap: MutableMap<String, Any> = HashMap()
                val listaCategoriaEditada : ArrayList<Categoria> = ArrayList()
                llenarCategorias(listaCategoriaEditada)
                for (categoria in listaCategoriaEditada) {
                    categoriasMap.putAll(categoria.toMap())
                }
                val listaServiciosEditada = obtenerServicios()
                storageReference = FirebaseStorage.getInstance().getReference("Restaurante/${textNombreRestauranteEdit.text.toString()}")
                storageReference.putFile(uri!!).addOnSuccessListener { snapshot ->
                    val uriTask: Task<Uri> = snapshot.storage.downloadUrl
                    uriTask.addOnSuccessListener { uri ->
                        db.collection("Restaurante")
                            .document(textNombreRestauranteEdit.text.toString()).set(
                                mapOf(
                                    "nombreRestaurante" to textNombreRestauranteEdit.text.toString(),
                                    "descripcion" to TextoDescripcionRestauranteEdit.text.toString(),
                                    "logo" to uri.toString(),
                                    "horarioAtencion" to horariosActualizados,
                                    "celularReferencia" to TextoCelularRestauranteEdit.text.toString(),
                                    "categoria" to categoriasMap,
                                    "servicios" to listaServiciosEditada,
                                    "ubicacion" to restaurante.ubicacion,
                                )
                            ).addOnCompleteListener {
                                db.collection("Usuarios").document(usuario.correo).update(
                                    mapOf("Restaurante" to textNombreRestauranteEdit.text.toString())
                                ).addOnCompleteListener {
                                    db.collection("Menu").document(nombreOriginal).get()
                                        .addOnSuccessListener { documentSnapshot ->
                                            if (documentSnapshot.exists()) {
                                                val datosMenu = documentSnapshot.data
                                                db.collection("Menu").document(nombreOriginal)
                                                    .delete()
                                                    .addOnSuccessListener {
                                                        if (datosMenu != null) {
                                                            db.collection("Menu")
                                                                .document(textNombreRestauranteEdit.text.toString())
                                                                .set(datosMenu)
                                                                .addOnSuccessListener {
                                                                    val storage =
                                                                        FirebaseStorage.getInstance()
                                                                    val oldPath =
                                                                        "Restaurante/${nombreOriginal}"
                                                                    val newPath =
                                                                        "Restaurante/${textNombreRestauranteEdit.text.toString()}"
                                                                    val oldRef =
                                                                        storage.reference.child(
                                                                            oldPath
                                                                        )
                                                                    val newRef =
                                                                        storage.reference.child(
                                                                            newPath
                                                                        )
                                                                    oldRef.downloadUrl.addOnSuccessListener { uri ->
                                                                        newRef.putFile(uri)
                                                                            .addOnSuccessListener {
                                                                                oldRef.delete()
                                                                                    .addOnSuccessListener {
                                                                                        LayoutMiRestaurante.visibility =
                                                                                            View.VISIBLE
                                                                                        LayoutEditarMiRestaurante.visibility =
                                                                                            View.GONE
                                                                                        botonEditarMiRestaurante.visibility =
                                                                                            View.VISIBLE
                                                                                        TextoDescripcionRestaurante.text =
                                                                                            TextoDescripcionRestauranteEdit.text.toString()
                                                                                        Glide.with(
                                                                                            this
                                                                                        )
                                                                                            .load(
                                                                                                uri.toString()
                                                                                            )
                                                                                            .circleCrop()
                                                                                            .into(
                                                                                                imageViewLogo
                                                                                            )
                                                                                        cargarDatos()
                                                                                        MotionToast.createToast(
                                                                                            this,
                                                                                            "Operación Exitosa",
                                                                                            "Se guardaron los datos correctamente",
                                                                                            MotionToast.TOAST_SUCCESS,
                                                                                            MotionToast.GRAVITY_BOTTOM,
                                                                                            MotionToast.LONG_DURATION,
                                                                                            null
                                                                                        )
                                                                                    }
                                                                                    .addOnFailureListener { e ->
                                                                                        println("Error al eliminar el archivo original: ${e.message}")
                                                                                    }
                                                                            }
                                                                    }
                                                                }
                                                        }
                                                    }
                                            }else{
                                                LayoutMiRestaurante.visibility =
                                                    View.VISIBLE
                                                LayoutEditarMiRestaurante.visibility =
                                                    View.GONE
                                                botonEditarMiRestaurante.visibility =
                                                    View.VISIBLE
                                                TextoDescripcionRestaurante.text =
                                                    TextoDescripcionRestauranteEdit.text.toString()
                                                Glide.with(
                                                    this
                                                )
                                                    .load(
                                                        uri
                                                    )
                                                    .circleCrop()
                                                    .into(
                                                        imageViewLogo
                                                    )
                                                cargarDatos()
                                                MotionToast.createToast(
                                                    this,
                                                    "Operación Exitosa",
                                                    "Se guardaron los datos correctamente",
                                                    MotionToast.TOAST_SUCCESS,
                                                    MotionToast.GRAVITY_BOTTOM,
                                                    MotionToast.LONG_DURATION,
                                                    null
                                                )
                                            }
                                        }
                                }
                            }
                    }
                }
            }
        } else {
            db.collection("Restaurante").document(nombreOriginal).delete().addOnCompleteListener {
                val horariosActualizados = obtenerHorarios()
                val categoriasMap: MutableMap<String, Any> = HashMap()
                val listaCategoriaEditada: ArrayList<Categoria> = ArrayList()
                llenarCategorias(listaCategoriaEditada)
                for (categoria in listaCategoriaEditada) {
                    categoriasMap.putAll(categoria.toMap())
                }
                val listaServiciosEditada = obtenerServicios()
                val storageReference = FirebaseStorage.getInstance().getReference()
                val oldFileRef = storageReference.child("Restaurante/${nombreOriginal}")
                val newFileRef =
                    storageReference.child("Restaurante/${textNombreRestauranteEdit.text.toString()}")
                oldFileRef.getBytes(Long.MAX_VALUE).addOnSuccessListener { bytes ->
                    newFileRef.putBytes(bytes).addOnSuccessListener {
                        oldFileRef.delete().addOnSuccessListener {
                            db.collection("Restaurante")
                                .document(textNombreRestauranteEdit.text.toString()).set(
                                mapOf(
                                    "nombreRestaurante" to textNombreRestauranteEdit.text.toString(),
                                    "descripcion" to TextoDescripcionRestauranteEdit.text.toString(),
                                    "logo" to uri.toString(),
                                    "horarioAtencion" to horariosActualizados,
                                    "celularReferencia" to TextoCelularRestauranteEdit.text.toString(),
                                    "categoria" to categoriasMap,
                                    "servicios" to listaServiciosEditada,
                                    "ubicacion" to restaurante.ubicacion,
                                )
                            ).addOnCompleteListener {
                                db.collection("Usuarios").document(usuario.correo).update(
                                    mapOf("Restaurante" to textNombreRestauranteEdit.text.toString())
                                ).addOnCompleteListener {
                                    db.collection("Menu").document(nombreOriginal).get()
                                        .addOnSuccessListener { documentSnapshot ->
                                            if (documentSnapshot.exists()) {
                                                val datosMenu = documentSnapshot.data
                                                db.collection("Menu").document(nombreOriginal)
                                                    .delete()
                                                    .addOnSuccessListener {
                                                        if (datosMenu != null) {
                                                            db.collection("Menu")
                                                                .document(textNombreRestauranteEdit.text.toString())
                                                                .set(datosMenu)
                                                                .addOnSuccessListener {
                                                                    val storage =
                                                                        FirebaseStorage.getInstance()
                                                                    val oldPath =
                                                                        "Restaurante/${nombreOriginal}"
                                                                    val newPath =
                                                                        "Restaurante/${textNombreRestauranteEdit.text.toString()}"
                                                                    val oldRef =
                                                                        storage.reference.child(
                                                                            oldPath
                                                                        )
                                                                    val newRef =
                                                                        storage.reference.child(
                                                                            newPath
                                                                        )
                                                                    oldRef.downloadUrl.addOnSuccessListener { uri ->
                                                                        newRef.putFile(uri)
                                                                            .addOnSuccessListener {
                                                                                oldRef.delete()
                                                                                    .addOnSuccessListener {
                                                                                        LayoutMiRestaurante.visibility =
                                                                                            View.VISIBLE
                                                                                        LayoutEditarMiRestaurante.visibility =
                                                                                            View.GONE
                                                                                        botonEditarMiRestaurante.visibility =
                                                                                            View.VISIBLE
                                                                                        TextoDescripcionRestaurante.text =
                                                                                            TextoDescripcionRestauranteEdit.text.toString()
                                                                                        Glide.with(
                                                                                            this
                                                                                        )
                                                                                            .load(
                                                                                                uri.toString()
                                                                                            )
                                                                                            .circleCrop()
                                                                                            .into(
                                                                                                imageViewLogo
                                                                                            )
                                                                                        cargarDatos()
                                                                                        MotionToast.createToast(
                                                                                            this,
                                                                                            "Operación Exitosa",
                                                                                            "Se guardaron los datos correctamente",
                                                                                            MotionToast.TOAST_SUCCESS,
                                                                                            MotionToast.GRAVITY_BOTTOM,
                                                                                            MotionToast.LONG_DURATION,
                                                                                            null
                                                                                        )
                                                                                    }
                                                                                    .addOnFailureListener { e ->
                                                                                        println("Error al eliminar el archivo original: ${e.message}")
                                                                                    }
                                                                            }
                                                                    }
                                                                }
                                                        }
                                                    }
                                            }else{
                                                LayoutMiRestaurante.visibility =
                                                    View.VISIBLE
                                                LayoutEditarMiRestaurante.visibility =
                                                    View.GONE
                                                botonEditarMiRestaurante.visibility =
                                                    View.VISIBLE
                                                TextoDescripcionRestaurante.text =
                                                    TextoDescripcionRestauranteEdit.text.toString()
                                                cargarLogo(textNombreRestauranteEdit.text.toString())
                                                cargarDatos()
                                                MotionToast.createToast(
                                                    this,
                                                    "Operación Exitosa",
                                                    "Se guardaron los datos correctamente",
                                                    MotionToast.TOAST_SUCCESS,
                                                    MotionToast.GRAVITY_BOTTOM,
                                                    MotionToast.LONG_DURATION,
                                                    null
                                                )
                                            }
                                        }
                                }
                            }
                        }

                    }
                }
            }


        }
    }

    private fun modificarBD() {
        val horariosActualizados = obtenerHorarios()
        val categoriasMap: MutableMap<String, Any> = HashMap()
        val listaCategoriaEditada : ArrayList<Categoria> = ArrayList()
        llenarCategorias(listaCategoriaEditada)
        for (categoria in listaCategoriaEditada) {
            categoriasMap.putAll(categoria.toMap())
        }
        val listaServiciosEditada = obtenerServicios()
        if (uri != null){
            storageReference = FirebaseStorage.getInstance().getReference("Restaurante/${usuario.nombreRestaurante}")
            storageReference.putFile((uri) as Uri).addOnSuccessListener { snapshot ->
                val uriTask: Task<Uri> = snapshot.getStorage().getDownloadUrl()

                uriTask.addOnSuccessListener { uri ->

                    db.collection("Restaurante").document(usuario.nombreRestaurante).update(
                        mapOf(
                            "descripcion" to TextoDescripcionRestauranteEdit.text.toString(),
                            "logo" to uri.toString(),
                            "horarioAtencion" to horariosActualizados,
                            "celularReferencia" to TextoCelularRestauranteEdit.text.toString(),
                            "categoria" to categoriasMap,
                            "servicios" to listaServiciosEditada,
                            "ubicacion" to restaurante.ubicacion,

                            )).addOnCompleteListener {
                        LayoutMiRestaurante.visibility = View.VISIBLE
                        LayoutEditarMiRestaurante.visibility = View.GONE
                        botonEditarMiRestaurante.visibility = View.VISIBLE
                        TextoDescripcionRestaurante.text = TextoDescripcionRestauranteEdit.text.toString()
                        Glide.with(this)
                            .load(uri)
                            .circleCrop()
                            .into(imageViewLogo)
                        cargarDatos()
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
                    "descripcion" to TextoDescripcionRestauranteEdit.text.toString(),
                    "horarioAtencion" to horariosActualizados,
                    "celularReferencia" to TextoCelularRestauranteEdit.text.toString(),
                    "categoria" to categoriasMap,
                    "servicios" to listaServiciosEditada,
                    "ubicacion" to restaurante.ubicacion,
                )).addOnCompleteListener {
                LayoutMiRestaurante.visibility = View.VISIBLE
                LayoutEditarMiRestaurante.visibility = View.GONE
                botonEditarMiRestaurante.visibility = View.VISIBLE
                TextoDescripcionRestaurante.text = TextoDescripcionRestauranteEdit.text.toString()
                cargarDatos()
                MotionToast.createToast(
                    this, "Operación Exitosa", "Se guardaron los datos correctamente", MotionToast.TOAST_SUCCESS,
                    MotionToast.GRAVITY_BOTTOM, MotionToast.LONG_DURATION, null
                )
            }
        }
    }
    lateinit var ubicacion : Map<*, *>
    var latitud = 0.0
    var longitud = 0.0
    private fun cargarDatos() {
        db.collection("Restaurante").document(documento).get().addOnCompleteListener { documentTask ->
            if (documentTask.isSuccessful) {
                val document = documentTask.result
                horariosAtencion = document.get("horarioAtencion") as Map<*, *>
                ubicacion = (document?.get("ubicacion") as? Map<*, *>)!!
                latitud = (ubicacion.get("latitude") as Number).toDouble()
                longitud = (ubicacion.get("longitude") as Number).toDouble()
                descripcion = document?.getString("descripcion").toString()
                ingredientesPrincipal = document.get("categoria.IngredientePrincipal") as List<String>
                regiones = document.get("categoria.Region") as List<String>
                tiposPlato = document.get("categoria.TipoPlato") as List<String>
                servicios = document.get("servicios") as List<String>
                celular = document.get("celularReferencia").toString()
                if (descripcion != ""){
                    TextoDescripcionRestaurante.text = descripcion
                }
                usuario.nombreRestaurante = document?.get("nombreRestaurante").toString()
                TextoCelularRestaurante.text = celular
                crearHorarioAtencion(horariosAtencion)
                crearCategorias(ingredientesPrincipal,regiones,tiposPlato)
                obtenerDireccion(latitud!!, longitud!!)
                crearServicios(servicios)
                obtenerReseñas()
            }
        }
    }

    private fun crearMapa (){
        val mapFragment = supportFragmentManager.findFragmentById(R.id.MapaEdit) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        crearMarcador()
        map.setOnMapClickListener { latLng ->
            cambiarUbicacion(latLng)
        }
    }

    private fun crearMarcador() {
        restaurante.ubicacion = LatLng(
            latitud,
            longitud
        )
        val coordenadas = LatLng(
            latitud,
            longitud
        )

        val marcador = MarkerOptions().position(coordenadas).title("Ubicacion restaurante")
        map.addMarker(marcador)
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(coordenadas,18f),4000,null)
    }
    private fun cambiarUbicacion(nuevasCoordenadas: com.google.android.gms.maps.model.LatLng) {
        map.clear()
        val marcadorNuevo = MarkerOptions().position(nuevasCoordenadas).title("Nueva ubicación restaurante")
        map.addMarker(marcadorNuevo)
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(nuevasCoordenadas, 18f), 4000, null)

        restaurante.ubicacion = nuevasCoordenadas
    }
    private fun obtenerServicios(): ArrayList<String> {
        val listaServicios = ArrayList<String>()
        if(DomicilioEdit.isChecked){
            listaServicios.add("Domicilio")
        }
        if(ParaLLevarEdit.isChecked){
            listaServicios.add("Para llevar")
        }
        if(ComedorInternoEdit.isChecked){
            listaServicios.add("Comedor interno")
        }
        if(BuffetLibreEdit.isChecked){
            listaServicios.add("Buffet libre")
        }
        return  listaServicios
    }
    private fun llenarCategorias(listaCategoriaEditada: java.util.ArrayList<Categoria>) {
        obtenerRegiones(listaCategoriaEditada)
        obtenerTipoPlato(listaCategoriaEditada)
        obtenerIngrediente(listaCategoriaEditada)
    }
    private fun obtenerRegiones(listaCategoria: ArrayList<Categoria>) {
        val subCategoria = ArrayList<String> ();
        if(ItalianaEdit.isChecked){
            subCategoria.add("Italiana")
        }
        if(MexicanaEdit.isChecked){
            subCategoria.add("Mexicana")
        }
        if(BolivianaEdit.isChecked){
            subCategoria.add("Boliviana")
        }
        if(AsiaticaEdit.isChecked){
            subCategoria.add("Asiatica")
        }
        if(ColombianaEdit.isChecked){
            subCategoria.add("Colombiana")
        }
        if(OtroEdit.isChecked){
            subCategoria.add("Otro")
        }
        val categoria = Categoria ("Region",subCategoria)
        listaCategoria.add(categoria)
    }
    private fun obtenerIngrediente(listaCategoria: ArrayList<Categoria>) {
        val subCategoria = ArrayList<String> ();
        if(PolloEdit.isChecked){
            subCategoria.add("Pollo")
        }
        if(CarneResEdit.isChecked){
            subCategoria.add("Carne de res")
        }
        if(CarneCerdoEdit.isChecked){
            subCategoria.add("Carne de cerdo")
        }
        if(PescadoEdit.isChecked){
            subCategoria.add("Pescado")
        }
        if(VerdurasEdit.isChecked){
            subCategoria.add("Verduras")
        }
        if(MariscosEdit.isChecked){
            subCategoria.add("Mariscos")
        }
        val categoria = Categoria ("IngredientePrincipal",subCategoria)
        listaCategoria.add(categoria)
    }
    private fun obtenerTipoPlato(listaCategoria: ArrayList<Categoria>) {
        val subCategoria = ArrayList<String> ();
        if(DesayunoEdit.isChecked){
            subCategoria.add("Desayuno")
        }
        if(EntranteEdit.isChecked){
            subCategoria.add("Entrante")
        }
        if(PlatoPrincipalEdit.isChecked){
            subCategoria.add("Plato principal")
        }
        if(PostresEdit.isChecked){
            subCategoria.add("Postres")
        }
        if(BebidasEdit.isChecked){
            subCategoria.add("Bebidas")
        }
        if(ComidaRapidaEdit.isChecked){
            subCategoria.add("Comida rapida")
        }
        if(EnsaladasEdit.isChecked){
            subCategoria.add("Ensaladas")
        }
        if(OpcionesDiateticasEdit.isChecked){
            subCategoria.add("Opciones diateticas")
        }
        val categoria = Categoria ("TipoPlato",subCategoria)
        listaCategoria.add(categoria)
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
        linearLayoutServicios.removeAllViews()

        val servicioLayout = LinearLayout(this)
        servicioLayout.orientation = LinearLayout.VERTICAL
        servicioLayout.setPadding(75, -10, 8, 8)

        for (servicio in listaServicios!!) {
            val textView = TextView(this).apply {
                text = "- $servicio"
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
        linearLayoutCategorias.removeAllViews()
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
            text = "Tipo de comida: "
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

    private val horariosGuardados = mutableMapOf<String, MutableMap<String, String>>()

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
    private fun obtenerHorarios(): Map<String, Map<String, String>> {
        return horariosGuardados.filterValues { horario ->
            val abrir = horario["abrir"] ?: ""
            val cerrar = horario["cerrar"] ?: ""
            abrir.isNotEmpty() || cerrar.isNotEmpty()         }
    }

    private fun marcarCategorias() {
        for (ingrediente in ingredientesPrincipal) {
            if (ingrediente == PolloEdit.text.toString()) {
                PolloEdit.isChecked = true
            }
            if (ingrediente == CarneResEdit.text.toString()) {
                CarneResEdit.isChecked = true
            }
            if (ingrediente == PescadoEdit.text.toString()) {
                PescadoEdit.isChecked = true
            }
            if (ingrediente == CarneCerdoEdit.text.toString()) {
                CarneCerdoEdit.isChecked = true
            }
            if (ingrediente == VerdurasEdit.text.toString()) {
                VerdurasEdit.isChecked = true
            }
            if (ingrediente == MariscosEdit.text.toString()) {
                MariscosEdit.isChecked = true
            }
        }

        for (plato in tiposPlato) {
            if (plato == DesayunoEdit.text.toString()) {
                DesayunoEdit.isChecked = true
            }
            if (plato == EntranteEdit.text.toString()) {
                EntranteEdit.isChecked = true
            }
            if (plato == PlatoPrincipalEdit.text.toString()) {
                PlatoPrincipalEdit.isChecked = true
            }
            if (plato == PostresEdit.text.toString()) {
                PostresEdit.isChecked = true
            }
            if (plato == BebidasEdit.text.toString()) {
                BebidasEdit.isChecked = true
            }
            if (plato == ComidaRapidaEdit.text.toString()) {
                ComidaRapidaEdit.isChecked = true
            }
            if (plato == EnsaladasEdit.text.toString()) {
                EnsaladasEdit.isChecked = true
            }
            if (plato == OpcionesDiateticasEdit.text.toString()) {
                OpcionesDiateticasEdit.isChecked = true
            }
        }

        for (region in regiones) {
            if (region == ItalianaEdit.text.toString()) {
                ItalianaEdit.isChecked = true
            }
            if (region == MexicanaEdit.text.toString()) {
                MexicanaEdit.isChecked = true
            }
            if (region == BolivianaEdit.text.toString()) {
                BolivianaEdit.isChecked = true
            }
            if (region == AsiaticaEdit.text.toString()) {
                AsiaticaEdit.isChecked = true
            }
            if (region == ColombianaEdit.text.toString()) {
                ColombianaEdit.isChecked = true
            }
            if (region == OtroEdit.text.toString()) {
                OtroEdit.isChecked = true
            }
        }
    }
    private fun marcarServicios(){
        for (servicio in servicios) {
            if (servicio == DomicilioEdit.text.toString()) {
                DomicilioEdit.isChecked = true
            }
            if (servicio == ParaLLevarEdit.text.toString()) {
                ParaLLevarEdit.isChecked = true
            }
            if (servicio == ComedorInternoEdit.text.toString()) {
                ComedorInternoEdit.isChecked = true
            }
            if (servicio == BuffetLibreEdit.text.toString()) {
                BuffetLibreEdit.isChecked = true
            }
        }
    }
    private fun crearHorarioAtencionEdit(horariosAtencion: Map<*, *>) {
        linearLayoutHorariosEdit.removeAllViews()
        horariosGuardados.clear()

        val diasDeLaSemana = listOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo")
        for (dia in diasDeLaSemana) {
            val horarios = horariosAtencion[dia] as? Map<*, *>

            val abrir = horarios?.get("abrir") as? String ?: ""
            val cerrar = horarios?.get("cerrar") as? String ?: ""

            horariosGuardados[dia] = mutableMapOf("abrir" to abrir, "cerrar" to cerrar)

            val diaLayout = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(75, -20, 8, 8)
            }
            val diaTextView = TextView(this).apply {
                text = dia
                textSize = 20f
                setTypeface(null, Typeface.BOLD)
            }
            val horarioLayout = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
            }

            val abrirEditText = EditText(this).apply {
                layoutParams = LinearLayout.LayoutParams(100.dpToPx(), 40.dpToPx()).apply {
                    marginEnd = 8.dpToPx()
                }
                setBackgroundResource(R.drawable.recuadro)
                isClickable = true
                textAlignment = View.TEXT_ALIGNMENT_CENTER
                textSize = 20f
                inputType = InputType.TYPE_CLASS_DATETIME or InputType.TYPE_DATETIME_VARIATION_TIME
                setEms(10)
                isFocusable = false
                setText(abrir)
                setPadding(0, 1.dpToPx(), 0, 0)
                addTextChangedListener {
                    horariosGuardados[dia]?.set("abrir", it.toString())
                }
            }

            val cerrarEditText = EditText(this).apply {
                layoutParams = LinearLayout.LayoutParams(100.dpToPx(), 40.dpToPx())
                setBackgroundResource(R.drawable.recuadro)
                isClickable = true
                textAlignment = View.TEXT_ALIGNMENT_CENTER
                textSize = 20f
                inputType = InputType.TYPE_CLASS_DATETIME or InputType.TYPE_DATETIME_VARIATION_TIME
                setEms(10)
                isFocusable = false
                setText(cerrar)
                setPadding(0, 1.dpToPx(), 0, 0)
                addTextChangedListener {
                    horariosGuardados[dia]?.set("cerrar", it.toString())
                }
            }

            horarioLayout.addView(abrirEditText)
            horarioLayout.addView(cerrarEditText)

            abrirEditText.setOnClickListener { ClickHora(abrirEditText) }
            cerrarEditText.setOnClickListener { ClickHora(cerrarEditText) }

            diaLayout.addView(diaTextView)
            diaLayout.addView(horarioLayout)
            linearLayoutHorariosEdit.addView(diaLayout)
        }
    }

    private fun ClickHora(edit: EditText) {
        val calendario = Calendar.getInstance()
        val hora = calendario.get(Calendar.HOUR_OF_DAY)
        val minuto = calendario.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(this,
            { _, selectedHour, selectedMinute ->
                edit.setText(String.format("%02d:%02d", selectedHour, selectedMinute))
            }, hora, minuto, true)

        timePickerDialog.show()
    }
    private fun Int.dpToPx(): Int = (this * resources.displayMetrics.density).toInt()
    val listaComentarios = ArrayList<Reseña>()

    private fun subirComentario(){
        db.collection("Reseñas").document().set(hashMapOf(
            "nombreUsuario" to usuario.nombre,
            "nombreRestaurante" to documento,
            "comentario" to textComentarioReseña.text.toString(),
            "correo" to usuario.correo,
            "fotoUsuario" to usuario.fotoPerfil
        )).addOnCompleteListener {
            MotionToast.createToast(
                this, "Operación Exitosa", "Comentario subido", MotionToast.TOAST_SUCCESS,
                MotionToast.GRAVITY_BOTTOM, MotionToast.LONG_DURATION, null
            )
            textComentarioReseña.setText("")
        }
    }

    private fun obtenerReseñas (){
        listaReseñas.removeAllViews()
        db.collection("Reseñas")
            .whereEqualTo("nombreRestaurante", documento)
            .get().addOnCompleteListener { documentTask ->
                if (documentTask.isSuccessful) {
                    listaComentarios.clear()
                    val totalDocuments = documentTask.result.size()
                    var loadedImages = 0

                    for (document in documentTask.result) {
                        val nombreUsuario = document.data["nombreUsuario"] as? String
                        val nombreRestaurante = document.data["nombreRestaurante"] as? String
                        val comentario = document.data["comentario"] as? String
                        val id = document.data["correo"] as? String
                        var fotoUsuario = ""

                        val storageReference = FirebaseStorage.getInstance().reference
                        val imagenReference = storageReference.child("Usuario/${id}")

                        imagenReference.downloadUrl.addOnSuccessListener { uri ->
                            fotoUsuario = uri.toString()
                            val reseña = Reseña(nombreUsuario, nombreRestaurante, id, comentario, fotoUsuario)
                            listaComentarios.add(reseña)

                            loadedImages++
                            if (loadedImages == totalDocuments) {
                                val listAdapter = ReseñaRestauranteAdapter(listaComentarios, this)
                                listaReseñas.setHasFixedSize(true)
                                listaReseñas.layoutManager = LinearLayoutManager(this)
                                listaReseñas.adapter = listAdapter
                            }
                        }.addOnFailureListener {
                            loadedImages++
                            if (loadedImages == totalDocuments) {
                                val listAdapter = ReseñaRestauranteAdapter(listaComentarios, this)
                                listaReseñas.setHasFixedSize(true)
                                listaReseñas.layoutManager = LinearLayoutManager(this)
                                listaReseñas.adapter = listAdapter
                            }
                        }
                    }
                }
            }
    }
}