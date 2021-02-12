/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.testparse;
import java.io.Console;
import java.net.URL;
import java.net.HttpURLConnection;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 *
 * @author rombot
 */
public class testParse {
    public static void main(String[] args) {
        
        System.out.println("*************");

        try {
        // préparation de la requête
        URL url = new URL("https://www.jeuxvideo.com/sorties/dates-de-sortie.htm");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET"); // facultatif : c'est GET par défaut
        conn.addRequestProperty("Accept-language", "fr");
        // définition d'autres en-têtes, au besoin...

        // envoi de la requête
        conn.connect();

        // exploitation de la réponse
        int status = conn.getResponseCode();
        String contentType = conn.getHeaderField("content-type");
        InputStream bodyStream = conn.getInputStream();
        String htmlCode = "";
        BufferedReader bodyReader =
                new BufferedReader( new InputStreamReader(bodyStream, "UTF-8"));//imporatnt !!!!!
        String line = bodyReader.readLine();
        while (line != null) {
            //System.out.println(line);
            htmlCode = htmlCode + line;
            line = bodyReader.readLine();
        }

        Document doc = Jsoup.parse(htmlCode);
        System.out.println(doc.title());
        
        for (Element i : doc.getElementsByClass("items__3tKMxC").get(0).getElementsByClass("item__3izRwo")) {
            System.out.println("=============");
            System.out.println(i.text());
        }
        
       

    } catch (Exception e) {
        e.printStackTrace();
    }
        
        
        
    }
    
}
