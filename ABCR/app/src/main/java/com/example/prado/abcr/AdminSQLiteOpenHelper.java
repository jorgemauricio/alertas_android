package com.example.prado.abcr;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class AdminSQLiteOpenHelper extends SQLiteOpenHelper {
    public AdminSQLiteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL
                ("create table Trabajador " +
                        "(RFC text primary key, " +
                        "Apellido_Paterno text, " +
                        "Apellido_Materno text," +
                        "Nombre text, " +
                        "Fecha_de_Nacimiento date," +
                        "Sexo text," +
                        "Estado_Civil text," +
                        "Correo_Electronico text," +
                        "Telefono text," +
                        "Direccion text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
