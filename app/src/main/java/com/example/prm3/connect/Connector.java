package com.example.prm3.connect;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class Connector {

    public static Object connect(String urlAddress)
    {
        try
        {
            URL url=new URL(urlAddress);
            //  HttpURLConnection con= (HttpURLConnection) url.openConnection();
            HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();

            //PROPERTIES
            httpsURLConnection.setRequestMethod("GET");
            httpsURLConnection.setConnectTimeout(15000);
            httpsURLConnection.setReadTimeout(15000);
            httpsURLConnection.setDoInput(true);

            return httpsURLConnection;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}