package com.example.restaurantes;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.Date;

public class Oferta implements Parcelable {
    private String descripcion;
    private long fechaFin;
    private long fechaInicio;
    private String imagen;
    private String menuId;
    private double precio;
    private String restauranteId;
    private String titulo;

    public Oferta() {
        this.descripcion = "";
        this.fechaFin = new Date().getTime();
        this.fechaInicio = new Date().getTime();
        this.imagen = "";
        this.menuId = "";
        this.precio = 0.0;
        this.restauranteId = "";
        this.titulo = "";
    }

    public Oferta(String descripcion, long fechaFin, long fechaInicio, String imagen, String menuId, double precio, String restauranteId, String titulo) {
        this.descripcion = descripcion;
        this.fechaFin = fechaFin;
        this.fechaInicio = fechaInicio;
        this.imagen = imagen;
        this.menuId = menuId;
        this.precio = precio;
        this.restauranteId = restauranteId;
        this.titulo = titulo;
    }

    // Getters y setters
    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public long getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(long fechaFin) {
        this.fechaFin = fechaFin;
    }

    public long getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(long fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public String getMenuId() {
        return menuId;
    }

    public void setMenuId(String menuId) {
        this.menuId = menuId;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public String getRestauranteId() {
        return restauranteId;
    }

    public void setRestauranteId(String restauranteId) {
        this.restauranteId = restauranteId;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    // Conversión de Long a Date
    public Date getFechaFinDate() {
        return new Date(fechaFin);
    }

    public Date getFechaInicioDate() {
        return new Date(fechaInicio);
    }

    // Conversión de Date a Long
    public static long dateToLong(Date date) {
        return date.getTime();
    }

    // Implementación de Parcelable
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.descripcion);
        dest.writeLong(this.fechaFin);
        dest.writeLong(this.fechaInicio);
        dest.writeString(this.imagen);
        dest.writeString(this.menuId);
        dest.writeDouble(this.precio);
        dest.writeString(this.restauranteId);
        dest.writeString(this.titulo);
    }

    public static final Parcelable.Creator<Oferta> CREATOR = new Parcelable.Creator<Oferta>() {
        @Override
        public Oferta createFromParcel(Parcel source) {
            return new Oferta(source);
        }

        @Override
        public Oferta[] newArray(int size) {
            return new Oferta[size];
        }
    };

    private Oferta(Parcel in) {
        this.descripcion = in.readString();
        this.fechaFin = in.readLong();
        this.fechaInicio = in.readLong();
        this.imagen = in.readString();
        this.menuId = in.readString();
        this.precio = in.readDouble();
        this.restauranteId = in.readString();
        this.titulo = in.readString();
    }
}