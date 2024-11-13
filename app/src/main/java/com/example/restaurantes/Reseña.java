package com.example.restaurantes;

public class Reseña {
    String usuario;
    String restaurante;
    String idUsuario;
    String comentario;
    String dirPerfil;
    public Reseña (String usuario, String restaurante, String idUsuario, String comentario,String dirPerfil){
        this.usuario = usuario;
        this.restaurante= restaurante;
        this.idUsuario= idUsuario;
        this.comentario= comentario;
        this.dirPerfil = dirPerfil;
    }
    public Reseña (){}
    public String getFoto(){
        return dirPerfil;
    }
    public String getNombre(){
        return usuario;
    }
    public String getComentario(){
        return comentario;
    }
}
