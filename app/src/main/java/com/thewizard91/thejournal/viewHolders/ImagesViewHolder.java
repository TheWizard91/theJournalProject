package com.thewizard91.thejournal.viewHolders;

import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.flexbox.FlexboxLayoutManager;
import com.thewizard91.thejournal.R;

import java.util.ArrayList;

public class ImagesViewHolder extends RecyclerView.ViewHolder {
    public ImageView imageView;
    private View v;

    public ImagesViewHolder(View itemView) {
        super(itemView);
//        https://androidgigatech.blogspot.com/2017/04/flexbox-inside-recyclerview-as.html
        v = itemView;
//        imageView = v.findViewById(R.id.flex_box_shapeable_image_view);
//        Log.d("positionIN","1");
//        Log.d("itemViewIs",String.valueOf(itemView));
//        Log.d("imageView ",String.valueOf(imageView));
    }

    public void bindTo(String image) {
        Log.d("positionIN","2");
        Log.d("image",image);
//        imageView.setImageDrawable(Drawable.createFromPath(image));
        ViewGroup.LayoutParams lp = imageView.getLayoutParams();
//        Log.d("TheimageView",imageView.toString());
        if (lp instanceof FlexboxLayoutManager.LayoutParams) {
            FlexboxLayoutManager.LayoutParams flexboxLp = (FlexboxLayoutManager.LayoutParams) lp;
            flexboxLp.setFlexGrow(1.0f);
        }
    }
}
