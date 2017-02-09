package com.example.quentin.mapmenu;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by Quentin on 07/02/2017.
 */

public class FavoriActivity extends Activity {
    ArrayList<Station> listStation;
    TextView fav;//
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favori);
        fav = (TextView)findViewById(R.id.text2);
       // fav.setText("azerty");
        String data[] = ReadSettings(this).split(",");
        if(data[0] != null) {
            fav.setText(data[0]);
        }
        setTitle("Favoris");
        listStation = new ArrayList<Station>();
    }

    public void addFavoris(Station station){
        listStation.add(station);
    }


// Read settings

    public String ReadSettings(Context context){

        FileInputStream fIn = null;

        InputStreamReader isr = null;



        char[] inputBuffer = new char[255];

        String data = null;



        try{
            fIn = openFileInput("settings.dat");
            isr = new InputStreamReader(fIn);
            isr.read(inputBuffer);
            data = new String(inputBuffer);
            Toast.makeText(context, "Settings read",Toast.LENGTH_SHORT).show();
        }

        catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Settings not read",Toast.LENGTH_SHORT).show();
        }

        finally {
            try {
                isr.close();
                fIn.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return data;
    }


}
