package com.example.restaurantes;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class Menu implements Parcelable {
    String fotoPlato;
    String nombrePlato;
    String precio;
    ArrayList<String> ingredientes;

    public Menu (){}

    public Menu (String fotoPlato , String nombrePlato , String precio, ArrayList<String> ingredientes){
        this.fotoPlato=fotoPlato;
        this.precio = precio;
        this.nombrePlato=nombrePlato;
        this.ingredientes = ingredientes;
    }

    protected Menu(Parcel in) {
        fotoPlato = in.readString();
        nombrePlato = in.readString();
        precio = in.readString();
        ingredientes = in.createStringArrayList();
    }

    public static final Creator<Menu> CREATOR = new Creator<Menu>() {
        @Override
        public Menu createFromParcel(Parcel in) {
            return new Menu(in);
        }

        @Override
        public Menu[] newArray(int size) {
            return new Menu[size];
        }
    };

    public String getFotoPlato (){
        return fotoPlato;
    }
    public String getNombrePlato (){
        return nombrePlato;
    }
    public String getPrecioPlato (){
        return precio;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(fotoPlato);
        parcel.writeString(nombrePlato);
        parcel.writeString(precio);
        parcel.writeStringList(ingredientes);
    }

    public ArrayList<String> getIngredientes (){
        return ingredientes;
    }
}
