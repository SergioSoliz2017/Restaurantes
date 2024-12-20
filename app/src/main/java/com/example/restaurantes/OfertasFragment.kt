package com.example.restaurantes

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot

class OfertasFragment : Fragment(), OnOfertaClickListener {
    private lateinit var recyclerRestaurantes: RecyclerView
    private lateinit var ofertaAdapter: OfertaAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_ofertas, container, false)
        recyclerRestaurantes = view.findViewById(R.id.recycler_view)
        recyclerRestaurantes.layoutManager = LinearLayoutManager(context)
        ofertaAdapter = OfertaAdapter(emptyList(), this)
        recyclerRestaurantes.adapter = ofertaAdapter
        obtenerOfertas()
        return view
    }

    private fun obtenerOfertas() {
        val db = FirebaseFirestore.getInstance()

        db.collection("Ofertas")
            .addSnapshotListener(object : EventListener<QuerySnapshot> {
                override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                    if (error != null) {
                        println("Error al leer datos: ${error.message}")
                        return
                    }

                    value?.let {
                        val ofertas = it.toObjects(Oferta::class.java)
                        for (i in ofertas.indices) {
                            ofertas[i].id = it.documents[i].id
                        }

                        if (ofertaAdapter != null && ofertas.isNotEmpty()) {
                            ofertaAdapter.ofertas = ofertas
                            ofertaAdapter.notifyDataSetChanged()
                            Log.d("Ofertas", "Datos asignados correctamente")
                        } else {
                            Log.d("Ofertas", "No hay datos o adaptador nulo")
                        }
                    }
                }
            })
    }

    override fun onOfertaClick(oferta: Oferta) {
        val dialog = DetalleOfertaDialogFragment()
        val args = Bundle()
        args.putParcelable("oferta", oferta)
        dialog.arguments = args
        dialog.show((activity as AppCompatActivity).supportFragmentManager, "DetalleOfertaDialogFragment")
    }
}