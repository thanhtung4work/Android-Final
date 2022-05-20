package com.example.dailyselfie;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.content.FileProvider;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Debug;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    ArrayList<Photo> listOfPhotos;
    PhotoGetter myTask;

    // identify camera intent
    private static final int TAKE_PHOTO = 1, SAVE_PHOTO = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //createNotification();
        createNotificationChannel();
    }

    @Override
    protected void onResume() {
        super.onResume();
        myTask = new PhotoGetter();
        myTask.execute();
        // inflatePopupView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my_option_menu, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.cameraOptionButton:
                // Alarm 30s after taking a photo
                setAlarm(30);

                dispatchCameraIntent();
                break;
            case R.id.aboutOptionButton:
                Toast.makeText(getApplicationContext(), "Made by TVK", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
        return true;
    }

    private void setAlarm(long waitingSeconds){
        waitingSeconds *= 1000;
        Intent intent = new Intent(MainActivity.this, ReminderBroadcast.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        long timeAtTakePhoto = System.currentTimeMillis();

        alarmManager.set(AlarmManager.RTC_WAKEUP,
                timeAtTakePhoto + waitingSeconds,
                pendingIntent);
    }

    private void dispatchCameraIntent(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                ex.printStackTrace();
                Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, TAKE_PHOTO);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // galleryAddPic();
        // cancel camera intent by clicking back
        if(requestCode == TAKE_PHOTO && resultCode != RESULT_OK){
            new File(currentPhotoPath).delete();
        } else if(requestCode == TAKE_PHOTO && resultCode == RESULT_OK){
            Intent intent = new Intent(MainActivity.this, AskKeepPhoto.class);
            intent.putExtra("photoUri", currentPhotoPath);
            startActivityForResult(intent, SAVE_PHOTO);
        } else if(requestCode == SAVE_PHOTO && resultCode == RESULT_CANCELED){
            dispatchCameraIntent();
        }
    }

    private void setUpGridView(){
        GridView gridView = (GridView) findViewById(R.id.MainGrid);
        File f = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        gridView.setAdapter(new CustomGridAdapter(getApplicationContext(), listOfPhotos, f.toString()));
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(i > listOfPhotos.size()) return;

                Photo photo = listOfPhotos.get(i);
                String fullPath = f.getAbsolutePath() + "/" + photo.uri;

                // send the full path of clicked photo
                File file = new File(fullPath);
                Intent intent = new Intent(MainActivity.this, ViewPhoto.class);
                Bundle extra = new Bundle();
                extra.putString("photoUri", file.getAbsolutePath());

                intent.putExtras(extra);
                startActivity(intent);
            }
        });
    }

    private void createNotificationChannel(){
        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O){
            CharSequence name = "DailySelfieChannel";
            String description = "Channel for daily selfie";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("notifyDailySelfie", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    // name of file going to be saved by the camera
    String currentPhotoPath;
    private File createImageFile() throws IOException{
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "DAILYSELFIE_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        /*
        * name (prefix)
        * suffix
        * directory
        */
        File image = File.createTempFile(
          imageFileName,
          ".jpg",
          storageDir
        );
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }



    // get all image path
    /**
     * Get all the image(pictures) paths
     * @return ArrayList with images paths
     */
    private ArrayList<Photo> getUriListOfPhotos(){
        listOfPhotos = new ArrayList<>();
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File[] files = storageDir.listFiles();
        for (int i = 0; i < files.length; i++){
            // empty file
            if(files[i].length() == 0) continue;

            // Log.d("File's name", files[i].getName());
            Photo p = new Photo();

            // path -> uri -> bitmap
            Uri uri = Uri.fromFile(files[i]);
            Bitmap bitmap = null;
            try {
                bitmap =  MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri) ;
            } catch (Exception e) {
                e.printStackTrace();
            }
            p.bitmap = bitmap;
            p.uri = files[i].getName();
            listOfPhotos.add(p);

            // Log.d("File's name", listOfPhotos.get(i).uri);
        }

        return listOfPhotos;
    }

    private class PhotoGetter extends AsyncTask<Void, Void, Void>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // set up loading popup

        }

        @Override
        protected Void doInBackground(Void... voids) {
            getUriListOfPhotos();
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            setUpGridView();
        }
    }
}