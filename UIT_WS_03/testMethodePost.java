/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.iut_ws_03;
import java.io.Console;
import java.net.URL;
import java.net.HttpURLConnection;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 *
 * @author rombot
 */
public class testMethodePost {
    
    public static void main(String[] args) {
        try {
            URL url = new URL("https://liris-ktbs01.insa-lyon.fr:8000/blogephem/");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.addRequestProperty("content-type", "application/xml");
            conn.setDoOutput(true);
            OutputStream stream = conn.getOutputStream();
            OutputStreamWriter writer = new OutputStreamWriter(stream);
            writer.write("<article>\n" +
                        "      <title>popopopopo</title>\n" +
                        "      <body>Il suffit de lire la documenation...</body>\n" +
                        "      <date>2015-09-15 12:34:56</date>\n" +
                        "  </article>"); //
            writer.close();
            conn.connect();
            
            System.out.println(conn.getResponseCode());
            
        } catch (MalformedURLException ex) {
            Logger.getLogger(testMethodePost.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(testMethodePost.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
