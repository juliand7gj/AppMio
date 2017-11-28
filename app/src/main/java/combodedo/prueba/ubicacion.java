package combodedo.prueba;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.kml.KmlLayer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import model.ConexionHTTP;
import model.Parada;
import model.PuntoRecarga;
import model.Seccion;
import model.SitioTuristico;
import model.Vehiculo;

public class ubicacion extends AppCompatActivity implements OnMapReadyCallback, PlaceSelectionListener, View.OnClickListener {

    private FloatingActionButton fab_filter, fab_money, fab_bus_stop, fab_bus, fab_nav;
    Animation fab_open, fab_close, fab_rotate_clockwise, fab_rotate_anticlockwise;
    private boolean isOpen = false;

    private GoogleMap mMap;
    private static double latitud,longitud;
    private ArrayList<Marker> puntosRecarga,busesVivo,paradas,planeacion;
    private Marker seleccionado,actual;

    private int primero,primerZoom;
    private LatLng sel;
    private ConexionHTTP conexionHTTP;
    private boolean activosbuses;
    private ArrayList<Seccion> secciones;
    private HashMap<String,String> routes;
    private PlaceAutocompleteFragment autocompleteFragment;
    private AutoCompleteTextView actv;
    private ArrayList<String> ruts,rutasescogidas;
    private KmlLayer layer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ruts = new ArrayList<String>();
        routes = leerRutas();
        activosbuses = false;
        primero = 0;
        primerZoom = 0;
        sel = new LatLng(0,0);
        puntosRecarga = new ArrayList<Marker>();
        busesVivo = new ArrayList<Marker>();
        paradas = new ArrayList<Marker>();
        planeacion = new ArrayList<Marker>();
        latitud = 0;
        longitud = 0;
        rutasescogidas= new ArrayList<String>();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ubicacion);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        autocompleteFragment = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        autocompleteFragment.setOnPlaceSelectedListener(this);
        autocompleteFragment.getView().findViewById(R.id.place_autocomplete_clear_button).setOnClickListener(this);

        //AutocompleteFilter filter = new AutocompleteFilter.Builder().setTypeFilter(Place.TYPE_ADMINISTRATIVE_AREA_LEVEL_3).setCountry("CO").build();
        //autocompleteFragment.setFilter(filter);


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.select_dialog_item,ruts);
        actv= (AutoCompleteTextView)findViewById(R.id.autoCompleteText);
        actv.setThreshold(1);//will start working from first character
        actv.setAdapter(adapter);//setting the adapter data into the AutoCompleteTextView
        actv.setTextColor(Color.RED);

        fab();
    }

    public void fab() {

        fab_filter = (FloatingActionButton) findViewById(R.id.fab_filter);
        fab_money = (FloatingActionButton) findViewById(R.id.fab_money);
        fab_bus_stop = (FloatingActionButton) findViewById(R.id.fab_bus_stop);
        fab_bus = (FloatingActionButton) findViewById(R.id.fab_bus);
        fab_nav = (FloatingActionButton) findViewById(R.id.fab_nav);

        fab_open = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_close);
        fab_rotate_clockwise = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_clockwise);
        fab_rotate_anticlockwise = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_anticlockwise);

        /*Se hace la condicion que valida si se unde el boton de filtros*/
                fab_filter.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (isOpen){

                            fab_nav.startAnimation(fab_close);
                            fab_money.startAnimation(fab_close);
                            fab_bus_stop.startAnimation(fab_close);
                            fab_bus.startAnimation(fab_close);
                            fab_filter.startAnimation(fab_rotate_anticlockwise);

                    /*Se setea para que NO puedan ser clickeables*/
                            fab_nav.setClickable(false);
                            fab_money.setClickable(false);
                            fab_bus_stop.setClickable(false);
                            fab_bus.setClickable(false);
                            isOpen = false;
                        }else{
                            fab_nav.startAnimation(fab_open);
                            fab_money.startAnimation(fab_open);
                            fab_bus_stop.startAnimation(fab_open);
                            fab_bus.startAnimation(fab_open);
                            fab_filter.startAnimation(fab_rotate_clockwise);

                    /*Se setea para que puedan ser clickeables*/
                            fab_nav.setClickable(true);
                            fab_money.setClickable(true);
                            fab_bus_stop.setClickable(true);
                            fab_bus.setClickable(true);
                            isOpen = true;


                        }
                    }
                });

    }

    public void borrartodosfiltros(View v){
        activosbuses = false;
        actv.setText("");
        for(int i = 0;i<busesVivo.size();i++){
            busesVivo.get(i).remove();
        }
        rutasescogidas.clear();
    }

    public void quitarfiltro(View v){
        String es = actv.getText().toString();
        if(es!=null && !es.equals("")) {
            for(int i = 0;i<busesVivo.size();i++){
                if(busesVivo.get(i).getTitle().equalsIgnoreCase(es)){
                    busesVivo.get(i).remove();
                }
            }
            for (int y = 0; y<rutasescogidas.size();y++){
                if(rutasescogidas.get(y).equalsIgnoreCase(es)){
                    rutasescogidas.remove(y);
                }
            }
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
        }else{
            Toast.makeText(getApplicationContext(), "Escribe una ruta", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onClick(View view) {
        autocompleteFragment.setText("");
        view.setVisibility(View.GONE);
        seleccionado.remove();
        seleccionado = null;
    }

    @Override
    public void onPlaceSelected(Place place) {
        //Toast.makeText(getApplicationContext(), "Logrado", Toast.LENGTH_SHORT).show();

            if (primero == 0) {
                sel = place.getLatLng();
                String nombre = place.getName().toString();
                seleccionado = mMap.addMarker(new MarkerOptions().position(sel).title(nombre));
                primero = 1;

            } else {
                if (seleccionado != null) {
                    seleccionado.remove();
                }
                sel = place.getLatLng();
                String nombre = place.getName().toString();
                seleccionado = mMap.addMarker(new MarkerOptions().position(sel).title(nombre));

            }


//        if(sel.latitude<3.319858  || sel.latitude>3.498064  || sel.longitude<(-76.578741) || sel.longitude>(-76.464570)) {
//            autocompleteFragment.setText("");
//            Toast.makeText(getApplicationContext(),"Acceso inaccesible",Toast.LENGTH_SHORT).show();
//        }

    }

    @Override
    public void onError(Status status) {

    }

    public void onCheckboxClicked(View view) {
        boolean checked = ((CheckBox) view).isChecked();

        switch(view.getId()) {
            case R.id.checkbox_puntos:
                if (checked) {
                    String x = leer();
                    ArrayList<PuntoRecarga> d = obtenerParadas(x);
                    for (int i = 0; i < d.size();i++){
                        if(esCercana(latitud,longitud,d.get(i).getLatitud(), d.get(i).getLongitud())) {
                             Marker w = mMap.addMarker(new MarkerOptions()
                                 .position(new LatLng(d.get(i).getLatitud(), d.get(i).getLongitud())).title(d.get(i).getNombre()));
                             puntosRecarga.add(w);
                        }
                    }
                }else
                    for(int i = 0;i<puntosRecarga.size();i++){
                        puntosRecarga.get(i).remove();
                    }
                break;
            case R.id.checkbox_bus:
                if (checked) {
                    try{
                        layer.addLayerToMap();
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }else
                    layer.removeLayerFromMap();

                break;
            case R.id.checkbox_paradas:
                if(checked){
                    ArrayList<Parada> e = leerParadas();
                    for (int i = 0; i < e.size();i++) {
                        if(esCercana(e.get(i).getLatitud(), e.get(i).getLongitud(), latitud, longitud)) {
                            Marker w = mMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(e.get(i).getLatitud(), e.get(i).getLongitud()))
                                    .title(e.get(i).getNombre()).icon(BitmapDescriptorFactory.fromResource(R.drawable.bus_stop_3)));
                            paradas.add(w);
                        }
                    }
                }else
                    for(int i = 0;i<paradas.size();i++){
                        paradas.get(i).remove();
                    }

                 break;
            case R.id.checkbox_planear:
                if(checked){
                    if(seleccionado!=null) {

                        new Planeacion().execute();

                    }else{
                        ((CheckBox) view).setChecked(false);
                        Toast.makeText(getApplicationContext(), "Busca un sitio", Toast.LENGTH_SHORT).show();
                    }
                }else
                    for(int i = 0;i<planeacion.size();i++){
                        planeacion.get(i).remove();
                    }
                    break;
        }
    }

    public String rutaa(){
        return "http://tuyo.herokuapp.com/request-route?x1=" + longitud + "" + "&y1=" + latitud + "" + "&x2=" + sel.longitude + "&y2=" + sel.latitude + "&mode=lessBuses";
    }

    public String leer() {
        BufferedReader reader = null;
        StringBuilder sb = null;
        try {
            reader = new BufferedReader(
                    new InputStreamReader(getAssets().open("paradas.txt")));
            sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            reader.close();
            return sb.toString();
        } catch (IOException e) {
            //log the exception
        }
        return sb.toString();
    }

    private ArrayList<PuntoRecarga> obtenerParadas(String respuesta) {
        JSONObject json;
        ArrayList<PuntoRecarga> recargas = new ArrayList<PuntoRecarga>();
        try {
            json = new JSONObject(respuesta);
            JSONObject jsonObj = json.getJSONObject("feed");
            JSONArray elem1 = jsonObj.getJSONArray("entry");
            for (int i = 0; i < elem1.length(); i++) {
                JSONObject mJsonObjectProperty = elem1.getJSONObject(i);
                String nombre, direccion, estado, coord;
                double latitud, longitud;
                if (!mJsonObjectProperty.getJSONObject("gsx$coordenadas").getString("$t").isEmpty()) {

                    nombre = mJsonObjectProperty.getJSONObject("gsx$nombredelestablecimiento").getString("$t");
                    direccion = mJsonObjectProperty.getJSONObject("gsx$direccionestablecimiento").getString("$t");
                    //tipo = mJsonObjectProperty.getString("gsx$direccionestablecimiento");
                    coord = mJsonObjectProperty.getJSONObject("gsx$coordenadas").getString("$t");
                    estado = mJsonObjectProperty.getJSONObject("gsx$estado").getString("$t");
                    PuntoRecarga punto = new PuntoRecarga();
                    punto.setDirección(direccion);
                    punto.setNombre(nombre);
                    punto.setEstado(estado);
                    String[] lonLat = coord.split(",");
                    longitud = Double.parseDouble(lonLat[1].trim());
                    latitud = Double.parseDouble(lonLat[0].trim());
                    punto.setLongitud(longitud);
                    punto.setLatitud(latitud);

                    recargas.add(punto);
                }

            }
            //cedulas = cedRec;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return recargas;
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

    public ArrayList<Parada> leerParadas() {
        ArrayList<Parada> paradas = new ArrayList<Parada>();
        try {
            BufferedReader br = null;
            br = new BufferedReader(
                    new InputStreamReader(getAssets().open("stops.txt")));
            br.readLine();
            for (String linea = br.readLine(); linea != null
                    && !linea.equals(""); linea = br.readLine()) {
                String[] datos = linea.split(",");
                if (datos.length == 5) {
                    Parada parada = new Parada(datos[0], datos[1],
                            Double.parseDouble(datos[2]),
                            Double.parseDouble(datos[3]),
                            Integer.parseInt(datos[4]));
                    paradas.add(parada);
                } else {
                    Parada parada = new Parada(datos[0], datos[1],
                            Double.parseDouble(datos[2]),
                            Double.parseDouble(datos[3]),
                            Integer.parseInt(datos[4]), datos[5], datos[6]);
                    paradas.add(parada);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return paradas;
    }

    public ArrayList<SitioTuristico> leerSitios() {
        ArrayList<SitioTuristico> sitios = new ArrayList<SitioTuristico>();
        try {
            BufferedReader br = null;
            br = new BufferedReader(
                    new InputStreamReader(getAssets().open("sitios.txt")));
            br.readLine();
            for (String linea = br.readLine(); linea != null; linea = br
                    .readLine()) {
                String[] datos = linea.split("\\|");
                for (int a = 0; a < datos.length; a++) {
                    datos[a] = datos[a].trim();
                }
                if (datos.length == 6) {
                    SitioTuristico sitio = new SitioTuristico(datos[0],
                            datos[1], datos[2], Double.parseDouble(datos[3]),
                            Double.parseDouble(datos[4]), datos[5]);
                    sitios.add(sitio);
                }else{
                    SitioTuristico sitio = new SitioTuristico(datos[0],
                            datos[1], datos[2], Double.parseDouble(datos[3]),
                            Double.parseDouble(datos[4]), datos[5], datos[6]);
                    sitios.add(sitio);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sitios;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            // Show rationale and request permission.
        }

        View locationButton = ((View) findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
        // position on right bottom
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
        rlp.setMargins(0, 1360, 30, 0);

        miUbucacion();
        try{
            layer = new KmlLayer(mMap,R.raw.sitios,getApplicationContext());
        }catch (Exception e){
            e.printStackTrace();
        }

        // LatLng cali = new LatLng(latitud, longitud);
        //LatLngBounds centro = new LatLngBounds(
        //      new LatLng(latitud-1, longitud-1), new LatLng(latitud+1, longitud+1));
        //  mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(centro.getCenter(), 15));
        // mMap.addMarker(new MarkerOptions().position(cali).title("Posición actual"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(cali));

        //Toast.makeText(getApplicationContext(), latitud+", "+longitud, Toast.LENGTH_SHORT).show();


//        Toast.makeText(getApplicationContext(), ""+ d.get(1).getLatitud()+", "+d.get(1).getLongitud(), Toast.LENGTH_SHORT).show();
//        latitud = d.get(1).getLatitud();
//        longitud = d.get(1).getLongitud();
//        LatLng punto = new LatLng(longitud,latitud);//(d.get(1).getLatitud(), d.get(1).getLongitud());
//        MarkerOptions mq = new MarkerOptions()
//                .position(new LatLng(longitud,latitud));
//        mMap.addMarker(new MarkerOptions()
//                .position(punto)
//                .title("punto"));

    }

    private void agregarActual(double lat, double lng) {

        LatLng coordenadas = new LatLng(lat, lng);

        if(primerZoom==0){
            CameraUpdate miUbicacion = CameraUpdateFactory.newLatLngZoom(coordenadas, 16);
            mMap.animateCamera(miUbicacion);
            primerZoom=1;
        }

        //if (actual != null) actual.remove();
        //actual = mMap.addMarker(new MarkerOptions().position(coordenadas).title("Tu posición actual"));


    }

    public ArrayList<Vehiculo> actualizarBuses(){

        ArrayList<Vehiculo> x = new ArrayList<Vehiculo>();

        if(activosbuses==true){


            String ruta = "http://190.216.202.35:90/gtfs/realtime/";
            conexionHTTP = new ConexionHTTP(ruta);
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            x = conexionHTTP.getRealtime().getVehiculos();

        }
        return x;

    }

    private void actualizarUbicacion(Location locacion) {
        //Toast.makeText(getApplicationContext(), "Actualiza", Toast.LENGTH_SHORT).show();
        if (locacion != null) {

            latitud = locacion.getLatitude();
            longitud = locacion.getLongitude();

            //Modificado
            agregarActual(latitud, longitud);
        }
    }

    LocationListener locListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
//            actualizarUbicacion(location);

              new ActualizacionBuses().execute();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    private void miUbucacion() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        final boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!gpsEnabled) {
            Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(settingsIntent);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
            return;
        }

        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        actualizarUbicacion(location);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,1000,0,locListener);

    }

    private boolean esCercana(double lat1, double long1, double lat2, double long2) {

        double x = 0.0;
        double pi = Math.PI;
        x  = Math.sin(lat1 * pi/180) *
                Math.sin(lat2 * pi/180) +
                Math.cos(lat1 * pi/180) *
                        Math.cos(lat2 * pi/180) *
                        Math.cos((long2 * pi/180) - (long1 * pi/180));
        x  = Math.atan((Math.sqrt(1 - Math.pow(x, 2))) / x);
        double dist = (1.852 * 60.0 * ((x/pi) * 180)) / 1.609344;

        if(dist<0.8){
            return true;
        }else{
            return false;
        }
    }

    public void regreso(View v){
        Intent i = new Intent(this, home.class);
        startActivity(i);
        finish();
    }

    public class ActualizacionBuses extends AsyncTask<URL, Void, String> {

        // COMPLETED (26) Override onPreExecute to set the loading indicator to visible
        private ArrayList<Vehiculo> x;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //Toast.makeText(getApplicationContext(), "Buscando buses", Toast.LENGTH_LONG).show();
            //mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(URL... params) {
            x = actualizarBuses();
            return "";
        }

        @Override
        protected void onPostExecute(String serverCedulas) {
            if(!busesVivo.isEmpty()){
                for(int y = 0; y < busesVivo.size();y++){
                    busesVivo.get(y).remove();
                }
            }

            for (int i = 0; i<x.size();i++) {
                for(int y = 0; y < rutasescogidas.size();y++){

                    if(routes.get(x.get(i).getRuta()).equalsIgnoreCase(rutasescogidas.get(y))) {
                        Marker w = mMap.addMarker(new MarkerOptions().position(new LatLng(x.get(i).getLatitud(), x.get(i).getLongitud())).title(routes.get(x.get(i).getRuta())).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_directions_bus_black_24dp)));
                        busesVivo.add(w);
                    }
                }

            }
            for(int a = 0; a<busesVivo.size(); a++){
                busesVivo.get(a).showInfoWindow();
            }
        }
    }

    public class Planeacion extends AsyncTask<URL, Void, String> {

        private boolean t = false;

        // COMPLETED (26) Override onPreExecute to set the loading indicator to visible
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(getApplicationContext(), "Planeando tu ruta", Toast.LENGTH_LONG).show();
            //mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(URL... params) {

            conexionHTTP = new ConexionHTTP(rutaa());
            System.out.println("------------");
            System.out.println("en ubicacion: "+conexionHTTP.getEstado());
                try {
                    while (!conexionHTTP.isTerminoProceso()) {
                        // Toast.makeText(getApplicationContext(), "CARGANDO", Toast.LENGTH_SHORT).show();
                        Thread.sleep(500);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            t = conexionHTTP.getEstado();

            return "";
        }

        @Override
        protected void onPostExecute(String serverCedulas) {

            if(!t){
                Toast.makeText(getApplicationContext(), "Busca un lugar en Cali", Toast.LENGTH_LONG).show();
            }else {

                if (conexionHTTP != null) {
                    secciones = conexionHTTP.getSecciones();

                    for (int i = 0; i < secciones.size(); i++) {
                        Seccion s = secciones.get(i);

                        Marker w = mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(s.getLatitud(), s.getLongitud()))
                                .title(s.getNameStation() + " - " + s.getNameRuta()));
                        planeacion.add(w);

                    }
                }

            }
        }
    }

//    private void locationStart() {
//        LocationManager mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//        Localizacion Local = new Localizacion();
//        Local.setMainActivity(this);
//        final boolean gpsEnabled = mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
//        if (!gpsEnabled) {
//            Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//            startActivity(settingsIntent);
//        }
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
//            return;
//        }
//        mlocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, (LocationListener) Local);
//        mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (LocationListener) Local);
//
//    }
//
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        if (requestCode == 1000) {
//            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                locationStart();
//                return;
//            }
//        }
//    }
//
//    public void setLocation(Location loc) {
//        //Obtener la direccion de la calle a partir de la latitud y la longitud
//        if (loc.getLatitude() != 0.0 && loc.getLongitude() != 0.0) {
//            try {
//                latitud = loc.getLatitude();
//                longitud = loc.getLongitude();
//                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
//                List<Address> list = geocoder.getFromLocation(
//                        loc.getLatitude(), loc.getLongitude(), 1);
//                if (!list.isEmpty()) {
//                    Address DirCalle = list.get(0);
//                    String dir = DirCalle.getAddressLine(0);
//                     }
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    /* Aqui empieza la Clase Localizacion */
//    public class Localizacion implements LocationListener {
//        ubicacion mainActivity;
//
//        public ubicacion getMainActivity() {
//            return mainActivity;
//        }
//
//        public void setMainActivity(ubicacion mainActivity) {
//            this.mainActivity = mainActivity;
//        }
//
//        @Override
//        public void onLocationChanged(Location loc) {
//            // Este metodo se ejecuta cada vez que el GPS recibe nuevas coordenadas
//            // debido a la deteccion de un cambio de ubicacion
//
//            loc.getLatitude();
//            loc.getLongitude();
//
//            this.mainActivity.setLocation(loc);
//        }
//
//        @Override
//        public void onProviderDisabled(String provider) {
//            // Este metodo se ejecuta cuando el GPS es desactivado
//
//        }
//
//        @Override
//        public void onProviderEnabled(String provider) {
//            // Este metodo se ejecuta cuando el GPS es activado
//
//        }
//
//        @Override
//        public void onStatusChanged(String provider, int status, Bundle extras) {
//            switch (status) {
//                case LocationProvider.AVAILABLE:
//                    Log.d("debug", "LocationProvider.AVAILABLE");
//                    break;
//                case LocationProvider.OUT_OF_SERVICE:
//                    Log.d("debug", "LocationProvider.OUT_OF_SERVICE");
//                    break;
//                case LocationProvider.TEMPORARILY_UNAVAILABLE:
//                    Log.d("debug", "LocationProvider.TEMPORARILY_UNAVAILABLE");
//                    break;
//            }
//        }
//    }

}
