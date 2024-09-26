package com.example.restaurantes

import Adaptador_Ofertas
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Ofertas.newInstance] factory method to
 * create an instance of this fragment.
 */
class Ofertas : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflamos el layout del fragmento
        val view = inflater.inflate(R.layout.fragment_ofertas, container, false)

        // Inicializamos el ListView
        val listView: ListView = view.findViewById(R.id.lvLista)

        // Creamos algunos datos de ejemplo
        val ofertas = listOf(
            Oferta("Oferta 1", "$50", R.drawable.banner, 4.5f),
            Oferta("Oferta 2", "$75", R.drawable.banner, 4.0f),
            Oferta("Oferta 3", "$100", R.drawable.banner, 5.0f)
        )

        // Configuramos el adaptador
        val adapter = Adaptador_Ofertas(requireContext(), ofertas)
        listView.adapter = adapter

        return view
    }
}