package com.example.dailyselfie;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;

public class ViewPhoto extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_photo);
        final ImageView imageView = (ImageView) findViewById(R.id.imageView2);

        Bundle bundle = getIntent().getExtras();
        String path = bundle.getString("photoUri");

        File image = new File(path);

        // set option
        BitmapFactory.Options bitmapOption = new BitmapFactory.Options();
        Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(), bitmapOption);
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        if(width > height){
            Matrix matrix = new Matrix();
            matrix.postRotate(90);
            Bitmap rotated = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
            // bitmap = rotated;
        }
        imageView.setImageBitmap(bitmap);

        final Button delButton = (Button) findViewById(R.id.deleteViewPhoto);
        delButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File deletingFile = new File(path);
                deletingFile.delete();
                onBackPressed();
            }
        });
    }
}