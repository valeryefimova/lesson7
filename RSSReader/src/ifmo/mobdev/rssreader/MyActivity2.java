package ifmo.mobdev.rssreader;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.*;

import java.util.ArrayList;
import java.util.HashMap;


public class MyActivity2 extends Activity {
    public static final String TITLE = "title";
    public static final String LINK = "link";
    public static final String DESCR = "description";
    public static final String DATE = "pubDate";
    public static final String RSS_LOADED = "rssloaded";
    public static final String RELOAD = "reload";
    ImageView pict;
    BroadcastReceiver2 myBroadcastReceiver2;
    BroadcastReceiver3 myBroadcastReceiver3;
    static AlarmManager am;
    PendingIntent pi;
    String url;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.w2);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        url = extras.getString("url");
        pict = (ImageView) findViewById(R.id.imageView);

        /*
        new DownloadXMLTask(new RSSLoadCallback()  {
            @Override
            public void onRSSLoaded(String xml) {
                //XMLParser parser = new XMLParser(xml);
                SAXXMLParser parser = new SAXXMLParser(xml);
                items = parser.parse();

                if (items == null) {
                    pict.setImageResource(R.drawable.wrongurl);
                    Toast toast = Toast.makeText(MyActivity2.this, "Wrong URL", 3000);
                    toast.show();
                } else {
                    pict.setVisibility(View.INVISIBLE);
                    addToListView();
                }
            }
            @Override
        public void onRSSLoadFailed(Exception e) {
                pict.setImageResource(R.drawable.wrongurl);
                Toast toast = Toast.makeText(MyActivity2.this, e.getLocalizedMessage(), 3000);
                toast.show();
            }
        }).execute(url);
        */

        Intent intentMyIntentService = new Intent(this, MyIntentService.class);
        startService(intentMyIntentService.putExtra("url", url));

        ListView lv = (ListView) findViewById(R.id.listView);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String title = ((TextView) view.findViewById(R.id.title)).getText().toString();
                String link = ((TextView) view.findViewById(R.id.link)).getText().toString();
                String descr = ((TextView) view.findViewById(R.id.descr)).getText().toString();
                String date = ((TextView) view.findViewById(R.id.date)).getText().toString();

                Intent intent = new Intent(MyActivity2.this, MyActivity3.class);
                intent.putExtra("title", title);
                intent.putExtra("link", link);
                intent.putExtra("descr", descr);
                intent.putExtra("date", date);
                startActivity(intent);
            }
        });

        myBroadcastReceiver2 = new BroadcastReceiver2(pict, lv);
        IntentFilter intentFilter2 = new IntentFilter(MyIntentService.ACTION_MyIntentService);
        intentFilter2.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(myBroadcastReceiver2, intentFilter2);

        myBroadcastReceiver3 = new BroadcastReceiver3(url, pict);
        IntentFilter intentFilter3 = new IntentFilter(RELOAD);
        intentFilter3.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(myBroadcastReceiver3, intentFilter3);

        Intent in = new Intent(MyActivity2.this, BroadcastReceiver3.class);
        in.putExtra("url", url);
        pi = PendingIntent.getBroadcast(MyActivity2.this, 0, in, PendingIntent.FLAG_UPDATE_CURRENT);
        am = (AlarmManager) (this.getSystemService(Context.ALARM_SERVICE));
        am.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(), 10000, pi);
    }

    @Override
    protected void onDestroy() {
        am.cancel(pi);
        unregisterReceiver(myBroadcastReceiver2);
        unregisterReceiver(myBroadcastReceiver3);
        super.onDestroy();
    }


}
