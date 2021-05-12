package com.example.ejemploimagen.database;

import android.content.Context;


import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;


import com.example.Converters;
import com.example.ejemploimagen.Imagen;
import com.example.ejemploimagen.Reporte;


@Database(entities={Reporte.class, Imagen.class}, version=1)
@TypeConverters({Converters.class})
public abstract class ReportesDatabase extends RoomDatabase {
    private static ReportesDatabase INSTANCE;
    public abstract ReporteDao getReporteDao();
    public abstract ImagenDao getImagenDao();
    public static ReportesDatabase getInstance(final Context context) {
        if (INSTANCE == null) {
            synchronized (ReportesDatabase.class) {
                if (INSTANCE == null) {
                 /*   INSTANCE =  Room.databaseBuilder(context,
                            ReportesDatabase.class, "muestreo").allowMainThreadQueries()
                            .addMigrations(MIGRATION_1_2)
                            .build();*/
                   INSTANCE =  Room.databaseBuilder(context,
                            ReportesDatabase.class, "muestreo").allowMainThreadQueries()

                            .build();
                }
            }
        }
        return INSTANCE;
    }
    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("alter table imagenes add column created_at timestamp");
        }
    };


}
