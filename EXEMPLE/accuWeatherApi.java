
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import static javafx.scene.input.KeyCode.T;


/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author rombot
 */
public class accuWeatherApi {
    
    public static void main(String[] args) throws MalformedURLException, IOException {
        String key = "KbEmme5EWFowNSAfGFWVq5bzbJ3sbNsR"; //!!! ajouter votre clé ici
        URL url = new URL("http://dataservice.accuweather.com/locations/v1/cities/search?apikey="+key+"&q=Lyon&offset=10");
        HttpURLConnection http = (HttpURLConnection)url.openConnection();
        System.out.println(http.getResponseCode() + " " + http.getResponseMessage());
        
        int status = 0;
        status = http.getResponseCode();
        String contentType = http.getHeaderField("content-type");
        InputStream bodyStream = http.getInputStream();
        String jsonRepnse = "";
        BufferedReader bodyReader =
                new BufferedReader(new InputStreamReader(bodyStream));
        String line = bodyReader.readLine();
        while (line != null) {
            jsonRepnse = jsonRepnse + line;
            line = bodyReader.readLine();
        }

        http.disconnect();
        
        
        //je commence a exploiter le json de l'api
        Gson gson = new Gson();
        ArrayList map = gson.fromJson(jsonRepnse,  ArrayList.class);
        Map tempMap = (Map) map.get(0);
        Map mapRegion =(Map) tempMap.get("Region");
        
        System.out.println("---------------------");
        System.out.println("Version : " + tempMap.get("Version"));
        System.out.println("Key : " + tempMap.get("Key"));
        System.out.println("LocalizedName : " + tempMap.get("LocalizedName"));
        System.out.println("Type : " + tempMap.get("Type"));
        //System.out.println("Region : " + tempMap.get("Region"));
        System.out.println("LocalizedName : " + mapRegion.get("LocalizedName"));
      
        //http://dataservice.accuweather.com/currentconditions/v1/171210?apikey=KbEmme5EWFowNSAfGFWVq5bzbJ3sbNsR&q=Lyon&offset=10
        
        
        url = new URL("http://dataservice.accuweather.com/currentconditions/v1/" + tempMap.get("Key") + "?apikey="+ key +"&q=Lyon&offset=10");
        http = (HttpURLConnection)url.openConnection();
        System.out.println(http.getResponseCode() + " " + http.getResponseMessage());
        
        status = 0;
        status = http.getResponseCode();
        contentType = http.getHeaderField("content-type");
        bodyStream = http.getInputStream();
        jsonRepnse = "";
        bodyReader = new BufferedReader(new InputStreamReader(bodyStream));
        line = bodyReader.readLine();
        while (line != null) {
            jsonRepnse = jsonRepnse + line;
            line = bodyReader.readLine();
        }
        
        
        map = gson.fromJson(jsonRepnse,  ArrayList.class);
        tempMap = (Map) map.get(0);
        mapRegion =(Map) tempMap.get("Region");
        
        System.out.println("---------------------");
        System.out.println("WeatherText : " + tempMap.get("WeatherText"));
        System.out.println("Temperature : " + ((Map)((Map)tempMap.get("Temperature")).get("Metric")).get("Value")+ ((Map)((Map)tempMap.get("Temperature")).get("Metric")).get("Unit"));
        //System.out.println(jsonRepnse);
    }
    
}
