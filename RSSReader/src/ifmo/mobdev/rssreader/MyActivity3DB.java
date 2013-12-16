package ifmo.mobdev.rssreader;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class MyActivity3DB extends Activity {

    private RSSDBAdapter mDbHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.w3);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        String title = extras.getString("title");
        String link = extras.getString("link");
        //String descr = extras.getString("descr");
        String date = extras.getString("date");

        String descr = null;
        mDbHelper = new RSSDBAdapter(this);
        mDbHelper.open();
        int art_id = mDbHelper.getArticleIdByTitle(title);

        Cursor cursor = mDbHelper.fetchContent(art_id);
        startManagingCursor(cursor);

        cursor.moveToNext();
        int index = cursor.getColumnIndex(RSSDBAdapter.KEY_CONTENT);
        descr = cursor.getString(index);
        WebView wv = (WebView) findViewById(R.id.webView);
        //WebSettings settings = wv.getSettings();
        //settings.setDefaultTextEncodingName("UTF-8");

        String res = "<b>" + title + "</b>" + "<br>" + "<a href=\"" + link + "\">" + link + "</a>"+ "<br>" + descr + "<br>" + date;
        wv.loadDataWithBaseURL(null, res, "text/html", "UTF-8", null);
    }
}

