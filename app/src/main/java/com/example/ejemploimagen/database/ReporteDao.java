package com.example.ejemploimagen.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.ejemploimagen.Imagen;
import com.example.ejemploimagen.Reporte;

import java.util.List;

import io.reactivex.Single;

@Dao
public interface ReporteDao {
    @Query("SELECT * FROM reportes")
    Single<List<Reporte>> getReportes();

    @Query("SELECT * FROM reportes WHERE id = :uuid")
    List<Reporte> getReporte(int uuid);

    @Query("SELECT * FROM reportes WHERE estatus =0")
    Single<List<Reporte>> getPendientes();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long addReporte(Reporte reporte);

    @Query("DELETE FROM reportes where id=:id")
    void deleteReporte(int id);

}
