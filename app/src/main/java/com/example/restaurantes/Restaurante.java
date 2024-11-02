package com.example.restaurantes;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class Restaurante implements Parcelable {
    String nombreRestaurante;
    ArrayList<Horario> horarioAtencion;
    String celularreferencia;
    LatLng ubicacion;
    Uri logo;
    int numero;
    ArrayList<String> categoria;
    ArrayList<MenuItem> menus;

    public Restaurante(){}

    public Restaurante(String nombreRestaurante, ArrayList<Horario> horarioAtencion, String celularreferencia,
                       LatLng ubicacion, Uri logo, int numero, ArrayList<String> categoria, ArrayList<MenuItem> menus) {
        this.nombreRestaurante = nombreRestaurante;
        this.horarioAtencion = horarioAtencion;
        this.celularreferencia = celularreferencia;
        this.ubicacion = ubicacion;
        this.logo = logo;
        this.numero = numero;
        this.categoria = categoria;
        this.menus = menus; // Asignar la lista de menús
    }

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

    protected Restaurante(Parcel in) {
        nombreRestaurante = in.readString();
        horarioAtencion = in.readParcelable(Horario.class.getClassLoader());
        celularreferencia = in.readString();
        ubicacion = in.readParcelable(LatLng.class.getClassLoader());
        logo = in.readParcelable(Uri.class.getClassLoader());
        numero = in.readInt();
        categoria = in.createStringArrayList();
        menus = in.createTypedArrayList(MenuItem.CREATOR);
    }

    public static final Parcelable.Creator<Restaurante> CREATOR = new Parcelable.Creator<Restaurante>() {
        @Override
        public Restaurante createFromParcel(Parcel in) {
            return new Restaurante(in);
        }

        @Override
        public Restaurante[] newArray(int size) {
            return new Restaurante[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(nombreRestaurante);
        dest.writeParcelable((Parcelable) horarioAtencion, flags);
        dest.writeString(celularreferencia);
        dest.writeParcelable(ubicacion, flags);
        dest.writeParcelable(logo, flags);
        dest.writeInt(numero);
        dest.writeStringList(categoria);
        dest.writeTypedList(menus);
    }
}