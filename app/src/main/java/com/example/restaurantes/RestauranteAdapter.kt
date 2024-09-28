package com.example.restaurantes

import MenuAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class RestauranteAdapter(
    private val restaurantes: List<Restaurante>
) : RecyclerView.Adapter<RestauranteAdapter.RestauranteViewHolder>() {

    class RestauranteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvRestauranteNombre: TextView = itemView.findViewById(R.id.tvRestauranteNombre)
        val recyclerMenus: RecyclerView = itemView.findViewById(R.id.recyclerMenus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestauranteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_restaurante, parent, false)
        return RestauranteViewHolder(view)
    }

    override fun onBindViewHolder(holder: RestauranteViewHolder, position: Int) {
        val restaurante = restaurantes[position]
        holder.tvRestauranteNombre.text = restaurante.nomRestaurante

        // Configurar el RecyclerView de los menús
        holder.recyclerMenus.layoutManager = LinearLayoutManager(holder.recyclerMenus.context, LinearLayoutManager.HORIZONTAL, false)
        holder.recyclerMenus.adapter = MenuAdapter(restaurante.menus) // Suponiendo que tienes una lista de menús
    }

    override fun getItemCount(): Int = restaurantes.size
}