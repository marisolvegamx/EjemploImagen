package com.example.ejemploimagen;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class AdaptadorImagenDetalle extends ArrayAdapter<Imagen> {
   AppCompatActivity appCompatActivity;
    List<Imagen> lista;

   AdaptadorImagenDetalle(AppCompatActivity context, List<Imagen> lista){
      super(context, R.layout.imagen_detalle2, lista);
      appCompatActivity=context;
      this.lista=lista;

   }

   public View getView(int position, View convertView, ViewGroup parent){
       LayoutInflater inflater=appCompatActivity.getLayoutInflater();
       View item=inflater.inflate(R.layout.imagen_detalle2,null);
       TextView textView1= (TextView) item.findViewById(R.id.txtdescripcion2);
       if(!lista.isEmpty()) {
           textView1.setText(lista.get(position).getId_descripcion());
           ImageView imageView1 = item.findViewById(R.id.imageView2);
           Bitmap bitmap1 = BitmapFactory.decodeFile(appCompatActivity.getExternalFilesDir(null) + "/" + lista.get(position).getId_ruta());
           imageView1.setImageBitmap(bitmap1);
       }
        return item;
   }
}
