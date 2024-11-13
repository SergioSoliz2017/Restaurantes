package com.example.restaurantes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Categoria {
    private String nombre;
    private ArrayList<String> subCategorias;
    public  Categoria (String nombre , ArrayList<String> subCategorias){
        this.nombre = nombre;
        this.subCategorias = subCategorias;
    }
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put(nombre, subCategorias);
        return map;
    }
    public String getNombre(){
        return nombre;
    }
    public ArrayList<String> getSubCategorias(){
        return subCategorias;
    }
}
