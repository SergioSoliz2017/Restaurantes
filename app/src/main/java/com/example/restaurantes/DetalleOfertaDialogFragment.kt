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
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale
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

        val titulo = oferta.titulo
        val imagenUrl = oferta.imagen
        val descripcion = oferta.descripcion
        val fechaFin = oferta.fechaFin
        val restauranteId = oferta.restauranteId

        tituloTextView.text = titulo
        descripcionTextView.text = descripcion
        fechaFinTextView.text = formatDate(fechaFin)
        restauranteIdTextView.text = restauranteId

        if (!imagenUrl.isNullOrEmpty()) {
            val storage = FirebaseStorage.getInstance()
            val imagenRef = storage.getReferenceFromUrl(imagenUrl)

            imagenRef.downloadUrl.addOnSuccessListener { url ->
                Picasso.get().load(url).into(imagenImageView)
            }.addOnFailureListener { e ->
                Log.d("Imagen", "Error al cargar imagen: ${e.message}")
                Picasso.get().load(R.drawable.banner).into(imagenImageView)
            }
        } else {
            Picasso.get().load(R.drawable.banner).into(imagenImageView)
        }
    }

    private fun formatDate(fecha: String): String {
        val formatoEntrada = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val formatoSalida = SimpleDateFormat("dd MMM yyyy, hh:mm:ss a", Locale.getDefault())
        val zonaHoraria = TimeZone.getTimeZone("UTC-4")

        formatoEntrada.timeZone = zonaHoraria
        formatoSalida.timeZone = zonaHoraria

        try {
            val fechaParseada = formatoEntrada.parse(fecha)
            return formatoSalida.format(fechaParseada)
        } catch (e: ParseException) {
            Log.e("Fecha", "Error al parsear fecha: $e")
            return "Formato de fecha inválido"
        }
    }
}