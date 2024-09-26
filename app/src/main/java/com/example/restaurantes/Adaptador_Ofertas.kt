package com.example.restaurantes

import android.content.Context
import android.database.DataSetObserver
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ListAdapter
import android.widget.RatingBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class Adaptador_Ofertas (
    private val context: Context,
    private val listaOfertas: List<Oferta>
) : RecyclerView.Adapter<Adaptador_Ofertas.OfertaViewHolder>(), ListAdapter {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OfertaViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_oferta, parent, false)
        return OfertaViewHolder(view)
    }

    override fun onBindViewHolder(holder: OfertaViewHolder, position: Int) {
        val oferta = listaOfertas[position]
        holder.titulo.text = oferta.titulo
        holder.precio.text = oferta.precio
        holder.imagen.setImageDrawable(ContextCompat.getDrawable(context, oferta.imagenResId))
        holder.ratingBar.rating = oferta.rating
    }

    override fun getItemCount(): Int {
        return listaOfertas.size
    }

    class OfertaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titulo: TextView = itemView.findViewById(R.id.itTitulo)
        val precio: TextView = itemView.findViewById(R.id.itPrecio)
        val imagen: ImageView = itemView.findViewById(R.id.itImg)
        val ratingBar: RatingBar = itemView.findViewById(R.id.ratingBar)
    }

    override fun registerDataSetObserver(p0: DataSetObserver?) {
        TODO("Not yet implemented")
    }

    override fun unregisterDataSetObserver(p0: DataSetObserver?) {
        TODO("Not yet implemented")
    }

    override fun getCount(): Int {
        TODO("Not yet implemented")
    }

    override fun getItem(p0: Int): Any {
        TODO("Not yet implemented")
    }

    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        TODO("Not yet implemented")
    }

    override fun getViewTypeCount(): Int {
        TODO("Not yet implemented")
    }

    override fun isEmpty(): Boolean {
        TODO("Not yet implemented")
    }

    override fun areAllItemsEnabled(): Boolean {
        TODO("Not yet implemented")
    }

    override fun isEnabled(p0: Int): Boolean {
        TODO("Not yet implemented")
    }
}