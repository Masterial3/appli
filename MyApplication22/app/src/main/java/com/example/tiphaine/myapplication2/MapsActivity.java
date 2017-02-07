package com.example.tiphaine.myapplication2;

import android.app.ActionBar;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;



import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Pattern;

import static android.R.id.list;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    ArrayList<Station> listStation = new ArrayList<Station>();
    ArrayList<ArrayList<String>> table;
    String result;
    boolean finThread = false;









    @Override
    protected void onCreate(Bundle savedInstanceState) {




        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);



    }










    //##################  MAP  #########################
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;




        //récupérer liste station + info



        Thread thread = new Thread() {
            @Override
            public void run() {

                //runOnUiThread(new Runnable() {

                    //public void run() {
                        Log.i("BIDULE", "TROLOLOLOLOLOLOLOLOLOLOLOLOLOLOLOLOL");

                        if(isNetworkAvailable()==true) {
                            try {
                                URL url = new URL("https://api.jcdecaux.com/vls/v1/stations?apiKey=fb6216c7927ccf5e52101074754ca000d42c2dd2&contract=Paris");
                                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                                readStream(con.getInputStream());

                                Log.i("BIDULE", "TROLOLOLOLOLOLOLOLOLOLOLOLOLOLOLOLOL");
                                table = decoupe();
                                print(table);
                            } catch (Exception e) {
                                Log.i("BIDULE", "TRAALALALALALALALALALALALALALALALAALALA");
                                e.printStackTrace();
                            }
                        }else{
                            Log.i("BIDULE","MAAAAAAAAAAAAARCHE PAAAAAAAAAAAAAAAAS");
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

                    public ArrayList<ArrayList<String>> decoupe() throws Exception{

                        //decoupage de la chaine
                        String liste[] = result.split(Pattern.quote("},"));


                        //regroupement des informations d'une meme station en une chaine
                        ArrayList<String> liste2 = new ArrayList<String>();
                        int increment = 0;
                        while(increment<liste.length){
                            liste2.add(liste[increment].substring(1)+"},"+liste[increment+1]);
                            increment += 2;
                        }

                        //suppression des derniers caractères génant
                        liste2.set(0, liste2.get(0).substring(1));
                        liste2.set(liste2.size()-1, liste2.get(liste2.size()-1).substring(0,liste2.get(liste2.size()-1).length()-2));

                        //decoupage des sous-chaines
                        ArrayList<ArrayList<String>> listeDeListe = new ArrayList<ArrayList<String>>();
                        for(int i=0;i<liste2.size();i++){

                            //modifier

                            String[] tempo = liste2.get(i).split(",\"");
                            listeDeListe.add(new ArrayList<String>());
                            for(int j=0;j<tempo.length;j++){
                                listeDeListe.get(listeDeListe.size()-1).add(tempo[j]);
                            }
                            //System.out.println(tempo[0]);
                        }

                        //extraction des informations
                        for(int i=0;i<listeDeListe.size();i++){
                            for(int j=0;j<listeDeListe.get(i).size();j++){
                                String[] listTempo = listeDeListe.get(i).get(j).split(":");
                                listeDeListe.get(i).set(j, listTempo[listTempo.length-1]);
                            }
                        }

                        //suppression parasite " et }
                        for(int i=0;i<listeDeListe.size();i++){
                            for(int j=0;j<listeDeListe.get(i).size();j++){
                                String tempo = listeDeListe.get(i).get(j);
                                if(tempo.charAt(0)=='"')
                                    listeDeListe.get(i).set(j, tempo.substring(1,tempo.length()));
                                String tempo2 = listeDeListe.get(i).get(j);
                                if(tempo2.charAt(tempo2.length()-1)=='}' || tempo2.charAt(tempo2.length()-1)=='"')
                                    listeDeListe.get(i).set(j, tempo2.substring(0,tempo2.length()-1));

                            }
                        }

                        return listeDeListe;
                    }




                    public void print(ArrayList<ArrayList<String>> liste){

                        for(int i=0;i<liste.size();i++){



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


                                    /*
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
                        Log.i("BIDULE", listStation.get(0).toString());
                        finThread = true;
                   // }

                //});
            }






        };
        thread.start();


        Log.i("BIDULE", "FHGMKELJFHMLKDHFGMVQDNFVMJHDDFGLVKJQHF");
        while(finThread == false)
        {

        }
        for(int i = 0 ; i<listStation.size() ; i++)
        {

            Log.i("BIDULE", listStation.get(i).toString());


        }











        for(int i = 0; i<listStation.size(); i++)
        {
            mMap.addMarker(new MarkerOptions().position(new LatLng(listStation.get(i).getLat(), listStation.get(i).getLng())));
        }





        //mMap.moveCamera(CameraUpdateFactory.newLatLng());

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                // Getting view from the layout file info_window_layout
                View v = getLayoutInflater().inflate(R.layout.windowlayout, null);


                LatLng latLng = marker.getPosition();


                TextView name = (TextView) v.findViewById(R.id.tv_name);
                TextView adress = (TextView) v.findViewById(R.id.tv_adresse);
                TextView nbvelo = (TextView) v.findViewById(R.id.tv_nbvelo);
                TextView nbplace = (TextView) v.findViewById(R.id.tv_nbplace);
                TextView coord = (TextView) v.findViewById(R.id.tv_coord);

                name.setText("Nom : " + "Bidule");
                adress.setText("Adresse : "+ "Machin");
                nbvelo.setText("Nombre de vélos : " + 0);
                nbplace.setText("Nombre de places : " + 0);
                coord.setText(latLng.latitude + " | " + latLng.longitude);

                return v;
            }
        });



    }

}
