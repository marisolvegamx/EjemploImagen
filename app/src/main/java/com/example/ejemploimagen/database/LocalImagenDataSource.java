package com.example.ejemploimagen.database;

import com.example.ejemploimagen.Imagen;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;

public class LocalImagenDataSource  implements ImagenDataSource {

        private final ImagenDao mImagenDao;

        public LocalImagenDataSource(ImagenDao ImagenDao) {
            mImagenDao = ImagenDao;
        }

        @Override
         public Single<List<Imagen>> getImagenes() {
        return mImagenDao.getImagenes();
    }


        @Override
        public Completable insertOrUpdateImagen(Imagen imagen) {
            return mImagenDao.addImagen(imagen);
        }

    @Override
    public Completable insertOrUpdateList(List<Imagen> listaImagen) {
        return mImagenDao.insertAllImagenes(listaImagen);
    }

    @Override
    public int getMaxReporte() {
        return mImagenDao.getMaxReporte();
    }

    @Override
    public Single<List<Imagen>> getPendientes() {
        return mImagenDao.getPendientes();
    }

    @Override
        public void deleteAllImagens(int idimagen) {
            mImagenDao.deleteImagen(idimagen);
        }
    @Override
    public void deleteImagen(int idimagen) {
        mImagenDao.deleteImagen(idimagen);
    }
    }