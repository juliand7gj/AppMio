package db;

/**
 * Created by juliand7gj on 16/11/17.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import model.PuntoRecarga;

public class LogicDataBase extends SQLiteOpenHelper {

    private static final int DB_VERSION  = 1;
    private static final String NOMBRE_BASE = "dbMIO";


    public LogicDataBase(Context context) {
        super(context, NOMBRE_BASE, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(DataBase.SQL_CREATE_TABLE_PUNTOS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void insertarPunto(PuntoRecarga punto){
        SQLiteDatabase db = getWritableDatabase();
        if(db != null){
            ContentValues valores = new ContentValues();
            valores.put(DataBase.DatosColumnasPuntos.PUNTO_NOMBRE, punto.getNombre());
            valores.put(DataBase.DatosColumnasPuntos.PUNTO_DIRECCION, punto.getDirección());
            valores.put(DataBase.DatosColumnasPuntos.PUNTO_ESTADO, punto.getEstado());
            valores.put(DataBase.DatosColumnasPuntos.PUNTO_LATITUD, punto.getLatitud());
            valores.put(DataBase.DatosColumnasPuntos.PUNTO_LONGITUD, punto.getLongitud());
            db.insert(DataBase.TABLA_PUNTOS, null, valores);
            db.close();
        }
    }
}