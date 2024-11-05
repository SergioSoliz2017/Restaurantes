package com.example.restaurantes

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import android.location.Geocoder
import android.util.Log
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.LatLng
import java.util.Locale

class PerfilRestaurante: Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_perfil_restaurante, container, false)
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

    private fun obtenerDatosUsuarioDesdeDB(usuario: Usuario) {
        val db = FirebaseFirestore.getInstance()
        val correoUsuario = usuario.correo

        db.collection("Usuarios").document(correoUsuario).get()
            .addOnSuccessListener { documento ->
                val nombreUsr = documento.data?.get("Nombre").toString()
                val restauranteNombre = documento.data?.get("Restaurante").toString()
                val imageView: ImageView? = view?.findViewById(R.id.imageView3)
                cargarImagenRestaurante(restauranteNombre, imageView!!)

                // Obtener datos del restaurante
                db.collection("Restaurante").document(restauranteNombre).get()
                    .addOnSuccessListener { restauranteDocumento ->
                        val linearCategorias: LinearLayout = requireView().findViewById(R.id.linearCategorias)
                        //Categoria
                        val categoria = restauranteDocumento.data?.get("categoria") as? Map<String, Any>
                        val ingredientePrincipal = restauranteDocumento.data?.get("IngredientePrincipal") as? List<String>
                        val region = restauranteDocumento.data?.get("Region") as? List<String>
                        val tipoPlato = restauranteDocumento.data?.get("TipoPlato") as? List<String>

                        // Agregar las categorías al LinearLayout
                        linearCategorias.addView(agregarCategoria("Categoría: ${categoria?.values?.joinToString(", ")}"))
                        linearCategorias.addView(agregarCategoria("Ingrediente Principal: ${ingredientePrincipal?.joinToString(", ")}"))
                        linearCategorias.addView(agregarCategoria("Región: ${region?.joinToString(", ")}"))
                        linearCategorias.addView(agregarCategoria("Tipo de Plato: ${tipoPlato?.joinToString(", ")}"))

                        val celularReferencia = restauranteDocumento.data?.get("celularReferencia").toString()
                        val horarioAtencion = restauranteDocumento.data?.get("horarioAtencion") as? ArrayList<Map<String, String>>
                        val logo = restauranteDocumento.data?.get("logo").toString()
                        val nombreRestaurante = restauranteDocumento.data?.get("nombreRestaurante").toString()

                        //Ubicación
                        val ubicacion = restauranteDocumento.data?.get("ubicacion") as? Map<String, Any>
                        if (ubicacion != null) {
                            val latitude = ubicacion.get("latitude") as? Double
                            val longitude = ubicacion.get("longitude") as? Double

                            if (latitude != null && longitude != null) {
                                val direccion = getDireccionDesdeCoordenadas(requireContext(), latitude, longitude)

                                val textViewDireccion: TextView = requireView().findViewById(R.id.outDirec)
                                textViewDireccion.text = direccion ?: "S/N"

                                if (direccion == null) {
                                    Toast.makeText(requireContext(), "No se pudo obtener la dirección", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                Log.d("PerfilRestaurante", "Latitude o Longitude es null")
                                val textViewDireccion: TextView = requireView().findViewById(R.id.outDirec)
                                textViewDireccion.text = "S/N"
                                Toast.makeText(requireContext(), "No se pudo obtener la dirección", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Log.d("PerfilRestaurante", "Ubicación es null")
                            val textViewDireccion: TextView = requireView().findViewById(R.id.outDirec)
                            textViewDireccion.text = "S/N"
                            Toast.makeText(requireContext(), "No se pudo obtener la dirección", Toast.LENGTH_SHORT).show()
                        }

                        val outNombre: TextView = view?.findViewById(R.id.outNombre)!!
                        val outTel: TextView = view?.findViewById(R.id.outTel)!!
                        val outRes: TextView = view?.findViewById(R.id.outRes)!!
                        val linearHorarios: LinearLayout = requireView().findViewById(R.id.linearHorarios)
                        val imageView: ImageView = requireView().findViewById(R.id.imageView3)

                        if (horarioAtencion != null) {
                            for (horario in horarioAtencion) {
                                val dia = horario["dia"]
                                val abrir = horario["abrir"]
                                val cerrar = horario["cerrar"]

                                if (dia != null && abrir != null && cerrar != null) {
                                    val textView = TextView(context)
                                    textView.text = "Dia: $dia - Horario: $abrir - $cerrar"
                                    textView.textSize = 16f
                                    textView.setPadding(16, 16, 16, 16)
                                    linearHorarios.addView(textView)
                                } else {
                                    Log.d("PerfilRestaurante", "Dia, Abrir o Cerrar es null")
                                }
                            }
                        } else {
                            Log.d("PerfilRestaurante", "HorarioAtencion es null")
                        }

                        // asignando
                        outNombre.text = nombreUsr
                        outTel.text = celularReferencia
                        outRes.text = nombreRestaurante
                        Picasso.get()
                            .load(logo)
                            .into(imageView)
                    }
                    .addOnFailureListener { excepción ->
                        println("Error al obtener datos del restaurante: $excepción")
                    }
            }
            .addOnFailureListener { excepción ->
                println("Error al obtener datos del usuario: $excepción")
            }
    }

    private fun cargarImagenRestaurante(restauranteNombre: String?, imageView: ImageView) {
        val storageReference = FirebaseStorage.getInstance().reference
        val imagenReference = storageReference.child("Restaurante/$restauranteNombre")
        val contexto = imageView.context
        imagenReference.downloadUrl.addOnSuccessListener { uri ->
            Picasso.get()
                .load(uri)
                .into(imageView)
        }.addOnFailureListener { exception ->
            Toast.makeText(contexto, "Error al cargar imagen: ${exception.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun getDireccionDesdeCoordenadas(context: Context, latitude: Double, longitude: Double): String? {
        val geocoder = Geocoder(context, Locale.getDefault())

        return try {
            // Obtener la dirección en base a las coordenadas
            val direcciones = geocoder.getFromLocation(latitude, longitude, 1)

            if (direcciones != null && direcciones.isNotEmpty()) {
                val direccion = direcciones[0]
                // Formatear la dirección para obtener el nombre de la calle o avenida
                "${direccion.thoroughfare}, ${direccion.subLocality}, ${direccion.locality}"
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun agregarCategoria(texto: String): TextView {
        val textView = TextView(context)
        textView.text = texto
        textView.textSize = 16f
        textView.setPadding(16, 16, 16, 16)
        textView.setBackgroundColor(Color.parseColor("#FFFFFF")) // Blanco
        return textView
    }
}