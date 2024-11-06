package com.example.restaurantes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;


public class RestauranteAdapterFiltro extends RecyclerView.Adapter<RestauranteAdapterFiltro.ViewHolder> {
    private List<Restaurante> mData;
    private LayoutInflater mInflater;
    private Context context;
    final RestauranteAdapterFiltro.OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick (Restaurante item);
    }
    public RestauranteAdapterFiltro(List<Restaurante> itemList, Context context, RestauranteAdapterFiltro.OnItemClickListener listener) {
        this.mData = itemList;
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

    public void setItems(List<Restaurante> items){mData=items;}

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