package com.example.restaurantes;

import java.util.ArrayList;

public class Menu {
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

    public String getFotoPlato (){
        return fotoPlato;
    }
    public String getNombrePlato (){
        return nombrePlato;
    }
    public String getPrecioPlato (){
        return precio;
    }

}
