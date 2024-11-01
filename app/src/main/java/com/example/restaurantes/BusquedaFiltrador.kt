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
                restauranteAdapterFiltro.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                println("Error al recuperar datos: ${exception.message}")
            }
    }
}
