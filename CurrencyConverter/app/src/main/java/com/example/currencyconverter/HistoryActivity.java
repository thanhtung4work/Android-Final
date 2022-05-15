package com.example.currencyconverter;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

public class HistoryActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        TextView textView = (TextView) findViewById(R.id.historiesTextView);
        textView.setMovementMethod(new ScrollingMovementMethod());
        printHistory();
    }

    private void printHistory(){
        if (ExchangeHistory.HISTORIES == null) {
            return;
        }
        String strHistories = "\n-------------------------\n";
        for(History h : ExchangeHistory.HISTORIES){
            String time = "";
            if(h.date != null){
                time = "Thời gian đổi: " + h.date.toLocaleString() + "\n";
            }
            strHistories = "\n-------------------------\n" + time + h.toString() + strHistories;
        }
        TextView textView = (TextView) findViewById(R.id.historiesTextView);
        textView.setText(strHistories);
    }
}