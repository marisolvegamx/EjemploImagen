package com.example.ejemploimagen;

import android.content.Context;
import android.media.Image;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.example.ejemploimagen", appContext.getPackageName());
    }
    @Test
    public void insertarReporte() {
        ReporteController rc=new ReporteController();
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        Reporte reporte=new Reporte();
        reporte.setEstatus(0);
        reporte.setUbicacion("123,456");
        Imagen imagen=new Imagen();
        imagen.setId_descripcion("prueba unitaria");
        imagen.setEstatus(0);
        imagen.setId_ruta("Name.jpg");
        List<Imagen> lista=new ArrayList<Imagen>();
        lista.add(imagen);
        rc.guardarReporte(appContext,reporte,lista);
       // assertThrows(rc.guardarReporte(appContext,reporte,lista));
    }
}