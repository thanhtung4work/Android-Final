package com.example.currencyconverter;

import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class FxExchangeXmlParser {
    private static final String nameSpace = null;
    /**
     * Instantiate the parser
     * */
    public ArrayList parse(InputStream in) throws XmlPullParserException, IOException {
        try{
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            Log.d("parse", "ok");
            return readFeed(parser);
        } finally {
            in.close();
        }
    }

    /**
     * Read the feed
     * */
    public ArrayList readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        ArrayList items = new ArrayList();

        parser.require(XmlPullParser.START_TAG, nameSpace, "rss");
        while(parser.next() != XmlPullParser.END_TAG){
            if(parser.getEventType() != XmlPullParser.START_TAG){
                continue;
            }
            String name = parser.getName();
            // starts by looking for channel tag
            if(name.equals("channel")){
                // items.add(readItem(parser));
                items.add(readChannel(parser));
            } else{
                skip(parser);
            }
        }
        Log.d("read feed", "ok");
        return items;
    }

    public Item readChannel(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, nameSpace, "channel");
        String description = null;
        while(parser.next() != XmlPullParser.END_TAG){
            if(parser.getEventType() != XmlPullParser.START_TAG){
                continue;
            }
            String tagName = parser.getName();
            if(tagName.equals("item")){
                return readItem(parser);
            } else{
                skip(parser);
            }
        }
        return new Item("Empty");
    }

    /**
     * Parse XML
     * */
    public Item readItem(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, nameSpace, "item");
        String description = null;
        while(parser.next() != XmlPullParser.END_TAG){
            if(parser.getEventType() != XmlPullParser.START_TAG){
                continue;
            }
            String tagName = parser.getName();
            if(tagName.equals("description")){
                Log.d("read item", "ok");
                description = readDescription(parser);
            } else{
                skip(parser);
            }
        }

        Item item = new Item(description);
        return item;
    }

    // Processes description in the xml feed
    private String readDescription(XmlPullParser parser)  throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, nameSpace, "description");
        String desc = readText(parser);
        parser.require(XmlPullParser.END_TAG, nameSpace, "description");
        Log.d("read desc", "ok");
        return desc;
    }

    // For the description
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
