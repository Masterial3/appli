package com.example.quentin.mapmenu;

import android.Manifest;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    TextView name;
    Intent page;
    String na;
    private GoogleMap mMap;
    public TextView textView;
    ArrayList<ArrayList<String>> table;
    String result;
    boolean finThread = false;
    ArrayList<Station> listStation = new ArrayList<Station>();
    Thread thread;
    int bidule = 0;

    FavoriActivity favoris = new FavoriActivity();
    private int number;

    final Context context = this;

    double lat1 = 0.0;
    double lon1 = 0.0;


    LatLng myLat;

    private Handler myHandler;
    private Runnable myRunnable = new Runnable() {
        @Override
        public void run() {
            mMap.clear();
            onMapReady(mMap);
            myHandler.postDelayed(this, 10000);

        }
    };









    //####################### Localisation ######################"
    private static final int FINE_LOCATION_PERMISSION_REQUEST = 1;
    private static final int CONNECTION_RESOLUTION_REQUEST = 2;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        setTitle("Coucou");

        myHandler = new Handler();
        myHandler.postDelayed(myRunnable, 10000);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        buildGoogleAPIClient();
    }

    protected void onResume()
    {
        super.onResume();
        buildGoogleAPIClient();
    }

    private void buildGoogleAPIClient()
    {
        if(mGoogleApiClient == null)
        {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    public void onConnected(@Nullable Bundle bundle)
    {
        findLocation();
    }

    protected void onStart()
    {
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop()
    {
        mGoogleApiClient.disconnect();
        super.onStop();
    }






    public void onConnectionSuspended(int i)
    {
        Toast.makeText(this, "Connections suspended", Toast.LENGTH_SHORT).show();
    }

    public void onConnectionFailed(@NonNull final ConnectionResult connectionResult)
    {
        if(connectionResult.hasResolution())
        {
            try
            {
                connectionResult.startResolutionForResult(this, CONNECTION_RESOLUTION_REQUEST);
            }
            catch(IntentSender.SendIntentException e)
            {
                mGoogleApiClient.connect();
            }
        }
        else
        {
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(), this, 1);
            dialog.show();
        }

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CONNECTION_RESOLUTION_REQUEST && resultCode == RESULT_OK)
        {
            mGoogleApiClient.connect();
        }
    }

    private void findLocation() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    FINE_LOCATION_PERMISSION_REQUEST);
        } else {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

            final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );
            LatLng myLat= null;
            if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
                buildAlertMessageNoGps();
            }else {
                if(mLastLocation != null) {
                    myLat = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());

                }
                else
                    myLat = new LatLng(48,48);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLat, 14));

            }
        }
    }
    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)
    {
        switch(requestCode)
        {
            case FINE_LOCATION_PERMISSION_REQUEST:{
                if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    findLocation();
                }
            }
        }
    }




    //################### Menu options ######################
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my_menu, menu);

        return true;
    }




    //############### récupération données ######################

    public void recupData()
    {
        thread = new Thread() {
            @Override
            public void run() {

                if (isNetworkAvailable() == true) {
                    try {
                        URL url = new URL("https://api.jcdecaux.com/vls/v1/stations?apiKey=fb6216c7927ccf5e52101074754ca000d42c2dd2&contract=Paris");
                        HttpURLConnection con = (HttpURLConnection) url.openConnection();
                        readStream(con.getInputStream());
                        table = decoupe();
                        print(table);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            private void readStream(InputStream in) {
                BufferedReader reader = null;
                try {
                    reader = new BufferedReader(new InputStreamReader(in));
                    String line = "";
                    result = "";
                    while ((line = reader.readLine()) != null) {
                        //System.out.println(line);
                        result = result + line;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            public boolean isNetworkAvailable() {
                ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = cm.getActiveNetworkInfo();
                // si aucun réseau n'est disponible, networkInfo sera null
                // sinon, vérifier si nous sommes connectés
                if (networkInfo != null && networkInfo.isConnected()) {
                    return true;
                }
                return false;
            }

            public ArrayList<ArrayList<String>> decoupe() throws Exception {

                //decoupage de la chaine
                String liste[] = result.split(Pattern.quote("},"));


                //regroupement des informations d'une meme station en une chaine
                ArrayList<String> liste2 = new ArrayList<String>();
                int increment = 0;
                while (increment < liste.length) {
                    liste2.add(liste[increment].substring(1) + "}," + liste[increment + 1]);
                    increment += 2;
                }

                //suppression des derniers caractères génant
                liste2.set(0, liste2.get(0).substring(1));
                liste2.set(liste2.size() - 1, liste2.get(liste2.size() - 1).substring(0, liste2.get(liste2.size() - 1).length() - 2));

                //decoupage des sous-chaines
                ArrayList<ArrayList<String>> listeDeListe = new ArrayList<ArrayList<String>>();
                for (int i = 0; i < liste2.size(); i++) {

                    //modifier

                    String[] tempo = liste2.get(i).split(",\"");
                    listeDeListe.add(new ArrayList<String>());
                    for (int j = 0; j < tempo.length; j++) {
                        listeDeListe.get(listeDeListe.size() - 1).add(tempo[j]);
                    }
                    //System.out.println(tempo[0]);
                }

                //extraction des informations
                for (int i = 0; i < listeDeListe.size(); i++) {
                    for (int j = 0; j < listeDeListe.get(i).size(); j++) {
                        String[] listTempo = listeDeListe.get(i).get(j).split(":");
                        listeDeListe.get(i).set(j, listTempo[listTempo.length - 1]);
                    }
                }

                //suppression parasite " et }
                for (int i = 0; i < listeDeListe.size(); i++) {
                    for (int j = 0; j < listeDeListe.get(i).size(); j++) {
                        String tempo = listeDeListe.get(i).get(j);
                        if (tempo.charAt(0) == '"')
                            listeDeListe.get(i).set(j, tempo.substring(1, tempo.length()));
                        String tempo2 = listeDeListe.get(i).get(j);
                        if (tempo2.charAt(tempo2.length() - 1) == '}' || tempo2.charAt(tempo2.length() - 1) == '"')
                            listeDeListe.get(i).set(j, tempo2.substring(0, tempo2.length() - 1));

                    }
                }

                return listeDeListe;
            }


            public void print(ArrayList<ArrayList<String>> liste) {

                for (int i = 0; i < liste.size(); i++) {


                    //création liste de stations
                    listStation.add(new Station(Integer.parseInt(liste.get(i).get(0)),
                            liste.get(i).get(1),
                            liste.get(i).get(2),
                            Float.parseFloat(liste.get(i).get(3).toString()),
                            Float.parseFloat(liste.get(i).get(4).toString()),
                            liste.get(i).get(5),
                            liste.get(i).get(6),
                            Integer.parseInt(liste.get(i).get(9)),
                            Integer.parseInt(liste.get(i).get(10)),
                            Integer.parseInt(liste.get(i).get(11)),
                            liste.get(i).get(12)));


                            /*  AFFICHAGE STATIONS
                            System.out.println("Numero: "+liste.get(i).get(0));
                            System.out.println("Nom: "+liste.get(i).get(1));
                            System.out.println("Adresse: "+liste.get(i).get(2));

                            System.out.println("Position:");
                            System.out.println("\tlatitude: "+liste.get(i).get(3));
                            System.out.println("\tlongitude: "+liste.get(i).get(4));

                            System.out.println("banking: "+liste.get(i).get(5));
                            System.out.println("Bonus: "+liste.get(i).get(6));
                            //if(liste.get(i).get(6)=="false")
                            //	compteur++;
                            System.out.println("Etat: "+liste.get(i).get(7));
                            System.out.println("Ville: "+liste.get(i).get(8));

                            System.out.println("Capacite totale: "+liste.get(i).get(9));
                            System.out.println("Borne disponible: "+liste.get(i).get(10));
                            System.out.println("Velo disponible: "+liste.get(i).get(11));

                            System.out.println("Derniere maj: "+liste.get(i).get(12));

                            System.out.println("--------------------------------------------------");
                            */
                }
                finThread = true;
            }
        };
        thread.start();

    }


    //################## update ###################




    //################## MAP ####################
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        recupData();

        //attent la fin du thread pour continuer
        while(finThread == false)
        {

        }
        finThread = false;


        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            mMap.setMyLocationEnabled(true);
        }
        else
        {

        }


        // boucle : ajout des marker à la map
        for(int i = 0; i<listStation.size(); i++)
        {

            mMap.addMarker(new MarkerOptions().position(new LatLng(listStation.get(i).getLat(), listStation.get(i).getLng())));
        }

        // Bulle custom

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                dialogue(marker);
                return null;
            }

            //@Override

        });
    }


    private void dialogue(Marker marker){
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.custom);


        // set the custom dialog components - text, image and button
        //TextView text = (TextView) dialog.findViewById(R.id.text);
        //text.setText("Android custom dialog example!");
        //ImageView image = (ImageView) dialog.findViewById(R.id.image);
        //image.setImageResource(R.drawable.ic_launcher);

        String strname = "DEFAULT";
        String stradresse = "DEFAULT";
        String intnbvelo = "DEFAULT";
        String intnbplace = "DEFAULT";

        LatLng latLng = marker.getPosition();


        // lien marker-station par les coord
        for(int i = 0 ; i < listStation.size(); i++){
            if(latLng.longitude == listStation.get(i).getLng() &&
                    latLng.latitude == listStation.get(i).getLat())
            {
                strname = listStation.get(i).getName();
                stradresse = listStation.get(i).getAddress();
                intnbvelo = Integer.toString(listStation.get(i).getNbvelo());
                intnbplace = Integer.toString(listStation.get(i).getNbplace());
            }

        }

        name = (TextView) dialog.findViewById(R.id.tv_name);
        TextView adress = (TextView) dialog.findViewById(R.id.tv_adresse);
        TextView nbvelo = (TextView) dialog.findViewById(R.id.tv_nbvelo);
        TextView nbplace = (TextView) dialog.findViewById(R.id.tv_nbplace);
        TextView coord = (TextView) dialog.findViewById(R.id.tv_coord);

        Log.i("Dialog","Texte a venir");

        dialog.setTitle(strname);
        na = strname;
        adress.setText("Adresse : " + stradresse);
        nbvelo.setText(intnbvelo + " vélo");
        nbplace.setText(intnbplace + " places");

        lat1 = latLng.latitude;
        lon1 = latLng.longitude;


        Log.i("Dialog","Texte fait");

        Button fav = (Button) dialog.findViewById(R.id.button_favoris);
        Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonOK);


        fav.setOnClickListener(new View.OnClickListener() {
            TextView name = (TextView) dialog.findViewById(R.id.tv_name);

            String n= name.getText().toString();
           // String n = findViewById(R.id.tv_name).toString();
            @Override
            public void onClick(View v) {

                WriteSettings(MapsActivity.this,na);
            }
        });
        // if button is clicked, close the custom dialog
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Debut

                Uri gmmIntentUri = Uri.parse("http://maps.google.com/maps?saddr=48.866667,2.333333&daddr="+ lat1 + "," + lon1 + "&mode=b");
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                try
                {
                    startActivity(mapIntent);
                }
                catch(ActivityNotFoundException ex)
                {
                    try
                    {
                        Intent unrestrictedIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        startActivity(unrestrictedIntent);
                    }
                    catch(ActivityNotFoundException innerEx)
                    {
                        //Toast.makeText(this, "Please install a maps application", Toast.LENGTH_LONG).show();
                    }
                }


                //Fin
                Log.i("Sortie","okai");
                //dialog.dismiss();
            }
        });

        dialog.show();
    }


    //################### Menu ############################"

    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_edit:
            /* DO EDIT */
                textView = (TextView) findViewById(R.id.text1);


                recupData();

                return true;
            case R.id.action_add:
            /* DO ADD */
                return true;
            case R.id.action_favori:
            /* DO Favori */
               page = new Intent(MapsActivity.this, FavoriActivity.class);
                startActivity(page);

                return true;
            case R.id.action_option:
            /* DO Option */
                page = new Intent(MapsActivity.this, OptionActivity.class);
                startActivity(page);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
////écriture d'un favoris
    public void WriteSettings(Context context, String data){
        FileOutputStream fOut = null;
        OutputStreamWriter osw = null;

        try{
            fOut = openFileOutput("settings.dat",MODE_PRIVATE);
            osw = new OutputStreamWriter(fOut);
            osw.write(data);
            osw.flush();
            Toast.makeText(context, "Settings saved",Toast.LENGTH_SHORT).show();
        }
        catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Settings not saved",Toast.LENGTH_SHORT).show();
        }

        finally {
            try {
                osw.close();
                fOut.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
