package com.example.restaurantes

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_perfil.cerrarSesion

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
        val modificarButton = view.findViewById<Button>(R.id.btnModificar)
        modificarButton.setOnClickListener {
            modificarPerfil()
        }
        cerrarSesion.setOnClickListener {
            (activity as PantallaPrincipal).BorrarDatos()
            val inicio = Intent(this.context, InicioSesion::class.java)
            startActivity(inicio)

            requireActivity().finish()
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

    @SuppressLint("SetTextI18n")
    private fun obtenerDatosUsuarioDesdeDB(usuario: Usuario) {
        val db = FirebaseFirestore.getInstance()
        val correoUsuario = usuario.correo

        db.collection("Usuarios").document(correoUsuario)
            .get()
            .addOnSuccessListener { documento ->
                val nombreUsr = documento.data?.get("Nombre").toString()
                val correoUsr = documento.id
                val numeroUsr = documento.data?.get("FechaNacimiento").toString()
                val imageView: ImageView? = view?.findViewById(R.id.imageView3)

                cargarImagenPerfil(correoUsuario, imageView!!) //predetermindo

                val outNombre: TextView = view?.findViewById(R.id.outNombre)!!
                val outCorreo: TextView = view?.findViewById(R.id.outCorreo)!!
                val outNumero: TextView = view?.findViewById(R.id.outNumero)!!

                outNombre.text = nombreUsr
                outCorreo.text = correoUsr
                outNumero.text = numeroUsr
            }
            .addOnFailureListener { exception ->
                println("Error al recuperar datos: ${exception.message}")
            }
    }

    private fun cargarImagenPerfil(usrNombre: String?, imageView: ImageView) {
        val storageReference = FirebaseStorage.getInstance().reference
        val imagenReference = storageReference.child("Usuario/$usrNombre")//cambiar la direccion de la carpeta donde se va a guarda la foto de usuario
        val contexto = imageView.context
        imagenReference.downloadUrl.addOnSuccessListener { uri ->
            Picasso.get()
                .load(uri)
                .into(imageView)
        }.addOnFailureListener { exception ->
            Toast.makeText(contexto, "Error al cargar imagen: ${exception.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun modificarPerfil() {
        val fragment = ModificarPerfilFragment()
        val actividad = activity as PantallaPrincipal
        val transaction = actividad.supportFragmentManager.beginTransaction()
        transaction.replace(R.id.containerView, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
}