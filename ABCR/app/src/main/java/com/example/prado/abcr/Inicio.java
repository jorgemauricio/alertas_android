package com.example.prado.abcr;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Inicio extends AppCompatActivity implements View.OnClickListener {

    private Button Registrar, Buscar, Modificar, Eliminar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);

        Registrar = (Button) findViewById(R.id.Registro);
        Registrar.setOnClickListener(this);
        Buscar = (Button) findViewById(R.id.Buscar);
        Buscar.setOnClickListener(this);
        Modificar = (Button) findViewById(R.id.Modificar);
        Modificar.setOnClickListener(this);
        Eliminar = (Button) findViewById(R.id.Eliminar);
        Eliminar.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent Altas = new Intent(this,Altas.class);
        Intent Bajas = new Intent(this,Bajas.class);
        Intent Cambios = new Intent(this,Cambios.class);
        Intent Consultas = new Intent(this,Consultas.class);

        if(v == Registrar){
            startActivity(Altas);
        }else if (v == Buscar){
            startActivity(Consultas);
        }else if (v == Modificar){
            startActivity(Cambios);
        }else if (v == Eliminar){
            startActivity(Bajas);
        }
    }
}
