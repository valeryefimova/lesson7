package ifmo.mobdev.rssreader;

import android.util.Log;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Date;

public class XMLParser {
    //public static final String ITEM = "item";
    //public static final String TITLE = "title";
    //public static final String LINK = "link";
    //public static final String DESCR = "description";
    //public static final String DATE = "pubDate";

    public static String ITEM = "item";
    public static String TITLE = "title";
    public static String LINK = "link";
    public static String DESCR = "description";
    public static String DATE = "pubDate";
    private final String xml;
    Document doc;

    XMLParser(String xml) {
        this.xml = xml;
        doc = getDomElement();
    }

    public Document getDomElement(){
        Document doc = null;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(xml));
            doc = db.parse(is);
        } catch (ParserConfigurationException e) {
            Log.d("getDomElement", e.getLocalizedMessage());
            return null;
        } catch (SAXException e) {
            Log.d("getDomElement", e.getLocalizedMessage());
            return null;
        } catch (IOException e) {
            Log.d("getDomElement", e.getLocalizedMessage());
            return null;
        }
        return doc;
    }

    public String getValue(Element item, String str) {
        NodeList n = item.getElementsByTagName(str);
        return this.getElementValue(n.item(0));
    }

    public final String getElementValue(Node elem ) {
        String res = "";
        Node child;
        if( elem != null){
            if (elem.hasChildNodes()){
                for( child = elem.getFirstChild(); child != null; child = child.getNextSibling() ){
                    //return child.getNodeValue();
                    res += child.getNodeValue();
                }
                return res;
            }
        }
        return "empty";
    }

    public ArrayList<HashMap<String, String>> parse() {
        if (doc == null) {
            return null;
        }
        NodeList feed = doc.getElementsByTagName("feed");
        NodeList rss = doc.getElementsByTagName("rss");
        if (rss.item(0)!= null && rss.item(0).hasChildNodes()) {
            ITEM = "item";
            DESCR = "description";
            DATE = "pubDate";
            LINK = "link";
        } else if (feed.item(0)!= null && feed.item(0).hasChildNodes()) {
            ITEM = "entry";
            DESCR = "summary";
            DATE = "published";
            LINK = "id";
        } else {
            //
        }
        NodeList nl = doc.getElementsByTagName(ITEM);
        ArrayList<HashMap<String, String>> items = new ArrayList<HashMap<String, String>>();
        for (int i = 0; i < nl.getLength(); i++) {
            HashMap<String, String> map = new HashMap<String, String>();
            Element e = (Element) nl.item(i);
            String description = getValue(e, DESCR);
            if (description.indexOf("<p>") != -1)  description = description.replaceAll("<p>", "");
            if (description.indexOf("</p>") != -1)  description = description.replaceAll("</p>", "");
            if (description.indexOf("<br>") != -1)  description = description.replaceAll("<br>", "\n");
            if (description.indexOf("<br/>") != -1)  description = description.replaceAll("<br/>", "\n");
            if (description.indexOf("&amp;") != -1)  description = description.replaceAll("&amp;", "&");
            if (description.indexOf("&quot;") != -1)  description = description.replaceAll("&quot;", "\"");
            if (description.indexOf("&gt;") != -1)  description = description.replaceAll("&gt;", ">");
            if (description.indexOf("&lt;") != -1)  description = description.replaceAll("&lt;", "<");
            if (description.indexOf("&apos;") != -1)  description = description.replaceAll("&apos;", "'");
            description = description.trim();

            map.put("title", getValue(e, TITLE));
            map.put("link", getValue(e, LINK));
            map.put("description", description);
            map.put("pubDate", getValue(e, DATE));
            items.add(map);
        }
        return items;
    }
}
