package combodedo.prueba;

import android.*;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import model.ConexionHTTP;
import model.PuntoRecarga;
import model.Vehiculo;

public class ubicacion extends AppCompatActivity implements OnMapReadyCallback, PlaceSelectionListener {

    private GoogleMap mMap;
    private Marker actual;
    private static double latitud;
    private static double longitud;
    private ArrayList<Marker> puntosRecarga;
    private ArrayList<Marker> busesVivo;
    private Marker seleccionado;
    private int primero;
    private LatLng sel;
    private ConexionHTTP conexionHTTP;
    private boolean activosbuses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        activosbuses = false;
        primero = 0;
        sel = new LatLng(0,0);
        puntosRecarga = new ArrayList<Marker>();
        busesVivo = new ArrayList<Marker>();
        latitud = 0;
        longitud = 0;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ubicacion);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        autocompleteFragment.setOnPlaceSelectedListener(this);

    }

    public void comollegar(View v){
//        conexionHTTP = new ConexionHTTP(" http://tuyo.herokuapp.com/request-route?x1="+longitud+"&y1="+latitud+"&x2="+sel.longitude+"&y2="+sel.latitude+"&mode=lessBuses");
//        try {
//            Thread.sleep(8000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        Toast.makeText(getApplicationContext(), conexionHTTP.getSecciones().get(1).getLatitud()+"", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPlaceSelected(Place place) {
        //Toast.makeText(getApplicationContext(), "Logrado", Toast.LENGTH_SHORT).show();
        if(primero==0){
            sel = place.getLatLng();
            String nombre = place.getName().toString();
            seleccionado = mMap.addMarker(new MarkerOptions()
                    .position(sel).title(nombre));
            primero = 1;
        }else{
            seleccionado.remove();
            sel = place.getLatLng();
            String nombre = place.getName().toString();
            seleccionado = mMap.addMarker(new MarkerOptions()
                    .position(sel).title(nombre));
        }
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
            //if(esCercana(latitud,longitud,d.get(i).getLatitud(), d.get(i).getLongitud())) {
                    Marker w = mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(d.get(i).getLatitud(), d.get(i).getLongitud()))
                        .title(d.get(i).getNombre()));
                    puntosRecarga.add(w);
            //}
        }
                }else
                    for(int i = 0;i<puntosRecarga.size();i++){
                        puntosRecarga.get(i).remove();
                    }
                break;
            case R.id.checkbox_bus:
                if (checked) {

                    //Toast.makeText(getApplicationContext(), "Logrado", Toast.LENGTH_SHORT).show();

                }else
                    for(int i = 0;i<busesVivo.size();i++){
                        busesVivo.get(i).remove();
                    }
                break;

        }
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        miUbucacion();


        String ruta = "http://190.216.202.35:90/gtfs/realtime/";
        conexionHTTP = new ConexionHTTP(ruta);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ArrayList<Vehiculo> d = conexionHTTP.getRealtime().getVehiculos();
        for (int i = 0; i<d.size();i++) {
            Marker w = mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(d.get(i).getLatitud(), d.get(i).getLongitud())));
            busesVivo.add(w);
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
        CameraUpdate miUbicacion = CameraUpdateFactory.newLatLngZoom(coordenadas, 16);
        if (actual != null) actual.remove();
        actual = mMap.addMarker(new MarkerOptions()
                .position(coordenadas)
                .title("Tu posición actual"));
        mMap.animateCamera(miUbicacion);

    }

    private void actualizarUbicacion(Location locacion) {
        //Toast.makeText(getApplicationContext(), "Actualiza", Toast.LENGTH_SHORT).show();
        if (locacion != null) {

            latitud = locacion.getLatitude();
            longitud = locacion.getLongitude();

            //Modificado
            agregarActual(3.341917, -76.530522);
        }
    }

    LocationListener locListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            actualizarUbicacion(location);
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

    private boolean esCercana(double miLat, double miLong, double latPunto, double longPunto) {

        double lat = Math.abs(miLat-latPunto);
        double lon = Math.abs(miLong-longPunto);

        if(lat<0.01 && lon<0.01){
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
