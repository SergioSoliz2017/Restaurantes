package com.example.restaurantes;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;


public class RestauranteAdapterFiltro extends RecyclerView.Adapter<RestauranteAdapterFiltro.ViewHolder> {
    private List<Restaurante> mData;
    private LayoutInflater mInflater;
    private Context context;
    final RestauranteAdapterFiltro.OnItemClickListener listener;
    private List<Restaurante> mDataOriginal;

    public interface OnItemClickListener {
        void onItemClick (Restaurante item);
    }
    public RestauranteAdapterFiltro(List<Restaurante> itemList, Context context, RestauranteAdapterFiltro.OnItemClickListener listener) {
        this.mData = itemList;
        this.mDataOriginal = itemList;
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.listener = listener;
    }

    @Override
    public int getItemCount () {return mData.size();}

    @Override
    public RestauranteAdapterFiltro.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = mInflater.inflate(R.layout.item_restaurante_filtro,null);
        return new RestauranteAdapterFiltro.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder (final RestauranteAdapterFiltro.ViewHolder holder,  final int position){
        holder.bindData(mData.get(position));
    }

    public void setItems(List<Restaurante> items){mData=items;mDataOriginal=items;
        notifyDataSetChanged();
    }

   /*
public void filtrar(String texto, List<String> serviciosSeleccionados) {
    if (texto.isEmpty() && serviciosSeleccionados.isEmpty()) {
        mData = mDataOriginal;
    } else {
        List<Restaurante> filtrada = new ArrayList<>();
        for (Restaurante restaurante : mDataOriginal) {
            boolean coincideTexto = restaurante.getNombreRestaurante().toLowerCase().contains(texto.toLowerCase());

            // verificación de los check
            //servicios
            boolean coincideServicios = true;
            for (String servicio : serviciosSeleccionados) {
                if (restaurante.getServicios() == null || !restaurante.getServicios().contains(servicio)) {
                    coincideServicios = false;
                    break;
                }
            }
            //region
            //for (String servicio : serviciosSeleccionados) {
            //  if (restaurante.getCategoria("Region") == null || !restaurante.getCategoria("Region").contains(servicio)) {
            //      coincideServicios = false;
            //      break;
            //  }
            //}


            // Agrega el restaurante solo si coincide el texto y los servicios seleccionados
            if (coincideTexto && coincideServicios) {
                filtrada.add(restaurante);
            }
        }
        mData = filtrada;
    }
    notifyDataSetChanged();
}
*/

    public void filtrar(String texto, List<String> serviciosSeleccionados, List<String> regionesSeleccionadas) {
        if (texto.isEmpty() && serviciosSeleccionados.isEmpty() && regionesSeleccionadas.isEmpty()) {
            mData = mDataOriginal;
        } else {
            List<Restaurante> filtrada = new ArrayList<>();
            for (Restaurante restaurante : mDataOriginal) {
                boolean coincideTexto = restaurante.getNombreRestaurante().toLowerCase().contains(texto.toLowerCase());

                // Verificación de los check
                // Servicios
                boolean coincideServicios = serviciosSeleccionados.isEmpty() || serviciosSeleccionados.stream()
                        .allMatch(servicio -> restaurante.getServicios() != null && restaurante.getServicios().contains(servicio));

                // Regiones
                boolean coincideRegiones = regionesSeleccionadas.isEmpty() || regionesSeleccionadas.stream()
                        .anyMatch(region -> restaurante.getCategoria("Region") != null && restaurante.getCategoria("Region").contains(region));

                // Agrega el restaurante solo si coincide el texto, los servicios y las regiones seleccionadas
                if (coincideTexto && coincideServicios && coincideRegiones) {
                    filtrada.add(restaurante);
                }
            }
            Log.d("este valor tieneeeee", String.valueOf(regionesSeleccionadas));
            mData = filtrada;
        }
        notifyDataSetChanged();
    }



    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView iconImage;
        TextView nombreRestaurante;
        ViewHolder(View itemView){
            super (itemView);
            iconImage = itemView.findViewById(R.id.logoRestaurante);
            nombreRestaurante=itemView.findViewById(R.id.nombreRestaurante);
        }
        void bindData(final Restaurante item){
            Glide.with(context)
                    .load(item.getDirLogo())
                    .circleCrop()
                    .into(iconImage);
            nombreRestaurante.setText(item.getNombreRestaurante());
            itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    listener.onItemClick(item);
                }
            });
        }
    }
}