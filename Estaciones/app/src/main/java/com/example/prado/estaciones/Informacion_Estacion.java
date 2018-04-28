package com.example.prado.estaciones;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.gms.ads.internal.gmsg.HttpClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Informacion_Estacion extends AppCompatActivity implements View.OnClickListener {

    //Declaración de variable para Gráfica Lineal
    private LineChart lineChart;
    boolean validar = true;
    private Button Cargar, Compartir, Nuevo;
    private TextView FechaI, FechaF, EFechaI, EFechaF;
    final Calendar calendar = Calendar.getInstance();
    LineDataSet lineDataSet;
    LineData datos;
    private int DiaI, MesI, AnioI, DiaF, MesF, AnioF;
    private String CSV = "Fecha,Tmax,Tmin,UCD,UCA\n";
    double UC = 0;
    String nombre,NombreEstacion;
    private ArrayList<String> DatosEnEjeX = new ArrayList<>();
    private ArrayList<Entry> DatosEnEjeY = new ArrayList<>();
    private ArrayList<Entry> AcumuladoEnEjeY = new ArrayList<>();
    private ArrayList<Entry> AcumuladoEnEjeYAlerta = new ArrayList<>();
    private ArrayList valoresX = new ArrayList();
    private ArrayList<ILineDataSet> lineDataSets = new ArrayList<>();
    ArrayList<UnidadesCalor> listUC = new ArrayList<UnidadesCalor>();
    private String Nestacion;
    private final String Carpeta = "UnidadesCalor/", Ruta_Imagen =Carpeta+"UC";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_informacion__estacion);

        lineChart = (LineChart) findViewById(R.id.GraficaLineal);
        Cargar = (Button) findViewById(R.id.BuscarFechas);
        Nuevo = (Button) findViewById(R.id.Nuevo);
        Nuevo.setVisibility(View.INVISIBLE);
        Compartir = (Button) findViewById(R.id.Compartit);
        Compartir.setVisibility(View.INVISIBLE);
        FechaI = (TextView) findViewById(R.id.FechaI);
        FechaF = (TextView) findViewById(R.id.FechaF);
        Cargar.setOnClickListener(this);
        Nuevo.setOnClickListener(this);
        Compartir.setOnClickListener(this);
        FechaI.setOnClickListener(this);
        FechaF.setOnClickListener(this);
        lineChart.setNoDataText("Sin datos para mostrar");

        NombreEstacion = getIntent().getStringExtra("Nombre");
        Nestacion = getIntent().getStringExtra("Nestacion");

      }

    DatePickerDialog.OnDateSetListener dI = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            if (dayOfMonth >= calendar.get(Calendar.DAY_OF_MONTH) && month >= calendar.get(Calendar.MONTH) && year >= calendar.get(Calendar.YEAR)) {
                Toast.makeText(Informacion_Estacion.this, "Aun no existe información", Toast.LENGTH_SHORT).show();
                FechaI.setText("Fecha inicial");
            } else {
                DiaI = dayOfMonth;
                MesI = (month + 1);
                AnioI = year;
                FechaI.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                FechaF.requestFocus();
            }
        }
    };

    DatePickerDialog.OnDateSetListener dF = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            if (dayOfMonth >= calendar.get(Calendar.DAY_OF_MONTH) && month >= calendar.get(Calendar.MONTH) && year >= calendar.get(Calendar.YEAR)) {
                Toast.makeText(Informacion_Estacion.this, "Aún no existe información", Toast.LENGTH_SHORT).show();
                FechaF.setText("Fecha final");
            } else {
                DiaF = dayOfMonth;
                MesF = (month + 1);
                AnioF = year;
                FechaF.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                Cargar.requestFocus();
            }
        }
    };

    @Override
    public void onClick(View v) {

        if (v == FechaI) {

            new DatePickerDialog(this, dI, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();

        }
        if (v == FechaF) {

            new DatePickerDialog(this, dF, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();

        }
        if (v == Cargar) {
            if (!FechaI.getText().toString().equals("Fecha inicial")) {
                if (!FechaF.getText().toString().equals("Fecha final")) {
                    if (AnioF < AnioI) {
                        Toast.makeText(Informacion_Estacion.this, "La fecha final no puede ser mas antigua que la inicial", Toast.LENGTH_LONG).show();
                    } else if (MesF < MesI & AnioF <= AnioI) {
                        Toast.makeText(Informacion_Estacion.this, "La fecha final no puede ser mas antigua que la inicial", Toast.LENGTH_LONG).show();
                    } else if (DiaF < DiaI & MesF <= MesI & AnioF <= AnioI) {
                        Toast.makeText(Informacion_Estacion.this, "La fecha final no puede ser mas antigua que la inicial", Toast.LENGTH_LONG).show();
                    } else {
                        //new JSONTaskUC().execute("http://pdiarios.alcohomeapp.com.mx/860138.json");
                        String URL ="http://clima.inifap.gob.mx/wapi/api/Datos?idEstado=8&IdEstacion=" + Nestacion + "&fch1="+ FechaI.getText().toString() +"&fch2="+FechaF.getText().toString();
                        System.out.println(URL);
                        new JSONTaskUC().execute(URL);
                        OcultarP();
                    }
                } else {
                    Toast.makeText(Informacion_Estacion.this, "Ingresa una fecha Final", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(Informacion_Estacion.this, "Ingresa una fecha Inicial", Toast.LENGTH_SHORT).show();
            }
        }
        if (v == Compartir) {
            grabar();
            Compartir();
        }
        if (v == Nuevo) {
            finish();
            startActivity(getIntent());
        }
    }

    public class JSONTaskUC extends AsyncTask<String, String, List<UnidadesCalor>> {

        @Override
        protected List<UnidadesCalor> doInBackground(String... param) {
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

                if (buffer.toString().equals("{\"estaciones\":[]}")) {
                    return null;

                }else {
                String finaljson = buffer.toString();

                    JSONObject jsonObject = new JSONObject(finaljson);
                    JSONArray jsonArray = jsonObject.getJSONArray("estaciones");
                    JSONObject objetofinal;
                    System.out.println(buffer.toString());
                    for (int i = 0; i < jsonArray.length(); i++) {
                        objetofinal = jsonArray.getJSONObject(i);


                        listUC.add(
                                new UnidadesCalor
                                        (objetofinal.getString("FechaParseo"),
                                                objetofinal.getDouble("Tmax"),
                                                objetofinal.getDouble("Tmin")));
                        System.out.println(objetofinal.getString("FechaParseo"));
                    }

                    return listUC;
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<UnidadesCalor> result) {
            super.onPostExecute(result);
            if (result != null) {
                double acomulado = 0;
                for (int i = 0; i < result.size(); i++) {
                    acomulado += UnidadesCalor(result.get(i).getTmax(), result.get(i).getTmin());
                    DatosEnEjeY.add(new Entry(i+1, (float) UnidadesCalor(result.get(i).getTmax(), result.get(i).getTmin())));
                    AcumuladoEnEjeY.add(new Entry(i + 1, (float) acomulado));
                    valoresX.add(result.get(i).getFecha());
                    if(acomulado >= 1019 && validar) {
                        AcumuladoEnEjeYAlerta.add(new Entry(i + 1, (float) acomulado));
                        validar = false;
                    }
                    CSV += result.get(i).getFecha() + "," + result.get(i).getTmax() + "," + result.get(i).getTmin() + "," + UnidadesCalor(result.get(i).getTmax(), result.get(i).getTmin()) +","+ acomulado +"\n";
                }

                LineDataSet lineDataSetAcomulado = new LineDataSet(AcumuladoEnEjeY,"UCDA");
                LineDataSet lineDataSetAcomuladoAlerta = new LineDataSet(AcumuladoEnEjeYAlerta,"Alerta");
                lineDataSet = new LineDataSet(DatosEnEjeY, "UCD");

                //lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
                lineDataSet.setDrawFilled(false);
                lineDataSet.setDrawCircles(false);
                lineDataSet.setLineWidth(1.5f);
                lineDataSet.setColor(Color.rgb(38,127,14));
                lineDataSet.setValueTextSize(10f);
                lineDataSet.setValueTextColor(Color.BLACK);
                lineDataSet.setAxisDependency(YAxis.AxisDependency.RIGHT);
                lineDataSets.add(lineDataSet);

                lineDataSetAcomulado.setDrawFilled(false);
                lineDataSetAcomulado.setDrawCircles(false);
                lineDataSetAcomulado.setLineWidth(1.5f);
                lineDataSetAcomulado.setColor(Color.rgb(0,0,254));
                lineDataSetAcomulado.setValueTextSize(10f);
                lineDataSetAcomulado.setValueTextColor(Color.BLACK);
                lineDataSetAcomulado.setAxisDependency(YAxis.AxisDependency.LEFT);
                lineDataSets.add(lineDataSetAcomulado);


                if(validar == false) {
                    lineDataSetAcomuladoAlerta.setDrawCircles(true);
                    lineDataSetAcomuladoAlerta.setCircleColor(Color.rgb(254, 0, 0));
                    lineDataSetAcomuladoAlerta.setCircleRadius(5f);
                    lineDataSetAcomuladoAlerta.setDrawCircleHole(false);
                    lineDataSetAcomuladoAlerta.setLineWidth(0f);
                    lineDataSetAcomuladoAlerta.setColor(Color.rgb(254, 93, 64));
                    lineDataSetAcomuladoAlerta.setValueTextSize(0f);
                    lineDataSetAcomuladoAlerta.setValueTextColor(Color.BLACK);
                    lineDataSetAcomuladoAlerta.setAxisDependency(YAxis.AxisDependency.LEFT);
                    lineDataSets.add(lineDataSetAcomuladoAlerta);
                }

                datos = new LineData(lineDataSets);

                Description descripcion = new Description();
                descripcion.setText("");


                lineChart.setClickable(false);

                lineChart.setData(datos);
                lineChart.setDescription(descripcion);
                lineChart.setVisibleXRangeMaximum(result.size());
                lineChart.animateX( 2000);
                //Cargar eje X
                lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
                lineChart.getXAxis().setLabelRotationAngle(-90f);
                lineChart.getXAxis().setGranularity(1f);
                lineChart.getXAxis().setDrawAxisLine(true);
                lineChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(valoresX));

                lineChart.invalidate();
            } else {
                Toast.makeText(Informacion_Estacion.this, "No existe información", Toast.LENGTH_SHORT).show();
                finish();
                startActivity(getIntent());
            }
        }
    }

    private double UnidadesCalor(double Tmax, double Tmin) {

        double Tbase = 3.3;

        //if (Tmax > 30) Tmax = 30;
        //if (Tmin < 10) Tmin = 10;

        UC = ((Tmax + Tmin) / 2) - Tbase;

        if (UC < 0) UC = 0;

        return UC;
    }

    public void OcultarP() {
        Cargar.setVisibility(View.INVISIBLE);
        FechaI.setVisibility(View.INVISIBLE);
        FechaF.setVisibility(View.INVISIBLE);
        Compartir.setVisibility(View.VISIBLE);
        Nuevo.setVisibility(View.VISIBLE);
    }

    private void Compartir() {
        NombreEstacion = NombreEstacion.replace(" ","_");
        String NombreArchivo = NombreEstacion + "_Datos_UC_" + DiaI + "-" + MesI + "-" + AnioI + "_" + DiaF + "-" + MesF + "-" + AnioF + ".csv";
        String path = Environment.getExternalStorageDirectory() + File.separator + Ruta_Imagen + File.separator + NombreArchivo;

        File fileWithinMyDir = new File(path);
        System.out.println(fileWithinMyDir);
        Uri dir = FileProvider.getUriForFile(this,"com.example.prado.estaciones",new File(path));
        System.out.println(dir);
        if (fileWithinMyDir.exists()) {

            Intent intentShareFile = new Intent();
            intentShareFile.setAction(Intent.ACTION_SEND);
            intentShareFile.putExtra(Intent.EXTRA_STREAM, dir);
            intentShareFile.putExtra(Intent.EXTRA_SUBJECT, "Unidades calor");
            intentShareFile.putExtra(Intent.EXTRA_TEXT, NombreArchivo);
            intentShareFile.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intentShareFile.setType("application/*");
            System.out.println(NombreArchivo);
            startActivity(Intent.createChooser(intentShareFile, nombre));
            //Toast.makeText(this, "Simon", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "El archivo no existe", Toast.LENGTH_SHORT).show();
        }
    }

    public void grabar() {
        File carpeta = new File (Environment.getExternalStorageDirectory(), Ruta_Imagen);
        boolean existe = carpeta.exists();
        String NombreArchivo = "";
        if (existe == false){
            existe = carpeta.mkdirs();
        }
        if(existe){
            NombreEstacion = NombreEstacion.replace(" ","_");
            NombreArchivo = NombreEstacion + "_Datos_UC_" + DiaI + "-" + MesI + "-" + AnioI + "_" + DiaF + "-" + MesF + "-" + AnioF + ".csv";
        }


        String path = Environment.getExternalStorageDirectory() + File.separator + Ruta_Imagen + File.separator + NombreArchivo;

        File ruta = new File(path);
        try {
            FileOutputStream fos = new FileOutputStream(ruta);
            fos.write(CSV.getBytes());
            fos.close();
            //Toast.makeText(this, "Archivo guardado", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}