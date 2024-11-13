import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurantes.MenuItem
import com.example.restaurantes.R

class MenuAdapter(
    private val menus: List<MenuItem>
) : RecyclerView.Adapter<MenuAdapter.MenuViewHolder>() {

    class MenuViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itPlato: TextView = itemView.findViewById(R.id.itPlato)
        val itPrecio: TextView = itemView.findViewById(R.id.itPrecio)
        val ratingBar: RatingBar = itemView.findViewById(R.id.ratingBar)
        val itImg: ImageView = itemView.findViewById(R.id.itImg)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_menu, parent, false)
        return MenuViewHolder(view)
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        val menuItem = menus[position]
        holder.itPlato.text = menuItem.nomPlato
        holder.itPrecio.text = "${menuItem.precio} $"
        holder.ratingBar.rating = menuItem.calificacion?.toFloat() ?: 0f


    }

    override fun getItemCount(): Int = menus.size
}