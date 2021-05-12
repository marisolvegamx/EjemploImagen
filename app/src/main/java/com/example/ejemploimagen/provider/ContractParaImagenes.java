package com.example.ejemploimagen.provider;

import android.content.UriMatcher;
import android.net.Uri;
import android.provider.BaseColumns;

/***
 * contiene cadenas de texto para conexiones a la bd
 */
public class ContractParaImagenes {

    public final static String AUTHORITY= "com.example.ejemploimagen.miprovider";
   public static final String IMAGEN="reportes";
    public final static String MULTIPLE_MIME =
            "vnd.android.cursor.dir/vnd." + AUTHORITY + IMAGEN;
    public final static String SINGLE_MIME=  "vnd.android.cursor.item/vnd." + AUTHORITY + IMAGEN;
    public final static Uri CONTENT_URI=  Uri.parse("content://" + AUTHORITY + "/" + IMAGEN);
    public static final UriMatcher uriMatcher;
    public static final int ALLROWS=1;
    public static final int SINGLE_ROW=2;
    //asignacion de uris
    static{
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, IMAGEN, ALLROWS);
        uriMatcher.addURI(AUTHORITY, IMAGEN + "/#", SINGLE_ROW);
    }
    //valores para la columna estado
    public static final int ESTADO_NUEVO=0;
    public static final int ESTADO_SYNC=1;



}
