package model;

/**
 * Created by Jorge Castaño on 19/11/2017.
 */

public class Vehiculo {

    float latitud, longitud;
    String ruta,stopid;

    public Vehiculo(float latitud, float longitud,String ruta, String stopid){
        this.latitud = latitud;
        this.longitud = longitud;
        this.ruta = ruta;
        this.stopid = stopid;
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

    public String getStopid() {
        return stopid;
    }

    public void setStopid(String stopid) {
        this.stopid = stopid;
    }
}
