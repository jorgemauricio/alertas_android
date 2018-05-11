package com.example.prado.abcr;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

public class Altas extends AppCompatActivity implements View.OnClickListener{

    private int ancho = 320, alto = 410;
    private Toolbar toolbar;
    private ImageButton Cam_Camara;
    private EditText edt_RFC, edt_Nombre, edt_Ape_Pat, edt_Ape_Mat, edt_Correo, edt_Direccion,edt_Telefono,edt_Fech_Nac;
    private Spinner spn_Edo_Civil,spn_Sexo;
    final Calendar calendar = Calendar.getInstance();
    private int Dia, Mes, Anio;
    private static final int REQUEST_CAMERA = 1;
    private static final int REQUEST_SELECT_PHOTO = 0;
    private final String Carpeta = "Fotos_Trabajadores/", Ruta_Imagen =Carpeta+"Trabajadores";
    String path = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_altas);

        toolbar = (Toolbar) findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);

        Cam_Camara = (ImageButton) findViewById(R.id.Camara);
        Cam_Camara.setOnClickListener(this);
        edt_RFC = (EditText) findViewById(R.id.RFC);
        edt_Nombre = (EditText) findViewById(R.id.Nombre);
        edt_Ape_Pat = (EditText) findViewById(R.id.ape_pat);
        edt_Ape_Mat = (EditText) findViewById(R.id.ape_mat);
        edt_Fech_Nac = (EditText) findViewById(R.id.fecha_naci);
        edt_Fech_Nac.setOnClickListener(this);
        edt_Fech_Nac.setInputType(InputType.TYPE_NULL);
        edt_Correo = (EditText) findViewById(R.id.correo);
        edt_Direccion = (EditText) findViewById(R.id.direccion);
        edt_Telefono = (EditText) findViewById(R.id.telefono);
        spn_Edo_Civil = (Spinner) findViewById(R.id.edo_civil);
        spn_Sexo = (Spinner) findViewById(R.id.sexo);



        String [] Sexo = {"Sexo","Mujer", "Hombre"};
        adaptadorspiners(Sexo,0);
        String [] Edo_Civil = {"Estado civil","Soltero", "Casado", "Viudo", "Divorciado", "Comprometido"};
        adaptadorspiners(Edo_Civil,1);



    }

    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.btn_alta,menu);
        return true;
    }
    public void Limpiar(){
        edt_Ape_Mat.setText("");
        edt_Ape_Pat.setText("");
        edt_Correo.setText("");
        edt_Direccion.setText("");
        edt_Fech_Nac.setText("");
        edt_Nombre.setText("");
        edt_RFC.setText("");
        edt_Telefono.setText("");
    }

    private void adaptadorspiners(String[] vector, int spin) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.item_estilo_spiner_efren, vector);
        if (spin == 0) {
        spn_Sexo.setAdapter(adapter);
        } else if (spin == 1){
            spn_Edo_Civil.setAdapter(adapter);
        }
    }

    DatePickerDialog.OnDateSetListener dI = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                Dia = dayOfMonth;
                Mes = (month + 1);
                Anio = year;
                edt_Fech_Nac.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
        }
    };

    public boolean onOptionsItemSelected(MenuItem menuItem){
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,"Trabajadores", null, 1);
        SQLiteDatabase BaseDeDatos = admin.getWritableDatabase();
        ContentValues registro = new ContentValues();

        String RFC = edt_RFC.getText().toString();
        String Nombre = edt_Nombre.getText().toString();
        String Ape_Pat = edt_Ape_Pat.getText().toString();
        String Ape_Mat = edt_Ape_Mat.getText().toString();
        String Fech_Nac = edt_Fech_Nac.getText().toString();
        String Correo = edt_Correo.getText().toString();
        String Dirección = edt_Direccion.getText().toString();
        String Telefono = edt_Telefono.getText().toString();
        String Sexo = spn_Sexo.getSelectedItem().toString();
        String Edo_Civil = spn_Edo_Civil.getSelectedItem().toString();

      switch (menuItem.getItemId()){
          case R.id.Alta:
              if(!RFC.isEmpty() && !Nombre.isEmpty() && !Ape_Pat.isEmpty() && !Ape_Mat.isEmpty() && !Fech_Nac.isEmpty() && !Correo.isEmpty() && !Dirección.isEmpty() && !Telefono.isEmpty()){

                  registro.put("RFC", RFC.toUpperCase());
                  registro.put("Apellido_Paterno", Ape_Pat);
                  registro.put("Apellido_Materno", Ape_Mat);
                  registro.put("Nombre", Nombre);
                  registro.put("Fecha_de_Nacimiento", Fech_Nac);
                  registro.put("Sexo", Sexo);
                  registro.put("Estado_Civil", Edo_Civil);
                  registro.put("Correo_Electronico", Ape_Mat);
                  registro.put("Telefono", Ape_Pat);
                  registro.put("Direccion", Ape_Mat);


                  BaseDeDatos.insert("Trabajador", null, registro);
                  Toast.makeText(this, "Trabajador registrado", Toast.LENGTH_SHORT).show();
                  Limpiar();
                  BaseDeDatos.close();

              }else{
                  Toast.makeText(this, "No dejes campos vacíos", Toast.LENGTH_SHORT).show();
              }

      }

        return true;
    }

    @Override
    public void onClick(View v) {
        if(v == edt_Fech_Nac){
            new DatePickerDialog(this, dI, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        }else if (v == Cam_Camara) {
            Opciones();
        }
    }


    public void Opciones(){
        final CharSequence [] opciones = {"Tomar foto", "Elegir de galería", "Cancelar"};
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Elige una opción");
        builder.setItems(opciones, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (opciones[which].equals("Tomar foto")) {
                    abrirCamara();
                } else if (opciones[which].equals("Elegir de galería")) {
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            MediaStore.Images.Media.CONTENT_TYPE);
                    startActivityForResult(intent, REQUEST_SELECT_PHOTO);
                }
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK){
           if (requestCode == REQUEST_SELECT_PHOTO) {
               Uri pat = data.getData();

               Bitmap myBitmap =
                       BitmapFactory.decodeFile(path);
               Cam_Camara.setImageURI(pat);
           }
           if (requestCode == REQUEST_CAMERA){
               MediaScannerConnection.scanFile(this, new String[]{path}, null, new MediaScannerConnection.MediaScannerConnectionClient() {
                   @Override
                   public void onMediaScannerConnected() {

                   }

                   @Override
                   public void onScanCompleted(String path, Uri uri) {

                   }
               });

               Bitmap myBitmap =
                       BitmapFactory.decodeFile(path);

               Cam_Camara.setImageBitmap(redimensionarImagenMaximo(myBitmap,ancho,alto));

           }
        }
    }

    private void abrirCamara(){
        File fileImagen = new File(Environment.getExternalStorageDirectory(),Ruta_Imagen);
        boolean existe = fileImagen.exists();
        String nombreImagen = "";

        if (existe == false){
            existe = fileImagen.mkdirs();
        }
        if(existe){
            nombreImagen = "IMG_" + (System.currentTimeMillis()/1000) + ".jpg";

        }

        path = Environment.getExternalStorageDirectory() + File.separator + Ruta_Imagen + File.separator + nombreImagen;
        Uri pathUri = FileProvider.getUriForFile(this,"com.example.prado.abcr", new File(path));
        System.out.println(pathUri);
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, pathUri);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivityForResult(Intent.createChooser(intent, "Tomar foto"), REQUEST_CAMERA);

    }

    public Bitmap redimensionarImagenMaximo(Bitmap mBitmap, float newWidth, float newHeigth){
        //Redimensionamos
        int width = mBitmap.getWidth();
        int height = mBitmap.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeigth) / height;
        // create a matrix for the manipulation
        Matrix matrix = new Matrix();
        // resize the bit map
        matrix.postScale(scaleWidth, scaleHeight);
        // recreate the new Bitmap
        return Bitmap.createBitmap(mBitmap, 0, 0, width, height, matrix, false);

    }
    }
