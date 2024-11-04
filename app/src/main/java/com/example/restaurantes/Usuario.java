package com.example.restaurantes;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Usuario implements Parcelable {
    String nombre;
    String correo;
    String contraseña;
    String fechaNacimiento;
    boolean tieneRestaurante;
    String restauranteRef;

    public Usuario () {

    }
    public Usuario(String nombre, String correo, String contraseña, String fechaNacimiento, boolean tieneRestaurante) {
        this.nombre = nombre;
        this.correo = correo;
        this.contraseña = contraseña;
        this.fechaNacimiento = fechaNacimiento;
        this.tieneRestaurante = tieneRestaurante;
        restauranteRef = "";
    }

    protected Usuario(Parcel in) {
        nombre = in.readString();
        correo = in.readString();
        contraseña = in.readString();
        fechaNacimiento = in.readString();
        tieneRestaurante = in.readByte() != 0;
        restauranteRef = in.readString();
    }

    public static final Creator<Usuario> CREATOR = new Creator<Usuario>() {
        @Override
        public Usuario createFromParcel(Parcel in) {
            return new Usuario(in);
        }

        @Override
        public Usuario[] newArray(int size) {
            return new Usuario[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(nombre);
        parcel.writeString(correo);
        parcel.writeString(contraseña);
        parcel.writeString(fechaNacimiento);
        parcel.writeByte((byte) (tieneRestaurante ? 1 : 0));
        parcel.writeString(restauranteRef);
    }
}