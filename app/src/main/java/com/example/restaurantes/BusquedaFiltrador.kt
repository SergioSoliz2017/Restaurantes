import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurantes.ListaRestauranteAdapterFiltro
import com.example.restaurantes.R
import com.google.firebase.firestore.FirebaseFirestore
import com.example.restaurantes.Restaurante

class BusquedaFiltrador : Fragment() {


    private lateinit var recyclerRestaurantes: RecyclerView
    private lateinit var listaRestaurantes : ArrayList<Restaurante>
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
        val nombreUsuario = item?.nombreRestaurante
        //val inicio = Intent(this, Inicio:: class.java)
        //inicio.putExtra("nombreUsuario",nombreUsuario)
        //startActivity(inicio)
    }

    private fun obtenerRestaurantesDesdeDB() {
        val db = FirebaseFirestore.getInstance()
        db.collection("Restaurante").get().addOnSuccessListener { resultado ->
            listaRestaurantes  = ArrayList<Restaurante>()
            for (documento in resultado){

                val nombreRestaurante = documento.data?.get("nombreRestaurante").toString()

                val restaurante : Restaurante = Restaurante()
                restaurante.nombreRestaurante = nombreRestaurante
                listaRestaurantes.add(restaurante)

            }
            var listAdapter : ListaRestauranteAdapterFiltro = ListaRestauranteAdapterFiltro(listaRestaurantes,this.context,ListaRestauranteAdapterFiltro.OnItemClickListener { item: Restaurante? -> moveToDescription(item) })
            recyclerRestaurantes.setLayoutManager(LinearLayoutManager(this.context))
            recyclerRestaurantes.setAdapter(listAdapter)
        }
    }
}
