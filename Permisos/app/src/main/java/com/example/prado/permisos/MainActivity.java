package com.example.prado.permisos;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;
import android.widget.Toast;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private Switch Almacenamiento,Camara,Calendario,Microfono,Mensaje,Contactos,Ubicacion,Sensor,Telefono;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Almacenamiento = (Switch) findViewById(R.id.switch1);
        Almacenamiento.setOnClickListener(this);
        Camara = (Switch) findViewById(R.id.switch2);
        Camara.setOnClickListener(this);
        Calendario = (Switch) findViewById(R.id.switch3);
        Calendario.setOnClickListener(this);
        Microfono = (Switch) findViewById(R.id.switch4);
        Microfono.setOnClickListener(this);
        Mensaje = (Switch) findViewById(R.id.switch5);
        Mensaje.setOnClickListener(this);
        Contactos = (Switch) findViewById(R.id.switch6);
        Contactos.setOnClickListener(this);
        Ubicacion = (Switch) findViewById(R.id.switch7);
        Ubicacion.setOnClickListener(this);
        Sensor = (Switch) findViewById(R.id.switch8);
        Sensor.setOnClickListener(this);
        Telefono = (Switch) findViewById(R.id.switch9);
        Telefono.setOnClickListener(this);

        validar_permisos();

    }

    @Override
    protected void onStart() {
        super.onStart();
        validar_permisos();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onClick(View v) {
       if (v == Almacenamiento){
            if (Almacenamiento.isChecked() == true){
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED){
                    Permisos(1);
                }else {
                    Toast.makeText(this, "Permiso consedido", Toast.LENGTH_SHORT).show();
                    Almacenamiento.setChecked(true);
                }
            }
       }else if (v == Camara){
           if (Camara.isChecked() == true){
               if (Camara.isChecked() == true){
                   if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.CAMERA)
                           != PackageManager.PERMISSION_GRANTED){
                       Permisos(2);
                   }else {
                       Toast.makeText(this, "Permiso consedido", Toast.LENGTH_SHORT).show();
                       Camara.setChecked(true);
                   }
               }
           }else {

           }
       }else if (v == Calendario){
           if (Calendario.isChecked() == true){
               if (Calendario.isChecked() == true){
                   if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CALENDAR)
                           != PackageManager.PERMISSION_GRANTED){
                       Permisos(3);
                   }else {
                       Toast.makeText(this, "Permiso consedido", Toast.LENGTH_SHORT).show();
                       Calendario.setChecked(true);
                   }
               }
           }else {

           }
       }else if (v == Microfono){
               if (Microfono.isChecked() == true){
                   if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.RECORD_AUDIO)
                           != PackageManager.PERMISSION_GRANTED){
                       Permisos(4);
                   }else {
                       Toast.makeText(this, "Permiso consedido", Toast.LENGTH_SHORT).show();
                       Microfono.setChecked(true);
                   }
           }
       }else if (v == Mensaje){
           if (Mensaje.isChecked() == true){
               if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.SEND_SMS)
                       != PackageManager.PERMISSION_GRANTED){
                   Permisos(5);
               }else {
                   Toast.makeText(this, "Permiso consedido", Toast.LENGTH_SHORT).show();
                   Mensaje.setChecked(true);
               }
           }
       }else if (v == Contactos){
           if (Contactos.isChecked() == true){
               if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS)
                       != PackageManager.PERMISSION_GRANTED){
                   Permisos(6);
               }else {
                   Toast.makeText(this, "Permiso consedido", Toast.LENGTH_SHORT).show();
                   Contactos.setChecked(true);
               }
           }
       }else if (v == Ubicacion){
           if (Ubicacion.isChecked() == true){
               if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                       != PackageManager.PERMISSION_GRANTED){
                   Permisos(7);
               }else {
                   Toast.makeText(this, "Permiso consedido", Toast.LENGTH_SHORT).show();
                   Ubicacion.setChecked(true);
               }
           }
       }else if (v == Sensor){
           if (Sensor.isChecked() == true){
               if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.BODY_SENSORS)
                       != PackageManager.PERMISSION_GRANTED){
                   Permisos(8);
               }else {
                   Toast.makeText(this, "Permiso consedido", Toast.LENGTH_SHORT).show();
                   Sensor.setChecked(true);
               }
           }
       }else if (v == Telefono){
           if (Telefono.isChecked() == true){
               if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.CALL_PHONE)
                       != PackageManager.PERMISSION_GRANTED){
                   Permisos(9);
               }else {
                   Toast.makeText(this, "Permiso consedido", Toast.LENGTH_SHORT).show();
                   Telefono.setChecked(true);
               }
           }
       }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void Permisos(int NPermiso){
        switch (NPermiso) {

            case 1:
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1001);
            break;

            case 2:
                requestPermissions(new String[]{Manifest.permission.CAMERA}, 1002);
                break;
            case 3:
                requestPermissions(new String[]{Manifest.permission.READ_CALENDAR}, 1003);
                requestPermissions(new String[]{Manifest.permission.WRITE_CALENDAR}, 1004);
                break;
            case 4:
                requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, 1005);
                break;
            case 5:
                requestPermissions(new String[]{Manifest.permission.SEND_SMS}, 1006);
                break;
            case 6:
                requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, 1007);
                break;
            case 7:
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1008);
                break;
            case 8:
                requestPermissions(new String[]{Manifest.permission.BODY_SENSORS}, 1009);
                break;
            case 9:
                requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 1010);
                break;

        }
    }

    @Override public void onRequestPermissionsResult(int requestCode,String[] permissions, int[] grantResults) {
        if (requestCode == 1001) {
            if (grantResults.length== 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
                Toast.makeText(this, "Sin el permiso, no puedo realizar la " +
                        "acción", Toast.LENGTH_SHORT).show();
                Almacenamiento.setChecked(false);
            }
        }
        if (requestCode == 1002) {
            if (grantResults.length== 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
                Toast.makeText(this, "Sin el permiso, no puedo realizar la " +
                        "acción", Toast.LENGTH_SHORT).show();
                Camara.setChecked(false);
            }
        }
        if (requestCode == 1003) {
            if (grantResults.length== 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
                Toast.makeText(this, "Sin el permiso, no puedo realizar la " +
                        "acción", Toast.LENGTH_SHORT).show();
                Calendario.setChecked(false);
            }
        }
        if (requestCode == 1005) {
            if (grantResults.length== 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
                Toast.makeText(this, "Sin el permiso, no puedo realizar la " +
                        "acción", Toast.LENGTH_SHORT).show();
                Microfono.setChecked(false);
            }
        }
        if (requestCode == 1006) {
            if (grantResults.length== 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
                Toast.makeText(this, "Sin el permiso, no puedo realizar la " +
                        "acción", Toast.LENGTH_SHORT).show();
                Mensaje.setChecked(false);
            }
        }
        if (requestCode == 1007) {
            if (grantResults.length== 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
                Toast.makeText(this, "Sin el permiso, no puedo realizar la " +
                        "acción", Toast.LENGTH_SHORT).show();
                Contactos.setChecked(false);
            }
        }
        if (requestCode == 1008) {
            if (grantResults.length== 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
                Toast.makeText(this, "Sin el permiso, no puedo realizar la " +
                        "acción", Toast.LENGTH_SHORT).show();
                Ubicacion.setChecked(false);
            }
        }
        if (requestCode == 1009) {
            if (grantResults.length== 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
                Toast.makeText(this, "Sin el permiso, no puedo realizar la " +
                        "acción", Toast.LENGTH_SHORT).show();
                Sensor.setChecked(false);
            }
        }
        if (requestCode == 1010) {
            if (grantResults.length== 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
                Toast.makeText(this, "Sin el permiso, no puedo realizar la " +
                        "acción", Toast.LENGTH_SHORT).show();
                Telefono.setChecked(false);
            }
        }
    }

/*Validación de permisos, reviza el estado de los permisos dentro del sistema
* si el permiso esta otorgado activa el switch y si no es así lo deja desactivado*/
private void validar_permisos(){

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            Almacenamiento.setChecked(false);
        }else {
            Almacenamiento.setChecked(true);
        }
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED){
            Camara.setChecked(false);
        }else {
            Camara.setChecked(true);
        }
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CALENDAR)
                != PackageManager.PERMISSION_GRANTED){
            Calendario.setChecked(false);
        }else {
            Calendario.setChecked(true);
        }
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED){
            Microfono.setChecked(false);
        }else {
            Microfono.setChecked(true);
        }
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED){
            Mensaje.setChecked(false);
        }else {
            Mensaje.setChecked(true);
        }
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED){
            Contactos.setChecked(false);
        }else {
            Contactos.setChecked(true);
        }
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            Ubicacion.setChecked(false);
        }else {
            Ubicacion.setChecked(true);
        }
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.BODY_SENSORS)
                != PackageManager.PERMISSION_GRANTED){
            Sensor.setChecked(false);
        }else {
            Sensor.setChecked(true);
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED){
            Telefono.setChecked(false);
        }else {
            Telefono.setChecked(true);
        }
    }

}


