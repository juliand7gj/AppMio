package model;

/**
 * Created by Jorge Castaño on 19/11/2017.
 */

public class Vehiculo {

    float latitud, longitud;
    String ruta;

    public Vehiculo(float latitud, float longitud,String ruta){
        this.latitud = latitud;
        this.longitud = longitud;
        this.ruta = ruta;
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

    public String getRuta() {
        return ruta;
    }

    public void setRuta(String ruta) {
        this.ruta = ruta;
    }
}
