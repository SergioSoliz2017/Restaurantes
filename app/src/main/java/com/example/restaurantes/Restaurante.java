package com.example.restaurantes;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class Restaurante implements Parcelable {
    String nombreRestaurante;
    ArrayList<Horario> horarioAtencion;
    String celularreferencia;
    LatLng ubicacion;
    Uri logo;
    int numero;
    ArrayList<Categoria> categoria;
    ArrayList<MenuItem> menus;
    ArrayList<String> servicios;

    public Restaurante(){}

    public Restaurante(String nombreRestaurante, ArrayList<Horario> horarioAtencion, String celularreferencia,
                       LatLng ubicacion, Uri logo, int numero, ArrayList<Categoria> categoria, ArrayList<MenuItem> menus) {
        this.nombreRestaurante = nombreRestaurante;
        this.horarioAtencion = horarioAtencion;
        this.celularreferencia = celularreferencia;
        this.ubicacion = ubicacion;
        this.logo = logo;
        this.numero = numero;
        this.categoria = categoria;
        this.menus = menus; // Asignar la lista de menús
    }

    protected Restaurante(Parcel in) {
        nombreRestaurante = in.readString();
        celularreferencia = in.readString();
        ubicacion = in.readParcelable(LatLng.class.getClassLoader());
        logo = in.readParcelable(Uri.class.getClassLoader());
        numero = in.readInt();
        menus = in.createTypedArrayList(MenuItem.CREATOR);
        servicios = in.createStringArrayList();
    }

    public static final Creator<Restaurante> CREATOR = new Creator<Restaurante>() {
        @Override
        public Restaurante createFromParcel(Parcel in) {
            return new Restaurante(in);
        }

        @Override
        public Restaurante[] newArray(int size) {
            return new Restaurante[size];
        }
    };

    public ArrayList<MenuItem> getMenus() {
        return menus;
    }

    public void setMenus(ArrayList<MenuItem> menus) {
        this.menus = menus;
    }

    public String getNombreRestaurante() {
        return nombreRestaurante;
    }

    public void setNombreRestaurante(String nombreRestaurante) {
        this.nombreRestaurante = nombreRestaurante;
    }
    public Uri getLogo() {return logo;}


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(nombreRestaurante);
        parcel.writeString(celularreferencia);
        parcel.writeParcelable(ubicacion, i);
        parcel.writeParcelable(logo, i);
        parcel.writeInt(numero);
        parcel.writeTypedList(menus);
        parcel.writeStringList(servicios);
    }

}