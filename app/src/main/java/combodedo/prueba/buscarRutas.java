package combodedo.prueba;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

public class buscarRutas extends AppCompatActivity {

    private ArrayList<String> ruts, rutasescogidas;
    private AutoCompleteTextView actv;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private HashMap<String,String> routes;
    private boolean activosbuses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buscar_rutas);
        init();
    }

    private void init() {
        rutasescogidas = new ArrayList<String>();
        activosbuses = false;
        sp = this.getSharedPreferences("com.example.app", Context.MODE_PRIVATE);
        editor = sp.edit();
        ruts = new ArrayList<String>();
        routes = leerRutas();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.select_dialog_item,ruts);
        actv= (AutoCompleteTextView)findViewById(R.id.autoCompleteText);
        actv.setThreshold(1);//will start working from first character
        actv.setAdapter(adapter);//setting the adapter data into the AutoCompleteTextView
        actv.setTextColor(Color.BLACK);
    }

    public HashMap<String,String> leerRutas(){
        HashMap<String, String> rutas = new HashMap<String,String>();
        BufferedReader reader = null;
        StringBuilder sb = null;
        try {
            reader = new BufferedReader(
                    new InputStreamReader(getAssets().open("routes.txt")));
            sb = new StringBuilder();
            String line = reader.readLine();

            while ((line = reader.readLine()) != null) {
                String[] ruta = line.split(",");
                rutas.put(ruta[0],ruta[1]);
                ruts.add(ruta[1]);
            }

            reader.close();
        } catch (IOException e) {
            //log the exception
        }
        return rutas;
    }

    public void borrartodosfiltros(View v){

        activosbuses = false;
        actv.setText("");
        rutasescogidas.clear();
        //
        editor.putBoolean("activosbuses", activosbuses);
        Gson gson = new Gson();
        String json = gson.toJson(rutasescogidas);
        editor.putString("rutasescogidas", json);
        editor.commit();
    }

    public void quitarfiltro(View v){

        String es = actv.getText().toString();
        if(es!=null && !es.equals("")) {
            for (int y = 0; y<rutasescogidas.size();y++){
                if(rutasescogidas.get(y).equalsIgnoreCase(es)){
                    rutasescogidas.remove(y);
                }
            }

            editor.putBoolean("activosbuses", activosbuses);
            Gson gson = new Gson();
            String json = gson.toJson(rutasescogidas);
            editor.putString("rutasescogidas", json);
            editor.commit();
            actv.setText("");
        }else{
            Toast.makeText(getApplicationContext(), "Escribe una ruta", Toast.LENGTH_SHORT).show();
        }
    }

    public void ponerfiltro(View v){

        String es = actv.getText().toString();
        if(es!=null && !es.equals("")) {
            activosbuses = true;

            rutasescogidas.add(es);
            actv.setText("");
            //
            editor.putBoolean("activosbuses", activosbuses);
            Gson gson = new Gson();
            String json = gson.toJson(rutasescogidas);
            editor.putString("rutasescogidas", json);
            editor.commit();
        }else{
            Toast.makeText(getApplicationContext(), "Escribe una ruta", Toast.LENGTH_SHORT).show();
        }

    }

    public void volver(View v){
        editor.commit();
        Intent i = new Intent(this, ubicacion.class);
        Toast.makeText(getApplicationContext(), "Los buses cargarán en breve", Toast.LENGTH_SHORT);
        startActivity(i);
    }

}
