package ifmo.mobdev.rssreader;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.lang.annotation.Documented;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class MyIntentService extends IntentService {
    public static final String ACTION_MyIntentService = "ifmo.mobdev.rssreader.intentservice.RESPONSE";
    public static final String RSS_LOAD_OK = "rss loaded successfully";
    public static final String RSS_LOAD_BAD = "xml = null";
    public static final String ITEMS_PARSE_OK = "items parsed successfully";
    public static final String ITEMS_PARSE_BAD = "items = null";
    private ArrayList<HashMap<String, String>> items;
    private RSSDBAdapter mDbHelper;

    public MyIntentService() {
        super("myname");
    }

    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String url = intent.getStringExtra("url");
        String id = intent.getStringExtra("id");
        String screen = intent.getStringExtra("screen");
        String xml = null;
        if (url != null && id != null) {
            long feed_id = Long.parseLong(id);
            try {
                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(url);
                HttpResponse httpResponse = httpClient.execute(httpGet);
                HttpEntity httpEntity = httpResponse.getEntity();
                byte[] x = EntityUtils.toByteArray(httpEntity);
                xml = new String(x);
                String encoding = xml.substring(xml.indexOf("encoding") + 10, xml.indexOf("?>"));
                if (encoding.indexOf("1251") != -1) {
                    xml = new String(x, "Cp-1251");
                }
            } catch (UnsupportedEncodingException e) {
                Log.d("DownloadXMLTask", e.getLocalizedMessage());
            } catch (ClientProtocolException e) {
                Log.d("DownloadXMLTask", e.getLocalizedMessage());
            } catch (IOException e) {
                Log.d("DownloadXMLTask", "IOException");
            } catch (RuntimeException e) {
                Log.d("DownloadXMLTask", "RuntimeException");
            }
            RSSDBAdapter mDbHelper = new RSSDBAdapter(this);
            mDbHelper.open();
            Intent intentResponse = new Intent();
            intentResponse.setAction(BroadcastReceiver2DB.ACTION_MYBROADCASTRECEIVER2);
            intentResponse.addCategory(Intent.CATEGORY_DEFAULT);
            intentResponse.putExtra("id", id);

            if (xml != null) {
                intentResponse.putExtra("xml", RSS_LOAD_OK);
                SAXXMLParser parser = new SAXXMLParser(xml);
                items = parser.parse();

                if (items == null) {
                    intentResponse.putExtra("items", ITEMS_PARSE_BAD);
                } else {
                    intentResponse.putExtra("items", ITEMS_PARSE_OK);
                    for (int i = 0; i < items.size(); i++) {
                        HashMap<String, String> map = items.get(i);
                        mDbHelper.createArticle(feed_id, map.get(MyActivity2DB.TITLE),
                                map.get(MyActivity2DB.DATE), map.get(MyActivity2DB.LINK));
                        int art_id = mDbHelper.getArticleIdByTitle(map.get(MyActivity2DB.TITLE));
                        mDbHelper.createContent(art_id, map.get(MyActivity2DB.DESCR));
                    }
                    //mDbHelper.deleteOver300Articles(feed_id);
                }
            } else {
                intentResponse.putExtra("xml", RSS_LOAD_BAD);
            }
            intentResponse.putExtra("screen", screen);
            sendBroadcast(intentResponse);
        }
    }
}