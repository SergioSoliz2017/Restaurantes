package com.example.restaurantes

import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso

class MisOfertasAdapter(var ofertas: List<Oferta>) : RecyclerView.Adapter<MisOfertasAdapter.OfertaViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OfertaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_oferta, parent, false)
        return OfertaViewHolder(view)
    }

    override fun onBindViewHolder(holder: OfertaViewHolder, position: Int) {
        val oferta = ofertas[position]

        Log.d("Oferta", "Titulo: ${oferta.titulo}, Descripcion: ${oferta.descripcion}, Precio: ${oferta.precio}")

        holder.titulo.text = oferta.titulo
        holder.descripcion.text = oferta.descripcion
        holder.precio.text = SpannableString("Precio oferta (bs): ${oferta.precio.toString()}").apply {
            setSpan(
                StyleSpan(Typeface.BOLD),
                0,
                10,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        // Verificar si la URL de la imagen está vacía o nula
        if (oferta.imagen.isNullOrEmpty()) {
            // Si la URL está vacía, cargar una imagen predeterminada
            Picasso.get().load(R.drawable.banner).into(holder.imagen)  // Imagen predeterminada
        } else {
            // Si la URL es válida, cargar la imagen desde Firebase Storage
            val storage = FirebaseStorage.getInstance()
            val imagenRef = storage.getReferenceFromUrl(oferta.imagen)
            imagenRef.downloadUrl.addOnSuccessListener { url ->
                Picasso.get().load(url).into(holder.imagen)
            }.addOnFailureListener { e ->
                Log.d("Imagen", "Error al cargar imagen: ${e.message}")
            }
        }
    }

    override fun getItemCount(): Int {
        return ofertas.size
    }

    inner class OfertaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titulo: TextView = itemView.findViewById(R.id.titulo)
        val descripcion: TextView = itemView.findViewById(R.id.descripcion)
        val precio: TextView = itemView.findViewById(R.id.precio)
        val imagen: ImageView = itemView.findViewById(R.id.imagenOf)
    }
}