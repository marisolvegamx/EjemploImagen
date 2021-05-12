package com.example.ejemploimagen;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.UUID;

public class IdentificadorUsuario {
    public String id;
    Context context;

    public IdentificadorUsuario(Context context) {
        this.context = context;
    }

    public void guardarId(){
        id = UUID.randomUUID().toString();
        SharedPreferences preferencias=context.getSharedPreferences("datos", Context.MODE_PRIVATE);
          SharedPreferences.Editor editor=preferencias.edit();
        editor.putString("idusuario", id);
        editor.commit();

    }
    public void obtenerID(){
        SharedPreferences prefe=context.getSharedPreferences("datos", Context.MODE_PRIVATE);
        this.id=prefe.getString("idusuario","");
    }

    public boolean tengoID(){
        obtenerID();
        if(this.id!=null&&!this.id.equals("")){
            return true;
        }
        return false;
    }

}
