package com.example.prado.estaciones;

import java.util.Date;

public class UnidadesCalor {

    private  String Fecha;
    private double Tmax;
    private  double Tmin;

    public UnidadesCalor(String fecha, double tmax, double tmin) {
        Fecha = fecha;
        Tmax = tmax;
        Tmin = tmin;
    }

    public String getFecha() {
        return Fecha;
    }

    public void setFecha(String fecha) {
        Fecha = fecha;
    }

    public double getTmax() {
        return Tmax;
    }

    public void setTmax(float tmax) {
        Tmax = tmax;
    }

    public double getTmin() {
        return Tmin;
    }

    public void setTmin(float tmin) {
        Tmin = tmin;
    }
}
