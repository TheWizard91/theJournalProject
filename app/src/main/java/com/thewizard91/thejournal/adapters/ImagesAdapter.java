package com.thewizard91.thejournal.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.material.imageview.ShapeableImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.thewizard91.thejournal.models.image.ImageModel;
import com.thewizard91.thejournal.R;
import com.stfalcon.frescoimageviewer.ImageViewer;

import java.util.ArrayList;
import java.util.List;

public class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.MyViewHolder> {
//    https://android-developers.googleblog.com/2017/02/build-flexible-layouts-with.html
//    https://androidgigatech.blogspot.com/2017/04/flexbox-inside-recyclerview-as.html
//    https://stackoverflow.com/questions/50340698/centering-recyclerview-items-with-flexboxlayoutmanager

    public Context context;
    private ArrayList<ImageModel> imagesArrayList;
    public ViewGroup parentReference;

    public ImagesAdapter() {}


    public ImagesAdapter(Context context, ArrayList<ImageModel> imagesArrayList) {
        this.context = context;
        this.imagesArrayList = imagesArrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.images,parent,false);
        parentReference = parent;
        return new MyViewHolder(view,parent,imagesArrayList);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String iURI = imagesArrayList.get(position).getImageURI();
        holder.setImage(iURI);
        holder.zoom(position);
    }
    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getItemCount() {
        return imagesArrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public ShapeableImageView shapeableImageView;
        private final View v;
        private final ArrayList<ImageModel> imagesArrayList;

        public MyViewHolder(View view, ViewGroup parent, ArrayList<ImageModel> imagesArrayList) {
            super(view);
            v = view;
            shapeableImageView = v.findViewById(R.id.flex_box_shapeable_image_view);
            this.imagesArrayList = imagesArrayList;
            Fresco.initialize(v.getContext());
        }

        public void setImage(String imageURI) {
//            Log.d("myImage",imagesModel.getImageURI());
            RequestOptions placeholderOption = new RequestOptions();
            placeholderOption.placeholder(R.drawable.image_placeholder);
            Glide.with(v.getContext())
                    .applyDefaultRequestOptions(placeholderOption)
                    .load(imageURI)
                    .into((ShapeableImageView) v.findViewById(R.id.flex_box_shapeable_image_view));
        }

        @SuppressLint("ResourceType")
        public void zoom(final int position) {
            v.findViewById(R.id.flex_box_shapeable_image_view)
                    .setOnClickListener(view -> {
                        List<String> uris = new ArrayList<>();
                        for (final ImageModel imageModel : imagesArrayList) {
                            uris.add(imageModel.getImageURI());
                        }
                        new ImageViewer.Builder<>(v.getContext(),uris).setStartPosition(position).hideStatusBar(false).allowZooming(true).allowSwipeToDismiss(true).show();
                    });
        }

        public void bindTo(String image) {
//            parent.addView(v);
//            Log.d("positionIN","2");
//            Log.d("image",image);
//            final Uri uri = Uri.parse(image);
//            Log.d("uri",uri.toString());
//            final String path = uri.getPath();
            final String path = image;
            Log.d("path",path);
            Drawable drawableImage = Drawable.createFromPath(path);
            shapeableImageView.setImageDrawable(drawableImage);
            Log.d("shapeableImageView",shapeableImageView.toString());
            ViewGroup.LayoutParams lp = shapeableImageView.getLayoutParams();
//            Log.d("lp",lp.getClass().toString());
//            Log.d("myFlexBoxM",FlexboxLayoutManager.class.toString());
//            parent.removeView(v);
            if (lp instanceof FlexboxLayoutManager.LayoutParams) {
                FlexboxLayoutManager.LayoutParams flexboxLp = (FlexboxLayoutManager.LayoutParams) shapeableImageView.getLayoutParams();
                flexboxLp.setFlexGrow(1.0f);
            }
        }
    }
}
