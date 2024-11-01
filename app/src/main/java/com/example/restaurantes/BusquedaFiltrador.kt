import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurantes.R
import com.google.firebase.firestore.FirebaseFirestore
import com.example.restaurantes.Restaurante

class BusquedaFiltrador : Fragment() {

    private lateinit var restauranteAdapter: RestauranteAdapterFiltro
    private lateinit var recyclerRestaurantes: RecyclerView
    private val listaRestaurantes = mutableListOf<Restaurante>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_busqueda, container, false)
        recyclerRestaurantes = view.findViewById(R.id.recyclerRestaurantes)
        recyclerRestaurantes.layoutManager = LinearLayoutManager(context)

        restauranteAdapter = RestauranteAdapterFiltro(listaRestaurantes)
        recyclerRestaurantes.adapter = restauranteAdapter

        obtenerRestaurantesDesdeDB()

        return view
    }

    private fun obtenerRestaurantesDesdeDB() {
        val db = FirebaseFirestore.getInstance()
        db.collection("Restaurante")
            .get()
            .addOnSuccessListener { result ->
                listaRestaurantes.clear()
                for (document in result) {
                    val restaurante = document.toObject(Restaurante::class.java)
                    listaRestaurantes.add(restaurante)
                }
                restauranteAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                println("Error al recuperar datos: ${exception.message}")
            }
    }
}
