package com.example.prado.estaciones;

import android.graphics.Bitmap;
import android.view.View;

public class Screenshot {
    public static Bitmap tomarScreenshot(View view){

        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);
        return bitmap;
    }

    public static Bitmap tomarRutadeScreenshot(View view){
        return tomarScreenshot(view.getRootView());
    }


}
