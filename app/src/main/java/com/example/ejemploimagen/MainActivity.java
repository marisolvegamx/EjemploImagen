package com.example.ejemploimagen;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.ejemploimagen.database.Injection;
import com.example.ejemploimagen.sync.SyncAdapter;
import com.example.ejemploimagen.ui.ReporteViewModel;
import com.example.ejemploimagen.ui.ViewModelFactory;
import com.example.ejemploimagen.utils.Constantes;
import com.example.ejemploimagen.web.VolleySingleton;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


public class MainActivity extends AppCompatActivity implements ImagenDetalleAdapter.AdapterCallback {

    private static final int REQUEST_CODE_TAKE_PHOTO = 1;


    private ImageButton boton1;

    private ListView lv1;
    private AdaptadorImagenDetalle adaptador;
    List<Imagen> reportesg;
    private FusedLocationProviderClient fusedLocationClient; //cliente servicio de ubicación

    int GPS_ACTIVE = 0;
    private ViewModelFactory mViewModelFactory;
    private static final String TAG = MainActivity.class.getSimpleName();
    private ReporteViewModel mViewModel;
    private final CompositeDisposable mDisposable = new CompositeDisposable();
    String nombre_foto;
    private RecyclerView recycler;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager lManager;

    private int queBoton=0;
    private TextView msjSinDatos;
    private static final int INTERVALO = 3000; //2 segundos para salir
    private long tiempoPrimerClick;
    private String userId;
    ProgressBar progreso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // imagen1=(ImageView) findViewById(R.id.imageView);
        boton1 = findViewById(R.id.bttomarfoto1);
        msjSinDatos = findViewById(R.id.msjSinDatos);

        boton1.setOnClickListener(b1Listener);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        mViewModelFactory = Injection.provideViewModelFactory(this);
        mViewModel = new ViewModelProvider(this, mViewModelFactory).get(ReporteViewModel.class);
       // lv1=(ListView)findViewById(R.id.listview);

        //reviso que tenga el idusuario
        IdentificadorUsuario iu=new IdentificadorUsuario(this);
        if(iu.tengoID()){
            userId=iu.id;
        }else {   //genero uno
            iu.guardarId();
            userId=iu.id;

        }
  //      progreso=(ProgressBar)findViewById(R.id.progressbar);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sync:
                sincronizar();
                return true;

            case R.id.action_ubicacion:
            //   probarUbicacion();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        // Subscribe to the emissions of the user name from the view model.
        // Update the user name text view, at every onNext emission.
        // In case of error, log the exception.

        mDisposable.add(mViewModel.getImagenes()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Imagen>>() {

                               @Override
                               public void accept(List<Imagen> tasks)  {
                                   if(tasks==null||tasks.size()==0)
                                       msjSinDatos.setText("Para agregar imágenes de click \nen el botón de arriba");

                                   else
                                       llenarLista(tasks);
                               }
                           },
                        throwable -> Log.e(TAG, "Unable to get username", throwable)));

    }


    private View.OnClickListener b1Listener = new View.OnClickListener() {
        public void onClick(View v) {
            //pongo el nombre en el textview
            tomarFoto(v);
            //foto1.setText(nombre_foto);
            //obtenerubicacion
          //  obtenerUbicacion();
            queBoton=1;
        }
    };

    public void tomarFoto(View v) {
        Intent intento1 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd_HHmmss");

        String dateString = format.format(new Date());
        File foto=null;
        try{
            nombre_foto = "img_" + dateString + ".jpg";
            foto = new File(getExternalFilesDir(null), nombre_foto);
            Log.e(TAG, "****"+foto.getAbsolutePath());
        } catch (Exception ex) {
            ex.printStackTrace();
            Toast.makeText(MainActivity.this, "No se encontró almacenamiento externo", Toast.LENGTH_SHORT).show();


        }
        Uri photoURI = FileProvider.getUriForFile(this,
                "com.example.ejemploimagen.fileprovider",
                foto);
        intento1.putExtra(MediaStore.EXTRA_OUTPUT, photoURI); //se pasa a la otra activity la referencia al archivo
        startActivityForResult(intento1, REQUEST_CODE_TAKE_PHOTO);

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_TAKE_PHOTO && resultCode == RESULT_OK) {
          /*  String state = Environment.getExternalStorageState();
            if (Environment.MEDIA_MOUNTED.equals(state)) {
                // Se puede leer y escribir en el almacenamiento
            } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
                // Sólo se puede leer
            } else {
                // No se puede leer ni escribir
            }
            File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES), "muesmerc");
            // This location works best if you want the created images to be shared
            // between applications and persist after your app has been uninstalled.

            // Create the storage directory if it does not exist
            if (! mediaStorageDir.exists()){
                if (! mediaStorageDir.mkdirs()){
                    Log.d("compras", "failed to create directory");
                   // return null;
                }
            }*/
            File file = new File(getExternalFilesDir(null), nombre_foto);
            if (file.exists()) {

                //envio a la actividad dos para ver la foto
                Intent intento1 = new Intent(this, Actividad2.class);
              intento1.putExtra("ei.archivo",nombre_foto);
                startActivity(intento1);
                 }



        }
    }
    public void recuperarFoto(View v) {
        //    Bitmap bitmap1= BitmapFactory.decodeFile(getExternalFilesDir(null)+"/"+et1.getText().toString());

        //  imagen1.setImageBitmap(bitmap1);
    }

    public void ver(View v) {
        Intent intento1 = new Intent(this, Actividad2.class);
        startActivity(intento1);
    }




    /*public void setLocation(Location loc) {
        //Obtener la direccion de la calle a partir de la latitud y la longitud
        if (loc.getLatitude() != 0.0 && loc.getLongitude() != 0.0) {
            try {
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                List<Address> list = geocoder.getFromLocation(
                        loc.getLatitude(), loc.getLongitude(), 1);
                if (!list.isEmpty()) {
                    Address DirCalle = list.get(0);
                    direccion.setText(DirCalle.getAddressLine(0));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }*/

        public void sincronizar(){

          //  SyncAdapter.sincronizarAhora(this,false);
            realizarSincronizacionRemota(this);
        }

    private void realizarSincronizacionRemota(Context context) {
        List<Imagen> listaIm = new ArrayList<Imagen>();
        Log.i(TAG, "Actualizando el servidor...");

       mDisposable.add(mViewModel.getImagenesPendientes()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                 .subscribe(new Consumer<List<Imagen>>() {
                      @Override
                      public void accept(List<Imagen> tasks)  {
                             listaIm.addAll(tasks);
                             //jecutar lo demas aqui
                             enviarDatos(context,listaIm);
                          Toast.makeText(MainActivity.this, "Finalizó la sincronización", Toast.LENGTH_SHORT).show();

                      }
                  },
                  throwable -> Log.e(TAG, "Unable to get username", throwable)));

    }

    private void enviarDatos(Context context, List<Imagen> listaIm){

        try {
            Gson gson = new Gson();
            SubirFoto sf = new SubirFoto();
            Log.i(TAG, "Se encontraron " + listaIm.size() + " registros nuevos");
            Toast.makeText(MainActivity.this, "Se encontraron" + listaIm.size() + " registros nuevos", Toast.LENGTH_SHORT).show();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
          //  progreso.show();
            for (int i = 0; i < listaIm.size(); i++) {
                final Imagen imagen = listaIm.get(i);
                Log.d(TAG, "***envio num " + i +"--"+imagen.getId_descripcion());

                VolleySingleton.getInstance(this).addToRequestQueue(
                        new StringRequest(Request.Method.POST, Constantes.INSERT_URL,


                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        try {
                                            //   procesarRespuestaInsert(response,imagen);
                                            Log.d(TAG, "*******llego mensaje***" + response + "---" + imagen.getCreated_at().getTime() + "---" + sdf.format(imagen.getCreated_at()));
                                            //envio la imagen
                                            sf.subirFoto(context.getExternalFilesDir(null)+ "/" + imagen.getId_ruta(), userId, context);
                                            actualizarEstado(imagen);
                                        } catch (Exception e) {
                                            Log.d(TAG, "******hubo un error al subir la imagen " + e.getMessage());

                                            e.printStackTrace();
                                        }
                                    }


                                },
                                new Response.ErrorListener() {
                                    public void onErrorResponse(VolleyError error) {
                                        Log.d(TAG, "*****codigo errror***" + error.networkResponse.statusCode);
                                   /* if(error instanceof TimeOutError)
                                        Log.d(TAG,"*****codigo errror***"+error.networkResponse.statusCode);
                else if(error instanceof ServerError)
                   *show server error occured*
                                                    same for others
                                    Log.d(TAG,"*****error en la respuesta***"+error.networkResponse.toString());
                               */
                                    }
                                }
                        ) {
                      /*                             public Map<String, String> getHeaders() {
                            Map<String, String> headers = new HashMap<String, String>();
                            headers.put("Content-Type", "multipart/form-data;  charset=utf-8");
                            headers.put("Accept", "multipart/form-data");
                            return headers;
                        }*/
                            @Override
                            protected Map<String, String> getParams() {
                                Map<String, String> params = new HashMap<String, String>();
                                params.put("ubicacion", imagen.getUbicacion());
                                //  params.put("file",);

                                params.put("idlocalrep", imagen.getId_reporte() + "");
                                params.put("idlocalim", imagen.getId_idimagen() + "");
                                if( imagen.getId_descripcion()!=null&& imagen.getId_descripcion()!="")
                                params.put("descripcion1", imagen.getId_descripcion());
                                params.put("ruta", imagen.getId_ruta());
                                params.put("idusuario", userId);
                                params.put("pais", imagen.getPais());
                                params.put("created_at", sdf.format(imagen.getCreated_at()));
                                return checkParams(params);
                            }

                            private Map<String, String> checkParams(Map<String, String> map){
                                Iterator<Map.Entry<String, String>> it = map.entrySet().iterator();
                                while (it.hasNext()) {
                                    Map.Entry<String, String> pairs = (Map.Entry<String, String>)it.next();
                                    if(pairs.getValue()==null){
                                        map.put(pairs.getKey(), "");
                                    }
                                }
                                return map;
                            }


                        }
                );
            }
        }catch (Exception ex){
            ex.getStackTrace();
        }

    }
    private void actualizarEstado(Imagen imagen){
      //  for(Imagen imagen:lista){
            imagen.setEstatus(1);
            mDisposable.add(mViewModel.updateImagen(imagen)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(()->{Log.d(TAG, "Se actualizó el estado");
                   cargarLista();},
                                throwable -> Log.e(TAG, "Unable to get username", throwable)));


       // }

    }

    private  void llenarLista(List<Imagen> reportes){
        reportesg=new ArrayList<>();
        reportesg.addAll(reportes);
       // for (Imagen imagen: reportes ) {
            //busco en reportes
        //    reportesg.add(imagen.getId_idimagen()+" "+imagen.getUbicacion()+" "+imagen.getId_reporte()+" "+imagen.getId_ruta()+" "+imagen.getId_descripcion()+" "+imagen.getEstatus());
        //}
//        Log.d("reportes=",reportes.size()+"");
//        adaptador = new AdaptadorImagenDetalle(this, reportes);
//
//        //  reportesg.addAll(reportes);
//        adaptador.notifyDataSetChanged();
        recycler = (RecyclerView) findViewById(R.id.reciclador);
    //    recycler.setHasFixedSize(true); //no variará de tamaño en la ejecucion
        // Usar un administrador para LinearLayout
        lManager = new LinearLayoutManager(this);
        recycler.setLayoutManager(lManager);
        // Crear un nuevo adaptador
        adapter = new ImagenDetalleAdapter(reportesg,this);
        recycler.setAdapter(adapter);
      //  lv1.setAdapter(adaptador);
    }
    @Override
    public void onClickCallback(int id) {
      elimiarImagen(id);
    }
    public void elimiarImagen(int idimagen){
        try{
            AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
            dialogo1.setTitle("Importante");
            dialogo1.setMessage("¿ Está seguro de eliminar este registro?");
            dialogo1.setCancelable(false);
            dialogo1.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogo1, int id) {
                    //busco la imagen a eliminar
                   int pos= buscarEnLista(idimagen);
                    mViewModel.deleteImagen(idimagen);
                    reportesg.remove(pos);
                    actualizarRecyclerView();
                 //   Toast.makeText(this, "Se eliminó el registro", Toast.LENGTH_LONG).show();

                }
            });
            dialogo1.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogo1, int id) {
                    dialogo1.cancel();
                }
            });
            dialogo1.show();

        }
        catch (Exception ex){
               Log.e(TAG,"Error"+ ex.getMessage());
                Toast.makeText(this, "Hubo un error al eliminar", Toast.LENGTH_LONG).show();

        }
    }
    public int buscarEnLista(int id){
            for(int i=0;i<reportesg.size();i++){
                Imagen im=reportesg.get(i);
                if(im.getId_idimagen()==id){
                    return i;
                }
            }
            return 0;
    }
    public void actualizarRecyclerView(){

        adapter = new ImagenDetalleAdapter(reportesg,this);
        recycler.setAdapter(adapter);
    }
    public void cargarLista() {
        mDisposable.add(mViewModel.getImagenes()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Imagen>>() {

                               @Override
                               public void accept(List<Imagen> tasks) {
                                   if (tasks == null || tasks.size() == 0)
                                       msjSinDatos.setText("Para agregar imágenes de click \nen el botón de arriba");

                                   else
                                       llenarLista(tasks);
                               }
                           },
                        throwable -> Log.e(TAG, "Unable to get username", throwable)));
    }
//    public AlertDialog createSimpleDialog() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
//        public interface NoticeDialogListener {
//            public void onDialogPositiveClick(DialogFragment dialog);
//            public void onDialogNegativeClick(DialogFragment dialog);
//        }
//
//        // Use this instance of the interface to deliver action events
//        NoticeDialogListener listener;
//        builder.setTitle("Titulo")
//                .setMessage("El Mensaje para el usuario")
//                .setPositiveButton("OK",
//                        new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                listener.onPossitiveButtonClick();
//                            }
//                        })
//                .setNegativeButton("CANCELAR",
//                        new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                listener.onNegativeButtonClick();
//                            }
//                        });
//
//        return builder.create();
//    }

    }