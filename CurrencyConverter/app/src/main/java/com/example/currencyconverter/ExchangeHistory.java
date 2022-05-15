package com.example.currencyconverter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ExchangeHistory {
    public static List<History> HISTORIES;
    public static void ADD(History history){
        HISTORIES.add(history);
    }
    public static void ADD(String cur1, double value1, String cur2, double value2){
        History history = new History(cur1, value1, cur2, value2);
        if(HISTORIES == null) HISTORIES = new ArrayList<History>();
        HISTORIES.add(history);
    }

    public static void ADD(String cur1, double value1, String cur2, double value2, Date date){
        History history = new History(cur1, value1, cur2, value2, date);
        if(HISTORIES == null) HISTORIES = new ArrayList<History>();
        HISTORIES.add(history);
    }
}
