package com.example.ejemploimagen.database;


import com.example.ejemploimagen.Reporte;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;

public interface ReporteDataSource {
    /**
     * Gets the Reporte from the data source.
     *
     * @return the Reporte from the data source.
     */
    Single<List<Reporte>> getPendientes();


    /**
     * Inserts the Reporte into the data source, or, if this is an existing Reporte, updates it.
     *
     * @param reporte the reporte to be inserted or updated.
     */
    Long insertOrUpdateReporte(Reporte reporte);

    /**
     * Deletes all Reportes from the data source.
     */
    void deleteReporte(int idreporte);
}
