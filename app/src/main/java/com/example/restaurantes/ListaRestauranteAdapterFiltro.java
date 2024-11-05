package com.example.restaurantes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ListaRestauranteAdapterFiltro extends RecyclerView.Adapter<ListaRestauranteAdapterFiltro.ViewHolder> {
    private List<Restaurante> mData;
    private LayoutInflater mInflater;
    private Context context;
    final ListaRestauranteAdapterFiltro.OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick (Restaurante item);
    }
    public ListaRestauranteAdapterFiltro(List<Restaurante> itemList, Context context, ListaRestauranteAdapterFiltro.OnItemClickListener listener) {
        this.mData = itemList;
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.listener = listener;
    }

    @Override
    public int getItemCount () {return mData.size();}

    @Override
    public ListaRestauranteAdapterFiltro.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = mInflater.inflate(R.layout.item_restaurante_filtro,null);
        return new ListaRestauranteAdapterFiltro.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder (final ListaRestauranteAdapterFiltro.ViewHolder holder,  final int position){
        holder.bindData(mData.get(position));
    }

    public void setItems(List<Restaurante> items){mData=items;}

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView iconImage;
        TextView nombreTrabajador,usuario,rol;
        ViewHolder(View itemView){
            super (itemView);
            //iconImage = itemView.findViewById(R.id.iconImagenView);
            nombreTrabajador=itemView.findViewById(R.id.nombreRestaurante);
        }
        void bindData(final Restaurante item){
            usuario.setText(item.getNombreRestaurante());
            itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    listener.onItemClick(item);
                }
            });
        }
    }
}
