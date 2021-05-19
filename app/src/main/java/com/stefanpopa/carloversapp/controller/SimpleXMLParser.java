package com.stefanpopa.carloversapp.controller;

import android.util.Log;

import com.stefanpopa.carloversapp.model.Entry;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class SimpleXMLParser {
    private String rssName;
    private String xmlData;
    private Entry newEntry;
    private String imageLinkTagName = "";
    private Integer imageLinkTagPosition = 0;

    public SimpleXMLParser(String rssName, String xmlData, String imageLinkTagName, Integer imageLinkTagPosition) {
        this.rssName = rssName;
        this.xmlData = xmlData;
        this.imageLinkTagName = imageLinkTagName;
        this.imageLinkTagPosition = imageLinkTagPosition;
    }

    public List parseXML() throws XmlPullParserException, IOException {
        List<Entry> entryList = new ArrayList();
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XmlPullParser xpp = factory.newPullParser();

        xpp.setInput(new StringReader(xmlData));
        int eventType = xpp.getEventType();
        newEntry = null;
        while (eventType != XmlPullParser.END_DOCUMENT) {
            String name = xpp.getName();
            if (eventType == XmlPullParser.START_TAG) {
                Log.d("XML_TAGS", name);
                if (name.equals("item")) {
                    newEntry = new Entry(rssName);
                } else {
                    if (newEntry != null) {
                        if (name.equals("title") && newEntry.getTitle() == null) {
                            newEntry.setTitle(xpp.nextText());
                        } else if (name.equals("description") && newEntry.getSummary() == null) {
                            newEntry.setSummary(xpp.nextText());
                        } else if (name.equals("link") && newEntry.getLink() == null) {
                            newEntry.setLink(xpp.nextText());
                        } else if (name.equals(imageLinkTagName) && newEntry.getImageUrl() == null) {
                            Log.d("XML_ATTRIBUTE_COUNT", String.valueOf(xpp.getAttributeCount()));
                            if(imageLinkTagPosition != -1){
                                newEntry.setImageUrl(xpp.getAttributeValue(imageLinkTagPosition));
                            }else{
                                newEntry.setImageUrl(xpp.nextText());
                            }
                        }
                    }
                }
            } else if (eventType == XmlPullParser.END_TAG) {
                if (name.equals("item") && newEntry != null) {
                    entryList.add(newEntry);
                }
            }
            eventType = xpp.next();
        }

        return entryList;
    }

}
