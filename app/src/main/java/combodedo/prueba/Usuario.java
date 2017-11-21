package combodedo.prueba;

/**
 * Created by juliand7gj on 26/10/17.
 */

public class Usuario {

    private  String nombre;
    private String contra;

    public Usuario(String nombre, String contra) {
        this.nombre = nombre;
        this.contra = contra;
    }

    public Usuario(){

    }

    public String getNombre() {
        return nombre;
    }

    public String getContra() {
        return contra;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setContra(String contra) {
        this.contra = contra;
    }
}
