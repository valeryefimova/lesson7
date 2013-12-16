package ifmo.mobdev.rssreader;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.HashMap;

public class SAXXMLHandler extends DefaultHandler {
    public static final String ITEM = "item";
    public static final String TITLE = "title";
    public static final String LINK = "link";
    public static final String DESCR = "description";
    public static final String DATE = "pubDate";

    public static final String ITEM2 = "entry";
    public static final String DESCR2 = "summary";
    public static final String DATE2 = "published";
    public static final String LINK2 = "id";

    private ArrayList<HashMap<String, String>> items;
    private String tempVal;
    private StringBuffer buffer;
    private HashMap<String, String> item;
    private boolean inItem = false;
    private boolean putLink = false;

    public SAXXMLHandler() {
        items = new ArrayList<HashMap<String, String>>();
    }

    public ArrayList<HashMap<String, String>> getItems() {
        return items;
    }

    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        tempVal = "";
        buffer = new StringBuffer();
        if (qName.equalsIgnoreCase(ITEM) || qName.equalsIgnoreCase(ITEM2)) {
            item = new HashMap<String, String>();
            inItem = true;
        }
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        tempVal = new String(ch, start, length);
        if (buffer != null) {
            buffer.append(tempVal);
        }
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
        tempVal = buffer.toString();
        if (qName.equalsIgnoreCase(ITEM) || qName.equalsIgnoreCase(ITEM2)) {
            items.add(item);
            putLink = false;
            inItem = false;
        } else if (qName.equalsIgnoreCase(TITLE) && inItem) {
            item.put(TITLE, tempVal);
        } else if ((qName.equalsIgnoreCase(LINK) || qName.equalsIgnoreCase(LINK2)) && inItem && !putLink) {
            putLink = true;
            item.put(LINK, tempVal);
        } else if ((qName.equalsIgnoreCase(DESCR) || qName.equalsIgnoreCase(DESCR2)) && inItem) {
            String description = tempVal;
            //if (description.indexOf("<p>") != -1)  description = description.replaceAll("<p>", "");
            //if (description.indexOf("</p>") != -1)  description = description.replaceAll("</p>", "");
            //if (description.indexOf("<br>") != -1)  description = description.replaceAll("<br>", "\n");
            //if (description.indexOf("<br/>") != -1)  description = description.replaceAll("<br/>", "\n");
            //if (description.indexOf("&amp;") != -1)  description = description.replaceAll("&amp;", "&");
            //if (description.indexOf("&quot;") != -1)  description = description.replaceAll("&quot;", "\"");
            //if (description.indexOf("&gt;") != -1)  description = description.replaceAll("&gt;", ">");
            //if (description.indexOf("&lt;") != -1)  description = description.replaceAll("&lt;", "<");
            //if (description.indexOf("&apos;") != -1)  description = description.replaceAll("&apos;", "'");
            description = description.trim();
            item.put(DESCR, description);
        } else if ((qName.equalsIgnoreCase(DATE) || qName.equalsIgnoreCase(DATE2)) && inItem) {
            item.put(DATE, tempVal);
        }
    }
}
