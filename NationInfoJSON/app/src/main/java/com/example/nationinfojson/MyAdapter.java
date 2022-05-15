package com.example.nationinfojson;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;

public class MyAdapter  extends BaseAdapter {
    ArrayList<Country> countryList;
    Context mContext;
    // ImageLoaderTask imageLoader;

    MyAdapter(ArrayList<Country> countries, Context context){
        countryList = countries;
        mContext = context;
    }

    @Override
    public int getCount() {
        return countryList.size();
    }

    @Override
    public Object getItem(int i) {
        return countryList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return countryList.get(i).id;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {

        // set ImageLoader
        setupImageLoader();

        ViewHolder holder;
        View vi = convertView;

        // get country in i(th) position
        Country country = countryList.get(i);
        String countryFlagURL = country.imageURL;

        if (convertView == null){
            vi = View.inflate(viewGroup.getContext(), R.layout.my_list_item_layout, null);

            holder = new ViewHolder();

            // put view from xml file to holder OBJECT
            holder.name = (TextView) vi.findViewById(R.id.coutryNameView);
            holder.info = (TextView) vi.findViewById(R.id.populationView);
            holder.icon = (ImageView) vi.findViewById(R.id.flagImage);

            vi.setTag(holder);
        } else holder = (ViewHolder) vi.getTag();

        // random color (for fun)
        int[] colorValue = {R.color.light_green, R.color.dark_green, R.color.dark_red, R.color.red_orange, R.color.yellow_orange};
        Random rand = new Random(); int range = 200;
        int nextRand = rand.nextInt(range) % 5;
        vi.setBackgroundTintList(ColorStateList.valueOf(mContext.getResources().getColor(colorValue[nextRand])));

        // create ImageLoader instance
        // if image not loaded, set image to a fallback image
        int fallback = mContext.getResources().getIdentifier("@drawable/image_fallback", null, mContext.getPackageName());

        ImageLoader imageLoader = ImageLoader.getInstance();
        DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)
                .cacheOnDisc(true).resetViewBeforeLoading(true)
                .showImageForEmptyUri(fallback)
                .showImageOnFail(fallback)
                .showImageOnLoading(fallback).build();

        // initialize image view
        ImageView imageView = holder.icon;

        // download and display image from url
        imageLoader.displayImage(countryFlagURL, imageView, options);

        // set text
        holder.name.setText(country.name);
        holder.info.setText(String.format("Population: %s", country.getFormattedPopulation()));
        holder.icon.setTag(country.countryCode);

        return vi;
    }

    private void setupImageLoader(){
        // UNIVERSAL IMAGE LOADER SETUP
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheOnDisc(true).cacheInMemory(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .displayer(new FadeInBitmapDisplayer(300)).build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                mContext)
                .defaultDisplayImageOptions(defaultOptions)
                .memoryCache(new WeakMemoryCache())
                .discCacheSize(100 * 1024 * 1024).build();

        ImageLoader.getInstance().init(config);
        // END - UNIVERSAL IMAGE LOADER SETUP
    }

    /*
        A CLASS HOLDS VIEW IN ITEM
    */
    static class ViewHolder{
        TextView name;
        TextView info;
        ImageView icon;
    }
}
