package com.example.ejemploimagen.ui;

import androidx.lifecycle.ViewModelProvider;

import com.example.ejemploimagen.database.ImagenDataSource;
import com.example.ejemploimagen.database.ReporteDataSource;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class ViewModelFactory implements ViewModelProvider.Factory {

    private final ReporteDataSource mDataSource;
    private final ImagenDataSource imagenDataSource;

    public ViewModelFactory(ReporteDataSource dataSource, ImagenDataSource mimagenDataSource) {
        mDataSource = dataSource;
        imagenDataSource=mimagenDataSource;
    }

    @Override
    @NonNull
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ReporteViewModel.class)) {
            return (T) new ReporteViewModel(mDataSource,imagenDataSource);
        }
        //noinspection unchecked
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}