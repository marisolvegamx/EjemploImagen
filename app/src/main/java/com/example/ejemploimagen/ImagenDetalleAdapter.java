package com.example.ejemploimagen;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ejemploimagen.Imagen;
import com.example.ejemploimagen.R;

import java.lang.reflect.Array;
import java.util.List;

public class ImagenDetalleAdapter extends RecyclerView.Adapter<ImagenDetalleAdapter.ImagenViewHolder> {
    private List<Imagen> items;
    private AdapterCallback callback;
    private String[] estatus;
    public ImagenDetalleAdapter(List<Imagen> items,AdapterCallback callback) {
        this.items = items;
        this.callback = callback;
     estatus =new String[2];
        estatus[0]="PENDIENTE";
        estatus[1]="ENVIADO";
    }
    public Context context;
    @NonNull
    @Override
    public ImagenViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context=viewGroup.getContext();
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.imagen_detalle, viewGroup, false);
        return new ImagenViewHolder(v,callback);
    }

    @Override
    public void onBindViewHolder(@NonNull ImagenViewHolder imagenViewHolder, int i) {
        Bitmap bitmap1 = BitmapFactory.decodeFile(context.getExternalFilesDir(null) + "/" + items.get(i).getId_ruta());
        imagenViewHolder.imageView2.setImageBitmap(bitmap1);
        imagenViewHolder.txtdescripcion2.setText(items.get(i).getId_descripcion());
        imagenViewHolder.txtestatus.setText(estatus[items.get(i).getEstatus()]);
        imagenViewHolder.txtid.setText(items.get(i).getId_idimagen()+"");
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ImagenViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView2;
        public TextView txtdescripcion2;
        public TextView txtid;
        public TextView txtestatus;
        public Button detalle;
        public Context context;
        private AdapterCallback callback;
        List<Imagen> lista;
        public ImagenViewHolder( View itemView,AdapterCallback callback ) {
            super(itemView);
            context = itemView.getContext();
            imageView2 = (ImageView) itemView.findViewById(R.id.imageView2);
            txtid = (TextView) itemView.findViewById(R.id.txtid);

            txtestatus = (TextView) itemView.findViewById(R.id.txtestatus);

            txtdescripcion2 = (TextView) itemView.findViewById(R.id.txtdescripcion2);

            detalle = (Button) itemView.findViewById(R.id.btneliminar);
            detalle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    switch (view.getId()) {
                        case R.id.btneliminar:
                          //  Log.d("Se seleccionó a ", txtid.getText().toString());
                         //   Toast.makeText(context, "Se seleccionó a " + txtid.getText().toString(), Toast.LENGTH_SHORT).show();
                            callback.onClickCallback(Integer.parseInt(txtid.getText().toString()));
                            break;
                        default:
                            break;
                    }
                }
            });
        }
    }
    public interface AdapterCallback {
        void onClickCallback(int id);
    }


}
