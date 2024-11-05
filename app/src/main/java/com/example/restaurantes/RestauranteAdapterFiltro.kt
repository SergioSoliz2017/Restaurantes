import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurantes.R
import com.example.restaurantes.Restaurante
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso

class RestauranteAdapterFiltro(private val restaurantes: List<Restaurante>) :
    RecyclerView.Adapter<RestauranteAdapterFiltro.RestauranteViewHolder>() {

    class RestauranteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val logoRestaurante: ImageView = itemView.findViewById(R.id.logoRestaurante)
        val nombreRestaurante: TextView = itemView.findViewById(R.id.nombreRestaurante)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestauranteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_restaurante_filtro, parent, false)
        return RestauranteViewHolder(view)
    }

    override fun onBindViewHolder(holder: RestauranteViewHolder, position: Int) {
        val restaurante = restaurantes[position]
        holder.nombreRestaurante.text = restaurante.nombreRestaurante

        val storageReference = FirebaseStorage.getInstance().reference
        val imagenReference = storageReference.child("Restaurante/${restaurante.nombreRestaurante}")
        imagenReference.downloadUrl.addOnSuccessListener { uri ->
            Picasso.get()
                .load(uri)
                .into(holder.logoRestaurante)
        }
    }

    override fun getItemCount() = restaurantes.size
}
