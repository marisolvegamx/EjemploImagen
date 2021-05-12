package com.example.ejemploimagen.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncResult;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;

import androidx.room.Room;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import com.android.volley.toolbox.StringRequest;
import com.example.ejemploimagen.Imagen;
import com.example.ejemploimagen.R;
import com.example.ejemploimagen.Reporte;
import com.example.ejemploimagen.SubirFoto;
import com.example.ejemploimagen.database.ImagenDao;

import com.example.ejemploimagen.database.ReporteDao;
import com.example.ejemploimagen.database.ReportesDatabase;
import com.example.ejemploimagen.provider.ContractParaImagenes;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.ejemploimagen.utils.Constantes;
import com.example.ejemploimagen.web.VolleySingleton;

import io.reactivex.Single;


/*maneja la transferencia de datos entre el servidor yel cliente

 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {

    private static final String TAG=SyncAdapter.class.getSimpleName();
    ContentResolver resolver;
    private Gson gson=new Gson();
    ImagenDao mImagenDao;
    ReporteDao reporteDao;
    Context context;
   // public static final String AUTHORITY =
      //      ContractParaImagenes.AUTHORITY;
    public static final String AUTHORITY ="com.example.ejemploimagen.sync.SyncAdapter";

    /**
     * Proyección para las consultas
     */
    private static final String[] PROJECTION = new String[]{
           "id_idimagen",
            "id_reporte",
            "id_ruta",
            "id_descripcion",

    };
    // Indices para las columnas indicadas en la proyección
    public static final int COLUMNA_ID = 0;
    public static final int COLUMNA_IDREPORTE = 1;
    public static final int COLUMNA_RUTA = 2;
    public static final int COLUMNA_DESCRIPCION = 3;


    public SyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        this.context=context;
        ReportesDatabase db = ReportesDatabase.getInstance(context);
        mImagenDao = db.getImagenDao();
          reporteDao = db.getReporteDao();
    }

    public static void inicializarSyncAdpter(Context context){
        obtenerCuentasASincronizar(context);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.i(TAG,"onPerformSync()..");
        boolean soloSubida=extras.getBoolean(ContentResolver.SYNC_EXTRAS_UPLOAD,false);
//        if(!soloSubida){
//            realizarSincronizacionLocal(syncResult);
//        }
//        else{
            realizarSincronizacionRemota();
     //   }
    }
/*
    private void realizarSincronizacionLocal(final SyncResult syncResult) {
        Log.i(TAG,"Actualizando el cliente");
        VolleySingleton.getInstance(getContext()).addToRequestQueue(
                new JsonObjectRequest(Request.Method.GET, Constantes.GET_URL, new Response.Listener<JSONObject>(){
                    public void onResponse(JSONObject response){
                        try {
                            procesarRespuestaGet(response,syncResult);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }


                },
                        new Response.ErrorListener(){
                        public void onErrorResponse(VolleyError error){
                            Log.d(TAG,error.networkResponse.toString());
                        }
                        }
        ));
    }*/
    /*procesar la respuesta del servidor al pedir que retornen todos los Imagenes

     */
   /* private void procesarRespuestaGet(JSONObject response, SyncResult syncResult) throws JSONException {

        String estado= null;

        estado = response.getString(Constantes.ESTADO);

        switch(estado){
                case Constantes.SUCCESS: //exito
                    actualizarDatosLocales(response,syncResult);
                    break;
                case Constantes.FAILED: String mensaje=response.getString(Constantes.MENSAJE);
                    Log.i(TAG,mensaje);
                    break;
            }

    }*/



    /*private void realizarSincronizacionRemota(){
        Log.i(TAG,"Actualizando el servidor...");
      //  iniciarActualizacion();
    //List<Imagen> listaIm=obtenerImagenesPendientes();
        List<Imagen> listaIm=null;
        Gson gson=new Gson();
        SubirFoto sf=new SubirFoto();
        Log.i(TAG,"Se encontraron "+listaIm.size()+" registros nuevos"       );
        for(int  i=0;i<listaIm.size();i++){
            final Imagen imagen=listaIm.get(i);

            VolleySingleton.getInstance(getContext()).addToRequestQueue(
                        new JsonObjectRequest(Request.Method.POST, Constantes.INSERT_URL,
                               gson.toJson(listaIm.get(i))  ,

                                new Response.Listener<JSONObject>(){
                            public void onResponse(JSONObject response){
                                try {
                                    procesarRespuestaInsert(response,imagen);
                                    //envio la imagen
                                 sf.subirFoto(context.getExternalFilesDir(null)+"/"+imagen.getId_ruta(),context);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }


                        },
                         new Response.ErrorListener(){
                         public void onErrorResponse(VolleyError error){
                         Log.d(TAG,error.networkResponse.toString());
                         }
                         }
                ){
                            public Map<String, String> getHeaders() {
                                Map<String, String> headers = new HashMap<String, String>();
                                headers.put("Content-Type", "application/json; charset=utf-8");
                                headers.put("Accept", "application/json");
                                return headers;
                            }

                            @Override
                            public String getBodyContentType() {
                                return "application/json; charset=utf-8" + getParamsEncoding();
                            }
                        }
                );
            }

    }*/
    private void realizarSincronizacionRemota(){
        Log.i(TAG,"Actualizando el servidor...");
        //  iniciarActualizacion();
      //  List<Imagen> listaIm=obtenerImagenesPendientes();
       List<Imagen> listaIm=null;
        Gson gson=new Gson();
        SubirFoto sf=new SubirFoto();
        Log.i(TAG,"Se encontraron "+listaIm.size()+" registros nuevos"       );
        for(int  i=0;i<listaIm.size();i++){
            final Imagen imagen=listaIm.get(i);

            VolleySingleton.getInstance(getContext()).addToRequestQueue(
                    new StringRequest(Request.Method.POST, Constantes.INSERT_URL,


                            new Response.Listener<String>(){
                                @Override
                                public void onResponse(String response){
                                    try {
                                     //   procesarRespuestaInsert(response,imagen);
                                        Log.d(TAG,response);
                                        //envio la imagen
                                        sf.subirFoto(context.getExternalFilesDir(null)+"/"+imagen.getId_ruta(),"1234",context);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }


                            },
                            new Response.ErrorListener(){
                                public void onErrorResponse(VolleyError error){
                                    Log.d(TAG,error.networkResponse.toString());
                                }
                            }
                    ){
                        public Map<String, String> getHeaders() {
                            Map<String, String> headers = new HashMap<String, String>();
                            headers.put("Content-Type", "application/multipart/form-data; charset=utf-8");
                            headers.put("Accept", "application/multipart/form-data");
                            return headers;
                        }
                            @Override
                            protected Map<String,String> getParams(){
                                Map<String,String> params = new HashMap<String, String>();
                             //   params.put("ubicacion",imagen.get);
                              //  params.put("file",);
                                params.put("idlocalrep",imagen.getId_reporte()+"");
                                params.put("idlocalim", imagen.getId_idimagen()+"");
                                params.put("descripcion1", imagen.getId_descripcion());


                                return params;
                        }


                    }
            );
        }

    }


    /************
     * Obtener registro que se acaba de marcar como pendiente de sincronizar y con estado de sincronizacion
     */

    private Single<List<Imagen>> obtenerImagenesPendientes() {

        return mImagenDao.getPendientes();
    }
    private Single<List<Reporte>> obtenerReportesPendientes() {

        return reporteDao.getPendientes();
    }

    /****Cambia a estado de sincronizacion el registro que se acaba de insertar localmente
     *
     */

//    private void iniciarActualizacion(){
//        Uri uri=ContractParaImagenes.CONTENT_URI;
//        String selection=ContractParaImagenes.Columnas.PENDIENTE_INSERCION+"=? and "+ContractParaImagenes.Columnas.ESTADO+"=?";
//        String[] selectionArgs=new String[]{"1",ContractParaImagenes.ESTADO_OK+""};
//        ContentValues v=new ContentValues();
//        v.put(ContractParaImagenes.Columnas.ESTADO,ContractParaImagenes.ESTADO_SYNC);
//        int results=resolver.update(uri,v,selection,selectionArgs);
//        Log.i(TAG,"Registros puestos en cola de insercion: "+results);
//
//    }

    /***
     * Limpia el registro que se sincronizó y le asigna la nueva id remota proveida por el servidor
     */
    private void finalizarActualizacionImagen( Imagen imagen){
        imagen.setEstatus(ContractParaImagenes.ESTADO_SYNC);

       // mImagenDao.updateImagen(imagen);
    }
    private void finalizarActualizacionReporte( Reporte reporte){
        reporte.setEstatus(ContractParaImagenes.ESTADO_SYNC);

     //   reporteDao.updateReporte(reporte);
    }

    /***
     * Procesa los diferentes tipos de respuesta obtenidos por el servidor
     */
    public void procesarRespuestaInsert(JSONObject response, Imagen imagen) throws JSONException {
     String estado=response.getString(Constantes.ESTADO);
     String mensaje=response.getString(Constantes.MENSAJE);
     String idRemota=response.getString(Constantes.ID_GASTO);
     switch(estado){
         case Constantes.SUCCESS: //exito
             Log.i(TAG,mensaje);
             finalizarActualizacionImagen(imagen);

             break;
         case Constantes.FAILED:
             Log.i(TAG,mensaje);
             break;
     }
    }
    /**
     * Actualiza los registros locales a traves de una comparacion con los datos del servidor
     */
   /* private void actualizarDatosLocales(JSONObject response, SyncResult syncResult){
        try {
            JSONArray Imagenes=null;
            Imagenes=response.getJSONArray(ContractParaImagenes.GASTO);
            Gasto[] res=gson.fromJson(Imagenes!=null? Imagenes.toString():null,Gasto[].class);
            List<Gasto> data = Arrays.asList(res);
            // Lista para recolección de operaciones pendientes
            ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();

            HashMap<String,Gasto>gastoMap=new HashMap<String,Gasto>();
            for(Gasto e:data){
                gastoMap.put(e.id,e);
            }
            //Consultar registros remotos actuales
            Uri uri=ContractParaImagenes.CONTENT_URI;
            String select=ContractParaImagenes.Columnas.ID_REMOTA+"  IS NOT NULL";
            Cursor c=resolver.query(uri,PROJECTION,select,null,null);
            assert c!=null;
            Log.i(TAG,"Se encontraron "+c.getCount()+"registros locales");

            //encontrar datos obsoletos
            String id;
            int monto;
            String etiqueta;
            String fecha;
            String descripcion;
            while(c.moveToNext()){
                syncResult.stats.numEntries++;
                id=c.getString(COLUMNA_ID_REMOTA);
                monto=c.getInt(COLUMNA_MONTO);
                etiqueta=c.getString(COLUMNA_ETIQUETA);
                fecha=c.getString(COLUMNA_FECHA);
                descripcion=c.getString(COLUMNA_DESCRIPCION);
                Gasto match=gastoMap.get(id);
                if(match!=null){
                    gastoMap.remove(id);
                    Uri existingUri=ContractParaImagenes.CONTENT_URI.buildUpon().appendPath(id).build();
                    //comprobar si necesita ser actualizado
                    boolean b = match.precio != monto;
                    boolean b1 = match.etiqueta != null && !match.etiqueta.equals(etiqueta);
                    boolean b2 = match.fecha != null && !match.fecha.equals(fecha);
                    boolean b3 = match.nombre != null && !match.nombre.equals(descripcion);

                    if (b || b1 || b2 || b3) {

                        Log.i(TAG, "Programando actualización de: " + existingUri);
                        ops.add(ContentProviderOperation.newUpdate(existingUri).withValue(ContractParaImagenes.Columnas.PRECIO, match.precio)
                                .withValue(ContractParaImagenes.Columnas.ETIQUETA, match.etiqueta)
                                .withValue(ContractParaImagenes.Columnas.FECHA, match.fecha)
                                .withValue(ContractParaImagenes.Columnas.NOMBRE, match.nombre).build());
                        syncResult.stats.numUpdates++;
                    }else
                    {
                        Log.i(TAG,"No hay acciones para este registro "+existingUri);
                    }
                }else {
                    Uri deleteUri=ContractParaImagenes.CONTENT_URI.buildUpon().appendPath(id).build();
                    Log.i(TAG,"se eliminara "+deleteUri);
                    ops.add(ContentProviderOperation.newDelete(deleteUri).build());
                    syncResult.stats.numDeletes++;
                }

            }
            c.close();
            // Insertar items resultantes
            for (Gasto e : gastoMap.values()) {
                Log.i(TAG, "Programando inserción de: " + e.id);
                ops.add(ContentProviderOperation.newInsert(ContractParaImagenes.CONTENT_URI)
                        .withValue(ContractParaImagenes.Columnas.ID_REMOTA, e.id)
                        .withValue(ContractParaImagenes.Columnas.PRECIO, e.precio)
                        .withValue(ContractParaImagenes.Columnas.ETIQUETA, e.etiqueta)
                        .withValue(ContractParaImagenes.Columnas.FECHA, e.fecha)
                        .withValue(ContractParaImagenes.Columnas.NOMBRE, e.nombre)
                        .build());
                syncResult.stats.numInserts++;
            }
            if(syncResult.stats.numInserts>0||syncResult.stats.numUpdates>0||syncResult.stats.numDeletes>0){
                Log.i(TAG, "Aplicando operaciones" );
                resolver.applyBatch(ContractParaImagenes.AUTHORITY,ops);
                resolver.notifyChange(ContractParaImagenes.CONTENT_URI,null, false);
                Log.i(TAG, "Sincronización finalizada" );

            }
            else{
                Log.i(TAG, "No se requiere sincronización" );
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (OperationApplicationException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        }


    }*/

    public static void sincronizarAhora(Context context, boolean onlyUpdate){
        Log.i(TAG, "Realizando petición de sincronización manual.");

        Bundle bundle=new Bundle();
         bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED,true);
         bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL,true);
         if(onlyUpdate)
             bundle.putBoolean(ContentResolver.SYNC_EXTRAS_UPLOAD,true);
        Log.i(TAG, "Realizando petición de sincronización manual2.");
         ContentResolver.requestSync(obtenerCuentasASincronizar(context),AUTHORITY,bundle);

    }

    private static Account obtenerCuentasASincronizar(Context context) {
        AccountManager accountManager=(AccountManager)context.getSystemService(Context.ACCOUNT_SERVICE);
        Account newAccount=new Account(context.getString(R.string.app_name), Constantes.ACCOUNT_TYPE);
        if(null==accountManager.getPassword(newAccount)){
            //añadir la cuenta
            if(!accountManager.addAccountExplicitly(newAccount,"",null))
                return null;
        }
        Log.i(TAG, "Cuenta de usuario obtenida");
        return newAccount;
    }


}
