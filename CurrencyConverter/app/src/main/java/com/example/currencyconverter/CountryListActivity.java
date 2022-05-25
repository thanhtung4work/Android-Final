package com.example.currencyconverter;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class CountryListActivity extends AppCompatActivity {
    public static int RESULT_OK_1st = 10001;
    public static int RESULT_OK_2nd = 10002;

    static ArrayList<Country> countries;
    ListView myList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_country_list);

        myList = (ListView) findViewById(R.id.myList);
        myList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Bundle bundle = new Bundle();
                bundle.putString("country's name", countries.get(i).name);
                bundle.putString("currency", countries.get(i).currency);
                getIntent().putExtras(bundle);
                int code = getIntent().getIntExtra("requestCode", 0);
                if(code == MainActivity.REQUEST_GET_FIRST_COUNTRY){
                    setResult(RESULT_OK_1st, getIntent());
                    finish();
                } else if(code == MainActivity.REQUEST_GET_SECOND_COUNTRY){
                    setResult(RESULT_OK_2nd, getIntent());
                    finish();
                }
            }
        });

        CurrencyGetTask myTask = new CurrencyGetTask();
        myTask.execute();
    }


    private ArrayList<Country> readXmlFeed() throws Exception{
        if(countries != null) return countries;
        countries = new ArrayList<>();
        // get country info xml file
        InputStream inputStream =  getResources().openRawResource(R.raw.country_info);
        CountryInfoXmlParser parser = new CountryInfoXmlParser();
        countries = parser.parse(inputStream);
        return countries;
    }

    // get country's currency with async task
    public class CurrencyGetTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                readXmlFeed();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            MyAdapter myAdapter = new MyAdapter(countries, getApplicationContext());
            myList.setAdapter(myAdapter);
        }
    }


}