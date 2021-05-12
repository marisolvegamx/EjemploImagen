package com.example.ejemploimagen.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class AuthenticationService extends Service {

    private Autenticador autenticador;


    @Override
    public IBinder onBind(Intent intent) {
        return autenticador.getIBinder();
    }

    public void onCreate(){
        autenticador=new Autenticador(this);
    }

}
