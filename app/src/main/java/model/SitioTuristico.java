package model;

public class SitioTuristico {

    private String  direccion, nombre, descripcion, tipo, foto;
    private double latitud, longitud;

    public SitioTuristico( String nom, String dir, String desc, double lat, double lon, String tip){
        latitud = lat;
        longitud = lon;
        descripcion = desc;
        nombre = nom;
        setDireccion(dir);
        tipo = tip;
        foto = "";
    }

    public SitioTuristico( String nom, String dir, String desc, double lat, double lon, String tip, String fot){
        latitud = lat;
        longitud = lon;
        descripcion = desc;
        nombre = nom;
        setDireccion(dir);
        tipo = tip;
        foto = fot;
    }

    public double getLatitud() {
        return latitud;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }


    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String toString(){
        return "Nombre: "+nombre+"\n"+
                "Direccion: "+direccion+"\n"+
                "Descripcion: "+descripcion+"\n"+
                "Latitud: "+latitud+"\n"+
                "Longitud: "+longitud+"\n"+
                "Tipo: "+tipo+"\n"+
                "Foto: "+foto+"\n";
    }

}