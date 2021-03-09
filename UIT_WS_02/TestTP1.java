
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.text.Document;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

public class TestTP1 {
    public static void main(String args[]) throws UnsupportedEncodingException, SAXException, IOException {

        try {
            testLinks();
            testExempleCours();
            testUrlMalformée();
            testNonHttpUrl();
            testErreursHttp();
            System.out.println("Tous les tests passent, bravo !");
        }
        catch (Exception ex) {
            if (ex instanceof StopTest) {
                throw (StopTest) ex;
            } else {
                throw new StopTest(ex);
            }
        }
    }

    static void testLinks() throws Exception {
        int values[] = {3, 5, 8};
        for (int value: values) {
            LinksChecker lc = new LinksChecker();
            String url = "http://httpbin.org/links/" + value + "/0";
            lc.check(url);
            myAssertLinksCount(lc, value-1, 0, 0);
        }
    }

    static void testExempleCours() throws Exception {
        LinksChecker lc = new LinksChecker();
        lc.check("https://perso.liris.cnrs.fr/pierre-antoine.champin/enseignement/apiweb/_static/test_liens.html");
        myAssertLinksCount(lc, 3, 4, 2);
    }

    static void testUrlMalformée() throws Exception {
        String url = "!";
        LinksChecker lc = new LinksChecker();
        try {
            lc.check(url);
            myAssert(false, "LinksChecker.check doit lancer une exception si l'URL est malformée");
        }
        catch (LinksChecker.Exception ex) {
            myAssert(ex.message.equals("URL malformée"), "le message de l'exception doit être \"URL malformée\"");
            myAssert(ex.url.equals(url), "l'attribut url de l'exception doit contenir l'URL qui a été passée à la méthode check");
        }
    }

    static void testNonHttpUrl() throws Exception {
        String url = "ftp://ftp.upload.debian.org";
        LinksChecker lc = new LinksChecker();
        try {
            lc.check(url);
            myAssert(false, "LinksChecker.check doit lancer une exception si l'URL est malformée");
        }
        catch (LinksChecker.Exception ex) {
            myAssert(ex.message.equals("URL non-HTTP"), "le message de l'exception doit être \"URL non-HTTP\"");
            myAssert(ex.url.equals(url), "l'attribut url de l'exception doit contenir l'URL qui a été passée à la méthode check");
        }        
    }

    static void testErreursHttp() throws Exception {
        int statusCodes[] = {403, 404, 500};
        for (int statusCode: statusCodes) {
            LinksChecker lc = new LinksChecker();
            String url = "http://httpbin.org/status/" + statusCode;
            try {
                lc.check(url);
                myAssert(false, "LinksChecker.check doit lancer une exception si la réponse HTTP a un status différent de 2xx");
            }
            catch (LinksChecker.Exception ex) {
                myAssert(ex.message.equals("erreur HTTP "+statusCode), "le message de l'exception doit être \"erreur HTTP XXX\" (ou XXX est le code de status)");
                myAssert(ex.url.equals(url), "l'attribut url de l'exception doit contenir l'URL qui a été passée à la méthode check");
            }
        }
    }

    static void testStatus201() throws Exception {
        String url = "http://httpbin.org/status/201";
        LinksChecker lc = new LinksChecker();
        try {
            lc.check(url);
        }
        catch (LinksChecker.Exception ex) {
            myAssert(ex.message != "erreur HTTP 201", "les codes démarrant par 2 ne sont PAS des erreurs");
            myAssert(false, "exception innatendue");
        }
    }




    static void myAssert(boolean test, String message) throws StopTest {
        if (!test) {
            throw new TestTP1.StopTest(message);
        }
    }

    static void myAssertLinksCount(LinksChecker lc, int ok, int ko, int unknown) {
        myAssert(lc.linksOk() == ok, "linksOk doit retourner "+ok+" et non " + lc.linksOk());
        myAssert(lc.linksKo() == ko, "linksKo doit retourner "+ko+" et non " + lc.linksKo());
        myAssert(lc.linksUnknown() == unknown, "linksUnknown doit retournet "+unknown+" et non " + lc.linksUnknown());    
    }

    public static class StopTest extends RuntimeException {
        StopTest(String message) {
            super(message);
        }
        StopTest(Throwable cause) {
            super(make_message(cause), cause);
            
        }
        static String make_message(Throwable cause) {
            if (cause instanceof LinksChecker.Exception) {
                return "erreur inattendue";
            } else {
                return "l'exception levée doit être de type LinksChecker.Exception";
            }
        }
    }
}
