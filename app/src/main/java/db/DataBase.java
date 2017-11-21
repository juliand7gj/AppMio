package db;

import android.provider.BaseColumns;

/**
 * Created by juliand7gj on 16/11/17.
 */

public class DataBase {

    public static final String TABLA_PUNTOS = "puntosRecarga";

    public static final String SQL_CREATE_TABLE_PUNTOS = "CREATE TABLE " +
            DataBase.TABLA_PUNTOS + " ("+
            DatosColumnasPuntos.PUNTO_NOMBRE + " TEXT PRIMARY KEY,"+
            DatosColumnasPuntos.PUNTO_DIRECCION + " TEXT,"+
            DatosColumnasPuntos.PUNTO_ESTADO + " TEXT,"+
            DatosColumnasPuntos.PUNTO_LATITUD + " DOUBLE,"+
            DatosColumnasPuntos.PUNTO_LONGITUD + " DOUBLE)";



    public static class DatosColumnasPuntos implements BaseColumns {
        public static final String PUNTO_NOMBRE = "nombre";
        public static final String PUNTO_DIRECCION = "direccion";
        public static final String PUNTO_ESTADO = "estado";
        public static final String PUNTO_LATITUD = "latitud";
        public static final String PUNTO_LONGITUD = "longitud";
    }

}
