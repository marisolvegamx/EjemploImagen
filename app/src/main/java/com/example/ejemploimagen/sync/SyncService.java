package com.example.ejemploimagen.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class SyncService extends Service {

    private static SyncAdapter syncAdapter=null;
    private static final Object lock=new Object(); //para prevenir errores entre hilos
@Override
    public void onCreate(){
        synchronized(lock){
            if(syncAdapter==null){
                syncAdapter= new SyncAdapter(getApplicationContext(),true,false);
            }
        }
    }


    @Override
    public IBinder onBind(Intent intent) {
        return syncAdapter.getSyncAdapterBinder();
    }
}
