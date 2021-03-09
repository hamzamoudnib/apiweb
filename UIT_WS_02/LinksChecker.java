import java.io.Console;
import java.net.URL;
import java.net.HttpURLConnection;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import sun.net.www.protocol.ftp.FtpURLConnection;

public class LinksChecker {

    /**
     * Le type d'exception retourné par les méthodes de cette classe.
     */
    public class Exception extends java.lang.Exception {
        /**
         * l'un des messages suivants, selon le problème :
         * - "URL malformée"
         * - "URL non-HTTP"
         * - "erreur HTTP xxx" (suivi du code à 3 chiffres)
         */
        public String message;
        /**
         * l'URL qui a posé problème
         */
        public String url;

        public Exception(String message, String url) {
            this.message = message;
            this.url = url;
        }
    }

    
    
    
    /**
     * Charge la page à l'URL donnée, et vérifie tous ses liens
     * en comptant les liens corrects, brisés et non-HTTP.
     */
    public void check(String urlstr) throws Exception  {
        int status = 0;
        try {
            // TODO
            // préparation de la requête
            
            URL url = new URL(urlstr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET"); // facultatif : c'est GET par défaut
            conn.addRequestProperty("Accept-language", "fr");
            // définition d'autres en-têtes, au besoin...
            // envoi de la requête
            conn.connect();
            
            //recuperer qlq infos utils
            System.out.println("********************");
            System.out.println("__getHost : " + url.getHost());
            System.out.println("__getPath : " + url.getPath());
            
            //recuperer le host
            hostServer = url.getHost();
            
            //recuperer le partial url
            String splitPath[] = url.getPath().split("/");
            splitPath[splitPath.length-1] = "";
            for (int i = 0 ; i < splitPath.length-1 ; i++){
                partialPath = partialPath + splitPath[i]  + "/" ;  
            }
            
            // exploitation de la réponse
            status = conn.getResponseCode();
            String contentType = conn.getHeaderField("content-type");
            InputStream bodyStream = conn.getInputStream();
            String htmlCode = "";
            BufferedReader bodyReader =
                    new BufferedReader(new InputStreamReader(bodyStream));
            String line = bodyReader.readLine();
            while (line != null) {
                htmlCode = htmlCode + line;
                line = bodyReader.readLine();
            }

            ArrayList<String> listLiensJsoup = new ArrayList<String>();
            System.out.println("///AVEC JSOUP =============================");
            Document doc = Jsoup.parse(htmlCode);
            //liens
            for (Element i : doc.getAllElements().select("a[href]")) {
                listLiensJsoup.add(i.attr("href"));
            }
            //resources
            for (Element i : doc.getAllElements().select("[src]")) {
                listLiensJsoup.add(i.attr("src"));
            }
            //imports
            for (Element i : doc.getAllElements().select("link[href]")) {
                listLiensJsoup.add(i.attr("href"));
            }
            
            for (String s : listLiensJsoup){
                if (s.contains(":"))
                    listUrl.add(s);
                else if (s.replace("..","").contains(".") || s.split("/").length == 2 ) {
                    String resource =  s.replace("..","").contains(".")?s.split("/")[s.split("/").length-1]:"";
                    listUrl.add( urlstr.split("//")[0] + "//" +  this.hostServer + this.partialPath + resource );
                }
                else{
                    String resource = "";
                    String pathAndResource [] = s.replace("..","").split("/");
                    String pathAndResourceBis [] = Arrays.copyOfRange(pathAndResource, 3, pathAndResource.length);
                    resource = String.join("/", pathAndResourceBis);
                    listUrl.add( urlstr.split("//")[0] + "//" +  this.hostServer + this.partialPath + resource );
                }
                
            }
            
            for (String s : listUrl){
                System.out.println("_______________");
                System.out.println(s);
            }
            

            
            
            
        } catch (MalformedURLException ex) {
            throw new Exception("URL malformée", urlstr); 
            
        } catch (java.lang.ClassCastException ex) {
            throw new Exception("URL non-HTTP", urlstr); 
        }catch (java.io.IOException ex) {
            throw new Exception("erreur HTTP "+ status, urlstr); 
        }
        
        
        
        
    }
    
    public int getResponseUrl(String urlstr) {
        
        URL url;
        int reponse = 0;
        try {
            url = new URL(urlstr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET"); // facultatif : c'est GET par défaut
            conn.addRequestProperty("Accept-language", "fr");
            conn.connect();
            // exploitation de la réponse
            reponse = conn.getResponseCode();
        } 
        catch (MalformedURLException ex) {
           reponse = -1;
           
        } 
        catch (IOException ex) {
            reponse = 0;
        } 
        catch (java.lang.ClassCastException e) {
            reponse = -1;
        }

        return reponse;
    }

    /**
     * Retourne le nombre de liens OK vérifiés jusqu'à maintenant
     */
    public int linksOk()  {
        linksOk = 0;
        linksKo = 0;
        linksUnknown = 0;
        for (int i  = 0 ; i < listUrl.size() ; i++ ){
            int reponse =0;
            reponse = this.getResponseUrl(this.listUrl.get(i));
            
            if  (reponse >= 200 && reponse <300)
            linksOk++;
            else if ((reponse >= 400 && reponse <500) || reponse == 0)
            linksKo++;
            
            else if ( reponse == -1)
            linksUnknown++;

            
        }

        return linksOk;
    }

    /**
     * Retourne le nombre de liens brisés vérifiés jusqu'à maintenant
     */
    public int linksKo() {
        return linksKo;
    }

    /**
     * Retourne le nombre de liens non-HTTP vérifiés jusqu'à maintenant
     */
    public int linksUnknown() {
        return linksUnknown;
    }

    int linksOk = 0;
    int linksKo = 0;
    int linksUnknown = 0;
    List<String> listUrl = new ArrayList<>() ;
    String partialPath = "";
    String hostServer = "";
    // Vous êtes bien sûr libres de rajouter
    // des méthodes et attributs privés à cette classe.
}
