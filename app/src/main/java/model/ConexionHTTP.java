package model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Jorge Castaño on 19/11/2017.
 */

public class ConexionHTTP extends Thread{

    private String respuesta;
    private ArrayList<Seccion> secciones;
    private boolean terminoProceso;
    private String ruta;
    private GtfsRealtime realtime;
    private boolean estado;

    public ConexionHTTP(String ruta) {
        realtime = new GtfsRealtime();
        secciones = new ArrayList<>();
        this.ruta = ruta;
        estado = false;
        start();
    }

    public String getRespuesta() {
        return respuesta;
    }

    public void run() {
        super.run();

        try {
            if(ruta.equals("http://190.216.202.35:90/gtfs/realtime/")){
                respuesta = clienteHttp(ruta);
            }else{
                respuesta = clienteHttp(ruta);
            }

        }catch (IOException e) {
            e.printStackTrace();
        }

    }

    public String clienteHttp(String dirweb) throws IOException {

        String body = " ";

        try {

            URL url = new URL(dirweb);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            Integer codigoRespuesta = urlConnection.getResponseCode();
            //int codigoRespuesta = 401;

            if(codigoRespuesta==HttpURLConnection.HTTP_UNAUTHORIZED){
                Authenticator.setDefault(new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication("appconcurso", "JcYbIry5sA".toCharArray());
                    }
                });
                //   url = new URL(dirweb+"vehiclePositions.pb.txt");
                urlConnection = (HttpURLConnection) url.openConnection();
            }

            body = readStream(urlConnection.getInputStream());
            urlConnection.disconnect();
        } catch (MalformedURLException e) {
            body = e.toString(); //Error URL incorrecta
            e.printStackTrace();
        } catch (SocketTimeoutException e){
            body = e.toString(); //Error: Finalizado el timeout esperando la respuesta del servidor.
            e.printStackTrace();
        } catch (Exception e) {
            body = e.toString();//Error diferente a los anteriores.
            e.printStackTrace();
        }
        return body;
    }


    private String readStream(InputStream in) throws IOException{

        BufferedReader r = null;
        r = new BufferedReader(new InputStreamReader(in));
        StringBuilder total = new StringBuilder();
        String line;

        if(ruta.equals("http://190.216.202.35:90/gtfs/realtime/")){

            realtime.descargar("http://190.216.202.35:90/gtfs/realtime/vehiclePositions.pb");


        }else {
            while ((line = r.readLine()) != null) {
                total.append(line).append("\n");
            }
            cargarSecciones(total.toString());
        }


        if(r != null){
            r.close();
        }

        in.close();
        return total.toString();
    }

    public void cargarSecciones(String respuesta) {
        JSONObject json;
        String nameRuta = "";
        try {
            json = new JSONObject(respuesta);
            boolean st = json.getBoolean("status");
            setEstado(st);
            if(estado){
                JSONObject jsonObj = json.getJSONObject("route");

                JSONArray elem1 = jsonObj.getJSONArray("sections");
                for (int i = 0; i < elem1.length(); i++) {
                    JSONObject mJsonObjectProperty = elem1.getJSONObject(i);

                    if (mJsonObjectProperty.has("name")) {
                        nameRuta = mJsonObjectProperty.getString("name");
                        if(nameRuta.equalsIgnoreCase("")){
                            nameRuta = "Caminar";
                        }
                    }

                    String tipo = mJsonObjectProperty.getString("type");
                    if(tipo.equalsIgnoreCase("JOURNEY")){
                        tipo = "Bus";
                    }else{
                        tipo = "Caminar";
                    }


                    JSONArray elem2 = mJsonObjectProperty.getJSONArray("locations");
                    for (int j = 0; j < elem2.length(); j++) {
                        JSONObject mJsonObjectProperty2 = elem2.getJSONObject(j);

                        String lon = mJsonObjectProperty2.getString("x").trim();
                        lon = lon.substring(0, 3) + "." + lon.substring(3, lon.length());
                        String lat = mJsonObjectProperty2.getString("y").trim();
                        lat = lat.substring(0, 1) + "." + lat.substring(1, lat.length());

                        Float latitud = Float.parseFloat(lat);
                        Float longitud = Float.parseFloat(lon);
                        String nameStation = mJsonObjectProperty2.getString("name");

                        Seccion s = new Seccion(nameStation, latitud, longitud, nameRuta,tipo);
                        secciones.add(s);
                    }
                }
                }
                terminoProceso = true;

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public boolean isTerminoProceso() {
        return terminoProceso;
    }

    public ArrayList<Seccion> getSecciones() { return secciones; }

    public GtfsRealtime getRealtime() {
        return realtime;
    }

    public boolean getEstado(){
        return estado;
    }

    public void setEstado(boolean x){
        estado = x;
    }

}