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
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso

class BusquedaFiltrador : Fragment() {

    private lateinit var restauranteAdapterFiltro: RestauranteAdapterFiltro
    private lateinit var recyclerRestaurantes: RecyclerView
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
        recyclerRestaurantes = view.findViewById(R.id.recyclerRestaurantes)
        recyclerRestaurantes.layoutManager = LinearLayoutManager(context)

        restauranteAdapterFiltro = RestauranteAdapterFiltro(listaRestaurantes)
        recyclerRestaurantes.adapter = restauranteAdapterFiltro

        obtenerRestaurantesDesdeDB()
    }
    /*
    private fun obtenerRestaurantesDesdeDB() {
        val db = FirebaseFirestore.getInstance()
        db.collection("Restaurante")
            .get()
            .addOnSuccessListener { resultado ->
                listaRestaurantes.clear()
                for (documento in resultado) {
                    // Recupera el nombre del restaurante
                    val nombreRestaurante = documento.data["nombreRestaurante"].toString()
                    // Recupera la URL del logo
                    val logoUrl = documento.data["logo"]?.toString()
                    //cargarImagenRestaurante(idRestaurante, imageView!!)
                    // Crea el objeto Restaurante y asigna los datos recuperados
                    val restaurante = Restaurante()
                    restaurante.nombreRestaurante = nombreRestaurante
                    println("LOGOOOOOOOOOOOOOOOOOO URL: $logoUrl")//este codigo imprime el logo supuestamente haber
                    // Convierte el logoUrl en Uri y lo asigna al campo logo, si no es nulo
                    if (logoUrl != null) {
                        restaurante.setLogo(Uri.parse(logoUrl))
                    }
                    // Agrega el restaurante a la lista
                    listaRestaurantes.add(restaurante)
                }
                restauranteAdapterFiltro.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                println("Error al recuperar datos: ${exception.message}")
            }
    }
    */

    private fun obtenerRestaurantesDesdeDB() {

        val db = FirebaseFirestore.getInstance()
        db.collection("Restaurante")
            .get()
            .addOnSuccessListener { resultado ->
                listaRestaurantes.clear()
                for (documento in resultado) {
                    // Recupera el nombre del restaurante
                    val nombreRestaurante = documento.data["nombreRestaurante"].toString()
                    // Crea el objeto Restaurante y asigna los datos recuperados
                    val restaurante = Restaurante()
                    restaurante.nombreRestaurante = nombreRestaurante
                    // Agrega el restaurante a la lista
                    listaRestaurantes.add(restaurante)
                }
                restauranteAdapterFiltro.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                println("Error al recuperar datos: ${exception.message}")
            }
    }
}
