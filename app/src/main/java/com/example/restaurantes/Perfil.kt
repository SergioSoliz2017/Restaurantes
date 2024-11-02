package com.example.restaurantes

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Perfil.newInstance] factory method to
 * create an instance of this fragment.
 */
class Perfil : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    //val usuario = (activity as? PantallaPrincipal)?.usuario //para obtener la info del usuario y obtener en firebase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_perfil, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val actividad = activity as? PantallaPrincipal
        if (actividad != null) {
            val usuario = actividad.usuario
            obtenerDatosUsuarioDesdeDB(usuario)
        } else {
            Toast.makeText(context, "Error al cargar la información", Toast.LENGTH_SHORT).show()
        }
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Perfil.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Perfil().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun obtenerDatosUsuarioDesdeDB(usuario: Usuario) {
        val db = FirebaseFirestore.getInstance()
        val correoUsuario = usuario.correo

        db.collection("Usuarios").document(correoUsuario)
            .get()
            .addOnSuccessListener { documento ->
                val nombreUsr = documento.data?.get("Nombre").toString()
                val fechaNacUsr = documento.data?.get("FechaNacimiento").toString()
                val restauranteRef = documento.data?.get("Restaurante") as? DocumentReference
                val idRestaurante = restauranteRef?.id
                val imageView: ImageView? = view?.findViewById(R.id.imageView3)

                if (idRestaurante != null) {
                    db.collection("Restaurante").document(idRestaurante)
                        .get()
                        .addOnSuccessListener { documento ->
                            val nombreRestaurante = documento.data?.get("nombreRestaurante").toString()

                            val outRes: TextView = view?.findViewById(R.id.outRes)!!

                            outRes.text = nombreRestaurante
                        }
                }else{
                    val outRes: TextView = view?.findViewById(R.id.outRes)!!

                    outRes.text = "Sin Informacion"
                }
                //Toast.makeText(context, "nombre BD: $nombreUsuario", Toast.LENGTH_SHORT).show()

                val outNombre: TextView = view?.findViewById(R.id.outNombre)!!
                val fechaNac: TextView = view?.findViewById(R.id.fechaNac)!!

                cargarImagenRestaurante(idRestaurante, imageView!!)

                outNombre.text = nombreUsr
                fechaNac.text = fechaNacUsr
            }
            .addOnFailureListener { exception ->
                println("Error al recuperar datos: ${exception.message}")
            }
        }

    private fun cargarImagenRestaurante(idRestaurante: String?, imageView: ImageView) {
        val storageReference = FirebaseStorage.getInstance().reference
        val imagenReference = storageReference.child("Restaurante/$idRestaurante")
        val contexto = imageView.context
        imagenReference.downloadUrl.addOnSuccessListener { uri ->
            Picasso.get()
                .load(uri)
                .into(imageView)
        }.addOnFailureListener { exception ->
            Toast.makeText(contexto, "Error al cargar imagen: ${exception.message}", Toast.LENGTH_LONG).show()
        }
    }
}