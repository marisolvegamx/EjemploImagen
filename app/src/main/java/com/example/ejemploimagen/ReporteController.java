package com.example.ejemploimagen;

import android.content.Context;

import androidx.room.Room;

import com.example.ejemploimagen.database.ImagenDao;

import com.example.ejemploimagen.database.ReporteDao;
import com.example.ejemploimagen.database.ReportesDatabase;

import java.util.List;

import static java.lang.Math.toIntExact;

public class ReporteController {

    ImagenDao imagenDao;
    ReporteDao repoDao;
    public void guardarReporte(Context context,Reporte reporte, List<Imagen> limagen){
        //guarda en la tabla
        //primero el reporte para obtener el id;

        ReportesDatabase db = ReportesDatabase.getInstance(context);


        imagenDao=db.getImagenDao();
        repoDao=db.getReporteDao();


        int idReporte=toIntExact(repoDao.addReporte(reporte));
        //inserto imagenes
        for(Imagen imagen:limagen){
            imagen.setId_reporte(idReporte);
            imagenDao.addImagen(imagen);
        }

    }
}
