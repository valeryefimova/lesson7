package ifmo.mobdev.rssreader;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

public class MyActivity3 extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.w3);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        String title = extras.getString("title");
        String link = extras.getString("link");
        String descr = extras.getString("descr");
        String date = extras.getString("date");

        WebView wv = (WebView) findViewById(R.id.webView);
        WebSettings settings = wv.getSettings();
        settings.setDefaultTextEncodingName("UTF-8");

        String res = "<b>" + title + "</b>" + "<br>" + "<a href=\"" + link + "\">" + link + "</a>"+ "<br>" + descr + "<br>" + date;
        wv.loadDataWithBaseURL(null, res, "text/html", "UTF-8", null);
    }
}
