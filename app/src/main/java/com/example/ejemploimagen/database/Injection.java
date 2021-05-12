package com.example.ejemploimagen.database;

import android.content.Context;

import com.example.ejemploimagen.ui.ViewModelFactory;

public class Injection {

    public static ImagenDataSource provideImagenDataSource(Context context) {
        ReportesDatabase database = ReportesDatabase.getInstance(context);
        return new LocalImagenDataSource(database.getImagenDao());
    }

    public static ReporteDataSource provideReporteDataSource(Context context) {
        ReportesDatabase database = ReportesDatabase.getInstance(context);
        return new LocalReporteDataSource(database.getReporteDao());
    }

    public static ViewModelFactory provideViewModelFactory(Context context) {
        ImagenDataSource idataSource = provideImagenDataSource(context);
        ReporteDataSource rdataSource = provideReporteDataSource(context);
        return new ViewModelFactory(rdataSource, idataSource);
    }
}