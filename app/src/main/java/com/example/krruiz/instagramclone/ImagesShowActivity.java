package com.example.krruiz.instagramclone;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.krruiz.instagramclone.Model.Picture;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ImagesShowActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ImageAdapter imageAdapter;

    private DatabaseReference dataRef;
    private List<Picture> mUploads;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images_show);

        setTitle("Hello "+ Prevalent.currentUser.getUsername()+"!");

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        mUploads = new ArrayList<>();

        dataRef = FirebaseDatabase.getInstance().getReference("Pictures");
        dataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){

                    Picture pictureShow = postSnapshot.getValue(Picture.class);
                    String URLAux = postSnapshot.getKey(); // This is returning ID for each picture
                    mUploads.add(pictureShow);

                    /*System.out.println("==========================================================");
                    System.out.println("Shared By:"+pictureShow.getSharedby());
                    System.out.println("Image URL:"+pictureShow.getImage());
                    System.out.println("URL:"+URLAux);
                    System.out.println("==========================================================");*/
                }
                imageAdapter = new ImageAdapter(ImagesShowActivity.this, mUploads);
                recyclerView.setAdapter(imageAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ImagesShowActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();

        menuInflater.inflate(R.menu.share_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.share){

            Intent intentForSharing = new Intent(ImagesShowActivity.this, SharingPictureActivity.class);
            startActivity(intentForSharing);

        }else if (item.getItemId() == R.id.logout){
            Prevalent.currentUser = null;
            Intent intent1 = new Intent(ImagesShowActivity.this, MainActivity.class);
            Toast.makeText(getApplicationContext(), "Logout", Toast.LENGTH_SHORT).show();
            startActivity(intent1);
        }

        return super.onOptionsItemSelected(item);
    }

}
