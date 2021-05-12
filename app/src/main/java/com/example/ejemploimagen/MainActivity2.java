package com.example.ejemploimagen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;


public class MainActivity2 extends AppCompatActivity {
    private static final int REQUEST_LOCATION = 99;
    private static final int REQUEST_CODE_TAKE_PHOTO = 1;
    private ImageView imagen1;
    private EditText descripcion1;
    private EditText descripcion2;
    private EditText descripcion3;
    private Button boton1;
    private Button boton2;
    private Button boton3;
    private TextView foto1;
    private TextView foto2;
    private TextView foto3;
    private TextView ubicacion;
    private FusedLocationProviderClient fusedLocationClient; //cliente servicio de ubicación
    final int REQUEST_CHECK_SETTINGS = 0;
    int GPS_ACTIVE = 0;
    private ViewModelFactory mViewModelFactory;
    private static final String TAG = MainActivity.class.getSimpleName();
    private ReporteViewModel mViewModel;
    private final CompositeDisposable mDisposable = new CompositeDisposable();
    String nombre_foto;
    LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private boolean requestingLocationUpdates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actividad2);
        // imagen1=(ImageView) findViewById(R.id.imageView);

    //    boton2 = (Button) findViewById(R.id.bttomarfoto2);
//        boton3 = (Button) findViewById(R.id.bttomarfoto3);

        boton1.setOnClickListener(b1Listener);
        boton2.setOnClickListener(b2Listener);
        boton3.setOnClickListener(b3Listener);

        createLocationRequest();
        /*para obtener la localiczacion*/
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mViewModelFactory = Injection.provideViewModelFactory(this);
        mViewModel = new ViewModelProvider(this, mViewModelFactory).get(ReporteViewModel.class);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    // Update UI with location data
                    // ...
                }
                requestingLocationUpdates=true;
            }
        };



    }

    // Create an anonymous implementation of OnClickListener
    private View.OnClickListener b1Listener = new View.OnClickListener() {
        public void onClick(View v) {
            //pongo el nombre en el textview
            tomarFoto(v);
            //foto1.setText(nombre_foto);
            //obtenerubicacion
            obtenerUbicacion();
        }
    };

    // Create an anonymous implementation of OnClickListener
    private View.OnClickListener b2Listener = new View.OnClickListener() {
        public void onClick(View v) {
            //pongo el nombre en el textview
            tomarFoto(v);
        }
    };

    // Create an anonymous implementation of OnClickListener
    private View.OnClickListener b3Listener = new View.OnClickListener() {
        public void onClick(View v) {
            //pongo el nombre en el textview
            tomarFoto(v);
        }
    };

    public void tomarFoto(View v) {
        Intent intento1 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd_HHmmss");

        String dateString = format.format(new Date());

        nombre_foto = "img_" + dateString + ".jpg";
        File foto = new File(getExternalFilesDir(null), nombre_foto);
        Uri photoURI = FileProvider.getUriForFile(this,
                "com.example.ejemploimagen.provider",
                foto);
        intento1.putExtra(MediaStore.EXTRA_OUTPUT, photoURI); //se pasa a la otra activity la referencia al archivo
        startActivityForResult(intento1, REQUEST_CODE_TAKE_PHOTO);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_TAKE_PHOTO && resultCode == RESULT_OK) {

            File file = new File(getExternalFilesDir(null), nombre_foto);
            if (file.exists()) {

                //pongo el nombre del archivo
                foto1.setText(nombre_foto);
                Toast.makeText(this, "se tomo la foto", Toast.LENGTH_SHORT).show();
            }

            /*if (requestCode == REQUEST_CODE_TAKE_PHOTO && resultCode == RESULT_OK) {
                Bundle extras = data.getExtras(); // Aquí es null
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                mPhotoImageView.setImageBitmap(imageBitmap);
            }*/

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

    private static final int PICK_PDF_FILE = 2;

    public void probarUbicacion(View v) {
        //
        //    createLocationRequest();
        GPS_ACTIVE = 1;
        obtenerUbicacion();
    }

    @SuppressLint("MissingPermission")
    public void obtenerUbicacion() {

     // if (isLocationPermissionGranted()) {
        if (GPS_ACTIVE == 1) {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            String latitude;
                            String longitude;
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                // Logic to handle location object
                                latitude = String.valueOf(location.getLatitude());
                                longitude = String.valueOf(location.getLongitude());
                                System.out.print("****Ya tengo la ubicacion" + latitude + "," + longitude);
                                ubicacion.setText(latitude + "," + longitude);
                            } else {
                                Toast.makeText(MainActivity2.this, "No se pudo obtener la ubicación. Habilitarla", Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
        } else {
            createLocationRequest();
        }
         /* } else {
              manageDeniedPermission();
             // throw new Exception();
          }*/


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

//    private void startLocationUpdates() {
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            fusedLocationClient.requestLocationUpdates(locationRequest,
//                    locationCallback,
//                    Looper.getMainLooper());
//            return;
//        }
//
//    }

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
                GPS_ACTIVE=1;
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
                    resolvable.startResolutionForResult(MainActivity2.this,
                            REQUEST_CHECK_SETTINGS);
                } catch (IntentSender.SendIntentException sendEx) {
                    // Ignore the error.
                }
            }
        }
    });


}
    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    private void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (requestingLocationUpdates) {
         //   startLocationUpdates();
        }
    }


    public void probarGuardar(View v){
    try {

            // ReporteController rc = new ReporteController();
            Reporte reporte = new Reporte();
            reporte.setUbicacion("88888,99999");
            List<Imagen> listaImagen = new ArrayList<Imagen>();
            Imagen imagen = new Imagen();
            imagen.setId_ruta("foto2.jpg");
            imagen.setEstatus(0);
            imagen.setId_descripcion("esta es una prueba");

            listaImagen.add(imagen);
            // rc.guardarReporte(this,reporte, listaImagen);
            mDisposable.add(mViewModel.insertReporte(reporte, listaImagen)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(()->Toast.makeText(this,getString(R.string.reporte_guardado), Toast.LENGTH_SHORT).show(),
                            throwable -> Log.e(TAG, "Unable to update username", throwable)));


    }catch(Exception ex){
        ex.printStackTrace();
        Toast.makeText(this,getString(R.string.error),Toast.LENGTH_SHORT).show();
    }
}
    public void guardarReporte(View v){
      //reviso si hay datos
    try {
        if (foto1.getText().toString() != "" && ubicacion.getText().toString() != "") {
           // ReporteController rc = new ReporteController();
            Reporte reporte = new Reporte();
            reporte.setUbicacion(ubicacion.getText().toString());
            List<Imagen> listaImagen = new ArrayList<Imagen>();
            Imagen imagen = new Imagen();
            imagen.setId_ruta(foto1.getText().toString());
            imagen.setEstatus(0);
            imagen.setId_descripcion(descripcion1.getText().toString());

            listaImagen.add(imagen);
           // rc.guardarReporte(this,reporte, listaImagen);
            mDisposable.add(mViewModel.insertReporte(reporte, listaImagen)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(()->Toast.makeText(this,getString(R.string.reporte_guardado), Toast.LENGTH_SHORT).show(),
                            throwable -> Log.e(TAG, "Unable to update username", throwable)));

        }
    }catch(Exception ex){
        ex.printStackTrace();
        Toast.makeText(this,getString(R.string.error),Toast.LENGTH_SHORT).show();
    }

    }



}