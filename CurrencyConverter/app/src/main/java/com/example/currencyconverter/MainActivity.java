package com.example.currencyconverter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParserException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    Button selectionBtn1;
    Button selectionBtn2;
    Button exchangeBtn;
    Button swapBtn;

    TextView fromAmount; TextView toAmount;

    Country first = null, second = null;
    Double mRatio[];

    static public final int REQUEST_GET_FIRST_COUNTRY = 1001;
    static public final int REQUEST_GET_SECOND_COUNTRY = 1002;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getTitle().equals("Lịch sử")){
            Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
            startActivity(intent);
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        fromAmount = (TextView) findViewById(R.id.fromAmount);
        toAmount = (TextView) findViewById(R.id.toAmount);

        selectionBtn1 = findViewById(R.id.selectButton1);
        selectionBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CountryListActivity.class);
                intent.putExtra("requestCode", REQUEST_GET_FIRST_COUNTRY);
                startActivityForResult(intent, REQUEST_GET_FIRST_COUNTRY);
            }
        });

        selectionBtn2 = findViewById(R.id.selectButton2);
        selectionBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CountryListActivity.class);
                intent.putExtra("requestCode", REQUEST_GET_SECOND_COUNTRY);
                startActivityForResult(intent, REQUEST_GET_SECOND_COUNTRY);
            }
        });

        exchangeBtn = (Button) findViewById(R.id.exchangeButton);
        exchangeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(first == null || second == null){
                    Toast.makeText(getApplicationContext(), "Please select both countries", Toast.LENGTH_LONG).show();
                    return;
                }

                EditText editText = (EditText) findViewById(R.id.editTextNumber);
                editText.clearFocus();

                String input = editText.getText().toString();
                if(input.equals("")) return;
                Double dInput = Double.parseDouble(input);
                fromAmount.setText(first.currency + " " + String.format(Locale.FRANCE, "%,.2f", dInput));

                if(first != null && second != null){
                    makeExchange(input);
                }
                //Toast.makeText(getApplicationContext(), input, Toast.LENGTH_LONG).show();
            }
        });

        swapBtn = (Button) findViewById(R.id.swapButton);
        swapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(first == null || second == null){
                    Toast.makeText(getApplicationContext(), "Please select both countries", Toast.LENGTH_LONG).show();
                    return;
                }
                TextView fromText = (TextView) findViewById(R.id.fromText);
                TextView toText = (TextView) findViewById(R.id.toText);

                Country temp = second;
                second = first;
                first = temp;

                fromText.setText(first.name + " - " + first.currency);
                toText.setText(second.name + " - " + second.currency);
                fromAmount.setText("");
                toAmount.setText("");
                //Toast.makeText(getApplicationContext(), input, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void makeExchange(String input){
        try {
            mRatio = new XmlGetTask(first.currency, second.currency).execute("").get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(mRatio == null) {
            Toast.makeText(getApplicationContext(), "Please try different value", Toast.LENGTH_SHORT).show();
            return;
        }
        // calculate exchange
        double fromValue = Double.parseDouble(input);
        double toValue = fromValue * mRatio[1];
        String out = String.format(Locale.FRANCE,"%,.2f", toValue);
        toAmount.setText(second.currency + " " + out);

        // add history
        Date exchangeTime = getCurrentTime();
        Log.d("OK", String.format("%s %f = %s %f", first.currency, fromValue, second.currency, toValue));
        ExchangeHistory.ADD(first.currency, fromValue, second.currency, toValue, exchangeTime);
    }

    private Date getCurrentTime(){
        Date date = new Date();
        Toast.makeText(getApplicationContext(), date.toString(), Toast.LENGTH_SHORT).show();
        return date;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        TextView fromText = (TextView) findViewById(R.id.fromText);
        TextView toText = (TextView) findViewById(R.id.toText);
        if(requestCode == REQUEST_GET_FIRST_COUNTRY && resultCode == CountryListActivity.RESULT_OK_1st){
            Bundle bundle = data.getExtras();
            String name = bundle.getString("country's name");
            String currency = bundle.getString("currency");
            first = new Country(); first.name = name; first.currency = currency;
            fromText.setText(name + " - " + currency);
        }
        if(requestCode == REQUEST_GET_SECOND_COUNTRY && resultCode == CountryListActivity.RESULT_OK_2nd){
            Bundle bundle = data.getExtras();
            String name = bundle.getString("country's name");
            String currency = bundle.getString("currency");
            second = new Country(); second.name = name; second.currency = currency;
            toText.setText(name + " - " + currency);
        }
    }

    // Uploads XML from url, parses it.
    private Double[] loadXmlFromNetwork(String urlString) throws XmlPullParserException, Exception {
        InputStream stream = null;
        // Instantiate the parser
        FxExchangeXmlParser parser = new FxExchangeXmlParser();
        List<Item> items = null;

        try {
            stream = downloadUrl(urlString);
            items = parser.parse(stream);
            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
        return items.get(0).parseCDATA();
    }

    // Given a string representation of a URL, sets up a connection and gets an input stream.
    private InputStream downloadUrl(String urlString) throws Exception {
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            return conn.getInputStream();
        } catch (Exception ex){
            ex.printStackTrace();
        }
        return null;
    }

    class XmlGetTask extends AsyncTask<String, Void, Double[]>{
        public String FROM = "";
        public String TO = "";
        public String strUrl = "https://from.fxexchangerate.com/to.xml";
        public XmlGetTask(String from, String to){
            FROM = from.toLowerCase();
            TO = to.toLowerCase();
            strUrl = strUrl.replace("from", FROM);
            strUrl = strUrl.replace("to", TO);
        }
        @Override
        protected Double[] doInBackground(String... strings) {
            try{
                return loadXmlFromNetwork(strUrl);
            } catch (Exception ex){
                ex.printStackTrace();
            }
            return null;
        }
    }
}