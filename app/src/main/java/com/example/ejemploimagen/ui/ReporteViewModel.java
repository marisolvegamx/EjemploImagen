package com.example.ejemploimagen.ui;

import android.util.Log;

import androidx.lifecycle.ViewModel;

import com.example.ejemploimagen.Imagen;
import com.example.ejemploimagen.Reporte;
import com.example.ejemploimagen.database.ImagenDataSource;
import com.example.ejemploimagen.database.ReporteDataSource;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.CompletableEmitter;
import io.reactivex.Flowable;
import io.reactivex.Single;

import static java.lang.Math.toIntExact;

public class ReporteViewModel extends ViewModel {

    private final ReporteDataSource mDataSource;
    private final ImagenDataSource imagenDataSource;

    private Reporte mReporte;

    public ReporteViewModel(ReporteDataSource dataSource,ImagenDataSource mimagenDataSource) {
        mDataSource = dataSource;
        imagenDataSource=mimagenDataSource;
    }

    /**
     * Get the Reporte name of the Reporte.
     *
     * @return a {@link Flowable} that will emit every time the Reporte name has been updated.
     */
    public Single<List<Reporte>> getReportesPendientes() {
        return mDataSource.getPendientes();

    }

    public Single<List<Imagen>> getImagenesPendientes() {

        return imagenDataSource.getPendientes();

 }
    public Single<List<Imagen>> getImagenes() {

        return imagenDataSource.getImagenes();

    }

    /**
     * Update the Reporte name.
     *
     *
     * @return a {@link Completable} that completes when the Reporte name is updated
     */
    public Completable insertReporte(final Reporte reporte, List<Imagen> limagen) {
        //busco el id del reporte
        int max=imagenDataSource.getMaxReporte();
        if(max>0)
        {
           max=max+1;
        }
        else
            max=1;
        Log.d("Info***","max="+max);
        for(Imagen im:limagen){
            im.setId_reporte(max);
        }
        return  imagenDataSource.insertOrUpdateList(limagen);

    }
    public Completable updateImagen(Imagen limagen) {
 return  imagenDataSource.insertOrUpdateImagen(limagen);

    }
    public void deleteImagen(int idimagen) {



          imagenDataSource.deleteImagen(idimagen);

    }
}