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


public class MenuRestauranteAdapter extends RecyclerView.Adapter<MenuRestauranteAdapter.ViewHolder> {
    private List<Menu> mData;
    private LayoutInflater mInflater;
    private Context context;
    final MenuRestauranteAdapter.OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick (Menu item);
    }
    public MenuRestauranteAdapter(List<Menu> itemList, Context context, MenuRestauranteAdapter.OnItemClickListener listener) {
        this.mData = itemList;
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.listener = listener;
    }

    @Override
    public int getItemCount () {return mData.size();}

    @Override
    public MenuRestauranteAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = mInflater.inflate(R.layout.item_menu_restaurante,null);
        return new MenuRestauranteAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder (final MenuRestauranteAdapter.ViewHolder holder, final int position){
        holder.bindData(mData.get(position));
    }

    public void setItems(List<Menu> items){mData=items;}

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView iconImage;
        TextView nombrePlato;
        TextView precio;
        TextView ingrediente;
        ViewHolder(View itemView){
            super (itemView);
            iconImage = itemView.findViewById(R.id.fotoPlatoMenu);
            nombrePlato=itemView.findViewById(R.id.nombrePlatoMenu);
            precio=itemView.findViewById(R.id.precioPlatoMenu);
            ingrediente=itemView.findViewById(R.id.ingredientesPlatoMenu);
        }
        void bindData(final Menu item){
            Glide.with(context)
                    .load(item.getFotoPlato())
                    .circleCrop()
                    .into(iconImage);
            nombrePlato.setText(item.getNombrePlato());
            precio.setText("Precio: " +item.getPrecioPlato() + "Bs.");


            itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    listener.onItemClick(item);
                }
            });
        }
    }
}