package com.example.prado.consumo;

import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText FI, FF, IdE, Tbase, Campo;
    private String mostrar="";
    private Button Boton;
    String URL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FI = (EditText) findViewById(R.id.FechaI);
        FF = (EditText) findViewById(R.id.FechaF);
        IdE = (EditText) findViewById(R.id.IdEstacion);
        Tbase = (EditText) findViewById(R.id.Tbase);
        Campo = (EditText) findViewById(R.id.Campo);
        Boton = (Button) findViewById(R.id.Consultar);
        Boton.setOnClickListener(this);

        ConnectivityManager cm;
        NetworkInfo ni;
        cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        ni = cm.getActiveNetworkInfo();
        boolean tipoConexion1 = false;
        boolean tipoConexion2 = false;

        if (ni != null) {
            ConnectivityManager connManager1 = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mWifi = connManager1.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

            ConnectivityManager connManager2 = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mMobile = connManager2.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            if (mWifi.isConnected()) {
                tipoConexion1 = true;
                Toast.makeText(this, "Conexión WIFI", Toast.LENGTH_SHORT).show();
            }
            if (mMobile.isConnected()) {
                tipoConexion2 = true;
                Toast.makeText(this, "Red Movil", Toast.LENGTH_SHORT).show();            }

            if (tipoConexion1 == true || tipoConexion2 == true) {
                /* Estas conectado a internet usando wifi o redes moviles, puedes enviar tus datos */
            }
        }
        else {
            /* No estas conectado a internet */
            Toast.makeText(this, "No estas conectado a internet", Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    public void onClick(View view){
        if (view == Boton){
            //URL = "http://clima.inifap.gob.mx/servicio/api/degreeday?EstacionID=" + IdE.getText() + "&FechaIni=" + FI.getText() + "&FechaFin=" + FF.getText() + "&TempBase=" + Tbase.getText();
            URL= "http://clima.inifap.gob.mx/wapi/api/Datos?idEstado=1&IdEstacion=22581&fch1="+ FI.getText() +"&fch2="+FF.getText();
            System.out.println(URL);
            new JSONTaskUC().execute(URL);
        }
    }

    public class JSONTaskUC extends AsyncTask<String, String, List<InformacionSetGet>> {

        @Override
        protected List<InformacionSetGet> doInBackground(String... param) {
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
                //JSONArray jsonArray = jsonObject.getJSONArray("datos");
                JSONArray jsonArray = jsonObject.getJSONArray("estaciones");
                JSONObject objetofinal;

                List<InformacionSetGet> ListaFinal = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    objetofinal = jsonArray.getJSONObject(i);
                    InformacionSetGet L1 = new InformacionSetGet();

                   /* L1.setFecha(objetofinal.getString("fecha"));
                    L1.setPrecipitacion(objetofinal.getDouble("precipitacion"));
                    L1.setEto(objetofinal.getDouble("eto"));
                    L1.setGrados_dia(objetofinal.getDouble("grados_dia"));
                    System.out.println(objetofinal.getDouble("grados_dia"));*/
                    L1.setFecha(objetofinal.getString("FechaParseo"));
                    L1.setPrecipitacion(objetofinal.getDouble("Tmax"));
                    L1.setEto(objetofinal.getDouble("Tmin"));


                    ListaFinal.add(L1);
                }

                return ListaFinal;

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<InformacionSetGet> result) {
            super.onPostExecute(result);
            if (result != null) {
                for (int i = 0; i < result.size(); i++) {

                    mostrar +=
                            result.get(i).getFecha() + "," +
                                    result.get(i).getPrecipitacion() + "," +
                                    result.get(i).getEto() + "," +
                                    result.get(i).getGrados_dia() + "\n";

                }
                Campo.setText(mostrar);
            }
        }
    }
}
