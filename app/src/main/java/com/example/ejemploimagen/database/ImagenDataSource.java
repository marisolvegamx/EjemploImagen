package com.example.ejemploimagen.database;

import com.example.ejemploimagen.Imagen;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;

public interface ImagenDataSource {
    /**
     * Gets the Imagen from the data source.
     *
     * @return the Imagen from the data source.
     */
    Single<List<Imagen>> getPendientes();
    Single<List<Imagen>> getImagenes();
    /**
     * Inserts the Imagen into the data source, or, if this is an existing Imagen, updates it.
     *
     * @param Imagen the Imagen to be inserted or updated.
     */
    Completable insertOrUpdateImagen(Imagen Imagen);

    Completable insertOrUpdateList(List<Imagen> Imagen);
    int getMaxReporte();
    /**
     * Deletes all Imagens from the data source.
     */
    void deleteAllImagens(int idimagen);
    void deleteImagen(int idimagen);
}
