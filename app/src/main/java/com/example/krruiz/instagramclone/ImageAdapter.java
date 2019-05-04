package com.example.krruiz.instagramclone;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.krruiz.instagramclone.Model.Picture;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {

    private Context context;
    private List<Picture> uploads;

    public ImageAdapter(Context context1, List<Picture> picturesUploaded){

        context = context1;
        uploads = picturesUploaded;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View v = LayoutInflater.from(context).inflate(R.layout.images_view, viewGroup, false);
        return new ImageViewHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder imageViewHolder, int i) {

        Picture picCurrent = uploads.get(i);
        imageViewHolder.textViewName.setText(picCurrent.getSharedby());

       /* System.out.println("+++++++++++++++++++++++PRINTING URL+++++++++++++++++++++");
        System.out.println(picCurrent.getImageUrl());
        System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++");*/
        Picasso.with(context).load(picCurrent.getImage()).fit().centerCrop().into(imageViewHolder.imageView);

    }

    @Override
    public int getItemCount() {
        return uploads.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder{

        public TextView textViewName;
        public ImageView imageView;

        public ImageViewHolder(View itemView){
            super(itemView);
            textViewName = itemView.findViewById(R.id.name_image);
            imageView = itemView.findViewById(R.id.view_image);
        }

    }

}
