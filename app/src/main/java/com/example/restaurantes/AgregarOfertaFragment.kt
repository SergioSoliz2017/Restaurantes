package com.example.restaurantes

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView

class AgregarOfertaFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_agregar_oferta, container, false)

        val textView = view.findViewById<TextView>(R.id.textView)
        textView.text = "Agregar Oferta"

        val btnCerrar = view.findViewById<Button>(R.id.btnCerrar)
        btnCerrar.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        return view
    }
}