import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import android.widget.ArrayAdapter
import com.example.restaurantes.Oferta
import com.example.restaurantes.R

class Adaptador_Ofertas(context: Context, private val ofertas: List<Oferta>) : ArrayAdapter<Oferta>(context, 0, ofertas) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val oferta = getItem(position)
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_oferta, parent, false)

        // Asignar valores a los componentes del layout item_oferta.xml
        val tituloTextView = view.findViewById<TextView>(R.id.itTitulo)
        val precioTextView = view.findViewById<TextView>(R.id.itPrecio)
        val imagenImageView = view.findViewById<ImageView>(R.id.itImg)
        val ratingBar = view.findViewById<RatingBar>(R.id.ratingBar)

        // Establecemos los valores
        tituloTextView.text = oferta?.titulo
        precioTextView.text = oferta?.precio
        imagenImageView.setImageResource(oferta?.imagenResId ?: 0)
        ratingBar.rating = oferta?.rating ?: 0f

        return view
    }
}