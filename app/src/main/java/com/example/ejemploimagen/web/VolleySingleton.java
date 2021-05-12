package com.example.ejemploimagen.web;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class VolleySingleton {
    public static VolleySingleton singleton;
    private RequestQueue requestQueue;
    private static Context context;

    public VolleySingleton(Context context) {
        VolleySingleton.context=context;

        requestQueue=getRequestQueue();
    }

    public static synchronized VolleySingleton getInstance(Context context){
        if(singleton==null){
            singleton=new VolleySingleton(context.getApplicationContext()); //creo uno nuevo
        }
        return singleton;
    }

    /****
     * Obtiene la instancia d la cola de peticiones
     * @return
     */
    private RequestQueue getRequestQueue() {
        if(requestQueue==null){
            //creo una nueva
            requestQueue= Volley.newRequestQueue(context.getApplicationContext());
        }
        return requestQueue;
    }

    /***
     * Añade la peticion ala cola
     */
    public <T> void addToRequestQueue(Request<T> req)
    {
        getRequestQueue().add(req);
    }
}
