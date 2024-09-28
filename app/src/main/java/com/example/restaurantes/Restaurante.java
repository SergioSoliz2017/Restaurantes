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
}