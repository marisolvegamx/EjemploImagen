package com.example.ejemploimagen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ejemploimagen.database.Injection;
import com.example.ejemploimagen.ui.ReporteViewModel;
import com.example.ejemploimagen.ui.ViewModelFactory;
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class Actividad2 extends AppCompatActivity {
    private ImageView imagen1;
    private EditText descripcion1;
    private static final int REQUEST_CODE_TAKE_PHOTO = 1;

    private TextView foto1;
    private LocationCallback locationCallback;
    private boolean requestingLocationUpdates;
    private LocationManager locManager;
    private LocationListener locListener;
    private TextView ubicacion;
    private static final int REQUEST_LOCATION = 99;
    final int REQUEST_CHECK_SETTINGS = 0;
    private String[] archivos;
    private static final String IMG_PATH1 = "img_path1";
    private static final String DESCRIPCION = "descripcion";

    private static final String TXTUBICACION = "txtubicacion";
    private static final String TAG = MainActivity.class.getSimpleName();
    private ReporteViewModel mViewModel;
    private ViewModelFactory mViewModelFactory;
    private FusedLocationProviderClient fusedLocationClient; //cliente servicio de ubicación
    private static final int INTERVALO = 3000; //2 segundos para salir
    private long tiempoPrimerClick;
    LocationRequest locationRequest;
    private final CompositeDisposable mDisposable = new CompositeDisposable();
    private String country;
    String nombre_foto;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actividad2);
      //  File dir=getExternalFilesDir(null);
        //archivos=dir.list();
        foto1 = (TextView) findViewById(R.id.txtfoto1);
        ubicacion = (TextView) findViewById(R.id.txtubicacion);
        descripcion1 = (EditText) findViewById(R.id.eddescripcion1);

        imagen1=(ImageView) findViewById(R.id.imageView);
        mViewModelFactory = Injection.provideViewModelFactory(this);
        mViewModel = new ViewModelProvider(this, mViewModelFactory).get(ReporteViewModel.class);
        // my_child_toolbar is defined in the layout file
        Toolbar myChildToolbar =
                (Toolbar) findViewById(R.id.my_child_toolbar);
        setSupportActionBar(myChildToolbar);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);
        Bundle extras = getIntent().getExtras(); // Aquí es null
         nombre_foto=extras.getString("ei.archivo");
        Log.d("algo******",nombre_foto);
        File file = new File(getExternalFilesDir(null)+"/"+nombre_foto);
        if (file.exists()) {
         //   Bitmap imageBitmap = (Bitmap) extras.get("data");
            Bitmap bitmap1= BitmapFactory.decodeFile(getExternalFilesDir(null)+"/"+nombre_foto);

            imagen1.setImageBitmap(bitmap1);
            foto1.setText( nombre_foto);
        }


        if (savedInstanceState != null) {
            // Restore value of counters from saved state
            foto1.setText( savedInstanceState.getString(IMG_PATH1));
            descripcion1.setText( savedInstanceState.getString(DESCRIPCION));

            ubicacion.setText( savedInstanceState.getString(TXTUBICACION));

            Bitmap bitmap1= BitmapFactory.decodeFile(foto1.getText().toString());

            imagen1.setImageBitmap(bitmap1);
            Log.d(TAG, "Restoring Data in onCreate savedInstanceState != null");
        }else
        {
            createLocationRequest();
            /*para obtener la localizacion*/
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
            //  requestPermissionLauncher.launch(
            //        Manifest.permission.REQUESTED_PERMISSION);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
            return;
        } else {
            // rastreoGPS();
            locationStart();
        }

    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current game state
        savedInstanceState.putString(IMG_PATH1, foto1.getText().toString());
        savedInstanceState.putString(DESCRIPCION, descripcion1.getText().toString());
        savedInstanceState.putString(TXTUBICACION, ubicacion.getText().toString());
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }
    @Override
    public void onBackPressed(){
        if (tiempoPrimerClick + INTERVALO > System.currentTimeMillis()){
            super.onBackPressed();
            return;
        }else {
            Toast.makeText(this, "Sí sale sin guardar la información esta se perderá. Vuelve a presionar para salir", Toast.LENGTH_LONG).show();
        }
        tiempoPrimerClick = System.currentTimeMillis();
    }
    // Create an anonymous implementation of OnClickListener



    public void probarUbicacion(View v) {
        //
        //    createLocationRequest();
        // GPS_ACTIVE = 1;
        //  obtenerUbicacion();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
            //  requestPermissionLauncher.launch(
            //        Manifest.permission.REQUESTED_PERMISSION);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
            return;
        } else {
            // rastreoGPS();
            locationStart();
        }

    }



    private boolean isLocationPermissionGranted() {
        int permission = ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION);
        return permission == PackageManager.PERMISSION_GRANTED;
    }

    private void manageDeniedPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)) {
            // Aquí muestras confirmación explicativa al usuario
            // por si rechazó los permisos anteriormente
        } else {
            ActivityCompat.requestPermissions(
                    this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_LOCATION);
        }
    }


    protected void createLocationRequest() {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                // All location settings are satisfied. The client can initialize
                // location requests here.
                // ...
                locationStart();
            }
        });
        task.addOnFailureListener(this, new OnFailureListener() {
            @Override

            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(Actividad2.this,
                                REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }
                }
            }
        });


    }


    public void probarGuardar(View v) {
        try {

            // ReporteController rc = new ReporteController();


            List<Imagen> listaImagen = new ArrayList<Imagen>();
            Imagen imagen = new Imagen();
            imagen.setId_ruta("foto2.jpg");
            imagen.setEstatus(0);
            imagen.setId_descripcion("esta es una prueba");
            imagen.setUbicacion("88888,99999");
            listaImagen.add(imagen);
            // rc.guardarReporte(this,reporte, listaImagen);
            mDisposable.add(mViewModel.insertReporte(null, listaImagen)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(() -> Toast.makeText(this, getString(R.string.reporte_guardado), Toast.LENGTH_SHORT).show(),
                            throwable -> Log.e(TAG, "Unable to update username", throwable)));


        } catch (Exception ex) {
            ex.printStackTrace();
            Toast.makeText(this, getString(R.string.error), Toast.LENGTH_SHORT).show();
        }
    }
    public void guardarReporte(View v) {
        //reviso si hay datos
        try {
            Date fecha=new Date();
            List<Imagen> listaImagen = new ArrayList<Imagen>();

            if (foto1.getText().toString() != "") {


                Imagen imagen = new Imagen();
                imagen.setId_ruta(foto1.getText().toString());
                imagen.setEstatus(0);
                imagen.setId_descripcion(descripcion1.getText().toString());
                imagen.setUbicacion(ubicacion.getText().toString());
                imagen.setCreated_at(fecha);
                imagen.setPais(country);
                listaImagen.add(imagen);
            }

            if(listaImagen.size()>0)//hay al menos una foto
            {// rc.guardarReporte(this,reporte, listaImagen);
                if( ubicacion.getText().toString() == ""){
                    Toast.makeText(this, "Falta activar la ubicación", Toast.LENGTH_SHORT).show();

                }else {
                    mDisposable.add(mViewModel.insertReporte(null, listaImagen)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(() -> {Toast.makeText(this, getString(R.string.reporte_guardado), Toast.LENGTH_SHORT).show();
                                        limpiarCampos();
                                        //volver a actividad principal
                                finish();
                                        },
                                    throwable -> Log.e(TAG, "Unable to update username", throwable)));
                }}
            else{
                Toast.makeText(this, "Capture al menos una foto", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            Toast.makeText(this, getString(R.string.error), Toast.LENGTH_SHORT).show();
        }

    }


    private void rastreoGPS() {
        /*Se asigna a la clase LocationManager el servicio a nivel de sistema a partir del nombre.*/
        locManager = (LocationManager) getSystemService(this.LOCATION_SERVICE);

        /*Se declara y asigna a la clase Location la última posición conocida proporcionada por el proveedor.*/
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
            return;
        }
        Location loc = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        mostrarPosicion(loc);

        //Se define la interfaz LocationListener, que deberá implementarse con los siguientes métodos.
        locListener = new LocationListener() {
            //Método que será llamado cuando cambie la localización.
            @Override
            public void onLocationChanged(Location location) {
                mostrarPosicion(location);
            }

            //Método que será llamado cuando se produzcan cambios en el estado del proveedor.
            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            //Método que será llamado cuando el proveedor esté habilitado para el usuario.
            @Override
            public void onProviderEnabled(String provider) {
            }

            //Método que será llamado cuando el proveedor esté deshabilitado para el usuario.
            @Override
            public void onProviderDisabled(String provider) {
            }
        };

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
            return;
        }
        locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, locListener);
    }
    public void mostrarPosicion(Location location){
        String latitude = String.valueOf(location.getLatitude());
        String longitude = String.valueOf(location.getLongitude());
        // System.out.print("****Ya tengo la ubicacion" + latitude + "," + longitude);
        //buscar direccion
        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> list = geocoder.getFromLocation(
                    location.getLatitude(), location.getLongitude(), 1);
            if (!list.isEmpty()) {
                Address DirCalle = list.get(0);
                String state = DirCalle.getAdminArea();
                country = DirCalle.getCountryName();
                //    ubicacion.setText(DirCalle.getAddressLine(0));
                //   ubicacion.setText(state+","+country);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        ubicacion.setText(latitude + "," + longitude);

    }

    public void rotarImagen(View v){

        Bitmap bitmapOrg=BitmapFactory.decodeFile(getExternalFilesDir(null)+"/"+nombre_foto);

        int width=bitmapOrg.getWidth();
        int height=bitmapOrg.getHeight();

        Matrix matrix = new Matrix();

        matrix.postRotate(90);

        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmapOrg, width, height, true);

        Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);

        imagen1.setImageBitmap(rotatedBitmap);
        File file = new File("your/desired/file/path");
        OutputStream os = null;
        try {
            os = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            Log.d("Compras",e.getMessage());
            Toast.makeText(this, getString(R.string.error), Toast.LENGTH_SHORT).show();

        }
        rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);

    }
    public void limpiarCampos(){
        foto1.setText("");
        descripcion1.setText("");

    }
    public void cancelar(View v) {
        if (tiempoPrimerClick + INTERVALO > System.currentTimeMillis()){
          /*  super.onBackPressed();
            return;*/
            Intent intento1 = new Intent(this, MainActivity.class);
            startActivity(intento1);
        }else {
            Toast.makeText(this, "Sí sale sin guardar la información esta se perderá. Vuelve a presionar para salir", Toast.LENGTH_LONG).show();
        }
        tiempoPrimerClick = System.currentTimeMillis();

    }


    private void locationStart() {
        LocationManager mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Localizacion Local = new Localizacion();
        Local.setMainActivity(this);
        final boolean gpsEnabled = mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!gpsEnabled) {
            Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(settingsIntent);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
            return;
        }
        if (mlocManager.getAllProviders().contains(LocationManager.NETWORK_PROVIDER)) {
            mlocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 0, (LocationListener) Local);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
            return;
        }else {
            if (mlocManager.getAllProviders().contains(LocationManager.GPS_PROVIDER)) {
                mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 0, (LocationListener) Local);
            }
        }
      //  ubicacion.setText("Localización agregada");

    }
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1000) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationStart();
                return;
            }
        }
    }

    /* Aqui empieza la Clase Localizacion */
    public class Localizacion implements LocationListener {
        Actividad2 mainActivity;
        public Actividad2 getMainActivity() {
            return mainActivity;
        }
        public void setMainActivity(Actividad2 mainActivity) {
            this.mainActivity = mainActivity;
        }
        @Override
        public void onLocationChanged(Location loc) {
            // Este metodo se ejecuta cada vez que el GPS recibe nuevas coordenadas
            // debido a la deteccion de un cambio de ubicacion
            mostrarPosicion(loc);

            // this.mainActivity.setLocation(loc);
        }
        @Override
        public void onProviderDisabled(String provider) {
            // Este metodo se ejecuta cuando el GPS es desactivado
          //  ubicacion.setText("GPS Desactivado");
        }
        @Override
        public void onProviderEnabled(String provider) {
            // Este metodo se ejecuta cuando el GPS es activado
           // ubicacion.setText("GPS Activado");
        }
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            switch (status) {
                case LocationProvider.AVAILABLE:
                    Log.d("debug", "LocationProvider.AVAILABLE");
                    break;
                case LocationProvider.OUT_OF_SERVICE:
                    Log.d("debug", "LocationProvider.OUT_OF_SERVICE");
                    break;
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    Log.d("debug", "LocationProvider.TEMPORARILY_UNAVAILABLE");
                    break;
            }
        }


    }
}