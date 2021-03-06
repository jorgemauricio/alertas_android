package com.example.prado.Barrenador_del_RUEZNO;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Informacion_Estacion extends AppCompatActivity implements View.OnClickListener {
    //Declaración de variable para Gráfica Lineal
    private FloatingActionButton nuevo, Pronostico;
    private FloatingActionsMenu Grupo;
    private LineChart lineChart;
    private ImageView Compartir;
    private boolean validar1 = true,validar2 = true,validar3 = true,validar4 = true,validar5 = true;
    private Button Cargar;
    private TextView FechaI, FechaF;
    private TextView cerrar,InformacionAlerta,Alerta;
    private final Calendar calendar = Calendar.getInstance();
    private LineDataSet lineDataSet;
    private LineData datos;
    private int DiaI, MesI, AnioI, DiaF, MesF, AnioF;
    private String CSV = "Fecha,Tmax,Tmin,UCD,UCA\n";
    private double UC = 0, Promedio = 0;
    private float posX1 = 0, posY1 = 0;
    private float posX2 = 0, posY2 = 0;
    private float posX3 = 0, posY3 = 0;
    private float posX4 = 0, posY4 = 0;
    private float posX5 = 0, posY5 = 0;
    private String NombreEstacion;
    private ArrayList<String> DatosEnEjeX = new ArrayList<>();
    private ArrayList<Entry> DatosEnEjeY = new ArrayList<>();
    private ArrayList<Entry> AcumuladoEnEjeY = new ArrayList<>();
    private ArrayList<Entry> AcumuladoEnEjeYPreoviposicion = new ArrayList<>();
    private ArrayList<Entry> AcumuladoEnEjeYHuevo = new ArrayList<>();
    private ArrayList<Entry> AcumuladoEnEjeYOviposicion = new ArrayList<>();
    private ArrayList<Entry> AcumuladoEnEjeYLarvaPupa = new ArrayList<>();
    private ArrayList<Entry> AcumuladoEnEjeYAdultoAdulto = new ArrayList<>();
    private ArrayList valoresX = new ArrayList();
    private ArrayList<ILineDataSet> lineDataSets = new ArrayList<>();
    private ArrayList<UnidadesCalor> listUC = new ArrayList<UnidadesCalor>();
    private String Nestacion;
    private final String Carpeta = "UnidadesCalor/", Ruta_Imagen =Carpeta+"UC";
    private Dialog dialog;
    private String fecpop1,fecpop2,fecpop3,fecpop4,fecpop5;
    private String fase1 = "Preoviposición",fase2 = "Huevo",fase3 = "Oviposición",fase4 = "Larva pupa",fase5 = "Adulto adulto";
    private String Recomendacion1, Recomendacion2, Recomendacion3, Recomendacion4, Recomendacion5;
    private int UCApop1, UCApop2, UCApop3, UCApop4, UCApop5, UCAcontrol;
    private int informa = 1;
    private View cuadroalerta,FondoAlertaEstacion;
    private CharSequence nombre;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_informacion__estacion);

        lineChart = (LineChart) findViewById(R.id.GraficaLineal);
        Cargar = (Button) findViewById(R.id.BuscarFechas);
        FechaI = (TextView) findViewById(R.id.FechaI);
        FechaF = (TextView) findViewById(R.id.FechaF);
        Cargar.setOnClickListener(this);
        FechaI.setOnClickListener(this);
        FechaF.setOnClickListener(this);
        lineChart.setNoDataText("Sin datos para mostrar");
        dialog = new Dialog(this);
        FondoAlertaEstacion = (View)findViewById(R.id.FondoInformacionEstacion);
        nuevo = (FloatingActionButton) findViewById(R.id.NuevaConsulta);
        nuevo.setOnClickListener(this);
        Grupo = (FloatingActionsMenu) findViewById(R.id.grupofloat);
        Grupo.setOnClickListener(this);
        Pronostico = (FloatingActionButton) findViewById(R.id.Pronostico);
        Pronostico.setOnClickListener(this);




        NombreEstacion = getIntent().getStringExtra("Nombre");
        Nestacion = getIntent().getStringExtra("Nestacion");

        lineChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {

                float x = e.getX();
                float y = e.getY();

                if (x == posX1 && y == posY1) {
                    Alerta(fecpop1, String.valueOf(UCApop1), fase1, Recomendacion1);
                }if (x == posX2 && y == posY2) {
                    Alerta(fecpop2, String.valueOf(UCApop2), fase2, Recomendacion2);
                }if (x == posX3 && y == posY3) {
                    Alerta(fecpop3, String.valueOf(UCApop3), fase3, Recomendacion3);
                }if (x == posX4 && y == posY4) {
                    Alerta(fecpop4, String.valueOf(UCApop4), fase4, Recomendacion4);
                }if (x == posX5 && y == posY5) {
                    Alerta(fecpop5, String.valueOf(UCApop5), fase5, Recomendacion5);
                }

            }
            @Override
            public void onNothingSelected() {

            }
        });

    }

    public void Alerta(String fecha, String UCA, String fase, String Recomendacion) {
        dialog.setContentView(R.layout.alerta_view);

        Alerta = (TextView) dialog.findViewById(R.id.Alerta);
        InformacionAlerta = (TextView) dialog.findViewById(R.id.InformacionAlerta);
        cerrar = (TextView) dialog.findViewById(R.id.cerrar);
        Compartir = (ImageView) dialog.findViewById(R.id.Compartir);
        cuadroalerta = (View) dialog.findViewById(R.id.FondoAlerta);


        Alerta.setText("Alerta");
        InformacionAlerta.setText(
                "Estación: " + NombreEstacion.replace("_", " ") + "\n\n" +
                        "Fecha de alerta: " + fecha + "\n\n" +
                        "UCA a la fecha: " + String.valueOf(UCA) + " UC" + "\n\n" +
                        "Fase biológica estimada: " + fase + "\n\n" +
                        "*Areas de influencia: 5 km de radio" + "\n\n" +
                        "Recomendación: " + Recomendacion + "\n\n");

        cerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                FondoAlertaEstacion.setBackgroundColor(Color.parseColor("#ffffff"));
            }
        });

        Compartir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bitmap screenshot = Screenshot.tomarRutadeScreenshot(cuadroalerta);
                saveScreenshot(screenshot);
                grabar();
                Compartir();
            }
        });
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        FondoAlertaEstacion.setBackgroundColor(Color.argb(2,174,171,170));
    }


    private void Informe(int UCA, double lim, String fase) {

        dialog.setContentView(R.layout.alerta_view);

        Alerta = (TextView) dialog.findViewById(R.id.Alerta);
        InformacionAlerta = (TextView) dialog.findViewById(R.id.InformacionAlerta);
        cerrar = (TextView) dialog.findViewById(R.id.cerrar);
        Compartir = (ImageView) dialog.findViewById(R.id.Compartir);
        cuadroalerta = (View) dialog.findViewById(R.id.FondoAlerta);

            Alerta.setText("Información");
            InformacionAlerta.setText(
                    "Al día " + FechaF.getText().toString() + " \n\nSe han acumulado " + UCA +
                            " UC\n\nEn la estación " + NombreEstacion.replace("_", " ") + "\n\n" +
                            "Acumulación diaria promedio: " + Promedio + " UC\n\n" +
                            "Se estiman " + Ndias(UCA,lim) + " días para llegar a la fase de " + fase +" \n\n" +
                            "Periodo de consulta: \n" + FechaI.getText().toString() + " al " + FechaF.getText().toString());

        cerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                FondoAlertaEstacion.setBackgroundColor(Color.parseColor("#ffffff"));
            }
        });

        Compartir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bitmap screenshot = Screenshot.tomarRutadeScreenshot(cuadroalerta);
                saveScreenshot(screenshot);
                grabar();
                Compartir();
            }
        });
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        FondoAlertaEstacion.setBackgroundColor(Color.argb(2,174,171,170));
    }


    private void saveScreenshot(Bitmap bitmap) {


        try {
            // nombre y ruta de la imagen a incluir
            String mPath = Environment.getExternalStorageDirectory().toString() + File.separator + Carpeta + "Alerta.png";

            File imageFile = new File(mPath);

            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.PNG, quality, outputStream);
            outputStream.flush();
            outputStream.close();

        } catch (Throwable e) {
            // Captura los distintos errores que puedan surgir
            e.printStackTrace();
        }
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
                        ProgressDialog progressDialog = new ProgressDialog(this);
                        progressDialog.setIndeterminate(true);
                        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        progressDialog.setMessage("Descargando información...");
                        new JSONTaskUC(progressDialog).execute(URL);
                        Grupo.setVisibility(View.VISIBLE);
                        OcultarP();
                    }
                } else {
                    Toast.makeText(Informacion_Estacion.this, "Ingresa una fecha Final", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(Informacion_Estacion.this, "Ingresa una fecha Inicial", Toast.LENGTH_SHORT).show();
            }
        }
        if (v == nuevo) {
            finish();
            startActivity(getIntent());
        }
        if (v == Pronostico){
            if (informa == 1){
                Informe(UCAcontrol,55.9,fase1);
            }if (informa == 2){
                Informe(UCAcontrol,122.1,fase2);
            }if (informa == 3){
                Informe(UCAcontrol,169.8,fase3);
            }if (informa == 4){
                Informe(UCAcontrol,715.1,fase4);
            }if (informa == 5){
                Informe(UCAcontrol,1327.4,fase5);
            }
        }
    }

    public class JSONTaskUC extends AsyncTask<String, String, List<UnidadesCalor>> {
        ProgressDialog progressDialog;
        JSONTaskUC(ProgressDialog progressDialog){
            this.progressDialog = progressDialog;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }


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
                    for (int i = 0; i < jsonArray.length(); i++) {
                        objetofinal = jsonArray.getJSONObject(i);
                        listUC.add(
                                new UnidadesCalor
                                        (objetofinal.getString("FechaParseo"),
                                                objetofinal.getDouble("Tmax"),
                                                objetofinal.getDouble("Tmin")));
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
                double acumulado = 0, acumuladocontrol = 0;

                for (int i = 0; i < result.size(); i++) {
                    acumulado += UnidadesCalor(result.get(i).getTmax(), result.get(i).getTmin());
                    acumuladocontrol += UnidadesCalor(result.get(i).getTmax(), result.get(i).getTmin());
                    DatosEnEjeY.add(new Entry(i, (float) UnidadesCalor(result.get(i).getTmax(), result.get(i).getTmin())));
                    AcumuladoEnEjeY.add(new Entry(i , (float) acumulado));
                    valoresX.add(result.get(i).getFecha());

                    if(acumulado >= 55.9 && validar1) {
                        AcumuladoEnEjeYPreoviposicion.add(new Entry(i, (float) acumulado));
                        posX1 = AcumuladoEnEjeYPreoviposicion.get(0).getX();
                        posY1 = AcumuladoEnEjeYPreoviposicion.get(0).getY();
                        fecpop1 = result.get(i).getFecha();
                        UCApop1 = (int) acumulado;
                        validar1 = false;
                        informa = 2;
                    }
                    if(acumulado >= 122.1 && validar2) {
                        AcumuladoEnEjeYHuevo.add(new Entry(i, (float) acumulado));
                        posX2 = AcumuladoEnEjeYHuevo.get(0).getX();
                        posY2 = AcumuladoEnEjeYHuevo.get(0).getY();
                        fecpop2 = result.get(i).getFecha();
                        UCApop2 = (int) acumulado;
                        validar2 = false;
                        informa = 3;
                    }
                    if(acumulado >= 169.8 && validar3) {
                        AcumuladoEnEjeYOviposicion.add(new Entry(i, (float) acumulado));
                        posX3 = AcumuladoEnEjeYOviposicion.get(0).getX();
                        posY3 = AcumuladoEnEjeYOviposicion.get(0).getY();
                        fecpop3 = result.get(i).getFecha();
                        UCApop3 = (int) acumulado;
                        validar3 = false;
                        informa = 4;
                    }
                    if(acumulado >= 715.1 && validar4) {
                        AcumuladoEnEjeYLarvaPupa.add(new Entry(i, (float) acumulado));
                        posX4 = AcumuladoEnEjeYLarvaPupa.get(0).getX();
                        posY4 = AcumuladoEnEjeYLarvaPupa.get(0).getY();
                        fecpop4 = result.get(i).getFecha();
                        UCApop4 = (int) acumulado;
                        validar4 = false;
                        informa = 5;
                        acumuladocontrol = 0;
                    }
                    if(acumulado >= 1327.4 && validar5) {
                        AcumuladoEnEjeYAdultoAdulto.add(new Entry(i, (float) acumulado));
                        posX5 = AcumuladoEnEjeYAdultoAdulto.get(0).getX();
                        posY5 = AcumuladoEnEjeYAdultoAdulto.get(0).getY();
                        fecpop5 = result.get(i).getFecha();
                        UCApop5 = (int) acumulado;
                        validar5 = false;
                        Pronostico.setVisibility(View.INVISIBLE);
                        Pronostico.setEnabled(false);
                    }
                    UCAcontrol = (int) acumulado;
                    Promedio = UCP(result.size());
                    CSV += result.get(i).getFecha() + "," + result.get(i).getTmax() + "," + result.get(i).getTmin() + "," + UnidadesCalor(result.get(i).getTmax(), result.get(i).getTmin()) +","+ acumulado +"\n";
                }

                LineDataSet lineDataSetAcomulado = new LineDataSet(AcumuladoEnEjeY,"UCDA");
                LineDataSet lineDataSetAcomuladoAlerta1 = new LineDataSet(AcumuladoEnEjeYPreoviposicion,"Preoviposición");
                LineDataSet lineDataSetAcomuladoAlerta2 = new LineDataSet(AcumuladoEnEjeYHuevo,"Huevo");
                LineDataSet lineDataSetAcomuladoAlerta3 = new LineDataSet(AcumuladoEnEjeYOviposicion,"Oviposición");
                LineDataSet lineDataSetAcomuladoAlerta4 = new LineDataSet(AcumuladoEnEjeYLarvaPupa,"Larva-pupa");
                LineDataSet lineDataSetAcomuladoAlerta5 = new LineDataSet(AcumuladoEnEjeYAdultoAdulto,"Adulto-Adulto");

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


                if(validar1 == false) {
                    lineDataSetAcomuladoAlerta1.setDrawCircles(true);
                    lineDataSetAcomuladoAlerta1.setCircleColor(Color.parseColor("#D0F407"));
                    lineDataSetAcomuladoAlerta1.setCircleRadius(8f);
                    lineDataSetAcomuladoAlerta1.setDrawCircleHole(false);
                    lineDataSetAcomuladoAlerta1.setLineWidth(0f);
                    lineDataSetAcomuladoAlerta1.setColor(Color.parseColor("#D0F407"));
                    lineDataSetAcomuladoAlerta1.setValueTextSize(0f);
                    lineDataSetAcomuladoAlerta1.setValueTextColor(Color.BLACK);
                    lineDataSetAcomuladoAlerta1.setAxisDependency(YAxis.AxisDependency.LEFT);
                    lineDataSets.add(lineDataSetAcomuladoAlerta1);
                }
                if(validar2 == false) {
                    lineDataSetAcomuladoAlerta2.setDrawCircles(true);
                    lineDataSetAcomuladoAlerta2.setCircleColor(Color.parseColor("#F0F407"));
                    lineDataSetAcomuladoAlerta2.setCircleRadius(8f);
                    lineDataSetAcomuladoAlerta2.setDrawCircleHole(false);
                    lineDataSetAcomuladoAlerta2.setLineWidth(0f);
                    lineDataSetAcomuladoAlerta2.setColor(Color.parseColor("#F0F407"));
                    lineDataSetAcomuladoAlerta2.setValueTextSize(0f);
                    lineDataSetAcomuladoAlerta2.setValueTextColor(Color.BLACK);
                    lineDataSetAcomuladoAlerta2.setAxisDependency(YAxis.AxisDependency.LEFT);
                    lineDataSets.add(lineDataSetAcomuladoAlerta2);
                }
                if(validar3 == false) {
                    lineDataSetAcomuladoAlerta3.setDrawCircles(true);
                    lineDataSetAcomuladoAlerta3.setCircleColor(Color.parseColor("#F4B707"));
                    lineDataSetAcomuladoAlerta3.setCircleRadius(8f);
                    lineDataSetAcomuladoAlerta3.setDrawCircleHole(false);
                    lineDataSetAcomuladoAlerta3.setLineWidth(0f);
                    lineDataSetAcomuladoAlerta3.setColor(Color.parseColor("#F4B707"));
                    lineDataSetAcomuladoAlerta3.setValueTextSize(0f);
                    lineDataSetAcomuladoAlerta3.setValueTextColor(Color.BLACK);
                    lineDataSetAcomuladoAlerta3.setAxisDependency(YAxis.AxisDependency.LEFT);
                    lineDataSets.add(lineDataSetAcomuladoAlerta3);
                }
                if(validar4 == false) {
                    lineDataSetAcomuladoAlerta4.setDrawCircles(true);
                    lineDataSetAcomuladoAlerta4.setCircleColor(Color.parseColor("#F47307"));
                    lineDataSetAcomuladoAlerta4.setCircleRadius(8f);
                    lineDataSetAcomuladoAlerta4.setDrawCircleHole(false);
                    lineDataSetAcomuladoAlerta4.setLineWidth(0f);
                    lineDataSetAcomuladoAlerta4.setColor(Color.parseColor("#F47307"));
                    lineDataSetAcomuladoAlerta4.setValueTextSize(0f);
                    lineDataSetAcomuladoAlerta4.setValueTextColor(Color.BLACK);
                    lineDataSetAcomuladoAlerta4.setAxisDependency(YAxis.AxisDependency.LEFT);
                    lineDataSets.add(lineDataSetAcomuladoAlerta4);
                }
                if(validar5 == false) {
                    lineDataSetAcomuladoAlerta5.setDrawCircles(true);
                    lineDataSetAcomuladoAlerta5.setCircleColor(Color.parseColor("#F40707"));
                    lineDataSetAcomuladoAlerta5.setCircleRadius(8f);
                    lineDataSetAcomuladoAlerta5.setDrawCircleHole(false);
                    lineDataSetAcomuladoAlerta5.setLineWidth(0f);
                    lineDataSetAcomuladoAlerta5.setColor(Color.parseColor("#F40707"));
                    lineDataSetAcomuladoAlerta5.setValueTextSize(0f);
                    lineDataSetAcomuladoAlerta5.setValueTextColor(Color.BLACK);
                    lineDataSetAcomuladoAlerta5.setAxisDependency(YAxis.AxisDependency.LEFT);
                    lineDataSets.add(lineDataSetAcomuladoAlerta5);
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
                lineChart.getXAxis().setLabelRotationAngle(0f);
                lineChart.getXAxis().setGranularity(1f);
                lineChart.getXAxis().setDrawAxisLine(false);
                lineChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(valoresX));
                progressDialog.dismiss();

                lineChart.isSaveEnabled();


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
    }

    @SuppressLint("ResourceType")
    private void Compartir() {
        NombreEstacion = NombreEstacion.replace(" ","_");
        String NombreArchivo = NombreEstacion + "_Datos_UC_" + DiaI + "-" + MesI + "-" + AnioI + "_" + DiaF + "-" + MesF + "-" + AnioF + ".csv";
        String path = Environment.getExternalStorageDirectory() + File.separator + Ruta_Imagen + File.separator + NombreArchivo;
        String pathG = Environment.getExternalStorageDirectory() + File.separator + Carpeta + File.separator + "Gráfica.png";
        String pathC = Environment.getExternalStorageDirectory() + File.separator + Carpeta + File.separator + "Alerta.png";
        ArrayList<Uri> archivosCarga = new ArrayList<Uri>();

        File fileWithinMyDir = new File(path);
        System.out.println(fileWithinMyDir);


        archivosCarga.add(FileProvider.getUriForFile(this,"com.example.prado.estaciones",new File(path)));
        archivosCarga.add(FileProvider.getUriForFile(this,"com.example.prado.estaciones",new File(pathG)));
        archivosCarga.add(FileProvider.getUriForFile(this,"com.example.prado.estaciones",new File(pathC)));

        if (fileWithinMyDir.exists()) {

            Intent intentShareFile = new Intent();
            intentShareFile.setAction(Intent.ACTION_SEND_MULTIPLE);
            intentShareFile.putParcelableArrayListExtra(Intent.EXTRA_STREAM,archivosCarga);
            intentShareFile.putExtra(Intent.EXTRA_SUBJECT, "Información de unidades calor");
            intentShareFile.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intentShareFile.putExtra(Intent.EXTRA_TEXT, "Gráfica y .csv");
            intentShareFile.setType("application/*");
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
            lineChart.saveToPath("Gráfica", File.separator + Carpeta);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private Double UCP(int vDias){
        double UCP; //Unidad Calor Promedio
        return UCP = UCAcontrol / vDias;
    }
    private int Ndias(int UCacumuladas, double lim){
        int  Prediccion = 0;
        while(UCacumuladas < lim){
            Prediccion += 1;
            UCacumuladas += Promedio;
        }
        return Prediccion;
    }

}