package com.example.prado.Barrenador_del_RUEZNO;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;

public class Inicio extends AppCompatActivity{

    private ImageView Portada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);

        Portada = (ImageView)findViewById(R.id.portada);
        final AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f,0.0f);
        alphaAnimation.setDuration(600);
        alphaAnimation.setStartOffset(3000);
        alphaAnimation.setFillAfter(true);

        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Intent Cambio = new Intent(Inicio.this,Mapa.class);
                startActivity(Cambio);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

            Portada.startAnimation(alphaAnimation);


            }
        }