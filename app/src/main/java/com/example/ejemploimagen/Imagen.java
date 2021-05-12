package com.example.ejemploimagen;


import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.Room;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.Date;

@Entity(tableName="imagenes")
    public class Imagen {
        @PrimaryKey(autoGenerate = true)
        @NonNull

        private int   id_idimagen;
        @NonNull

        private int id_reporte;
        @NonNull

        private String id_ruta;
        private String ubicacion;
        private String pais;

        private String id_descripcion;

        private int estatus;
        private Date created_at;

    public int getId_idimagen() {
        return id_idimagen;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public void setId_idimagen(int id_idimagen) {
        this.id_idimagen = id_idimagen;
    }

    public int getId_reporte() {
        return id_reporte;
    }

    public void setId_reporte(int id_reporte) {
        this.id_reporte = id_reporte;
    }

    @NonNull
    public String getId_ruta() {
        return id_ruta;
    }

    public int getEstatus() {
        return estatus;
    }

    public void setEstatus(int estatus) {
        this.estatus = estatus;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public void setId_ruta(@NonNull String id_ruta) {
        this.id_ruta = id_ruta;
    }

    public String getId_descripcion() {
        return id_descripcion;
    }

    public void setId_descripcion(String id_descripcion) {
        this.id_descripcion = id_descripcion;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }




}