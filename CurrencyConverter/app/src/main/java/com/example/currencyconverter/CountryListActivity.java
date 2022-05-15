package com.example.currencyconverter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

    // get json from url
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
    public class CurrencyGetTask extends AsyncTask<Void, Void, String>{

        private final String USERNAME = "tung_worldwide";
        private final String strURL = "http://api.geonames.org/countryInfoJSON?formatted=true&username=" + USERNAME;

        @Override
        protected String doInBackground(Void... voids) {
            return jsonFromURL(strURL);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                readXmlFeed();
//                JSONObject root = new JSONObject(s);
//                JSONArray array = root.getJSONArray("geonames");
//
//                for(int i = 0; i < array.length(); i++){
//                    JSONObject countryJSON = array.getJSONObject(i);
//                    Country c = new Country();
//                    c.currency = countryJSON.getString("currencyCode");
//                    c.name = countryJSON.getString("countryName");
//                    if(c.name.equals("Antarctica")) continue;
//
//                    countries.add(c);
//                    Log.d("Country", c.name);
//                    Log.d("Currency", c.currency);
//                }


                MyAdapter myAdapter = new MyAdapter(countries, getApplicationContext());
                myList.setAdapter(myAdapter);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}