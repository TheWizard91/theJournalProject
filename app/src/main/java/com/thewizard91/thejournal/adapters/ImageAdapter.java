package com.thewizard91.thejournal.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.thewizard91.thejournal.R;
import com.thewizard91.thejournal.models.image.ImageModel;

import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<ImageModel> list_of_images;
    private Context context;

    public ImageAdapter (List<ImageModel> list_of_images) {
        this.list_of_images = list_of_images;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View my_view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_blueprint, parent, false);
        my_view.setLayoutParams(new RecyclerView.LayoutParams(-1,-2));
        context = parent.getContext();
        return new ImageViewHolder(my_view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        holder.setIsRecyclable(false);
        ImageViewHolder image_view_holder = (ImageViewHolder) holder;
        String image_uri = list_of_images.get(position).getImageURI();
        image_view_holder.setImageUri(image_uri);
    }

    @Override
    public int getItemCount() {
        return list_of_images.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {
        private final View view;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            Fresco.initialize(context);
        }

        @SuppressLint("CheckResult")
        public void setImageUri (String image_uri) {
            ImageView image_view = view.findViewById(R.id.image_object);
            RequestOptions placeholder_options = new RequestOptions();
            placeholder_options.placeholder(R.drawable.image_placeholder);

            Glide.with(context)
                    .applyDefaultRequestOptions(placeholder_options)
                    .load(image_uri)
                    .into(image_view);
        }
    }
}
