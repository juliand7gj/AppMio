package model;

/**
 * Created by Jorge Castaño on 19/11/2017.
 */

public class Vehiculo {

    float latitud, longitud;

    public Vehiculo(float latitud, float longitud){
        this.latitud = latitud;
        this.longitud = longitud;

    }

    public double getLatitud() {
        return latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLatitud(float latitud) {
        this.latitud = latitud;
    }

    public void setLongitud(float longitud) {
        this.longitud = longitud;
    }
}
