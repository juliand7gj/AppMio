package model;


import com.google.transit.realtime.GtfsRealtime.FeedEntity;
import com.google.transit.realtime.GtfsRealtime.FeedMessage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class GtfsRealtime {

   private ArrayList<Vehiculo> vehiculos;
public GtfsRealtime(){
    vehiculos = new ArrayList<Vehiculo>();
}

public void descargar(String dirWeb) throws IOException {

    URL url = new URL(dirWeb);
    FeedMessage feed = FeedMessage.parseFrom(url.openStream());

    for (FeedEntity entity : feed.getEntityList()) {
        if (entity.hasVehicle()) {
             float latitud = entity.getVehicle().getPosition().getLatitude();
             float longitud = entity.getVehicle().getPosition().getLongitude();
             String ruta = entity.getVehicle().getTrip().getRouteId();
            Vehiculo n = new Vehiculo(latitud, longitud,ruta);
            vehiculos.add(n);
        }
    }
}

    public ArrayList<Vehiculo> getVehiculos() {
        return vehiculos;
    }
}