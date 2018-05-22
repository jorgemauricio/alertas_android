package com.example.prado.estaciones;

public class Estaciones {
    private String Nombre;
    private int Indice;

    public Estaciones(String nombre, int indice) {

        Nombre = nombre;
        Indice = indice;
    }

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String nombre) {
        Nombre = nombre;
    }

    public int getIndice() {
        return Indice;
    }

    public void setIndice(int indice) {
        Indice = indice;
    }


}
