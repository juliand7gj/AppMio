package model;

public class Parada {

    private String ubicacion, nombre, id, estacionPadre, codigoPlataforma;
    private double longitud, latitud;
    private int tipo;
    public Parada(String id, String nombre, double latitud, double longitud, int tipo, String ep, String cp) {
        super();
        this.nombre = nombre;
        this.id = id;
        this.longitud = longitud;
        this.latitud = latitud;
        this.tipo = tipo;
        estacionPadre = ep;
        codigoPlataforma = cp;
    }

    public Parada(String id, String nombre, double latitud, double longitud, int tipo) {
        super();
        this.nombre = nombre;
        this.id = id;
        this.longitud = longitud;
        this.latitud = latitud;
        this.tipo = tipo;
        estacionPadre = "";
        codigoPlataforma = "";
    }

    public String getUbicacion() {
        return ubicacion;
    }
    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }
    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public double getLongitud() {
        return longitud;
    }
    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }
    public double getLatitud() {
        return latitud;
    }
    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }
    public int getTipo() {
        return tipo;
    }
    public void setTipo(int tipo) {
        this.tipo = tipo;
    }

    public String getEstacionPadre() {
        return estacionPadre;
    }

    public void setEstacionPadre(String estacionPadre) {
        this.estacionPadre = estacionPadre;
    }

    public String getCodigoPlataforma() {
        return codigoPlataforma;
    }

    public void setCodigoPlataforma(String codigoPlataforma) {
        this.codigoPlataforma = codigoPlataforma;
    }

    public String toString(){
        return "ID: "+id+"\n"+
                "Nombre: "+nombre+"\n"+
                "Latitud: "+latitud+"\n"+
                "Longitud: "+longitud+"\n"+
                "Tipo: "+tipo+"\n"+
                "Estación Padre: "+estacionPadre+"\n"+
                "Codigo Plataforma: "+codigoPlataforma+"\n";
    }

}