package com.example.ejemploimagen;

import android.content.Context;
import android.service.carrier.CarrierMessagingService;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.UUID;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.ServerResponse;
import net.gotev.uploadservice.UploadFile;
import net.gotev.uploadservice.UploadInfo;
import net.gotev.uploadservice.UploadStatusDelegate;


public class SubirFoto {
    MultipartUploadRequest upload;
    String URL_SUBIRPICTURE="https://muesmerc.mx/postmixv3/api/Subirfotos.php";


    public void subirFoto(List<String> uploadFileArrayList,String idusuario, Context context){

//filenameGaleria=getFilename();

            try {
                 upload= new MultipartUploadRequest(context,URL_SUBIRPICTURE)
                        .setMaxRetries(2)
                          .addParameter("idusuario", idusuario)
                         .setUtf8Charset();

                upload.setDelegate(new UploadStatusDelegate(){

                            @Override
                            public void onProgress(UploadInfo uploadInfo) {

                            }

                            @Override
                            public void onError(UploadInfo uploadInfo, Exception exception) {

                            }

                            @Override
                            public void onCompleted(UploadInfo uploadInfo, ServerResponse serverResponse) {
                               /* File eliminar = new File(rutaFotoCamaraGaleria.getPath());
                                if (eliminar.exists()) {
                                    if (eliminar.delete()) {
                                        System.out.println(“archivo eliminado:” + rutaFotoCamaraGaleria.getPath());
                                    } else {
                                        System.out.println(“archivo no eliminado” + rutaFotoCamaraGaleria.getPath());
                                    }*/
                            }

                            @Override
                            public void onCancelled(UploadInfo uploadInfo) {

                            }


                        });

                 int i=0;
                for (String uf : uploadFileArrayList) {
                    addFileToUploadRequest(uf,i++);
                }
                upload.startUpload();
            } catch (Exception e) {
                e.printStackTrace();
            }

    }

    public void subirFoto(String uploadFileArrayList, String idusuario,Context context) throws Exception {

//filenameGaleria=getFilename();

        try {
            upload= new MultipartUploadRequest(context,URL_SUBIRPICTURE)
                    .setMaxRetries(2)
                      .addParameter("idusuario", idusuario)
                    .setUtf8Charset();
            upload.setDelegate(new UploadStatusDelegate(){

                @Override
                public void onProgress(UploadInfo uploadInfo) {

                }

                @Override
                public void onError(UploadInfo uploadInfo, Exception exception) {

                }

                @Override
                public void onCompleted(UploadInfo uploadInfo, ServerResponse serverResponse) {
                               /* File eliminar = new File(rutaFotoCamaraGaleria.getPath());
                                if (eliminar.exists()) {
                                    if (eliminar.delete()) {
                                        System.out.println(“archivo eliminado:” + rutaFotoCamaraGaleria.getPath());
                                    } else {
                                        System.out.println(“archivo no eliminado” + rutaFotoCamaraGaleria.getPath());
                                    }*/
                }

                @Override
                public void onCancelled(UploadInfo uploadInfo) {

                }


            });

            int i=0;

                addFileToUploadRequest(uploadFileArrayList,1);

            upload.startUpload();
        } catch (Exception e) {
            throw e;
        }

    }


    public void addFileToUploadRequest(String uf,int i) throws Exception {
        try {
        upload.addFileToUpload(uf, "file_"+i);
        } catch (FileNotFoundException e) {
                throw new Exception("No se encontró el archivo "+uf+" Verifique");
        }
        }
}
