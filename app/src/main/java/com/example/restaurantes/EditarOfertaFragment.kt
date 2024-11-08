package com.example.restaurantes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment

class EditarOfertaFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.editar_oferta_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val oferta = arguments?.getParcelable(Oferta::class.java.simpleName) ?: Oferta()

        // Inicializa los componentes del fragmento aquí

        val btnCancelar: Button = view.findViewById(R.id.btnCancelar)
        btnCancelar.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
        }
    }
}