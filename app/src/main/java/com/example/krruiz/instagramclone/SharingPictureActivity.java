package com.example.krruiz.instagramclone;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class SharingPictureActivity extends AppCompatActivity {

    private Button buttonForSharing;
    private StorageReference ProductImagesRef;
    private DatabaseReference ProductsRef;
    private Uri selectImage;
    private ProgressDialog loadingBar;
    private String saveCurrentDate, saveCurrenTime, productRandomKey, downloadImageURL;
    private ImageView imageForsharing;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sharing_picture);

        loadingBar = new ProgressDialog(this);

        ProductImagesRef = FirebaseStorage.getInstance().getReference().child("Images");
        ProductsRef = FirebaseDatabase.getInstance().getReference().child("Pictures");
        imageForsharing = (ImageView) findViewById(R.id.imageForSharingPic);

        buttonForSharing = (Button) findViewById(R.id.buttonSharing);
        buttonForSharing.setVisibility(View.INVISIBLE);

        if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String [] {Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        } else{
            getPhoto();

        }

        buttonForSharing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loadingBar.setTitle("Saving Data");
                loadingBar.setMessage("Please wait");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();
                validateData();
                //openImagesActivity();

            }
        });


    }

    public void getPhoto(){

        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getPhoto();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        selectImage = data.getData();

        if (requestCode == 1 && resultCode == RESULT_OK && data != null){
            try{
                //Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectImage);
                //imageForsharing.setImageBitmap(bitmap);

                Picasso.with(this).load(selectImage).fit().centerCrop().into(imageForsharing);

                buttonForSharing.setVisibility(View.VISIBLE);
            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }

    private void validateData(){

        if (selectImage == null){
            Toast.makeText(SharingPictureActivity.this, "Image is required", Toast.LENGTH_LONG).show();
        }else{
            StoreProductInformation();

        }

    }

    private void StoreProductInformation() {

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currenDate = new SimpleDateFormat("yyyy-MM-dd");
        saveCurrentDate = currenDate.format(calendar.getTime());

        SimpleDateFormat currenTime = new SimpleDateFormat("HH:mm:ss");
        saveCurrenTime = currenTime.format(calendar.getTime());

        productRandomKey = saveCurrentDate + saveCurrenTime;

        // StorageReference variable to save everything
        final StorageReference filePath = ProductImagesRef.child(selectImage.getLastPathSegment() + productRandomKey + ".jpg" );
        final UploadTask uploadTask = filePath.putFile(selectImage);

        // checking if failure upload
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Error: "+e.getMessage(), Toast.LENGTH_LONG).show();

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Toast.makeText(getApplicationContext(), "Image uploaded successfully", Toast.LENGTH_LONG).show();
                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {

                        if (!task.isSuccessful()){
                            throw task.getException();
                        }
                        downloadImageURL = filePath.getDownloadUrl().toString();
                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {

                        if (task.isSuccessful()){
                            downloadImageURL = task.getResult().toString();
                            System.out.println("========================================================");
                            System.out.println(downloadImageURL);
                            System.out.println("========================================================");

                            Toast.makeText(getApplicationContext(), "Product Image URL sucessfully ", Toast.LENGTH_LONG);

                            saveProductInfoDatabase();
                        }
                    }
                });

            }
        });
    }

    private void saveProductInfoDatabase() {

        HashMap<String, Object> photoMap = new HashMap<>();
        photoMap.put("pid", productRandomKey);
        photoMap.put("date", saveCurrentDate);
        photoMap.put("time", saveCurrenTime);
        photoMap.put("image", downloadImageURL);
        photoMap.put("sharedby", Prevalent.currentUser.getUsername());

        String clearCaracter = productRandomKey.replace('.', ':').replace(',', ' ');

        System.out.println("++++++++++++++++++++++++++++++++++++++++++++++");
        System.out.println(productRandomKey);
        System.out.println("++++++++++++++++++++++++++++++++++++++++++++++");

        ProductsRef.child(productRandomKey).updateChildren(photoMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()){
                    Toast.makeText(SharingPictureActivity.this, "Product is saved sucessfully", Toast.LENGTH_LONG).show();
                    loadingBar.dismiss();
                    Intent intent = new Intent(SharingPictureActivity.this, ImagesShowActivity.class);
                    startActivity(intent);

                }else {
                    loadingBar.dismiss();
                    Toast.makeText(SharingPictureActivity.this, "Error saving Product", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

}
