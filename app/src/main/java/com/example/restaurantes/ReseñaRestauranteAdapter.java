package com.example.restaurantes;



import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;


public class ReseñaRestauranteAdapter extends RecyclerView.Adapter<ReseñaRestauranteAdapter.ViewHolder> {
    private List<Reseña> mData;
    private LayoutInflater mInflater;
    private Context context;

    public ReseñaRestauranteAdapter(List<Reseña> itemList, Context context) {
        this.mData = itemList;
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
    }

    @Override
    public int getItemCount () {return mData.size();}

    @Override
    public ReseñaRestauranteAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = mInflater.inflate(R.layout.item_resenia_restaurante,null);
        return new ReseñaRestauranteAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder (final ReseñaRestauranteAdapter.ViewHolder holder, final int position){
        holder.bindData(mData.get(position));
    }

    public void setItems(List<Reseña> items){mData=items;}

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView iconImage;
        TextView nombre;
        TextView comentario;
        ViewHolder(View itemView){
            super (itemView);
            iconImage = itemView.findViewById(R.id.fotoUsuarioReseña);
            nombre=itemView.findViewById(R.id.nombreUsuarioReseña);
            comentario=itemView.findViewById(R.id.comentarioReseña);
        }
        void bindData(final Reseña item){

            Glide.with(context)
                    .load(item.getFoto())
                    .circleCrop()
                    .into(iconImage);
            nombre.setText(item.getNombre());
            comentario.setText(item.getComentario());
        }
    }
}