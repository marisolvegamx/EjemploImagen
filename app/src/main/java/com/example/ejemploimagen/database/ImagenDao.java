package com.example.ejemploimagen.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.ejemploimagen.Imagen;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;

@Dao
 public interface ImagenDao {
        @Query("SELECT * FROM imagenes")
        Single<List<Imagen>> getImagenes();

        @Query("SELECT * FROM imagenes WHERE id_idimagen = :uuid")
        Single<Imagen> getImagen(int uuid);
        @Query("SELECT * FROM imagenes WHERE estatus = 0")
        Single<List<Imagen>>  getPendientes();
        @Query("SELECT max(id_reporte) FROM imagenes")
        int getMaxReporte();

       @Insert(onConflict = OnConflictStrategy.REPLACE)
       Completable addImagen(Imagen imagen);
       @Insert(onConflict = OnConflictStrategy.REPLACE)
       Completable insertAllImagenes(List<Imagen> imagenes);
       @Query("DELETE FROM imagenes where id_idimagen=:id")
        void deleteImagen(int id);




}
