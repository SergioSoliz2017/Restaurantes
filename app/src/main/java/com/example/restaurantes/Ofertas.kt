package com.example.restaurantes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Ofertas : Fragment() {
    private lateinit var recyclerRestaurantes: RecyclerView
    private lateinit var restauranteAdapter: RestauranteAdapter
    private val restaurantes = mutableListOf<Restaurante>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_ofertas, container, false)
        recyclerRestaurantes = view.findViewById(R.id.recyclerRestaurantes)
        recyclerRestaurantes.layoutManager = LinearLayoutManager(context)

        obtenerRestaurantes() // Método para obtener los datos

        return view
    }

    private fun obtenerRestaurantes() {
        val database = FirebaseDatabase.getInstance()
        val restauranteRef = database.getReference("Restaurante")

        restauranteRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                restaurantes.clear()
                for (restauranteSnapshot in snapshot.children) {
                    val restaurante = restauranteSnapshot.getValue(Restaurante::class.java)
                    restaurante?.let {
                        // Aquí deberías obtener los menús correspondientes
                        obtenerMenusPorRestaurante(it.nombreRestaurante) // Asumiendo que tienes un método para esto
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                println("Error al leer datos: ${error.message}")
            }
        })
    }

    private fun obtenerMenusPorRestaurante(nombreRestaurante: String) {
        val menuRef = FirebaseDatabase.getInstance().getReference("Menu")

        // Usar "nomRestaurante" como el identificador en lugar de "restauranteId"
        menuRef.orderByChild("nomRestaurante").equalTo(nombreRestaurante).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val menus = mutableListOf<MenuItem>()
                for (menuSnapshot in snapshot.children) {
                    val menuItem = menuSnapshot.getValue(MenuItem::class.java)
                    menuItem?.let { menus.add(it) }
                }

                // Después de obtener los menús, agregar el restaurante y su lista de menús
                val restaurante = restaurantes.find { it.nombreRestaurante == nombreRestaurante }
                restaurante?.menus = ArrayList(menus)

                // Actualiza el adapter para reflejar los menús del restaurante
                restauranteAdapter = RestauranteAdapter(restaurantes)
                recyclerRestaurantes.adapter = restauranteAdapter
            }

            override fun onCancelled(error: DatabaseError) {
                println("Error al leer datos de menús: ${error.message}")
            }
        })
    }
}