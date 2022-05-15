package com.example.currencyconverter;

import android.util.Log;

public class Item {
    String description = null;
    double ratio;
    Item() {}

    Item(String description){
        this.description = description;
    }

    public Double[] parseCDATA(){
        int index = description.indexOf("<br/>");
        String ratioString = description.substring(0, index).trim();
        String[] strs = ratioString.split(" ");

        Double[] ratio = new Double[2];
        int i = 0;
        for(String str : strs){
            try{
                str = str.trim();
                ratio[i] = Double.parseDouble(str);
                i++;
            } catch (Exception ex){
                continue;
            }
        }
        Log.d("1", String.valueOf(ratio[0]));
        Log.d("2", String.valueOf(ratio[1]));
        return ratio;
    }
}
