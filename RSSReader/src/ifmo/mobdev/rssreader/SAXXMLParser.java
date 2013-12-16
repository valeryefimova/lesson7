package ifmo.mobdev.rssreader;

import android.util.Log;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import javax.xml.parsers.SAXParserFactory;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;

public class SAXXMLParser {

    String xml;

    public SAXXMLParser(String xml) {
        this.xml = xml;
    }

    public ArrayList<HashMap<String, String>> parse() {
        ArrayList<HashMap<String, String>> items = null;
        try {
            XMLReader xmlReader = SAXParserFactory.newInstance().newSAXParser().getXMLReader();
            SAXXMLHandler saxHandler = new SAXXMLHandler();
            xmlReader.setContentHandler(saxHandler);
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(xml));
            xmlReader.parse(is);
            items = saxHandler.getItems();

        } catch (Exception ex) {
            Log.d("SAXXMLParser", "parsing failed");
        }
        return items;
    }
}
