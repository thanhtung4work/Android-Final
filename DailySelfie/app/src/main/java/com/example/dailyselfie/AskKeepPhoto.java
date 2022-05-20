package com.example.dailyselfie;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;

public class AskKeepPhoto extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask_keep_photo);

        String photoUri = getIntent().getStringExtra("photoUri");
        Toast.makeText(getApplicationContext(), photoUri, Toast.LENGTH_LONG).show();

        // set image from file
        ImageView imageView = (ImageView) findViewById(R.id.keepImageView);
        File image = new File(photoUri);
        BitmapFactory.Options bitmapOption = new BitmapFactory.Options();
        Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(), bitmapOption);
        imageView.setImageBitmap(bitmap);

        // set onclick event for buttons
        final Button keep = (Button) findViewById(R.id.keepButton);
        keep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_OK);
                finish();
            }
        });

        final Button remove = (Button) findViewById(R.id.removeButton);
        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File deletingFile = new File(photoUri);
                deletingFile.delete();
                setResult(RESULT_CANCELED);
                finish();
            }
        });
    }


}