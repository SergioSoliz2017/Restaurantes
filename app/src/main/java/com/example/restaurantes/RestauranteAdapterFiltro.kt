import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.restaurantes.R
import com.example.restaurantes.Restaurante

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

        // Cargar imagen desde la URL usando Glide
        Glide.with(holder.logoRestaurante.context)
            .load(restaurante.logo)
            .into(holder.logoRestaurante)
    }

    override fun getItemCount() = restaurantes.size
}
