package com.example.prado.estaciones;

/**
 * Created by Prado on 21/03/2018.
 */

public class DatosEstaciones {

    String Nombre;
    double latitud;
    double longitud;

    public DatosEstaciones(String Nombre, double latitud, double longitud) {
        this.Nombre = Nombre;
        this.latitud = latitud;
        this.longitud = longitud;
    }

    public String getNombre() {
        System.out.println(this.Nombre);
        return this.Nombre;

    }

    public double getLatitud() {
        System.out.println(this.latitud);
        return this.latitud;
    }

    public double getLongitud() {
        System.out.println(this.longitud);
        return this.longitud;
    }

    public void setNombre(String Nombre) {
        this.Nombre = Nombre;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

}
