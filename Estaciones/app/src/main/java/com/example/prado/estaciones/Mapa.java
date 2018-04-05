package com.example.prado.estaciones;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
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
    private GoogleMap mMap;
    private LatLng coordenadas;
    private Spinner spiner_EDO;
    private Spinner spiner_MPIO;
    int carga = 0;
    String Municipio = "";
    int filtro = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_mapa);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1000);
        }

        spiner_EDO = (Spinner) findViewById(R.id.spinner_EDO);
        spiner_MPIO = (Spinner) findViewById(R.id.spinner_MPIO);


        spiner_EDO.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mMap.clear();
                if (spiner_EDO.getSelectedItemPosition() != 0) {
                    filtro = 1;
                    new JSONTask().execute(URL(spiner_EDO.getSelectedItemPosition()));
                    edo(spiner_EDO.getSelectedItemPosition());
                    municipios(spiner_EDO.getSelectedItemPosition());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }

        });

        spiner_MPIO.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                mMap.clear();
                if (spiner_MPIO.getSelectedItemPosition() != 0) {
                    filtro = 2;
                    mMap.clear();
                    Municipio = spiner_MPIO.getSelectedItem().toString();
                    new JSONTask().execute(URL(spiner_EDO.getSelectedItemPosition()));
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        this.coordenadas = new LatLng(23.634501, -102.55278399999997);
        CameraUpdate MiUbicacion = CameraUpdateFactory.newLatLngZoom(coordenadas, 4);
        mMap.animateCamera(MiUbicacion);

        String[] inicio = {"Municipios"};
        adaptadorspiners(inicio);
        LlenarSpineredo();

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Intent cambio = new Intent(Mapa.this, Informacion_Estacion.class);
                startActivity(cambio);
            }
        });

    }

    public class JSONTask extends AsyncTask<String, String, List<DatosEstaciones>> {
        int au;

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

                while ((lineas = reader.readLine()) != null) {
                    buffer.append(lineas);
                }

                String finaljson = buffer.toString();
                JSONObject jsonObject = new JSONObject(finaljson);
                JSONArray jsonArray = jsonObject.getJSONArray("estado");
                JSONObject objetofinal;

                ArrayList<DatosEstaciones> listdatosEstaciones = new ArrayList<DatosEstaciones>();

                if (filtro == 1) {

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

                        //Aguascalientes

                        if (spiner_MPIO.getSelectedItemPosition() != 1 && spiner_MPIO.getSelectedItemPosition() != 0) {
                            au = spiner_MPIO.getSelectedItemPosition();
                        } else {
                            au = 1;
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

    public void edo(int id) {

        if (id == 1) {
            //Aguascalientes
            this.coordenadas = new LatLng(21.947102, -102.308799);
            CameraUpdate MiUbicacion = CameraUpdateFactory.newLatLngZoom(coordenadas, 9);
            mMap.animateCamera(MiUbicacion);

        } else if (id == 2) {
            //Baja California
            this.coordenadas = new LatLng(30.472813, -114.479477);
            CameraUpdate MiUbicacion = CameraUpdateFactory.newLatLngZoom(coordenadas, 6);
            mMap.animateCamera(MiUbicacion);

        } else if (id == 3) {
            //Baja California Sur
            this.coordenadas = new LatLng(24.082039, -113.101963);
            CameraUpdate MiUbicacion = CameraUpdateFactory.newLatLngZoom(coordenadas, 6);
            mMap.animateCamera(MiUbicacion);

        } else if (id == 4) {
            //Campeche
            this.coordenadas = new LatLng(19.264958, -90.636586);
            CameraUpdate MiUbicacion = CameraUpdateFactory.newLatLngZoom(coordenadas, 7);
            mMap.animateCamera(MiUbicacion);

        } else if (id == 5) {
            //Coahuila
            this.coordenadas = new LatLng(27.408257, -101.981846);
            CameraUpdate MiUbicacion = CameraUpdateFactory.newLatLngZoom(coordenadas, 7);
            mMap.animateCamera(MiUbicacion);

        } else if (id == 6) {
            //Colima
            this.coordenadas = new LatLng(19.140471, -103.924778);
            CameraUpdate MiUbicacion = CameraUpdateFactory.newLatLngZoom(coordenadas, 9);
            mMap.animateCamera(MiUbicacion);

        } else if (id == 7) {
            //Chiapas
            this.coordenadas = new LatLng(16.468254, -92.522169);
            CameraUpdate MiUbicacion = CameraUpdateFactory.newLatLngZoom(coordenadas, 7);
            mMap.animateCamera(MiUbicacion);

        } else if (id == 8) {
            //Chihuahua
            coordenadas = new LatLng(28.847903, -106.534416);
            CameraUpdate MiUbicacion = CameraUpdateFactory.newLatLngZoom(coordenadas, 6);
            mMap.animateCamera(MiUbicacion);

        } else if (id == 9) {
            //Durango
            this.coordenadas = new LatLng(24.8007926, -104.789986);
            CameraUpdate MiUbicacion = CameraUpdateFactory.newLatLngZoom(coordenadas, 7);
            mMap.animateCamera(MiUbicacion);

        } else if (id == 10) {
            //Estado de México
            this.coordenadas = new LatLng(19.499130, -99.715721);
            CameraUpdate MiUbicacion = CameraUpdateFactory.newLatLngZoom(coordenadas, 8);
            mMap.animateCamera(MiUbicacion);

        } else if (id == 11) {
            //Guanajuato
            this.coordenadas = new LatLng(20.846765, -101.060817);
            CameraUpdate MiUbicacion = CameraUpdateFactory.newLatLngZoom(coordenadas, 7);
            mMap.animateCamera(MiUbicacion);

        } else if (id == 12) {
            //Guerrero
            this.coordenadas = new LatLng(17.666794, -100.008812);
            CameraUpdate MiUbicacion = CameraUpdateFactory.newLatLngZoom(coordenadas, 7);
            mMap.animateCamera(MiUbicacion);

        } else if (id == 13) {
            //Pendiente hacia abajo
            //Hidalgo
            //this.coordenadas = new LatLng(21.947102,-102.308799);
            CameraUpdate MiUbicacion = CameraUpdateFactory.newLatLngZoom(coordenadas, 9);
            mMap.animateCamera(MiUbicacion);

        } else if (id == 14) {
            //this.coordenadas = new LatLng(21.947102,-102.308799);
            //Jalisco
            CameraUpdate MiUbicacion = CameraUpdateFactory.newLatLngZoom(coordenadas, 8);
            mMap.animateCamera(MiUbicacion);

        } else if (id == 15) {
            //Michoacán
            //this.coordenadas = new LatLng(21.947102,-102.308799);
            CameraUpdate MiUbicacion = CameraUpdateFactory.newLatLngZoom(coordenadas, 8);
            mMap.animateCamera(MiUbicacion);

        } else if (id == 16) {
            //Morelos
            //this.coordenadas = new LatLng(21.947102,-102.308799);
            CameraUpdate MiUbicacion = CameraUpdateFactory.newLatLngZoom(coordenadas, 8);
            mMap.animateCamera(MiUbicacion);

        } else if (id == 17) {
            //Nayarit
            //this.coordenadas = new LatLng(21.947102,-102.308799);
            CameraUpdate MiUbicacion = CameraUpdateFactory.newLatLngZoom(coordenadas, 8);
            mMap.animateCamera(MiUbicacion);

        } else if (id == 18) {
            //Nuevo León
            //this.coordenadas = new LatLng(21.947102,-102.308799);
            CameraUpdate MiUbicacion = CameraUpdateFactory.newLatLngZoom(coordenadas, 8);
            mMap.animateCamera(MiUbicacion);

        } else if (id == 19) {
            //Oaxaca
            //this.coordenadas = new LatLng(21.947102,-102.308799);
            CameraUpdate MiUbicacion = CameraUpdateFactory.newLatLngZoom(coordenadas, 8);
            mMap.animateCamera(MiUbicacion);

        } else if (id == 20) {
            //this.coordenadas = new LatLng(21.947102,-102.308799);
            CameraUpdate MiUbicacion = CameraUpdateFactory.newLatLngZoom(coordenadas, 8);
            mMap.animateCamera(MiUbicacion);

        } else if (id == 21) {
            //this.coordenadas = new LatLng(21.947102,-102.308799);
            CameraUpdate MiUbicacion = CameraUpdateFactory.newLatLngZoom(coordenadas, 8);
            mMap.animateCamera(MiUbicacion);

        } else if (id == 22) {
            //this.coordenadas = new LatLng(21.947102,-102.308799);
            CameraUpdate MiUbicacion = CameraUpdateFactory.newLatLngZoom(coordenadas, 8);
            mMap.animateCamera(MiUbicacion);

        } else if (id == 23) {
            //this.coordenadas = new LatLng(21.947102,-102.308799);
            CameraUpdate MiUbicacion = CameraUpdateFactory.newLatLngZoom(coordenadas, 8);
            mMap.animateCamera(MiUbicacion);

        } else if (id == 24) {
            //this.coordenadas = new LatLng(21.947102,-102.308799);
            CameraUpdate MiUbicacion = CameraUpdateFactory.newLatLngZoom(coordenadas, 8);
            mMap.animateCamera(MiUbicacion);

        } else if (id == 25) {
            //this.coordenadas = new LatLng(21.947102,-102.308799);
            CameraUpdate MiUbicacion = CameraUpdateFactory.newLatLngZoom(coordenadas, 8);
            mMap.animateCamera(MiUbicacion);

        } else if (id == 26) {
            //this.coordenadas = new LatLng(21.947102,-102.308799);
            CameraUpdate MiUbicacion = CameraUpdateFactory.newLatLngZoom(coordenadas, 8);
            mMap.animateCamera(MiUbicacion);

        } else if (id == 27) {
            //this.coordenadas = new LatLng(21.947102,-102.308799);
            CameraUpdate MiUbicacion = CameraUpdateFactory.newLatLngZoom(coordenadas, 8);
            mMap.animateCamera(MiUbicacion);

        } else if (id == 28) {
            //this.coordenadas = new LatLng(21.947102,-102.308799);
            CameraUpdate MiUbicacion = CameraUpdateFactory.newLatLngZoom(coordenadas, 8);
            mMap.animateCamera(MiUbicacion);

        } else if (id == 29) {
            //this.coordenadas = new LatLng(21.947102,-102.308799);
            CameraUpdate MiUbicacion = CameraUpdateFactory.newLatLngZoom(coordenadas, 8);
            mMap.animateCamera(MiUbicacion);

        } else if (id == 30) {
            //this.coordenadas = new LatLng(21.947102,-102.308799);
            CameraUpdate MiUbicacion = CameraUpdateFactory.newLatLngZoom(coordenadas, 8);
            mMap.animateCamera(MiUbicacion);

        } else if (id == 31) {
            //this.coordenadas = new LatLng(21.947102,-102.308799);
            CameraUpdate MiUbicacion = CameraUpdateFactory.newLatLngZoom(coordenadas, 8);
            mMap.animateCamera(MiUbicacion);

        } else if (id == 32) {
            //this.coordenadas = new LatLng(21.947102,-102.308799);
            CameraUpdate MiUbicacion = CameraUpdateFactory.newLatLngZoom(coordenadas, 8);
            mMap.animateCamera(MiUbicacion);
        }
    }

    protected Marker createMarker(double latitude, double longitude, String title) {

        return this.mMap.addMarker(new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                .title(title)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET))
        );
    }

    private String URL(int id) {
        if (id == 1) {
            return "http://pdiarios.alcohomeapp.com.mx/1.json";
        }
        if (id == 8) {
            return "http://pdiarios.alcohomeapp.com.mx/8.json";
        }

        return null;
    }

    private void LlenarSpineredo() {
        String[] Edos = {
                "Estados",
                "Aguascalientes",
                "Baja California",
                "Baja California Sur",
                "Campeche",
                "Coahuila",
                "Colima",
                "Chiapas",
                "Chihuahua",
                "Durango",
                "Estado de México",
                "Guanajuato",
                "Guerrero",
                "Hidalgo",
                "Jalisco",
                "Michoacán",
                "Morelos",
                "Nayarit",
                "Nuevo León",
                "Oaxaca",
                "Puebla",
                "Querétaro",
                "Quintana Roo",
                "San Luis Potosí",
                "Sinaloa",
                "Sonora",
                "Tabasco",
                "Tamaulipas",
                "Tlaxcala",
                "Veracruz",
                "Yucatán",
                "Zacatecas",
                "Distrito Federal"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.item_estilo_spiner_mapa, Edos);
        spiner_EDO.setAdapter(adapter);
    }

    private void municipios(int id) {

        switch (id) {
            case 1:
                String[] municipio1 = {
                        "Municipios",
                        "Aguascalientes",
                        "Asientos",
                        "Calvillo",
                        "Cosío",
                        "El Llano",
                        "Jesús María",
                        "Pabellón de Arteaga",
                        "Rincón de Romos",
                        "San Francisco de los Romo",
                        "San José de Gracia",
                        "Tepezalá"
                };
                adaptadorspiners(municipio1);
                break;

            case 8:
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
                break;
        }


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

                }else{

                }
        }
    }
}

