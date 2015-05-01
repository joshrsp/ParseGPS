package com.example.joserafael.parsegps;

import android.app.ProgressDialog;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ActionBarActivity {
    private TextView salida;
    private TextView salida2;
    private TextView salida3;
    private TextView salida4;
    private TextView salida5;

    private LocationManager locManager;
    private LocationListener locListener;
    ParseObject testObject;
    private ProgressDialog pDialog;
    List<ParseObject> ob;
    private ArrayList latitud;
    private ArrayList longitud;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);

        Parse.initialize(this, "KztzT20oBCdVfe4ua3Cm9pz2vgaplroZFJas0wRA", "g3hyZUawmclrLvyoqrNpAZ5iAQkpQRu9fpIanYZC");

        /*testObject.put("foo", "bar");
        testObject.saveInBackground();*/
        /////
        salida = (TextView) findViewById(R.id.texto1);
        salida.setText("Cargando...Espere un momento...");
        salida2 = (TextView) findViewById(R.id.texto2);
        salida2.setText("");
        salida3 = (TextView) findViewById(R.id.texto3);
        salida3.setText("");
        salida4 = (TextView) findViewById(R.id.texto4);
        salida4.setText("");
        salida5 = (TextView) findViewById(R.id.texto5);
        salida5.setText("");
        latitud = new ArrayList<String>();
        longitud = new ArrayList<String>();
        comenzarLocalizacion();
    }
    private void comenzarLocalizacion()
    {
        //Obtenemos una referencia al LocationManager
        locManager =
                (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        //Obtenemos la última posición conocida
        Location loc =
                locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        //Mostramos la última posición conocida
        mostrarPosicion(loc);

        //Nos registramos para recibir actualizaciones de la posición
        locListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                mostrarPosicion(location);
            }
            public void onProviderDisabled(String provider){
                salida.setText("Provider OFF");
            }
            public void onProviderEnabled(String provider){
                salida.setText("Provider ON ");
            }
            public void onStatusChanged(String provider, int status, Bundle extras){
                Log.i("", "Provider Status: " + status);
                salida.setText("Provider Status: " + status);
            }
        };

        locManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, 30000, 0, locListener);
    }

    private void mostrarPosicion(Location loc) {
        if(loc != null)
        {
            salida.setText("GPS activado");
            salida2.setText("Latitud: " + String.valueOf(loc.getLatitude()));
            salida3.setText("Longitud: " + String.valueOf(loc.getLongitude()));
            salida4.setText("Precision: " + String.valueOf(loc.getAccuracy()));
            Log.i("", String.valueOf(loc.getLatitude() + " - " + String.valueOf(loc.getLongitude())));
            testObject = new ParseObject("GPS");
            testObject.put("latitud",String.valueOf(loc.getLatitude()));
            testObject.put("longitud",String.valueOf(loc.getLongitude()));

            testObject.saveInBackground();
        }
        else
        {
            salida2.setText("Latitud: (sin_datos)");
            salida3.setText("Longitud: (sin_datos)");
            salida4.setText("Precision: (sin_datos)");
        }
    }
    public void hola(){
        String va="";

            for(int i=0;i< latitud.size();i++)
            {
                va=va+"Latitud: "+i+ " :\r\n" + latitud.get(i) +"\r\nLongitud : \r\n" + longitud.get(i) + "\r\n" ;

            }
            salida5.setText(va);


    }
    public void requestData(View view) {
        latitud.clear();
        longitud.clear();
       new GetData().execute();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * Async task class to get json by making HTTP call
     */
    private class GetData extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // Creating service handler class instance


            try {

                ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("GPS");

                ob = query.find();
                for (ParseObject dato : ob) {

                    latitud.add(dato.get("latitud") );
                    longitud.add(dato.get("longitud"));

                }
            } catch (com.parse.ParseException e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return null;



        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            /**
             * Updating parsed JSON data into ListView
             * */
            //  CustomAdapter adapter = new CustomAdapter(MainActivity.this, listaClima);
            //  listView.setAdapter(adapter);//toca mirar algo asi aca
            hola();
        }
    }
}
