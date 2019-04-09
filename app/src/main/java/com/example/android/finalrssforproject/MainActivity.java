// ***************************************************************
//
//   finalRSSProject
//
//   MainActivity.java
//
//   By: Urry Donahoe - Richard Cosma
//
//   April 7th, 2019
//
//   Goes to this link "" and and reads the specified rates and then puts them into an array list and then pastes them in the activity_main.xml
//   and then proceeds to attach a link to each one one the rates displayed on the list view and then when you click on one of the boxes it takes
//   you to this website and lets you mess with the rates some more
//
// ***************************************************************


package com.example.android.finalrssforproject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    ListView myRss;
    ArrayList<String> titles;
    ArrayList<String> link;
    ArrayList<String> inverse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //some stuff
        myRss = (ListView) findViewById(R.id.myRss);

        titles = new ArrayList<String>();

        //a cool hack because it wont show the first thing that you add to the ArrayList titles
        titles.add("first");
        link = new ArrayList<String>();
        inverse = new ArrayList<String>();

        myRss.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //this is the thing that makes the box a link that you can click on
                Uri uri = Uri.parse(link.get(position));
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        //some more stuff
        new ProccessInBackground().execute();

    }

    //some more stuff
    public InputStream getInputStream(URL url) {
        try {
            return url.openConnection().getInputStream();
        }
        catch (IOException e) {
            return null;
        }
    }


    public class ProccessInBackground extends AsyncTask <Integer, Void, String> {

        ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog.setMessage("Loading . . .");
            progressDialog.show();
        }

        @Override
        protected String doInBackground(Integer... integers) {
            try {
                //this is where you set which link it goes to
                URL url = new URL("http://www.floatrates.com/daily/usd.xml");
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                System.out.println(factory.toString());
                XmlPullParser xpp = factory.newPullParser();
                xpp.setInput(getInputStream(url), "UTF-8");
                boolean insideItem = false;
                int eventType = xpp.getEventType();

                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equalsIgnoreCase("item")) {
                            insideItem = true;
                        }
                        else if (xpp.getName().equalsIgnoreCase("description")) {
                            if (insideItem) {
                                titles.add(xpp.nextText());
                            }
                        }
                        else if (xpp.getName().equalsIgnoreCase("link")) {
                            if (insideItem) {
                                link.add(xpp.nextText());

                            }
                        }
                        else if (xpp.getName().equalsIgnoreCase("inversedescription")) {
                            if (insideItem) {
                                titles.add(xpp.nextText());
                            }
                        }
                    }
                    else if (eventType == XmlPullParser.END_TAG && xpp.getName().equalsIgnoreCase("item")) {
                        insideItem = false;
                    }
                    eventType = xpp.next();
                }

            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            //this is where it sets up the ArrayList so that it can be displayed on the activity_main
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, titles);
            myRss.setAdapter(adapter);

            progressDialog.dismiss();
        }
    }
}