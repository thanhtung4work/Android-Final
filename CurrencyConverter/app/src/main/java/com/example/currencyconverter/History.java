package com.example.currencyconverter;

import java.util.Date;

public class History {
    public String cur1, cur2;
    public double value1, value2;
    public Date date = null;
    History(String cur1, double value1, String cur2, double value2){
        this.cur1 = cur1;
        this.cur2 = cur2;
        this.value1 = value1;
        this.value2 = value2;
    }

    History(String cur1, double value1, String cur2, double value2, Date date){
        this.cur1 = cur1;
        this.cur2 = cur2;
        this.value1 = value1;
        this.value2 = value2;
        this.date = date;
    }

    @Override
    public String toString() {
        return String.format("%s %,.2f%n\t=%n%s %,.2f", cur1, value1, cur2, value2);
    }
}
