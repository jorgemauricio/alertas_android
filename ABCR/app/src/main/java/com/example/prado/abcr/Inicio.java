package com.example.prado.abcr;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Inicio extends AppCompatActivity implements View.OnClickListener {

    private Button Registrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);

        Registrar = (Button) findViewById(R.id.Registro);
        Registrar.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        Intent Altas = new Intent(this,Altas.class);


        if(v == Registrar) {
            startActivity(Altas);
        }
    }
}
