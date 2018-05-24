package com.example.prado.estaciones;

import android.Manifest;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Mapa extends AppCompatActivity implements OnMapReadyCallback {

    //Declaración de variables globales.
    private GoogleMap mMap;
    public TextView anuncio;
    private LatLng coordenadas;
    private Spinner spiner_EDO;
    private Spinner spiner_MPIO;
    private ImageButton actualizar;
    String arreglomun = "";
    int filtro = 2;
    private boolean autoriza;
    private long tiempoPrimerClick;
    private ArrayList NumerosEstacion = new ArrayList();
    private ArrayList<Estaciones>estaciones = new ArrayList<Estaciones>();
    ConnectivityManager cm;
    NetworkInfo ni;
    private boolean WiFI = false;
    private boolean MoviL = false;
    private int cont = 0;
    ProgressDialog progressDialog;
    private String MuniciopioId;
    private String[] municipioID = {"0"};
    private int aux;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //** Pantalla completa
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //**

        setContentView(R.layout.activity_mapa);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        spiner_EDO = (Spinner) findViewById(R.id.spinner_EDO);
        spiner_MPIO = (Spinner) findViewById(R.id.spinner_MPIO);
        actualizar = (ImageButton) findViewById(R.id.Actualizar);
        anuncio = (TextView)findViewById(R.id.advertencia);
        anuncio.setVisibility(View.INVISIBLE);

        new JSONTaskMun().execute(URL(2,8));
        spiner_MPIO.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (cont == 1) {
                    mMap.clear();
                }
                cont = 1;
                        new JSONTask(Progreso()).execute(URL(1, 8));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        anuncio.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1000);
            }
        });

        //** Pide permiso de manipulación de archivos del usuario.
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            autoriza = false;
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1000);
        }else {
            autoriza = true;
        }
        //**
        actualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                conexion();
            }
        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        LlenarSpineredo();
        edo();

        if (autoriza) {

            new JSONTask(Progreso()).execute(URL(1, 8), URL(2, 8));
        }



        //Extrae el nombre del marcador seleccionado y cambia de activity
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Intent cambio = new Intent(Mapa.this, Informacion_Estacion.class);
                String Nombre = marker.getTitle();
                cambio.putExtra("Nombre",Nombre);
                cambio.putExtra("Nestacion" , marker.getSnippet());
                startActivity(cambio);
            }
        });
        //***
    }


    //Proceso realizado en segundo plano, descarga de servicio web
    public class JSONTask extends AsyncTask<String, String, List<DatosEstaciones>> {
        private final ProgressDialog progressDialog1;

        JSONTask(ProgressDialog progressDialog){
            this.progressDialog1 = progressDialog;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog1.show();
        }

        //Obtiene la URL del servicio web.
        @Override
        protected List<DatosEstaciones> doInBackground(String... param) {

            try {

                URL urledo = new URL(param[0]);

                HttpURLConnection urlConnectionedo = (HttpURLConnection) urledo.openConnection();
                urlConnectionedo.connect();

                InputStream inedo = urlConnectionedo.getInputStream();

                BufferedReader readeredo = new BufferedReader(new InputStreamReader(inedo));
                StringBuffer bufferedo = new StringBuffer();

                String lineasedo = "";

                //Recorre línea por línea de lo encontrado en la página
                while ((lineasedo = readeredo.readLine()) != null) {
                    bufferedo.append(lineasedo);
                }

                String finaljsonedo = bufferedo.toString();
                JSONObject jsonObjectedo = new JSONObject(finaljsonedo);
                JSONArray jsonArrayedo = jsonObjectedo.getJSONArray("est");
                JSONObject objetofinaledo;

                ArrayList<DatosEstaciones> listdatosEstaciones = new ArrayList<DatosEstaciones>();

                if (filtro == 1) {
                    //Ciclo que obtiene los parametros deseados y llena una lista con ellos
                    for (int i = 0; i < jsonArrayedo.length(); i++) {
                        objetofinaledo = jsonArrayedo.getJSONObject(i);
                        listdatosEstaciones.add(
                                new DatosEstaciones
                                        (objetofinaledo.getString("Nombre"),
                                                objetofinaledo.getDouble("Latitud"),
                                                objetofinaledo.getDouble("Longitud"),
                                                objetofinaledo.getInt("Numero")));
                    }
                    return listdatosEstaciones;

                } else if (filtro == 2) {
                    for (int j = 0; j < estaciones.size(); j++){
                        System.out.println("Datos de objeto "+estaciones.get(j).getNombre().toString());
                        String objetoestaciones = estaciones.get(j).getNombre().toString();
                        String varSpinerMun = spiner_MPIO.getSelectedItem().toString();
                        if (varSpinerMun.equals(objetoestaciones)){
                            aux = estaciones.get(j).getIndice();
                        }
                    }

                    for (int i = 0; i < jsonArrayedo.length(); i++) {
                        objetofinaledo = jsonArrayedo.getJSONObject(i);

                        if (objetofinaledo.getInt("MunicipioId") ==  aux) {
                            listdatosEstaciones.add(
                                    new DatosEstaciones
                                            (objetofinaledo.getString("Nombre"),
                                                    objetofinaledo.getDouble("Latitud"),
                                                    objetofinaledo.getDouble("Longitud"),
                                                    objetofinaledo.getInt("Numero")));
                        }

                    }
                    return listdatosEstaciones;
                    ///////////////////*******
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
            }

            return null;
        }

        @Override
        protected void onPostExecute(List<DatosEstaciones> result) {
            super.onPostExecute(result);
            if (result != null) {
                if (filtro == 1) {
                    Toast.makeText(Mapa.this, "Carga completada existen " + result.size() + " estaciones en " + spiner_EDO.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
                    progressDialog1.dismiss();
                    if (!result.isEmpty()) {
                        for (int i = 0; i < result.size(); i++) {
                            createMarker(result.get(i).getLatitud(), result.get(i).getLongitud(), result.get(i).getNombre(),String.valueOf(result.get(i).getID()));
                            progressDialog1.dismiss();
                        }

                    }
                }
                if (filtro == 2) {
                    Toast.makeText(Mapa.this, "Carga completada existen " + result.size() + " estaciones en " + spiner_MPIO.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
                    progressDialog1.dismiss();
                    if (!result.isEmpty()) {
                        for (int i = 0; i < result.size(); i++) {
                            createMarker(result.get(i).getLatitud(), result.get(i).getLongitud(), result.get(i).getNombre(),String.valueOf(result.get(i).getID()));
                            progressDialog1.dismiss();
                        }
                    }
                }
            } else {
                //Toast.makeText(Mapa.this, "", Toast.LENGTH_LONG).show();
            }
        }
    }

    public class JSONTaskMun extends AsyncTask<String,String,String[]> {

    @Override
    protected String[] doInBackground(String... param) {

        try {

            URL urlmun = new URL(param[0]);

            HttpURLConnection urlConnectionmun = (HttpURLConnection) urlmun.openConnection();

            urlConnectionmun.connect();

            InputStream inmun = urlConnectionmun.getInputStream();

            BufferedReader reademun = new BufferedReader(new InputStreamReader(inmun));
            StringBuffer buffermun = new StringBuffer();

            String lineamun = "";

            //Recorre línea por línea de lo encontrado en la página
            while ((lineamun = reademun.readLine()) != null) {
                buffermun.append(lineamun);
            }

            String finaljsonmun = buffermun.toString();
            JSONObject jsonObjectmun = new JSONObject(finaljsonmun);
            JSONArray jsonArraymun = jsonObjectmun.getJSONArray("municipios");
            JSONObject objetofinalmun;


            for (int i = 0; i < jsonArraymun.length(); i++) {
                objetofinalmun = jsonArraymun.getJSONObject(i);
                arreglomun += objetofinalmun.getString("Nombre") + "\n";
                estaciones.add(new Estaciones
                        (objetofinalmun.getString("Nombre"),objetofinalmun.getInt("Indice")));

            }
            String[] municipio = arreglomun.split("\n");
            return municipio;

        } catch (IOException e1) {
            e1.printStackTrace();
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
        return null;
        ///////////////////*******
    }

    @Override
    protected void onPostExecute(String[] s) {
        super.onPostExecute(s);
        adaptadorspiners(s);

    }
}

    public void edo() {

            //Chihuahua
            coordenadas = new LatLng(28.847903, -106.534416);
            CameraUpdate MiUbicacion = CameraUpdateFactory.newLatLngZoom(coordenadas, 6);
            mMap.animateCamera(MiUbicacion);


    }

    protected Marker createMarker(double latitude, double longitude, String title,String NEstacion) {
        return this.mMap.addMarker(new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                .title(title)
                .snippet(NEstacion)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))

        );

    }

    private String URL(int url, int valor) {
        if (url == 1) {
            return "http://clima.inifap.gob.mx/wapi/api/Estacion?idEstado=" + valor;
        }if (url == 2){
            return "http://clima.inifap.gob.mx/wapi/api/Estacion?idEst=" + valor;
        }
        return null;
    }

    private void LlenarSpineredo() {
        String[] Edos = {
                "Chihuahua"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.item_estilo_spiner_mapa, Edos);
        spiner_EDO.setAdapter(adapter);
    }
    //Método para cargar la cadena de información a los objetos spinner
    private void adaptadorspiners(String[] vector) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.item_estilo_spiner_mapa, vector);
        spiner_MPIO.setAdapter(adapter);
    }
    //Método para comparar la respuesta al permiso solicitado
    @Override
    public void onRequestPermissionsResult(int codigopermiso, @NonNull String[] permiso, @NonNull int[] respuestaPermiso){
        switch (codigopermiso){
            case 1000:
                if (respuestaPermiso[0] == PackageManager.PERMISSION_GRANTED){
                    anuncio.setVisibility(View.INVISIBLE);
                    autoriza = true;
                    new JSONTask(Progreso()).execute(URL(1, 8), URL(2, 8));
                }else{
                    anuncio.setVisibility(View.VISIBLE);
                    autoriza = false;
                    Toast.makeText(this, "Se requiere permiso para mostrar información", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
    //Método para salir de la aplicación precionando dos veces atras
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBackPressed(){
        if (tiempoPrimerClick + 2000 > System.currentTimeMillis()){
            super.onBackPressed();
            finishAffinity();
            return;
        }else {
            Toast.makeText(this, "Vuelve a presionar para salir", Toast.LENGTH_SHORT).show();
        }
        tiempoPrimerClick = System.currentTimeMillis();
        System.out.println(tiempoPrimerClick);
    }

    private void actualizar(){
        ProgressDialog progressDialog = new ProgressDialog(Mapa.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Descargando información...");

    }
    //Comprueba que tipo de conexión a red utiliza
    private void conexion(){
        cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        ni = cm.getActiveNetworkInfo();
        if (ni != null) {
            ConnectivityManager connManager1 = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo Wifi = connManager1.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

            ConnectivityManager connManager2 = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo Mobile = connManager2.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            if (Wifi.isConnected()) {
                WiFI = true;
                Toast.makeText(this, "Conectado a red WIFI", Toast.LENGTH_SHORT).show();
            }
            if (Mobile.isConnected()) {
                MoviL = true;
                Toast.makeText(this, "Conectado a red Móvil", Toast.LENGTH_SHORT).show();
            }

            if (WiFI != false || MoviL != false){

                new JSONTaskMun().execute(URL(2,8));
                new JSONTask(Progreso()).execute(URL(1, 8));

            }

        }
        else {
            /* No estas conectado a internet */
            Toast.makeText(this, "No tienes conexión a internet", Toast.LENGTH_SHORT).show();
        }
    }
    //Ejecuta la animación de progreso de descarga de JSON
    private ProgressDialog Progreso(){
        progressDialog = new ProgressDialog(Mapa.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Descargando información...");
        return progressDialog;
    }

}