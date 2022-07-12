package com.istiaksaif.medops.Utils;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.os.Environment.getExternalStoragePublicDirectory;

/**
 * Created by Istiak Saif on 20/07/21.
 * updated by Istiak Saif on 05/09/21.
 */

public class ImageGetHelper {

    private Fragment fragment;
    private Activity activity;
    public static final int CAMERA_REQUEST_CODE=100;
    public static final int STORAGE_REQUEST_CODE=200;
    public static final int IMAGE_PICK_GALLERY_CODE=300;
    public static final int IMAGE_PICK_CAMERA_CODE=400;
    public Uri imageUri;
    public String pathFile;

    String cameraPermission[] = new String[]{android.Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    String storagePermission[] = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

    public ImageGetHelper(Fragment fragment, Activity activity) {
        this.fragment = fragment;
        this.activity = activity;
    }

    private boolean checkStoragePermission(){
        boolean result = ContextCompat.checkSelfPermission(fragment.getActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestStoragePermission(){
        fragment.requestPermissions(storagePermission, STORAGE_REQUEST_CODE);
    }

    private boolean checkCameraPermission(){
        boolean result = ContextCompat.checkSelfPermission(fragment.getActivity(),
                Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);

        boolean result1 = ContextCompat.checkSelfPermission(fragment.getActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }
    public void requestCameraPermission(){
        fragment.requestPermissions(cameraPermission, CAMERA_REQUEST_CODE);
    }
    private void checkPermission(){
        if(Build.VERSION.SDK_INT>=23){
            if (ContextCompat.checkSelfPermission(fragment.getActivity(),Manifest.permission.CAMERA)!=
                    PackageManager.PERMISSION_GRANTED){
                fragment.requestPermissions(new String[]{Manifest.permission.CAMERA},1);
            }
        }
    }

    public void showImagePicDialog() {
        String options[] = {"Camera","Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(fragment.getActivity());
        builder.setTitle("Pick Image");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which ==0){
                    if(!checkCameraPermission()){
                        checkPermission();
                    }else {
                        pickFromCamera();
                    }
                }
                else if (which == 1){
                    if(!checkStoragePermission()){
                        requestStoragePermission();
                    }
                    else{
                        pickFromGallery();
                    }
                }
            }
        });
        builder.create().show();
    }

    public void pickFromCamera(){
        Intent takePic = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePic.resolveActivity(fragment.getActivity().getPackageManager())!=null){
            File picFile = null;
            picFile= createPhotoFile();
            if (picFile !=null){
                pathFile = picFile.getAbsolutePath();
                imageUri = FileProvider.getUriForFile(fragment.getActivity(),"com.istiaksaif.uniclubz.fileprovider",picFile);
                takePic.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
                fragment.startActivityForResult(takePic,IMAGE_PICK_CAMERA_CODE);
            }
        }
    }

    private File createPhotoFile() {
        String name = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File storageDir = getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = null;
        try {
            image = File.createTempFile(name,".jpg",storageDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    public void pickFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        if (activity==null) {
            fragment.startActivityForResult(galleryIntent, IMAGE_PICK_GALLERY_CODE);
        }else {
            activity.startActivityForResult(galleryIntent, IMAGE_PICK_GALLERY_CODE);
        }
    }


    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode){
            case CAMERA_REQUEST_CODE:{
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    boolean cameraAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if(cameraAccepted && writeStorageAccepted){
                        pickFromCamera();
                    }
                    else{
                        Toast.makeText(fragment.getActivity(),"Please camera enable", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
            case STORAGE_REQUEST_CODE:{
                if(grantResults.length>0){
                    boolean writeStorageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if(writeStorageAccepted){
                        pickFromGallery();
                    }
                    else{
                        Toast.makeText(fragment.getActivity(),"Please enable", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
        }
    }

}