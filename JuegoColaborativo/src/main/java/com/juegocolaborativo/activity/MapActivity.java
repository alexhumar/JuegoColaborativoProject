package com.juegocolaborativo.activity;

import android.app.Fragment;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.juegocolaborativo.JuegoColaborativo;
import com.juegocolaborativo.R;
import com.juegocolaborativo.model.Poi;
import com.juegocolaborativo.model.Subgrupo;
import com.juegocolaborativo.receiver.ProximityIntentReceiver;

public class MapActivity extends DefaultActivity implements
        GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener,
        LocationListener {

    /* Milliseconds per second. */
    private static final int MILLISECONDS_PER_SECOND = 1000;
    /* Update frequency in seconds. */
    public static final int UPDATE_INTERVAL_IN_SECONDS = 10;
    /* Update frequency in milliseconds. */
    private static final long UPDATE_INTERVAL = MILLISECONDS_PER_SECOND * UPDATE_INTERVAL_IN_SECONDS;
    /* The fastest update frequency, in seconds. */
    private static final int FASTEST_INTERVAL_IN_SECONDS = 1;
    /* A fast frequency ceiling in milliseconds. */
    private static final long FASTEST_INTERVAL = MILLISECONDS_PER_SECOND * FASTEST_INTERVAL_IN_SECONDS;
    /* Define an object that holds accuracy and frequency parameters. */
    LocationRequest mLocationRequest;
    private GoogleMap googleMap;
    private LocationClient mLocationClient;
    boolean mUpdatesRequested;

    private static final long POINT_RADIUS = 100; // in Meters
    private static final long PROX_ALERT_EXPIRATION = -1;

    public static final String PROX_ALERT_POI_SUBGRUPO = "com.juegocolaborativo.ProximityAlertPoiSubgrupo";
    public static final String PROX_ALERT_POI_SIGUIENTE = "com.juegocolaborativo.ProximityAlertPoiSiguiente";

    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /* Create the LocationRequest object. */
        mLocationRequest = LocationRequest.create();
        /* Use high accuracy. */
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        /* Set the update interval to 5 seconds. */
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        /* Set the fastest update interval to 1 second. */
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        setContentView(R.layout.activity_map);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
        /*
         * Create a new location client, using the enclosing class to
         * handle callbacks.
         */
        mLocationClient = new LocationClient(this, this, this);
        /* Start with updates turned off.*/
        mUpdatesRequested = true;
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    }

    /*
     * Called when the Activity becomes visible.
     */
    @Override
    protected void onStart() {
        super.onStart();
        /* Connect the client. */
        mLocationClient.connect();
    }
    /*
     * Called when the Activity is no longer visible.
     */
    @Override
    protected void onStop() {
        /* If the client is connected. */
        if (mLocationClient.isConnected()) {
            /*
             * Remove location updates for a listener.
             * The current Activity is the listener, so
             * the argument is "this".
             */
            mLocationClient.removeLocationUpdates(this);
        }
        /*
         * After disconnect() is called, the client is
         * considered "dead".
         */
        mLocationClient.disconnect();
        super.onStop();
    }

    public GoogleMap getGoogleMap() {
        return googleMap;
    }

    public void setGoogleMap(GoogleMap googleMap) {
        this.googleMap = googleMap;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /* Inflate the menu; this adds items to the action bar if it is present. */
        getMenuInflater().inflate(R.menu.map, menu);
        return true;
    }

    /*
     * Called by Location Services when the request to connect the
     * client finishes successfully. At this point, you can
     * request the current location or start periodic updates
     */
    @Override
    public void onConnected(Bundle dataBundle) {
        // If already requested, start periodic updates
        if (mUpdatesRequested) {
            mLocationClient.requestLocationUpdates(mLocationRequest,this);
        }
    }

    /*
     * Called by Location Services if the connection to the
     * location client drops because of an error.
     */
    @Override
    public void onDisconnected() {
        Log.i("MapActivity", "onDisconnected");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i("MapActivity","onConnectionFailed");
    }

    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_map, container, false);
            try {
                /* Loading map. */
                ((MapActivity) getActivity()).initializeMap();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return rootView;
        }
    }

    private void initializeMap() {
        if (this.getGoogleMap() == null) {
            this.setGoogleMap(((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap());
            Subgrupo subgrupo = ((JuegoColaborativo) getApplication()).getSubgrupo();
            /* La primera vez que pasa por aca la pieza aun no se ha seteado. */
            if(subgrupo.getPosta().getPiezaARecolectar() == null || !subgrupo.getPosta().getPiezaARecolectar().isVisitada()){
                /* Mostramos el punto correspondiente a la posta del subgrupo. */
                Poi poiSubgrupo = subgrupo.getPosta().getPoi();
                addProximityAlert(poiSubgrupo, MapActivity.PROX_ALERT_POI_SUBGRUPO, 0);
                this.getGoogleMap().addMarker(new MarkerOptions()
                        .position(new LatLng(poiSubgrupo.getCoordenadas().getLatitud(), poiSubgrupo.getCoordenadas().getLongitud()))
                        .title("Poi Subgrupo")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.start_flag)));
            } else {
                Poi poiSiguiente = subgrupo.getPosta().getSiguientePosta().getPoi();
                if(poiSiguiente.getCoordenadas().getLatitud() == -1) {
                    /* No hay posta siguiente, termina el juego. */
                    ((JuegoColaborativo) getApplication()).enviarFinJuego(true);
                }else{
                    /* Mostramos el punto al que debe dirigirse luego de reponder su Pieza. */
                    this.showDialogInfo(R.string.dialog_consigna_respondida);
                    addProximityAlert(poiSiguiente, MapActivity.PROX_ALERT_POI_SIGUIENTE, 0);
                    this.getGoogleMap().addMarker(new MarkerOptions()
                            .position(new LatLng(poiSiguiente.getCoordenadas().getLatitud(), poiSiguiente.getCoordenadas().getLongitud()))
                            .title("Poi Siguiente")
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.end_flag)));
                }
            }
        }
    }

    private void addProximityAlert(Poi poi,String intentName, int extraParameter) {
        Intent intent = new Intent(intentName);
        intent.putExtra("id",extraParameter);
        PendingIntent proximityIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        locationManager.addProximityAlert(
            poi.getCoordenadas().getLatitud(), /* The latitude of the central point of the alert region. */
            poi.getCoordenadas().getLongitud(), /* the longitude of the central point of the alert region. */
            POINT_RADIUS, /* the radius of the central point of the alert region, in meters. */
            PROX_ALERT_EXPIRATION, /* time for this proximity alert, in milliseconds, or -1 to indicate no expiration. */
            proximityIntent /* will be used to generate an Intent to fire when entry to or exit from the alert region is detected. */
        );
        IntentFilter filter = new IntentFilter(intentName);
        ProximityIntentReceiver proximityIntentReceiver = new ProximityIntentReceiver();
        proximityIntentReceiver.setApplication((JuegoColaborativo) getApplication());
        registerReceiver(proximityIntentReceiver, filter);
        /* De esta manera, cuando se este llegando al punto de interes, el locationManager va a disparar proximityIntent cuya action va a ser intentName
         * (por haberse creado a partir de intent). Como el filter del receiver tambien se corresponde con intentName, cuando se dispare proximityIntent,
         * se ejecutará el onReceive del proximityIntentReceiver.
         * */
    }

    @Override
    protected void onResume() {
        enviarMsjConsultaRespondida();
        super.onResume();
        /* Cuando estas jugando y tenes que responder una pregunta, la activity se detiene. Luego de responder, reanuda. */
        this.initializeMap();
        Subgrupo subgrupo = ((JuegoColaborativo) getApplication()).getSubgrupo();
        if(subgrupo.getEstado() == Subgrupo.ESTADO_INICIAL){
            this.showProgressDialog(R.string.dialog_consulta_respondida_incial);
        }else if(subgrupo.getEstado() == Subgrupo.ESTADO_FINAL){
            this.showProgressDialog(R.string.dialog_consulta_respondida_final);
        }
    }

    public void onLocationChanged(Location location) {
        this.getGoogleMap().moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 15));
        this.getGoogleMap().animateCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
    }

    public void removeProximityAlert(String intent) {
        String context = Context.LOCATION_SERVICE;
        LocationManager locationManager = (LocationManager) getSystemService(context);
        Intent anIntent = new Intent(intent);
        PendingIntent operation = PendingIntent.getBroadcast(getApplicationContext(), 0 , anIntent, 0);
        locationManager.removeProximityAlert(operation);
    }

    public void removerPuntoInicial(){
        this.removeProximityAlert(PROX_ALERT_POI_SUBGRUPO);
        /* Limpio el mapa de markers. */
        this.googleMap.clear();
    }

    public void removerPuntoFinal(){
        this.removeProximityAlert(PROX_ALERT_POI_SIGUIENTE);
        /* Limpio el mapa de markers. */
        this.googleMap.clear();
    }
}
