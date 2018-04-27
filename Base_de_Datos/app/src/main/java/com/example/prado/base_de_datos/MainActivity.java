package com.example.prado.base_de_datos;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.sql.SQLData;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText edt_codigo,edt_descripcion,edt_precio;
    private Button registrar,buscar,modificar,eliminar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edt_codigo = (EditText)findViewById(R.id.edt_codigo);
        edt_descripcion = (EditText)findViewById(R.id.edt_descripcion);
        edt_precio = (EditText)findViewById(R.id.edt_precio);
        registrar = (Button)findViewById(R.id.Registrar);
        registrar.setOnClickListener(this);
        buscar = (Button)findViewById(R.id.Buscar);
        buscar.setOnClickListener(this);
        modificar = (Button)findViewById(R.id.Modificar);
        modificar.setOnClickListener(this);
        eliminar = (Button)findViewById(R.id.Eliminar);
        eliminar.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,"administracion", null, 1);
        SQLiteDatabase BaseDeDatos = admin.getWritableDatabase();
        ContentValues registro = new ContentValues();
        String codigo = edt_codigo.getText().toString();
        String descripcion = edt_descripcion.getText().toString();
        String precio = edt_precio.getText().toString();

        if (v == registrar){
            if(!codigo.isEmpty() && !descripcion.isEmpty() && !precio.isEmpty()){


                registro.put("codigo",codigo);
                registro.put("descripcion",descripcion);
                registro.put("precio",precio);

                BaseDeDatos.insert("articulos", null, registro);
                Toast.makeText(this, "Articulo registrado", Toast.LENGTH_SHORT).show();
                Limpiar();
                BaseDeDatos.close();

            }else{
                Toast.makeText(this, "No dejes campos vacíos", Toast.LENGTH_SHORT).show();
            }

        }else if(v == buscar){
            if (!codigo.isEmpty()){
                Cursor fila = BaseDeDatos.rawQuery
                        ("select descripcion, precio from articulos where codigo =" + codigo, null);

                if (fila.moveToFirst()){

                    edt_descripcion.setText(fila.getString(0));
                    edt_precio.setText((fila.getString(1)));
                    BaseDeDatos.close();
                }else {
                    Toast.makeText(this, "No existe el artículo", Toast.LENGTH_SHORT).show();
                    BaseDeDatos.close();
                }

            }else{
                Toast.makeText(this, "Debes introducir el código del artículo", Toast.LENGTH_SHORT).show();
            }

        }else if(v == modificar){
            if (!codigo.isEmpty() && !descripcion.isEmpty() && !precio.isEmpty()){

                registro.put("codigo",codigo);
                registro.put("descripcion",descripcion);
                registro.put("precio",precio);

                int cantidad = BaseDeDatos.update("articulos",registro, "codigo=" + codigo, null);
                BaseDeDatos.close();

                if (cantidad == 1){
                    Toast.makeText(this, "Articulo modificado", Toast.LENGTH_SHORT).show();
                    Limpiar();
                }else{
                    Toast.makeText(this, "El articulo no existe", Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(this, "El articulo no existe", Toast.LENGTH_SHORT).show();
            }

        }else if(v == eliminar){
            if (!codigo.isEmpty()){

                int cantidad = BaseDeDatos.delete("articulos", "codigo=" + codigo, null);
                BaseDeDatos.close();
                Limpiar();


                if (cantidad == 1){
                    Toast.makeText(this, "Artículo eliminado", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(this, "El articulo no existe", Toast.LENGTH_SHORT).show();
                }

            }else{
                Toast.makeText(this, "Debes introducir el código del artículo", Toast.LENGTH_SHORT).show();
            }
        }
        BaseDeDatos.close();
    }

    private void Limpiar(){
        edt_codigo.setText("");
        edt_descripcion.setText("");
        edt_precio.setText("");
    }
}