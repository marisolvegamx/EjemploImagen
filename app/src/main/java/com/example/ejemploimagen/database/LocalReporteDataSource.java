package com.example.ejemploimagen.database;

import com.example.ejemploimagen.Reporte;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;

public class LocalReporteDataSource implements ReporteDataSource {

        private final ReporteDao mReporteDao;

        public LocalReporteDataSource(ReporteDao ReporteDao) {
            mReporteDao = ReporteDao;
        }



        @Override
        public Long insertOrUpdateReporte(Reporte Reporte) {
            return mReporteDao.addReporte(Reporte);
        }

    @Override
    public Single<List<Reporte>> getPendientes() {
        return mReporteDao.getPendientes();
    }

    @Override
        public void deleteReporte(int idreporte) {
            mReporteDao.deleteReporte(idreporte);
        }
    }