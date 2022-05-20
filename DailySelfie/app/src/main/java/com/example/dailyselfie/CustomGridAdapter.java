package com.example.dailyselfie;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class CustomGridAdapter extends BaseAdapter {
    ArrayList<Photo> photos;
    Context context;
    String environmentDir;

    CustomGridAdapter(Context context, ArrayList<Photo> photos, String environmentDir){
        this.context = context;
        this.photos = photos;
        this.environmentDir = environmentDir;
    }

    @Override
    public int getCount() {
        return photos.size();
    }

    @Override
    public Object getItem(int i) {
        return photos.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null){
            convertView = View.inflate(context, R.layout.image_list_item, null);
            viewHolder = new ViewHolder();
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.imageView);
            viewHolder.textView = (TextView) convertView.findViewById(R.id.fileName);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // get photo at position
        Photo foto = photos.get(i);
        Log.d("Name", foto.uri);
        viewHolder.textView.setText(foto.uri);
        Bitmap reduced = getResizedBitmap(foto.bitmap, 500);
        viewHolder.imageView.setImageBitmap(reduced);

        return convertView;
    }

    /**
     * reduces the size of the image (prevent lagging)
     * @param image
     * @param maxSize
     * @return
     */
    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    private class ViewHolder{
        public ImageView imageView;
        public TextView textView;
    }
}
