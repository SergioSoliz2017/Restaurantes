package com.example.restaurantes

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Button
import androidx.fragment.app.Fragment
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_registro_restaurante_datos2.ImagenLogoRe
import kotlinx.android.synthetic.main.fragment_registro_restaurante_datos2.SubirLogo


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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_registro_restaurante_datos2, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        SubirLogo.setOnClickListener {
            abrirGaleria()
        }
    }
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
                Picasso.get().load(uri).into(ImagenLogoRe) // Cargar la imagen en el ImageView
            }
        }
    }
}

