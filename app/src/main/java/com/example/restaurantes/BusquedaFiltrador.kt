import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurantes.R
import com.google.firebase.firestore.FirebaseFirestore
import com.example.restaurantes.Restaurante
import com.example.restaurantes.RestauranteAdapterFiltro
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_busqueda.recyclerRestaurantes

class BusquedaFiltrador : Fragment() {


    private val listaRestaurantes = ArrayList<Restaurante>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_busqueda, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        obtenerRestaurantesDesdeDB()
    }
    private fun moveToDescription(item: Restaurante?) {
        /*val descripcion = Intent(this.context, Descripcion:: class.java)
        descripcion.putExtra("codigo", item?.codigo)
        startActivity(descripcion)
        activity?.finish()*/
        Toast.makeText(context, item?.nombreRestaurante.toString(), Toast.LENGTH_SHORT).show()
    }

    private fun obtenerRestaurantesDesdeDB() {
        val db = FirebaseFirestore.getInstance()
        db.collection("Restaurante")
            .get()
            .addOnSuccessListener { resultado ->
                listaRestaurantes.clear()
                var documentosProcesados = 0
                val totalDocumentos = resultado.size()

                for (documento in resultado) {
                    val nombreRestaurante = documento.data["nombreRestaurante"].toString()
                    val storageRef = FirebaseStorage.getInstance().reference
                    val imageRef = storageRef.child("Restaurante/$nombreRestaurante")

                    imageRef.downloadUrl.addOnSuccessListener { uri ->
                        val imageUrl = uri.toString()
                        val restaurante = Restaurante().apply {
                            this.nombreRestaurante = nombreRestaurante
                            this.dirLogo = imageUrl
                        }
                        listaRestaurantes.add(restaurante)
                        documentosProcesados++

                        // Cuando se hayan procesado todos los documentos
                        if (documentosProcesados == totalDocumentos) {
                            configurarAdaptador()
                        }
                    }.addOnFailureListener {
                        documentosProcesados++
                        if (documentosProcesados == totalDocumentos) {
                            configurarAdaptador()
                        }
                    }
                }
            }
            .addOnFailureListener { exception ->
                println("Error al recuperar datos: ${exception.message}")
            }
    }

    private fun configurarAdaptador() {
        val listAdapter = RestauranteAdapterFiltro(listaRestaurantes, this.context, RestauranteAdapterFiltro.OnItemClickListener { item ->
            moveToDescription(item)
        })
        recyclerRestaurantes.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this.context)
            adapter = listAdapter
        }
    }
}
