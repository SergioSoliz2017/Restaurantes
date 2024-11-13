package com.example.restaurantes

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_registro_restaurante_datos2.Adelante2
import kotlinx.android.synthetic.main.fragment_registro_restaurante_datos2.Asiatica
import kotlinx.android.synthetic.main.fragment_registro_restaurante_datos2.Bebidas
import kotlinx.android.synthetic.main.fragment_registro_restaurante_datos2.Boliviana
import kotlinx.android.synthetic.main.fragment_registro_restaurante_datos2.BuffetLibre
import kotlinx.android.synthetic.main.fragment_registro_restaurante_datos2.CarneCerdo
import kotlinx.android.synthetic.main.fragment_registro_restaurante_datos2.CarneRes
import kotlinx.android.synthetic.main.fragment_registro_restaurante_datos2.Colombiana
import kotlinx.android.synthetic.main.fragment_registro_restaurante_datos2.ComedorInterno
import kotlinx.android.synthetic.main.fragment_registro_restaurante_datos2.ComidaRapida
import kotlinx.android.synthetic.main.fragment_registro_restaurante_datos2.Desayuno
import kotlinx.android.synthetic.main.fragment_registro_restaurante_datos2.Domicilio
import kotlinx.android.synthetic.main.fragment_registro_restaurante_datos2.Ensaladas
import kotlinx.android.synthetic.main.fragment_registro_restaurante_datos2.Entrante
import kotlinx.android.synthetic.main.fragment_registro_restaurante_datos2.ImagenLogoRe
import kotlinx.android.synthetic.main.fragment_registro_restaurante_datos2.Italiana
import kotlinx.android.synthetic.main.fragment_registro_restaurante_datos2.Mariscos
import kotlinx.android.synthetic.main.fragment_registro_restaurante_datos2.Mexicana
import kotlinx.android.synthetic.main.fragment_registro_restaurante_datos2.OpcionesDiateticas
import kotlinx.android.synthetic.main.fragment_registro_restaurante_datos2.Otro
import kotlinx.android.synthetic.main.fragment_registro_restaurante_datos2.ParaLLevar
import kotlinx.android.synthetic.main.fragment_registro_restaurante_datos2.Pescado
import kotlinx.android.synthetic.main.fragment_registro_restaurante_datos2.PlatoPrincipal
import kotlinx.android.synthetic.main.fragment_registro_restaurante_datos2.Pollo
import kotlinx.android.synthetic.main.fragment_registro_restaurante_datos2.Postres
import kotlinx.android.synthetic.main.fragment_registro_restaurante_datos2.SubirLogo
import kotlinx.android.synthetic.main.fragment_registro_restaurante_datos2.Verduras


class RegistroRestauranteDatos2 : Fragment() {

    private val PICK_IMAGE_REQUEST = 1
    private var uri: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_registro_restaurante_datos2, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        SubirLogo.setOnClickListener {
            abrirGaleria()
        }
        Adelante2.setOnClickListener {
            val logo = uri
            (activity as RegistroRestaurante).agregarLogo(logo!!)
            (activity as RegistroRestaurante).agregarCategoria(obtenerCategorias())
            (activity as RegistroRestaurante).agregarServicios(obtenerServicios())
            (activity as RegistroRestaurante).cambiarFragmento(3)
        }

    }

    private fun obtenerServicios(): ArrayList<String> {
        val listaServicios = ArrayList<String>()
        if(Domicilio.isChecked){
            listaServicios.add("Domicilio")
        }
        if(ParaLLevar.isChecked){
            listaServicios.add("Para llevar")
        }
        if(ComedorInterno.isChecked){
            listaServicios.add("Comedor interno")
        }
        if(BuffetLibre.isChecked){
            listaServicios.add("Buffet libre")
        }
        return  listaServicios
    }

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
                this.context?.let {
                    Glide.with(it)
                        .load(uri)
                        .circleCrop()
                        .into(ImagenLogoRe)
                }
            }
        }
    }
    fun obtenerCategorias (): ArrayList<Categoria>  {
        val listaCategoria = ArrayList<Categoria>()
        obtenerRegiones(listaCategoria)
        obtenerTipoPlato(listaCategoria)
        obtenerIngrediente(listaCategoria)
        return listaCategoria
    }

    private fun obtenerRegiones(listaCategoria: ArrayList<Categoria>) {
        val subCategoria = ArrayList<String> ();
        if(Italiana.isChecked){
            subCategoria.add("Italiana")
        }
        if(Mexicana.isChecked){
            subCategoria.add("Mexicana")
        }
        if(Boliviana.isChecked){
            subCategoria.add("Boliviana")
        }
        if(Asiatica.isChecked){
            subCategoria.add("Asiatica")
        }
        if(Colombiana.isChecked){
            subCategoria.add("Colombiana")
        }
        if(Otro.isChecked){
            subCategoria.add("Otro")
        }
        val categoria = Categoria ("Region",subCategoria)
        listaCategoria.add(categoria)
    }
    private fun obtenerIngrediente(listaCategoria: ArrayList<Categoria>) {
        val subCategoria = ArrayList<String> ();
        if(Pollo.isChecked){
            subCategoria.add("Pollo")
        }
        if(CarneRes.isChecked){
            subCategoria.add("Carne de res")
        }
        if(CarneCerdo.isChecked){
            subCategoria.add("Carne de cerdo")
        }
        if(Pescado.isChecked){
            subCategoria.add("Pescado")
        }
        if(Verduras.isChecked){
            subCategoria.add("Verduras")
        }
        if(Mariscos.isChecked){
            subCategoria.add("Mariscos")
        }
        val categoria = Categoria ("IngredientePrincipal",subCategoria)
        listaCategoria.add(categoria)
    }
    private fun obtenerTipoPlato(listaCategoria: ArrayList<Categoria>) {
        val subCategoria = ArrayList<String> ();
        if(Desayuno.isChecked){
            subCategoria.add("Desayuno")
        }
        if(Entrante.isChecked){
            subCategoria.add("Entrante")
        }
        if(PlatoPrincipal.isChecked){
            subCategoria.add("Plato principal")
        }
        if(Postres.isChecked){
            subCategoria.add("Postres")
        }
        if(Bebidas.isChecked){
            subCategoria.add("Bebidas")
        }
        if(ComidaRapida.isChecked){
            subCategoria.add("Comida rapida")
        }
        if(Ensaladas.isChecked){
            subCategoria.add("Ensaladas")
        }
        if(OpcionesDiateticas.isChecked){
            subCategoria.add("Opciones diateticas")
        }
        val categoria = Categoria ("TipoPlato",subCategoria)
        listaCategoria.add(categoria)
    }
}

