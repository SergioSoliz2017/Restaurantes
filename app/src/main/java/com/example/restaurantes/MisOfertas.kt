package com.example.restaurantes

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot

class MisOfertas : Fragment() {
    private lateinit var recyclerMisOfertas: RecyclerView
    private lateinit var misOfertasAdapter: MisOfertasAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_mis_ofertas, container, false)

        val btnAgregarOferta = view.findViewById<FloatingActionButton>(R.id.floatingActionButton)
        btnAgregarOferta.setOnClickListener {
            agregarOferta()
        }

        recyclerMisOfertas = view.findViewById(R.id.recyclerView)
        recyclerMisOfertas.layoutManager = LinearLayoutManager(context)

        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.Callback() {
            override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
                return makeMovementFlags(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT)
            }

            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val posicion = viewHolder.adapterPosition
                val oferta = misOfertasAdapter.ofertas[posicion]
                when (direction) {
                    ItemTouchHelper.LEFT -> {
                        // Eliminar oferta
                        eliminarOferta(oferta)
                    }
                    ItemTouchHelper.RIGHT -> {
                        // Editar oferta
                        editarOferta(oferta)
                    }
                }
            }

            override fun onChildDraw(
                canvas: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                super.onChildDraw(canvas, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

                val itemView = viewHolder.itemView
                val paint = Paint()
                val icon: Bitmap

                when {
                    dX > 0 -> {
                        // Deslizar a la derecha
                        paint.color = Color.GREEN
                        canvas.drawRect(
                            itemView.left.toFloat(),
                            itemView.top.toFloat(),
                            itemView.left + dX,
                            itemView.bottom.toFloat(),
                            paint
                        )

                        // Dibujar icono
                        val drawable = ContextCompat.getDrawable(recyclerView.context, R.drawable.baseline_edit_24)
                        val scale = 2f // Ajusta este valor para cambiar el tamaño
                        val width = (drawable?.intrinsicWidth!! * scale).toInt()
                        val height = (drawable.intrinsicHeight * scale).toInt()
                        drawable.setBounds(
                            itemView.left + 16,
                            itemView.top + (itemView.height / 2) - (height / 2),
                            itemView.left + 16 + width,
                            itemView.top + (itemView.height / 2) + (height / 2)
                        )
                        drawable?.draw(canvas)
                    }
                    dX < 0 -> {
                        // Deslizar a la izquierda
                        paint.color = Color.RED
                        canvas.drawRect(
                            itemView.right + dX,
                            itemView.top.toFloat(),
                            itemView.right.toFloat(),
                            itemView.bottom.toFloat(),
                            paint
                        )
                        // Dibujar icono
                        val drawable = ContextCompat.getDrawable(recyclerView.context, R.drawable.icon_eliminar)
                        val scale = 2f
                        val width = (drawable?.intrinsicWidth!! * scale).toInt()
                        val height = (drawable.intrinsicHeight * scale).toInt()
                        drawable.setBounds(
                            itemView.right - 16 - width, // Ajusta la posición x
                            (itemView.top + (itemView.height / 2) - (height / 2)).toInt(),
                            itemView.right - 16,
                            (itemView.top + (itemView.height / 2) + (height / 2)).toInt()
                        )
                        drawable?.draw(canvas)
                    }
                }
            }
        })

        itemTouchHelper.attachToRecyclerView(recyclerMisOfertas)

        misOfertasAdapter = MisOfertasAdapter(emptyList())
        recyclerMisOfertas.adapter = misOfertasAdapter

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val actividad = activity as? PantallaPrincipal
        if (actividad != null) {
            val usuario = actividad.usuario
            obtenerOfertas(usuario)
        } else {
            Toast.makeText(context, "Error al cargar la información", Toast.LENGTH_SHORT).show()
        }
    }

    private fun obtenerOfertas(usuario: Usuario) {
        val db = FirebaseFirestore.getInstance()
        val userId = usuario.correo

        if (userId != null) {
            db.collection("Usuarios").document(userId).get()
                .addOnSuccessListener { usuarioDoc ->
                    val restauranteId = usuarioDoc.getString("Restaurante")

                    db.collection("Ofertas")
                        .whereEqualTo("restauranteId", restauranteId)
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

                                    if (misOfertasAdapter != null && ofertas.isNotEmpty()) {
                                        misOfertasAdapter.ofertas = ofertas
                                        misOfertasAdapter.notifyDataSetChanged()
                                        Log.d("Ofertas", "Datos asignados correctamente")
                                    } else {
                                        Log.d("Ofertas", "No hay datos o adaptador nulo")
                                    }
                                }
                            }
                        })
                }
                .addOnFailureListener { exception ->
                    Log.d("Error", "Error obteniendo restauranteId", exception)
                }
        } else {
            Log.d("Error", "El usuario no tiene correo")
        }
    }

    private fun eliminarOferta(oferta: Oferta) {
        val db = FirebaseFirestore.getInstance()
        db.collection("Ofertas").document(oferta.id).delete()
            .addOnSuccessListener {
                Log.d("EliminarOferta", "Oferta eliminada correctamente")
            }
            .addOnFailureListener { e ->
                Log.w("EliminarOferta", "Error al eliminar oferta", e)
            }
    }

    private fun editarOferta(oferta: Oferta) {
        val fragment = EditarOfertaFragment()
        val args = Bundle()
        args.putParcelable("oferta", oferta)
        fragment.setArguments(args)

        // Utiliza supportFragmentManager de la actividad principal
        val actividad = activity as PantallaPrincipal
        val transaction = actividad.supportFragmentManager.beginTransaction()
        transaction.replace(R.id.containerView, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun agregarOferta() {
        val fragment = AgregarOfertaFragment()
        val actividad = activity as PantallaPrincipal
        val transaction = actividad.supportFragmentManager.beginTransaction()
        transaction.replace(R.id.containerView, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
}