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
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

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
    private Button Cargar, Compartir, Nuevo;
    private TextView FechaI, FechaF, EFechaI, EFechaF;
    final Calendar calendar = Calendar.getInstance();
    LineDataSet lineDataSet;
    LineData datos;
    private int DiaI, MesI, AnioI, DiaF, MesF, AnioF;
    private String CSV = "Fecha,Tmax,Tmin,UCD,UCA\n";
    String nombre,NombreEstacion;
    private ArrayList<String> DatosEnEjeX = new ArrayList<>();
    private ArrayList<Entry> DatosEnEjeY = new ArrayList<>();
    private ArrayList<Entry> AcumuladoEnEjeY = new ArrayList<>();
    private ArrayList<ILineDataSet> lineDataSets = new ArrayList<>();
    ArrayList<UnidadesCalor> listUC = new ArrayList<UnidadesCalor>();



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
        File path = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "Consultas_UCD");
        path.mkdir();
      }


    DatePickerDialog.OnDateSetListener dI = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            if (dayOfMonth >= calendar.get(Calendar.DAY_OF_MONTH) && month >= calendar.get(Calendar.MONTH) && year >= calendar.get(Calendar.YEAR)) {
                Toast.makeText(Informacion_Estacion.this, "Aun no existe información", Toast.LENGTH_SHORT).show();
                FechaI.setText("");
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
                FechaF.setText("");
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
            if (!FechaI.getText().toString().equals("")) {
                if (!FechaF.getText().toString().equals("")) {
                    if (AnioF < AnioI) {
                        Toast.makeText(Informacion_Estacion.this, "La fecha final no puede ser mas antigua que la inicial", Toast.LENGTH_LONG).show();
                    } else if (MesF < MesI & AnioF <= AnioI) {
                        Toast.makeText(Informacion_Estacion.this, "La fecha final no puede ser mas antigua que la inicial", Toast.LENGTH_LONG).show();
                    } else if (DiaF < DiaI & MesF <= MesI & AnioF <= AnioI) {
                        Toast.makeText(Informacion_Estacion.this, "La fecha final no puede ser mas antigua que la inicial", Toast.LENGTH_LONG).show();
                    } else {
                        new JSONTaskUC().execute("http://pdiarios.alcohomeapp.com.mx/860138.json");
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

                String finaljson = buffer.toString();
                JSONObject jsonObject = new JSONObject(finaljson);
                JSONArray jsonArray = jsonObject.getJSONArray("estacion");
                JSONObject objetofinal;


                for (int i = 0; i < jsonArray.length(); i++) {
                    objetofinal = jsonArray.getJSONObject(i);
                    String fecha = objetofinal.getString("fecha");
                    listUC.add(
                            new UnidadesCalor
                                    (objetofinal.getString("fecha"),
                                            objetofinal.getDouble("tmax"),
                                            objetofinal.getDouble("tmin")));
                }

                return listUC;

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
            double acomulado = 0;
            if (result != null) {
                for (int i = 0; i < result.size(); i++) {
                    acomulado += UnidadesCalor(result.get(i).getTmax(), result.get(i).getTmin());
                    DatosEnEjeY.add(new Entry(i+1, (float) UnidadesCalor(result.get(i).getTmax(), result.get(i).getTmin())));
                    AcumuladoEnEjeY.add(new Entry(i+1,(float)acomulado));
                    CSV += result.get(i).getFecha() + "," + result.get(i).getTmax() + "," + result.get(i).getTmin() + "," + UnidadesCalor(result.get(i).getTmax(), result.get(i).getTmin()) +","+ acomulado +"\n";
                }

                LineDataSet lineDataSetAcomulado = new LineDataSet(AcumuladoEnEjeY,"Acomulado UCD");
                lineDataSet = new LineDataSet(DatosEnEjeY, "UCD del " + FechaI.getText().toString() + " al " + FechaF.getText().toString());

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
                lineDataSetAcomulado.setColor(Color.rgb(254,0,0));
                lineDataSetAcomulado.setValueTextSize(10f);
                lineDataSetAcomulado.setValueTextColor(Color.BLACK);
                lineDataSetAcomulado.setAxisDependency(YAxis.AxisDependency.LEFT);

                lineDataSets.add(lineDataSetAcomulado);

                datos = new LineData(lineDataSets);

                Description descripcion = new Description();
                descripcion.setText("");


                lineChart.setClickable(false);
                lineChart.setData(datos);
                lineChart.setDescription(descripcion);
                lineChart.setVisibleXRangeMaximum(result.size());
                lineChart.animateX(2000);
                lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);

                lineChart.invalidate();
            } else {
                Toast.makeText(Informacion_Estacion.this, "Conexión sin éxito", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private double UnidadesCalor(double Tmax, double Tmin) {
        double UC = 0;
        double Tbase = 12;

        if (Tmax > 30) Tmax = 30;
        if (Tmin < 10) Tmin = 10;

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

        File path = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),"/" + "Consultas_UCD/" + nombre);


        File fileWithinMyDir = new File(path.getPath());

        if (fileWithinMyDir.exists()) {
            Intent intentShareFile = new Intent(Intent.ACTION_SEND);
            intentShareFile.setType("application/*");
            intentShareFile.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + path.getAbsolutePath().toString()));
            System.out.println(path.getPath());
            intentShareFile.putExtra(Intent.EXTRA_SUBJECT, "Unidades calor");
            intentShareFile.putExtra(Intent.EXTRA_TEXT, nombre);
            System.out.println(nombre);
            startActivity(Intent.createChooser(intentShareFile, nombre));
            //Toast.makeText(this, "Simon", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "El archivo no existe", Toast.LENGTH_SHORT).show();
        }
    }

    public void grabar() {
        NombreEstacion = NombreEstacion.replace(" ","_");
        NombreEstacion = NombreEstacion.replace("\"","");
        nombre = NombreEstacion + "_Datos_UC_" + DiaI + "-" + MesI + "-" + AnioI + "_" + DiaF + "-" + MesF + "-" + AnioF + ".csv";
        File path = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),  "Consultas_UCD/" + nombre);

        try {
            FileOutputStream fos = new FileOutputStream(path);
            fos.write(CSV.getBytes());
            fos.close();
            //Toast.makeText(this, "Archivo guardado", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}