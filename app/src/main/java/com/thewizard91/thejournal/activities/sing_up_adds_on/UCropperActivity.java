package com.thewizard91.thejournal.activities.sing_up_adds_on;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.thewizard91.thejournal.R;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.UUID;

public class UCropperActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        Intent intent = getIntent();
        Uri uri;

        if (intent.getExtras() != null) {
            String sourceUir = intent.getStringExtra("SendImageData");
            uri = Uri.parse(sourceUir);
            String destinationUri = UUID.randomUUID().toString() + ".jpg";
            startCrop(uri, destinationUri);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == UCrop.REQUEST_CROP && resultCode == RESULT_OK) {
            assert data != null;
            final Uri resultUri = UCrop.getOutput(data);
            Intent intent = new Intent();
            intent.putExtra("CROP",resultUri+"");
            setResult(101, intent);
            finish();
        } else if (requestCode == UCrop.RESULT_ERROR) {
            assert data != null;
            final Throwable cropError = UCrop.getError(data);
            Toast.makeText(this,"Error:"+cropError,Toast.LENGTH_LONG).show();
        }
    }

    private void startCrop(@NonNull Uri uri, String destinationUri) {

        UCrop ucrop = UCrop.of(uri, Uri.fromFile(new File(getCacheDir(), destinationUri)));

        ucrop.withAspectRatio(16,9);
        ucrop.useSourceImageAspectRatio();

        ucrop.withMaxResultSize(2000, 2000);
        ucrop.withOptions(getOptions());
        ucrop.start(UCropperActivity.this);
    }

    public UCrop.Options getOptions() {
        UCrop.Options options = new UCrop.Options();
        options.setCompressionQuality(30);

        // CompressType
        options.setCompressionFormat(Bitmap.CompressFormat.PNG);

        // Ui
        options.setHideBottomControls(false);
        options.setFreeStyleCropEnabled(true);

        //Colors
        options.setStatusBarColor(getResources().getColor(R.color.darker_blue));
        options.setToolbarTitle("Choose The Image For Your Profile!");

        // Option to see the rounded image when you select it.
//        options.setCircleDimmedLayer(true);

        return options;
    }

}