package com.example.prado.estaciones;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
    String Municipio = "";
    int filtro = 0;
    private boolean autoriza;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //** Pantalla completa
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //**

        setContentView(R.layout.activity_mapa);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        spiner_EDO = (Spinner) findViewById(R.id.spinner_EDO);
        spiner_MPIO = (Spinner) findViewById(R.id.spinner_MPIO);

        anuncio = (TextView)findViewById(R.id.advertencia);
        anuncio.setVisibility(View.INVISIBLE);
        //** Pide permiso de manipulación de archivos del usuario.
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            autoriza = false;
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1000);
        }else {
            autoriza = true;
            new JSONTask().execute(URL());
        }
        //**

        spiner_MPIO.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                mMap.clear();
                if (spiner_MPIO.getSelectedItemPosition() != 0) {
                    filtro = 2;
                    mMap.clear();
                    Municipio = spiner_MPIO.getSelectedItem().toString();
                    new JSONTask().execute(URL());
                }

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

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        String[] inicio = {"Municipios"};
        adaptadorspiners(inicio);
        LlenarSpineredo();
        edo();

        if (autoriza) {
            municipios();
        }

        filtro = 1;

        //Extrae el nombre del marcador seleccionado y cambia de activity
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Intent cambio = new Intent(Mapa.this, Informacion_Estacion.class);
                String Nombre = marker.getTitle();
                cambio.putExtra("Nombre",Nombre);
                startActivity(cambio);
            }
        });
        //***
    }

    //Proceso realizado en segundo plano, descarga de servicio web
    public class JSONTask extends AsyncTask<String, String, List<DatosEstaciones>> {
        int au;

        //Obtiene la URL del servicio web.
        @Override
        protected List<DatosEstaciones> doInBackground(String... param) {

            HttpURLConnection urlConnection = null;
            try {

                URL url = new URL(param[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();

                InputStream in = urlConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                StringBuffer buffer = new StringBuffer();

                String lineas = "";

                //Recorre línea por línea de lo encontrado en la página
                while ((lineas = reader.readLine()) != null) {
                    buffer.append(lineas);
                }

                String finaljson = buffer.toString();
                JSONObject jsonObject = new JSONObject(finaljson);
                JSONArray jsonArray = jsonObject.getJSONArray("estado");
                JSONObject objetofinal;

                ArrayList<DatosEstaciones> listdatosEstaciones = new ArrayList<DatosEstaciones>();

                if (filtro == 1) {
                    //Ciclo que obtiene los parametros deseados y llena una lista con ellos
                    for (int i = 0; i < jsonArray.length(); i++) {
                        objetofinal = jsonArray.getJSONObject(i);
                        listdatosEstaciones.add(
                                new DatosEstaciones
                                        (objetofinal.getString("Nombre"),
                                                objetofinal.getDouble("latitud"),
                                                objetofinal.getDouble("longitud")));
                    }
                    return listdatosEstaciones;

                } else if (filtro == 2) {

                    for (int i = 0; i < jsonArray.length(); i++) {
                        objetofinal = jsonArray.getJSONObject(i);
                        //Chihuahua au = 199
                        if (spiner_MPIO.getSelectedItemPosition() != 1 && spiner_MPIO.getSelectedItemPosition() != 0) {
                            au = spiner_MPIO.getSelectedItemPosition() + 198;
                        } else {
                            au = 199;
                        }
                        if (objetofinal.getInt("municipioid") == au) {
                            listdatosEstaciones.add(
                                    new DatosEstaciones
                                            (objetofinal.getString("Nombre"),
                                                    objetofinal.getDouble("latitud"),
                                                    objetofinal.getDouble("longitud")));
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
                if (urlConnection != null) urlConnection.disconnect();
            }

            return null;
        }

        @Override
        protected void onPostExecute(List<DatosEstaciones> result) {
            super.onPostExecute(result);
            if (result != null) {
                if (filtro == 1) {
                    Toast.makeText(Mapa.this, "Carga completada existen " + result.size() + " estaciones en " + spiner_EDO.getSelectedItem().toString(), Toast.LENGTH_LONG).show();
                    if (!result.isEmpty()) {
                        for (int i = 0; i < result.size(); i++) {
                            createMarker(result.get(i).getLatitud(), result.get(i).getLongitud(), result.get(i).getNombre());
                        }

                    }
                }
                if (filtro == 2) {
                    Toast.makeText(Mapa.this, "Carga completada existen " + result.size() + " estaciones en " + spiner_MPIO.getSelectedItem().toString(), Toast.LENGTH_LONG).show();
                    if (!result.isEmpty()) {
                        for (int i = 0; i < result.size(); i++) {
                            createMarker(result.get(i).getLatitud(), result.get(i).getLongitud(), result.get(i).getNombre());
                        }
                    }
                }
            } else {
                Toast.makeText(Mapa.this, "El servicio web no esta disponible", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void edo() {

            //Chihuahua
            coordenadas = new LatLng(28.847903, -106.534416);
            CameraUpdate MiUbicacion = CameraUpdateFactory.newLatLngZoom(coordenadas, 6);
            mMap.animateCamera(MiUbicacion);


    }

    protected Marker createMarker(double latitude, double longitude, String title) {
        return this.mMap.addMarker(new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                .title(title)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))

        );
    }

    private String URL() {

            return "http://pdiarios.alcohomeapp.com.mx/8.json";

    }

    private void LlenarSpineredo() {
        String[] Edos = {
                "Chihuahua"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.item_estilo_spiner_mapa, Edos);
        spiner_EDO.setAdapter(adapter);
    }

    private void municipios() {

                String[] municipio8 = {
                        "Municipios",
                        "Ahumada",
                        "Aldama",
                        "Allende",
                        "Aquiles Serdán",
                        "Ascensión",
                        "Bachíniva",
                        "Balleza",
                        "Batopilas",
                        "Bocoyna",
                        "Buenaventura",
                        "Camargo",
                        "Carichí",
                        "Casas Grandes",
                        "Chihuahua",
                        "Chínipas",
                        "Coronado",
                        "Coyame del Sotol",
                        "Cuauhtémoc",
                        "Cusihuiriachi",
                        "Delicias",
                        "Dr. Belisario Domínguez",
                        "El Tule",
                        "Galeana",
                        "Gómez Farías",
                        "Gran Morelos",
                        "Guachochi",
                        "Guadalupe",
                        "Guadalupe y Calvo",
                        "Guazapares",
                        "Guerrero",
                        "Hidalgo del Parral",
                        "Huejotitán",
                        "Ignacio Zaragoza",
                        "Janos",
                        "Jiménez",
                        "Juárez",
                        "Julimes",
                        "La Cruz",
                        "López",
                        "Madera",
                        "Maguarichi",
                        "Manuel Benavides",
                        "Matachí",
                        "Matamoros",
                        "Meoqui",
                        "Morelos",
                        "Moris",
                        "Namiquipa",
                        "Nonoava",
                        "Nuevo Casas Grandes",
                        "Ocampo",
                        "Ojinaga",
                        "Praxedis G. Guerrero",
                        "Riva Palacio",
                        "Rosales",
                        "Rosario",
                        "San Francisco de Borja",
                        "San Francisco de Conchos",
                        "San Francisco del Oro",
                        "Santa Bárbara",
                        "Santa Isabel",
                        "Satevó",
                        "Saucillo",
                        "Temósachi",
                        "Urique",
                        "Uruachi",
                        "Valle de Zaragoz"
                };
                adaptadorspiners(municipio8);

    }

    private void adaptadorspiners(String[] vector) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.item_estilo_spiner_mapa, vector);
        spiner_MPIO.setAdapter(adapter);
    }

    @Override
    public void onRequestPermissionsResult(int codigopermiso, @NonNull String[] permiso, @NonNull int[] respuestaPermiso){
        switch (codigopermiso){
            case 1000:
                if (respuestaPermiso[0] == PackageManager.PERMISSION_GRANTED){
                    anuncio.setVisibility(View.INVISIBLE);
                    autoriza = true;
                    municipios();
                    new JSONTask().execute(URL());
                }else{
                    anuncio.setVisibility(View.VISIBLE);
                    autoriza = false;
                    Toast.makeText(this, "Se requiere permiso para mostrar información", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}

