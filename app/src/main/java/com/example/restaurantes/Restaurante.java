package com.example.restaurantes;

import android.net.Uri;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class Restaurante {
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
}