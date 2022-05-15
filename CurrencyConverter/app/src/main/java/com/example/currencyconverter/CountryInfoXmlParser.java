package com.example.currencyconverter;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class CountryInfoXmlParser {
    private static final String nameSpace = null;
    /**
     * Instantiate the parser
     * */
    public ArrayList parse(InputStream in) throws XmlPullParserException, IOException{
        try{
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return readFeed(parser);
        } finally {
            in.close();
        }
    }

    /**
     * Read the feed
     * */
    public ArrayList readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        ArrayList countries = new ArrayList();

        parser.require(XmlPullParser.START_TAG, nameSpace, "geonames");
        while(parser.next() != XmlPullParser.END_TAG){
            if(parser.getEventType() != XmlPullParser.START_TAG){
                continue;
            }
            String name = parser.getName();
            // starts by looking for country tag
            if(name.equals("country")){
                countries.add(readCountry(parser));
            } else{
                skip(parser);
            }
        }
        return countries;
    }

    /**
     * Parse XML
     * */
    public Country readCountry(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, nameSpace, "country");
        String name = null;
        String currency = null;
        while(parser.next() != XmlPullParser.END_TAG){
            if(parser.getEventType() != XmlPullParser.START_TAG){
                continue;
            }
            String tagName = parser.getName();
            if(tagName.equals("countryName")){
                name = readCountryName(parser);
            } else if(tagName.equals("currencyCode")) {
                currency = readCurrencyCode(parser);
            } else{
                skip(parser);
            }
        }

        Country country = new Country();
        country.name = name;
        country.currency = currency;
        return country;
    }

    // Processes country name in the xml feed
    private String readCountryName(XmlPullParser parser)  throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, nameSpace, "countryName");
        String name = readText(parser);
        parser.require(XmlPullParser.END_TAG, nameSpace, "countryName");
        return name;
    }

    // Processes country name in the xml feed
    private String readCurrencyCode(XmlPullParser parser)  throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, nameSpace, "currencyCode");
        String currency = readText(parser);
        parser.require(XmlPullParser.END_TAG, nameSpace, "currencyCode");
        return currency;
    }

    // For the country's name and currency, extracts their text values.
    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String text = "";
        if (parser.next() == XmlPullParser.TEXT) {
            text = parser.getText();
            parser.nextTag();
        }
        return text;
    }

    /**
     * Skip tags you don't care about
     * */
    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }
}
