package com.example.restaurantes

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.dialog_detalle_oferta.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone

class DetalleOfertaDialogFragment : DialogFragment() {

    private lateinit var oferta: Oferta

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        oferta = requireArguments().getParcelable("oferta")!!
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_detalle_oferta, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Utiliza el objeto Oferta completo
        val titulo = oferta.titulo
        val imagenUrl = oferta.imagen
        val descripcion = oferta.descripcion
        val fechaFin = oferta.fechaFin
        val restauranteId = oferta.restauranteId

        // Asigna los valores a las vistas
        tituloTextView.text = titulo
        descripcionTextView.text = descripcion
        fechaFinTextView.text = formatDate(fechaFin)
        restauranteIdTextView.text = restauranteId

        // Carga la imagen desde Firebase Storage
        val storage = FirebaseStorage.getInstance()
        val imagenRef = storage.getReferenceFromUrl(imagenUrl)
        imagenRef.downloadUrl.addOnSuccessListener { url ->
            Picasso.get().load(url).into(imagenImageView)
        }.addOnFailureListener { e ->
            Log.d("Imagen", "Error al cargar imagen: ${e.message}")
        }
    }

    private fun formatDate(fecha: Long): String {
        val dateFormat = SimpleDateFormat("dd MMM yyyy, hh:mm:ss a")
        val zonaHoraria = TimeZone.getTimeZone("UTC-4")
        dateFormat.timeZone = zonaHoraria
        return dateFormat.format(Date(fecha))
    }
}