package com.example.nationinfojson;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLPeerUnverifiedException;

public class MainActivity extends AppCompatActivity {
    ListView listView;
    ArrayList<Country> countryList;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getTitle().toString()){
            case("Reload"):
                recreate();
                break;
            case("About"):
                Intent intent = new Intent(getApplicationContext(), AboutActivity.class);
                startActivity(intent);
                break;
            default:
                Log.d("Title", item.getTitle().toString());
        }

        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (ListView) findViewById(R.id.countryListView);
        new JsonGetTask().execute("");

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new JsonGetTask().execute();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Country country = countryList.get(i);
                Toast.makeText(getApplicationContext(), country.name, Toast.LENGTH_SHORT).show();

                // create inflater
                LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                View popUpView = inflater.inflate(R.layout.detail_country_info_layout, null);

                // create pop up
                int width = ConstraintLayout.LayoutParams.WRAP_CONTENT;
                int height = ConstraintLayout.LayoutParams.WRAP_CONTENT;
                boolean focusable = true;
                final PopupWindow popupWindow = new PopupWindow(popUpView, width, height, focusable);
                TextView label = (TextView) popUpView.findViewById(R.id.countryNameDetail);
                label.setText(country.name);
                TextView info = (TextView) popUpView.findViewById(R.id.infoDetail);
                ImageView countryImageView = (ImageView) popUpView.findViewById(R.id.imageCountry);

                // set up country image
                setupImageLoader();
                Context mContext = getApplicationContext();
                int fallback = mContext.getResources().getIdentifier("@drawable/image_fallback", null, mContext.getPackageName());

                ImageLoader imageLoader = ImageLoader.getInstance();
                DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)
                        .cacheOnDisc(true).resetViewBeforeLoading(true)
                        .showImageForEmptyUri(fallback)
                        .showImageOnFail(fallback)
                        .showImageOnLoading(fallback).build();
                String imageUrl = String.format("https://img.geonames.org/img/country/250/%s.png", country.countryCode);
                imageLoader.displayImage(imageUrl, countryImageView, options);

                // set detailed info
                info.setText(String.format("Country code: %s%nCountry population: %s%nContinent: %s%nCapital: %s%nCurrency: %s%nArea: %s km2%n",
                        country.countryCode,
                        country.getFormattedPopulation(),
                        country.continent,
                        country.capital,
                        country.currency,
                        String.format(Locale.ENGLISH, "%,.2f", Double.parseDouble(country.areaInKm2)))
                );
                // String.format(Locale.ENGLISH, "%,d", Integer.parseInt(country.areaInKm2))

                // show pop up
                popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

                // dismiss when touched
                popUpView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        popupWindow.dismiss();
                        return true;
                    }
                });
            }
        });

    }

    private static String jsonFromURL(String strURL){
        StringBuilder sb = new StringBuilder();
        URLConnection urlConn;
        try{
            // url object
            URL url = new URL(strURL);

            // open connection
            urlConn = url.openConnection();

            // wrap connection in a buffer
            // InputStreamReader inputStreamReader = new InputStreamReader(urlConn.getInputStream());
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));

            String inputLine;
            while((inputLine = bufferedReader.readLine()) != null){
                sb.append(inputLine + "\n");
            }
            bufferedReader.close();
        } catch(Exception e){
            e.printStackTrace();
        }
        return sb.toString();
    }

    // set up lazy load for country image
    private void setupImageLoader(){
        Context mContext = getApplicationContext();
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

    // CLASS JSONGETTASK
    // get info from internet, pull it into countryList
    private class JsonGetTask extends AsyncTask<String, Void, String>{
        private static final String Tag = "JsonGetTask";

        private final String USERNAME = "tung_worldwide";
        private final String strURL = "http://api.geonames.org/countryInfoJSON?formatted=true&username=" + USERNAME;

        @Override
        protected String doInBackground(String... params) {
            return jsonFromURL(strURL);
        }

        @Override
        protected void onPostExecute(String s) {
            // Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
            countryList = new ArrayList<Country>();
            try {
                JSONObject root = new JSONObject(s);
                JSONArray array = root.getJSONArray("geonames");

                for(int i = 0; i < array.length(); i++){
                    JSONObject countryJSON = array.getJSONObject(i);
                    Country c = new Country(countryJSON.getString("countryName"),
                            countryJSON.getString("population"),
                            countryJSON.getString("countryCode"),
                            countryJSON.getString("capital"),
                            countryJSON.getString("continentName"),
                            countryJSON.getString("areaInSqKm"),
                            countryJSON.getString("currencyCode")
                    );
                    countryList.add(c);
                }

                MyAdapter myAdapter = new MyAdapter(countryList, getApplicationContext());
                listView.setAdapter(myAdapter);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


}